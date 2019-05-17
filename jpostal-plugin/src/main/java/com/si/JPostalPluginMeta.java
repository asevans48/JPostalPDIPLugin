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

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.util.List;


/**
 * Skeleton for PDI Step plugin.
 */
@Step( id = "JPostalPlugin", image = "JPostalPlugin.svg", name = "Extract Address",
    description = "Extracts an address from the string.", categoryDescription = "Transform" )
public class JPostalPluginMeta extends BaseStepMeta implements StepMetaInterface{


  private static Class<?> PKG = JPostalPlugin.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

  /*
    Specific metadata for the step.
  */
  private String extractField = "Field to Extract";
  private int extractIndex = -1;
  private String houseOutField = "house_out";
  private String addressOutField = "address_out";
  private String address2OutField = "address2_out";
  private String cityOutField = "city_out";
  private String stateOutField = "state_out";
  private String zipOutField = "zip_out";
  private Boolean isNer = false;
  private String lpPath = null;
  private String nerPath = null;

  public JPostalPluginMeta() {
    super(); // allocate BaseStepMeta
  }

  /*
  Getters and setters for specific meta data
   */
  public String getLpPath() {
      return lpPath;
  }

  public void setLpPath(String lpPath) {
      this.lpPath = lpPath;
  }

  public String getNerPath() {
      return nerPath;
  }

  public void setNerPath(String nerPath) {
      this.nerPath = nerPath;
  }

  public boolean isNer() {
        return isNer;
    }

  public void setNer(boolean ner) {
        isNer = ner;
    }

  public int getExtractIndex() {
      return extractIndex;
  }

  public void setExtractIndex(int extractIndex) {
      this.extractIndex = extractIndex;
  }

  public String getHouseOutField() {
      return houseOutField;
  }

  public void setHouseOutField(String houseOutField) {
      this.houseOutField = houseOutField;
  }

  public String getAddressOutField() {
    return addressOutField;
  }

  public String getExtractField() {
    return extractField;
  }

  public void setExtractField(String extractField) {
   this.extractField = extractField;
  }

  public void setAddressOutField(String addressOutField) {
    this.addressOutField = addressOutField;
  }

  public String getAddress2OutField() {
    return address2OutField;
  }

  public void setAddress2OutField(String address2OutField) {
    this.address2OutField = address2OutField;
  }

  public String getCityOutField() {
    return cityOutField;
  }

  public void setCityOutField(String cityOutField) {
    this.cityOutField = cityOutField;
  }

  public String getStateOutField() {
    return stateOutField;
  }

  public void setStateOutField(String stateOutField) {
    this.stateOutField = stateOutField;
  }

  public String getZipOutField() {
    return zipOutField;
  }

  public void setZipOutField(String zipOutField) {
    this.zipOutField = zipOutField;
  }

  public String getXML() throws KettleValueException {
        StringBuilder xml = new StringBuilder();

        xml.append( XMLHandler.addTagValue( "extractField", extractField ) );
        xml.append(XMLHandler.addTagValue("houseOutField", houseOutField));
        xml.append( XMLHandler.addTagValue( "addressOutField", addressOutField ) );
        xml.append( XMLHandler.addTagValue( "address2OutField", address2OutField ) );
        xml.append( XMLHandler.addTagValue( "cityOutField", cityOutField ) );
        xml.append( XMLHandler.addTagValue( "stateOutField", stateOutField ) );
        xml.append( XMLHandler.addTagValue( "zipOutField", zipOutField ) );
        xml.append(XMLHandler.addTagValue("isNer", isNer));
        xml.append(XMLHandler.addTagValue("lpPath", lpPath));
        xml.append(XMLHandler.addTagValue("nerPath", nerPath));
        return xml.toString();
  }

