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

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
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

import java.io.IOException;
import java.util.List;


/**
 * Describe your step plugin.
 * 
 */
public class JPostalPlugin extends BaseStep implements StepInterface{

  private static Class<?> PKG = JPostalPluginMeta.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

  private JPostalPluginMeta meta;
  private JPostalPluginData data;
  private int newRowSize = 0;

  public JPostalPlugin(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
    Runtime.getRuntime().addShutdownHook(new OnExitHook());
  }

  /**
   * Attempt at running a shutdown hook to cleanup the address parser
   * when the step is actually done running.
   */
  class OnExitHook extends Thread{

    @Override
    public void run(){
      data.teardownAddressParser();
    }
  }

  private void initNER(){
    try {
      if(data.getClassifier() == null) {
        if(meta.getNerPath() == null){
          throw new NullPointerException("No NLP Model Specified");
        }

        data.initClassifier(meta.getNerPath());
        if(data.getClassifier() == null){
          if(isBasic()){
            logBasic("NER Classifier Not Loaded");
          }
          stopAll();
        }else{
          if(isBasic()){
            logBasic("NER Classifier Loaded");
          }
        }
      }
    }catch(IOException e){
      if(isBasic()){
        logBasic("Failed to Load Core NLP Classifier");
      }
    }catch(NullPointerException e) {
      if(isBasic()){
        logBasic("Path to Model Was Null");
      }
    }catch(ClassNotFoundException e){
      if(isBasic()){
        logBasic("Failed to find Class when Loading Core NLP Model");
      }
    }
  }

  private void initAddressParser(){
    boolean wasSetup = data.setupAddressParser(meta.getLpPath());
    if(!wasSetup){
      if(isBasic()){
        logBasic("Failed to Initialize Address Parser");
      }
      stopAll();
    }else{
      if(isBasic()){
        logBasic("Address Parser Loaded");
      }
    }
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
      this.meta = (JPostalPluginMeta) stepMetaInterface;
      this.data = (JPostalPluginData) stepDataInterface;
      initNER();
      initAddressParser();
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
    if(text != null) {
      if(data.getClassifier() != null) {
        List<List<CoreLabel>> labels = data.getClassifier().classify(text);
        for (List<CoreLabel> sentence : labels) {
          for (CoreLabel word : sentence) {
            String ctype = word.get(CoreAnnotations.AnswerAnnotation.class);
            if (ctype.toUpperCase().equals("LOCATION")) {
              contains_loc = true;
            }
          }
        }
      }else{
        throw new NullPointerException("Classifier For Location Detection Null");
      }
    }
    return contains_loc;
  }

  /**
   * Packages the row.
   *
   * @param rmi       The row meta interface
   * @param house     The house
   * @param postcode  The postal code
   * @param unit      The unit
   * @param road      The road part
   * @param city      The city
   * @param state     The state
   * @param r         The output row
   * @return  The updated object array row
   */
  private Object[] packageRow(RowMetaInterface rmi, String house, String postcode, String unit, String road, String city, String state, Object[] r){
    int idx = rmi.indexOfValue(meta.getAddressOutField());
    r[idx] = road;

    idx = rmi.indexOfValue(meta.getAddress2OutField());
    r[idx] = unit;

    idx = rmi.indexOfValue(meta.getCityOutField());
    r[idx] = city;

    idx = rmi.indexOfValue(meta.getStateOutField());
    r[idx] = state;

    idx = rmi.indexOfValue(meta.getZipOutField());
    r[idx] = postcode;

    idx = rmi.indexOfValue(meta.getHouseOutField());
    r[idx] = house;

    return r.clone();
  }

  /**
   * Parse the address from the appropriate field
   *
   * @param rmi       The RowMetaInterface containing output meta
   * @param process   Whether to process the row
   * @param text      The text to process
   * @param r         The row to process
   * @return  The object array row representation
   */
  private Object[] parseAddress(RowMetaInterface rmi, boolean process, String text, Object[] r){
    if(process) {
      String house = null;
      String postcode = null;
      String unit = null;
      String road = null;
      String city = null;
      String state = null;
      String house_number = null;
      try {
        libpostal_address_parser_response_t response = data.parseAddress(text);
        long count = response.num_components();
        for (int j = 0; j < count; j++) {
          String label = response.labels(j).getString();
          switch(label.toUpperCase().trim()){
            case "HOUSE":
              house = response.components(j).getString();
              break;
            case "POSTCODE":
              postcode = response.components(j).getString();
              break;
            case "UNIT":
              unit = response.components(j).getString();
              break;
            case "HOUSE_NUMBER":
              house_number = response.components(j).getString();
              break;
            case "ROAD":
              road = response.components(j).getString();
              break;
            case "CITY":
              city = response.components(j).getString();
              break;
            case "STATE":
              state = response.components(j).getString();
              break;
            default:
              break;
          }
        }
        if(house_number != null && road != null){
          road = house_number + " " + road;
        }else if(house_number != null && road == null){
          road = house_number;
        }
      }catch(IOException e){
        if(isBasic()){
          logBasic("Failed to get Byte Pointer From Text in Address Parser");
        }
      }
      r = packageRow(rmi, house, postcode, unit, road, city, state, r);
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
        orow = parseAddress(data.outputRowMeta, process, extractText, orow);
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
    String[] fields = rowMeta.getFieldNames();
    newRowSize = getInputRowMeta().size();
    String houseField = meta.getHouseOutField();
    String addressField = meta.getAddressOutField();
    String address2Field = meta.getAddress2OutField();
    String cityField = meta.getCityOutField();
    String stateField = meta.getStateOutField();
    String zipField = meta.getZipOutField();
    String[] fieldnames = {houseField, addressField, address2Field, cityField, stateField, zipField};

    int idx = stringArrayContains(fields, meta.getExtractField());
    if(idx == -1){
      throw new KettleException("JPostal Plugin missing Extract Field");
    }
    meta.setExtractIndex(idx);

    for(int i = 0; i < fieldnames.length; i++){
      String fname = fieldnames[i];
      int cidx = stringArrayContains(fields, fname);
      if(cidx == -1){
        ValueMetaInterface value = ValueMetaFactory.createValueMeta(fname, ValueMetaInterface.TYPE_STRING);
        rowMeta.addValueMeta(value);
      }
    }
    return rowMeta;
  }

  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    Object[] r = getRow();
    if ( r == null ) {
      setOutputDone();
      return false;
    }

    if(first) {
      data.outputRowMeta = getInputRowMeta().clone();
      meta.getFields(data.outputRowMeta, getStepname(), null, null, this, null, null);
      getNewRowMeta(data.outputRowMeta, meta);
      first = false;
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
