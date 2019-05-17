/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.si;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.libpostal.libpostal_address_parser_options_t;
import org.bytedeco.libpostal.libpostal_address_parser_response_t;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bytedeco.libpostal.global.postal.*;


/**
 * Describe your step plugin.
 * 
 */
public class JPostalPlugin extends BaseStep implements StepInterface{

  private static Class<?> PKG = JPostalPluginMeta.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

  private JPostalPluginMeta meta;
  private JPostalPluginData data;
  private Map<String, Integer> idxMap = new HashMap<String, Integer>();
  private int newRowSize = 0;

  private String nerFpath = null;
  private AbstractSequenceClassifier classifier;

  private String libPostalFpath = System.getProperty("libpostal.data.dir");
  private boolean isLibPostalInitialized = false;
  private boolean setup1 = false;
  private boolean setup2 = false;
  private boolean setup3 = false;
  private libpostal_address_parser_options_t options = libpostal_get_address_parser_default_options();

  public JPostalPlugin(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
    if(meta.isNer()) {
      initNER();
    }
    initAddressParser();
    Runtime.getRuntime().addShutdownHook(new OnExitHook());
  }

  /**
   * Attempt at running a shutdown hook to cleanup the address parser
   * when the step is actually done running.
   */
  class OnExitHook extends Thread{

    @Override
    public void run(){
      if(libPostalFpath != null) {
        libpostal_teardown();
        libpostal_teardown_parser();
        libpostal_teardown_language_classifier();
        setup1 = false;
        setup2 = false;
        setup3 = false;
      }
      isLibPostalInitialized = false;
    }
  }

  private void initNER(){
    try {
      nerFpath = JPostalPluginDialog.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      String model = System.getProperty("corenlp.ner.model", "english.muc.7class.distsim.crf.ser.gz");
      nerFpath = nerFpath + File.separator + "lib" + File.separator + model;
      classifier = CRFClassifier.getClassifier(nerFpath);
    }catch(URISyntaxException e){
      if(isBasic()){
        logBasic("Syntax Error Parsing Library Path for JPostal");
      }
    }catch(IOException e){
      if(isBasic()){
        logBasic("Failed to Load Core NLP Classifier");
      }
    }catch(ClassNotFoundException e){
      if(isBasic()){
        logBasic("Failed to find Class when Loading Core NLP Model");
      }
    }
  }

  private void initAddressParser(){
    setup1 = libpostal_setup_datadir(libPostalFpath);
    setup2 = libpostal_setup_parser_datadir(libPostalFpath);
    setup3 = libpostal_setup_language_classifier_datadir(libPostalFpath);
    isLibPostalInitialized = true;
  }

  private void teardownAddressParser(){
    libpostal_teardown();
    libpostal_teardown_parser();
    libpostal_teardown_language_classifier();
    setup1 = false;
    setup2 = false;
    setup3 = false;
    isLibPostalInitialized = false;
  }

  /**
   * Initialize and do work where other steps need to wait for...
   *
   * @param stepMetaInterface
   *          The metadata to work with
   * @param stepDataInterface
   *          The data to initialize
   */
  @Override
  public boolean init( StepMetaInterface stepMetaInterface, StepDataInterface stepDataInterface ) {
      return super.init( stepMetaInterface, stepDataInterface );
  }

  /**
   * Checks whether a string contains a specific location.
   *
   * @param text  The text to check against
   * @return  Whether the text contains the location
   */
  private boolean stringContainsLocation(String text){
    boolean contains_loc = false;
    List<List<CoreLabel>> labels = classifier.classify(text);
    for(List<CoreLabel> sentence: labels){
      for(CoreLabel word: sentence) {
        String ctype = word.get(CoreAnnotations.AnswerAnnotation.class);
        if(ctype.toUpperCase().equals("LOCATION")){
          contains_loc = true;
        }
      }
    }
    return contains_loc;
  }

  /**
   * Packages the row.
   *
   * @param house     The house
   * @param postcode  The postal code
   * @param unit      The unit
   * @param road      The road part
   * @param city      The city
   * @param state     The state
   * @param r         The output row
   * @return  The updated object array row
   */
  private Object[] packageRow(String house, String postcode, String unit, String road, String city, String state, Object[] r){
    int idx = this.idxMap.get(meta.getAddressOutField());
    r[idx] = road;

    idx = this.idxMap.get(meta.getAddress2OutField());
    r[idx] = unit;

    idx = this.idxMap.get(meta.getCityOutField());
    r[idx] = city;

    idx = this.idxMap.get(meta.getStateOutField());
    r[idx] = state;

    idx = this.idxMap.get(meta.getZipOutField());
    r[idx] = postcode;

    idx = this.idxMap.get(meta.getHouseOutField());
    r[idx] = house;
    return r;
  }