  public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
      try {
          setHouseOutField(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "houseOutField")));
          setAddressOutField( XMLHandler.getNodeValue( XMLHandler.getSubNode( stepnode, "addressOutField" ) ) );
          setAddress2OutField( XMLHandler.getNodeValue( XMLHandler.getSubNode( stepnode, "address2OutField" ) ) );
          setCityOutField( XMLHandler.getNodeValue( XMLHandler.getSubNode( stepnode, "cityOutField" ) ) );
          setStateOutField( XMLHandler.getNodeValue( XMLHandler.getSubNode( stepnode, "stateOutField" ) ) );
          setZipOutField( XMLHandler.getNodeValue( XMLHandler.getSubNode( stepnode, "zipOutField" ) ) );
          setExtractField( XMLHandler.getNodeValue( XMLHandler.getSubNode( stepnode, "extractField" ) ) );
          setNer(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "isNer")).toUpperCase().equals("Y"));
          setNerPath(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "nerPath")));
          setNerPath(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "lpPath")));
      } catch ( Exception e ) {
          throw new KettleXMLException( "Demo plugin unable to read step info from XML node", e );
      }
  }

  public Object clone() {
    Object retval = super.clone();
    return retval;
  }

  public void setDefault() {
      extractField = "Field to Extract";
      houseOutField = "house_out";
      addressOutField = "address_out";
      address2OutField = "address2_out";
      cityOutField = "city_out";
      stateOutField = "state_out";
      zipOutField = "zip_out";
      isNer = false;
  }

  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases ) throws KettleException {
      try {
          extractField  = rep.getStepAttributeString( id_step, "extractfield" );
          houseOutField = rep.getStepAttributeString(id_step, "houseOutField");
          addressOutField  = rep.getStepAttributeString( id_step, "addressOutField" );
          address2OutField  = rep.getStepAttributeString( id_step, "address2OutField" );
          cityOutField  = rep.getStepAttributeString( id_step, "cityOutField" );
          stateOutField  = rep.getStepAttributeString( id_step, "stateOutField" );
          zipOutField  = rep.getStepAttributeString( id_step, "zipOutField" );
          isNer = rep.getStepAttributeBoolean(id_step, "isNer");
          lpPath = rep.getStepAttributeString(id_step, "lpPath");
          nerPath = rep.getStepAttributeString(id_step, "nerPath");
      } catch ( Exception e ) {
          throw new KettleException( "Unable to load step from repository", e );
      }
  }

  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step ) throws KettleException {
      try {
          rep.saveStepAttribute( id_transformation, id_step, "extractField", extractField );
          rep.saveStepAttribute(id_transformation, id_step, "houseOutField", houseOutField);
          rep.saveStepAttribute( id_transformation, id_step, "addressOutField", addressOutField );
          rep.saveStepAttribute( id_transformation, id_step, "address2OutField", address2OutField );
          rep.saveStepAttribute( id_transformation, id_step, "cityOutField", cityOutField );
          rep.saveStepAttribute( id_transformation, id_step, "stateOutField", stateOutField );
          rep.saveStepAttribute( id_transformation, id_step, "zipOutField", zipOutField );
          rep.saveStepAttribute( id_transformation, id_step, "isNer", isNer );
          rep.saveStepAttribute( id_transformation, id_step, "lpPath", lpPath );
          rep.saveStepAttribute( id_transformation, id_step, "nerPath", nerPath );
      } catch ( Exception e ) {
          throw new KettleException( "Unable to save step into repository: " + id_step, e );
      }
  }

    public void getFields( RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
                           VariableSpace space, Repository repository, IMetaStore metaStore ) throws KettleStepException {

        ValueMetaInterface v0 = new ValueMetaString(houseOutField);
        v0.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
        v0.setOrigin(name);
        inputRowMeta.addValueMeta( v0 );

        ValueMetaInterface v = new ValueMetaString( addressOutField );
        v.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
        v.setOrigin(name);
        inputRowMeta.addValueMeta( v );

        ValueMetaInterface v2 = new ValueMetaString( address2OutField );
        v2.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
        v2.setOrigin(name);
        inputRowMeta.addValueMeta( v2 );

        ValueMetaInterface v3 = new ValueMetaString( cityOutField );
        v3.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
        v3.setOrigin(name);
        inputRowMeta.addValueMeta( v3 );

        ValueMetaInterface v4 = new ValueMetaString( stateOutField );
        v4.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
        v4.setOrigin(name);
        inputRowMeta.addValueMeta( v4 );

        ValueMetaInterface v5 = new ValueMetaString( zipOutField );
        v5.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
        v5.setOrigin(name);
        inputRowMeta.addValueMeta( v5 );
  }

  public void check( List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
      String input[], String output[], RowMetaInterface info, VariableSpace space, Repository repository,
      IMetaStore metaStore ) {
    CheckResult cr;
    if ( prev == null || prev.size() == 0 ) {
      cr =
          new CheckResult( CheckResultInterface.TYPE_RESULT_WARNING, BaseMessages.getString( PKG,
              "JPostalPluginMeta.CheckResult.NotReceivingFields" ), stepMeta );
      remarks.add( cr );
    } else {
      cr =
          new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG,
              "JPostalPluginMeta.CheckResult.StepRecevingData", prev.size() + "" ), stepMeta );
      remarks.add( cr );
    }

    // See if we have input streams leading to this step!
    if ( input.length > 0 ) {
      cr =
          new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG,
              "JPostalPluginMeta.CheckResult.StepRecevingData2" ), stepMeta );
      remarks.add( cr );
    } else {
      cr =
          new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString( PKG,
              "JPostalPluginMeta.CheckResult.NoInputReceivedFromOtherSteps" ), stepMeta );
      remarks.add( cr );
    }
  }

  public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta tr, Trans trans) {
    return new JPostalPlugin(stepMeta, stepDataInterface, cnr, tr, trans);
  }

  public StepDataInterface getStepData() {
    return new JPostalPluginData();
  }

}
