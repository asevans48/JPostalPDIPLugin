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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class JPostalPluginDialog extends BaseStepDialog implements StepDialogInterface{

    private static Class<?> PKG = JPostalPluginMeta.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$
    private Group wSettingsGroup;
    private FormData fdSettingsGroup;
    private Text wStepname;
    private CCombo extractCombo;
    private Text wAddrOut;
    private Text wAddr2Out;
    private Text wCityOut;
    private Text wStateOut;
    private Text wZipOut;
    private JPostalPluginMeta meta;

  public JPostalPluginDialog( Shell parent, Object stepMeta, TransMeta transMeta, String stepname ) {
    super( parent, (BaseStepMeta) stepMeta, transMeta, stepname );
    meta = (JPostalPluginMeta) stepMeta;
  }

  @Override
  public String open() {
      Shell parent = getParent();
      Display display = parent.getDisplay();

      shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
      props.setLook( shell );
      setShellImage( shell, meta );

      ModifyListener lsMod = new ModifyListener() {
          public void modifyText( ModifyEvent e ) {
              meta.setChanged();
          }
      };
      changed = meta.hasChanged();

      FormLayout formLayout = new FormLayout();
      formLayout.marginWidth = Const.FORM_MARGIN;
      formLayout.marginHeight = Const.FORM_MARGIN;

      shell.setLayout( formLayout );
      shell.setText( BaseMessages.getString( PKG, "JPostalPluginDialog.Shell.Title" ) );

      int middle = props.getMiddlePct();
      int margin = Const.MARGIN;

      // Stepname line
      wlStepname = new Label( shell, SWT.RIGHT );
      wlStepname.setText( BaseMessages.getString( PKG, "System.Label.StepName" ) );
      props.setLook( wlStepname );
      fdlStepname = new FormData();
      fdlStepname.left = new FormAttachment( 0, 0 );
      fdlStepname.right = new FormAttachment( middle, -margin );
      fdlStepname.top = new FormAttachment( 0, margin );
      wlStepname.setLayoutData( fdlStepname );
      wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
      wStepname.setText( stepname );
      props.setLook( wStepname );
      wStepname.addModifyListener( lsMod );
      fdStepname = new FormData();
      fdStepname.left = new FormAttachment( middle, 0 );
      fdStepname.top = new FormAttachment( 0, margin );
      fdStepname.right = new FormAttachment( 100, 0 );
      wStepname.setLayoutData( fdStepname );

      // Some buttons
      wOK = new Button( shell, SWT.PUSH );
      wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
      wCancel = new Button( shell, SWT.PUSH );
      wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

      setButtonPositions( new Button[] { wOK, wCancel }, margin, null );

      // /////////////////////////////////
      // START OF Extract Fields Group
      // /////////////////////////////////

      wSettingsGroup = new Group( shell, SWT.SHADOW_NONE );
      props.setLook( wSettingsGroup );
      wSettingsGroup.setText( BaseMessages.getString( PKG, "JPostalPluginDialog.Fields.Address" ) );
      FormLayout settingsLayout = new FormLayout();
      settingsLayout.marginWidth = 10;
      settingsLayout.marginHeight = 10;
      wSettingsGroup.setLayout( settingsLayout );

      // Set the field name
      Label lfname = new Label( wSettingsGroup, SWT.RIGHT );
      lfname.setText( BaseMessages.getString( PKG, "JPostalPluginDialog.Fields.FieldName" ) );
      props.setLook( lfname );
      FormData fdlFname = new FormData();
      fdlFname.left = new FormAttachment( 0, 0 );
      fdlFname.right = new FormAttachment( middle, -margin );
      fdlFname.top = new FormAttachment( wStepname, margin );
      lfname.setLayoutData( fdlFname );
      extractCombo = new CCombo( wSettingsGroup, SWT.BORDER );
      props.setLook( extractCombo );

      StepMeta stepinfo = transMeta.findStep( stepname );
      if ( stepinfo != null ) {
          try {
              String[] fields = transMeta.getStepFields(stepname).getFieldNames();
              for (int i = 0; i < fields.length; i++) {
                  extractCombo.add(fields[i]);
              }
          }catch(KettleException e){
              if ( log.isBasic())
                  logBasic("Failed to Get Step Fields");
          }
      }

      extractCombo.addModifyListener( lsMod );
      FormData fdStep = new FormData();
      fdStep.left = new FormAttachment( middle, 0 );
      fdStep.top = new FormAttachment( wStepname, margin );
      fdStep.right = new FormAttachment( 100, 0 );
      extractCombo.setLayoutData( fdStep );

      // set the address output name
      Label laddrName = new Label( wSettingsGroup, SWT.RIGHT );
      laddrName.setText( BaseMessages.getString( PKG, "JPostalPluginDialog.Output.Address" ) );
      props.setLook( laddrName );
      FormData fdlAddrName = new FormData();
      fdlAddrName.top = new FormAttachment( lfname, margin );
      fdlAddrName.left = new FormAttachment( 0, 0 );
      fdlAddrName.right = new FormAttachment( middle, -margin );
      laddrName.setLayoutData( fdlAddrName );
      wAddrOut = new Text( wSettingsGroup, SWT.SINGLE | SWT.BORDER );
      wAddrOut.setText( "" );
      props.setLook( wAddrOut );
      wAddrOut.addModifyListener( lsMod );
      fdStepname = new FormData();
      fdStepname.left = new FormAttachment( middle, 0 );
      fdStepname.top = new FormAttachment( lfname, margin );
      fdStepname.right = new FormAttachment( 100, 0 );
      wAddrOut.setLayoutData( fdStepname );

      //address2
      Label laddr2Name = new Label( wSettingsGroup, SWT.RIGHT );
      laddr2Name.setText( BaseMessages.getString( PKG, "JPostalPluginDialog.Output.Address2" ) );
      props.setLook( laddr2Name );
      FormData fdladdr2Name = new FormData();
      fdladdr2Name.top = new FormAttachment( laddrName, margin );
      fdladdr2Name.left = new FormAttachment( 0, 0 );
      fdladdr2Name.right = new FormAttachment( middle, -margin );
      laddr2Name.setLayoutData( fdladdr2Name );
      wAddr2Out = new Text( wSettingsGroup, SWT.SINGLE | SWT.BORDER );
      wAddr2Out.setText( "" );
      props.setLook( wAddr2Out );
      wAddr2Out.addModifyListener( lsMod );
      fdStepname = new FormData();
      fdStepname.left = new FormAttachment( middle, 0 );
      fdStepname.top = new FormAttachment( laddrName, margin );
      fdStepname.right = new FormAttachment( 100, 0 );
      wAddr2Out.setLayoutData( fdStepname );


      // set the city output name
      Label lcityName = new Label( wSettingsGroup, SWT.RIGHT );
      lcityName.setText( BaseMessages.getString( PKG, "JPostalPluginDialog.Output.City" ) );
      props.setLook( lcityName );
      FormData fdlcityName = new FormData();
      fdlcityName.top = new FormAttachment( laddr2Name, margin );
      fdlcityName.left = new FormAttachment( 0, 0 );
      fdlcityName.right = new FormAttachment( middle, -margin );
      lcityName.setLayoutData( fdlcityName );
      wCityOut = new Text( wSettingsGroup, SWT.SINGLE | SWT.BORDER );
      wCityOut.setText( "" );
      props.setLook( wCityOut );
      wCityOut.addModifyListener( lsMod );
      fdStepname = new FormData();
      fdStepname.left = new FormAttachment( middle, 0 );
      fdStepname.top = new FormAttachment( laddr2Name, margin );
      fdStepname.right = new FormAttachment( 100, 0 );
      wCityOut.setLayoutData( fdStepname );


      // set the state output name
      Label lstateName = new Label( wSettingsGroup, SWT.RIGHT );
      lstateName.setText( BaseMessages.getString( PKG, "JPostalPluginDialog.Output.State" ) );
      props.setLook( lstateName );
      FormData fdlstateName = new FormData();
      fdlstateName.top = new FormAttachment( lcityName, margin );
      fdlstateName.left = new FormAttachment( 0, 0 );
      fdlstateName.right = new FormAttachment( middle, -margin );
      lstateName.setLayoutData( fdlstateName );
      wStateOut = new Text( wSettingsGroup, SWT.SINGLE | SWT.BORDER );
      wStateOut.setText("");
      props.setLook( wStateOut );
      wStateOut.addModifyListener( lsMod );
      fdStepname = new FormData();
      fdStepname.left = new FormAttachment( middle, 0 );
      fdStepname.top = new FormAttachment( lcityName, margin );
      fdStepname.right = new FormAttachment( 100, 0 );
      wStateOut.setLayoutData( fdStepname );



      // set the zip output name
      Label lzipName = new Label( wSettingsGroup, SWT.RIGHT );
      lzipName.setText( BaseMessages.getString( PKG, "JPostalPluginDialog.Output.Zip" ) );
      props.setLook( lzipName );
      FormData fdlzipName = new FormData();
      fdlzipName.top = new FormAttachment( lstateName, margin );
      fdlzipName.left = new FormAttachment( 0, 0 );
      fdlzipName.right = new FormAttachment( middle, -margin );
      lzipName.setLayoutData( fdlzipName );
      wZipOut = new Text( wSettingsGroup, SWT.SINGLE | SWT.BORDER );
      wZipOut.setText("" );
      props.setLook( wZipOut );
      wZipOut.addModifyListener( lsMod );
      fdStepname = new FormData();
      fdStepname.left = new FormAttachment( middle, 0 );
      fdStepname.top = new FormAttachment( lstateName, margin );
      fdStepname.right = new FormAttachment( 100, 0 );
      wZipOut.setLayoutData( fdStepname );



      fdSettingsGroup = new FormData();
      fdSettingsGroup.left = new FormAttachment( 0, margin );
      fdSettingsGroup.top = new FormAttachment( wStepname, margin );
      fdSettingsGroup.right = new FormAttachment( 100, -margin );
      fdSettingsGroup.bottom = new FormAttachment( wOK, -margin );
      wSettingsGroup.setLayoutData( fdSettingsGroup );

      // ///////////////////////////////////////////////////////////
      // / END OF Settings GROUP
      // ///////////////////////////////////////////////////////////


      // Add listeners for cancel and OK
      lsCancel = new Listener() {
          public void handleEvent( Event e ) {
              cancel();
          }
      };
      lsOK = new Listener() {
          public void handleEvent( Event e ) {
              ok();
          }
      };
      wCancel.addListener( SWT.Selection, lsCancel );
      wOK.addListener( SWT.Selection, lsOK );

      // default listener (for hitting "enter")
      lsDef = new SelectionAdapter() {
          public void widgetDefaultSelected( SelectionEvent e ) {
              ok();
          }
      };
      wStepname.addSelectionListener( lsDef );
      extractCombo.addSelectionListener( lsDef );
      wAddrOut.addSelectionListener(lsDef);
      wAddr2Out.addSelectionListener(lsDef);
      wCityOut.addSelectionListener(lsDef);
      wStateOut.addSelectionListener(lsDef);
      wZipOut.addSelectionListener(lsDef);

      // Detect X or ALT-F4 or something that kills this window and cancel the dialog properly
      shell.addShellListener( new ShellAdapter() {
          public void shellClosed( ShellEvent e ) {
              cancel();
          }
      } );

      // Set/Restore the dialog size based on last position on screen
      // The setSize() method is inherited from BaseStepDialog
      setSize();

      // populate the dialog with the values from the meta object
      getData();

      // restore the changed flag to original value, as the modify listeners fire during dialog population
      meta.setChanged( changed );

      // open dialog and enter event loop
      shell.open();
      while ( !shell.isDisposed() ) {
          if ( !display.readAndDispatch() ) {
              display.sleep();
          }
      }

      // at this point the dialog has closed, so either ok() or cancel() have been executed
      // The "stepname" variable is inherited from BaseStepDialog
      return stepname;
  }


    /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wStepname.selectAll();
    extractCombo.setText(Const.NVL(meta.getExtractField(),""));
    wStateOut.setText(Const.NVL(meta.getStateOutField(),""));
    wAddrOut.setText(Const.NVL(meta.getAddressOutField(),""));
    wAddr2Out.setText(Const.NVL(meta.getAddress2OutField(),""));
    wCityOut.setText(Const.NVL(meta.getCityOutField(),""));
    wZipOut.setText(Const.NVL(meta.getZipOutField(),""));
    wStepname.setFocus();
  }

  private void cancel() {
    stepname = null;
    meta.setChanged( changed );
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wStepname.getText() ) ) {
      return;
    }

    stepname = wStepname.getText(); // return value

    String extractField = Const.NVL(extractCombo.getText(), null);
    String addressFieldName = Const.NVL(wAddrOut.getText(), null);
    String address2FieldName = Const.NVL(wAddr2Out.getText(), null);
    String cityFieldName = Const.NVL(wCityOut.getText(), null);
    String stateFieldName = Const.NVL(wStateOut.getText(), null);
    String zipFieldName = Const.NVL(wZipOut.getText(), null);

    meta.setExtractField(extractField);
    meta.setAddressOutField(addressFieldName);
    meta.setAddress2OutField(address2FieldName);
    meta.setCityOutField(cityFieldName);
    meta.setStateOutField(stateFieldName);
    meta.setZipOutField(zipFieldName);

    dispose();
  }
}