  /**
   * Parse the address from the appropriate field
   *
   * @param process   Whether to process the row
   * @param text      The text to process
   * @param r         The row to process
   * @return  The object array row representation
   */
  private Object[] parseAddress(boolean process, String text, Object[] r){
    if(process) {
      String house = null;
      String postcode = null;
      String unit = null;
      String road = null;
      String city = null;
      String state = null;
      try {
        BytePointer address = new BytePointer(text, "UTF-8");
        libpostal_address_parser_response_t response = libpostal_parse_address(address, options);
        long count = response.num_components();
        for (int j = 0; j < count; j++) {
          String label = response.labels(j).getString();
          switch(label.toUpperCase()){
            case "HOUSE":
              house = response.components(j).getString();
            case "POSTCODE":
              postcode = response.components(j).getString();
            case "UNIT":
              unit = response.components(j).getString();
            case "ROAD":
              road = response.components(j).getString();
            case "CITY":
              city = response.components(j).getString();
            case "STATE":
              state = response.components(j).getString();
          }
        }
      }catch(IOException e){
        if(isBasic()){
          logBasic("Failed to get Byte Pointer From Text in Address Parser");
        }
      }
      r = packageRow(house, postcode, unit, road, city, state, r);
    }
    return r;
  }

  /**
   * Get and set the address fields.
   *
   * @param rowMeta  The row meta interface
   * @param r        The existing row object without field values;
   * @return  The new row with values
   */
  private Object[] computeRowValues(RowMetaInterface rowMeta, Object[] r){
    Object[] orow = r.clone();
    if(newRowSize > r.length){
      orow = RowDataUtil.resizeArray(r, newRowSize);
    }

    if(meta.getExtractIndex() >= 0) {
      String extractText = (String) orow[meta.getExtractIndex()];
      boolean process = true;
      if(meta.isNer() && !stringContainsLocation(extractText)){
        process = false;
      }

      if(process){
        orow = parseAddress(process, extractText, orow);
      }
    }

    return orow;
  }


  /**
   * Check if the value exists in the array
   *
   * @param arr  The array to check
   * @param v    The value in the array
   * @return  Whether the value exists
   */
  private int stringArrayContains(String[] arr, String v){
    int exists = -1;
    int i = 0;
    while(i < arr.length && exists == -1){
      if(arr[i].equals(v)){
        exists = i;
      }else {
        i += 1;
      }
    }
    return exists;
  }

  /**
   * Recreate the meta, adding the new fields.
   *
   * @param rowMeta   The row meta
   * @return  The changed row meta interface
   */
  private RowMetaInterface getNewRowMeta(RowMetaInterface rowMeta, JPostalPluginMeta meta) throws KettleException{
    String[] fields = getInputRowMeta().getFieldNames();
    newRowSize = getInputRowMeta().size();
    String houseField = meta.getHouseOutField();
    String addressField = meta.getAddressOutField();
    String address2Field = meta.getAddress2OutField();
    String cityField = meta.getCityOutField();
    String stateField = meta.getStateOutField();
    String zipField = meta.getZipOutField();
    String[] fieldnames = {houseField, addressField, address2Field, cityField, stateField, zipField};

    int idx = stringArrayContains(fieldnames, meta.getExtractField());
    if(idx == -1){
      throw new KettleException("JPostal Plugin missing Extract Field");
    }
    meta.setExtractIndex(idx);

    for(int i = 0; i < fieldnames.length; i++){
      String fname = fieldnames[i];
      int cidx = stringArrayContains(fields, fname);
      if(cidx == -1){
        idxMap.put(fname, newRowSize);
        newRowSize += 1;
        ValueMetaInterface value = ValueMetaFactory.createValueMeta(fname, ValueMetaInterface.TYPE_STRING);
        rowMeta.addValueMeta(value);
      }else{
        idxMap.put(fname, cidx);
      }
    }
    return rowMeta;
  }

  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    meta = (JPostalPluginMeta) smi;
    data = (JPostalPluginData) sdi;


    Object[] r = getRow();
    if ( r == null ) {
      teardownAddressParser();
      setOutputDone();
      return false;
    }

    if(first) {
      first = false;
      data.outputRowMeta = getNewRowMeta(getInputRowMeta(), meta);
      meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
    }

    if(isLibPostalInitialized == false){
      initAddressParser();
    }

    Object[] outRow = computeRowValues(data.outputRowMeta, r);
    putRow( data.outputRowMeta, outRow); // copy row to possible alternate rowset(s).

    if ( checkFeedback( getLinesRead() ) ) {
      if ( log.isBasic() )
        logBasic( BaseMessages.getString( PKG, "JPostalPlugin.Log.LineNumber" ) + getLinesRead() );
    }

    return true;
  }
}
