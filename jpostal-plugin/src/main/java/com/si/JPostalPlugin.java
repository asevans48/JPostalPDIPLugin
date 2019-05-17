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

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

import java.util.HashMap;
import java.util.Map;


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

  public JPostalPlugin(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  /**
   * Initialize and do work where other steps need to wait for...
   *
   * @param stepMetaInterface
   *          The metadata to work with
   * @param stepDataInterface
   *          The data to initialize
   */
  public boolean init( StepMetaInterface stepMetaInterface, StepDataInterface stepDataInterface ) {
      return super.init( stepMetaInterface, stepDataInterface );
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

    String house = null;
    String postcode = null;
    String unit = null;
    String road = null;
    String city = null;
    String state = null;

    if(meta.getExtractIndex() >= 0) {

    }

    int idx = this.idxMap.get(meta.getAddressOutField());
    orow[idx] = road;

    idx = this.idxMap.get(meta.getAddress2OutField());
    orow[idx] = unit;

    idx = this.idxMap.get(meta.getCityOutField());
    orow[idx] = city;

    idx = this.idxMap.get(meta.getStateOutField());
    orow[idx] = state;

    idx = this.idxMap.get(meta.getZipOutField());
    orow[idx] = postcode;

    idx = this.idxMap.get(meta.getHouseOutField());
    orow[idx] = house;

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


    Object[] r = getRow(); // get row, set busy!
    if ( r == null ) {
      // no more input to be expected...
      setOutputDone();
      return false;
    }

    if(first) {
      first = false;
      data.outputRowMeta = getNewRowMeta(getInputRowMeta(), meta);
      meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
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
