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
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;


public class JPostalPluginDialog extends BaseStepDialog implements StepDialogInterface{

    private static Class<?> PKG = JPostalPluginMeta.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

    private Label wlStepname;
    private Text wStepname;
    private FormData fdStepname, fdlStepname;

    private Label lfname;
    private CCombo extractCombo;
    private FormData fdlFname, fdStep;

    private Label wUserNerName;
    private Button wuseNer;
    private FormData fdlUseNer, fdlUseNerName;

    private Label wAddrName;
    private TextVar wAddrOut;
    private FormData fdlAddrName, fdlAddrOut;

    private Label wAddr2Name;
    private TextVar wAddr2Out;
    private FormData fdlAddr2Name, fdlAddr2Out;

    private Label wCityName;
    private TextVar wCityOut;
    private FormData fdlCityName, fdlCityOut;

    private Label wStateName;
    private TextVar wStateOut;
    private FormData fdlStateName, fdlStateOut;

    private Label wZipName;
    private TextVar wZipOut;
    private FormData fdlZipName, fdlZipOut;

    private Label wHouseName;
    private TextVar wHouseOut;
    private FormData fdlHouseName, fdlHouseOut;

    private Label wLpName;
    private TextVar wLpOut;
    private FormData fdlLpName, fdlLpOut;

    private Label wNerName;
    private TextVar wNerOut;
    private FormData fdlNerName, fdlNerOut;

    private JPostalPluginMeta meta;

    public JPostalPluginDialog( Shell parent, Object stepMeta, TransMeta transMeta, String stepname ) {
        super( parent, (BaseStepMeta) stepMeta, transMeta, stepname );
        meta = (JPostalPluginMeta) stepMeta;
    }

    @Override
    public String open() {
        // store some convenient SWT variables
        Shell parent = getParent();
        Display display = parent.getDisplay();

        // SWT code for preparing the dialog
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
        props.setLook(shell);
        setShellImage(shell, meta);

        // Save the value of the changed flag on the meta object. If the user cancels
        // the dialog, it will be restored to this saved value.
        // The "changed" variable is inherited from BaseStepDialog
        changed = meta.hasChanged();

        // The ModifyListener used on all controls. It will update the meta object to
        // indicate that changes are being made.
        ModifyListener lsMod = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                meta.setChanged();
            }
        };

        // ------------------------------------------------------- //
        // SWT code for building the actual settings dialog        //
        // ------------------------------------------------------- //
        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;
        shell.setLayout(formLayout);
        shell.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Shell.Title"));
        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        // Stepname line
        wlStepname = new Label(shell, SWT.RIGHT);
        wlStepname.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Stepname.Label"));
        props.setLook(wlStepname);
        fdlStepname = new FormData();
        fdlStepname.left = new FormAttachment(0, 0);
        fdlStepname.right = new FormAttachment(middle, -margin);
        fdlStepname.top = new FormAttachment(0, margin);
        wlStepname.setLayoutData(fdlStepname);

        wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wStepname.setText(stepname);
        props.setLook(wStepname);
        wStepname.addModifyListener(lsMod);
        fdStepname = new FormData();
        fdStepname.left = new FormAttachment(middle, 0);
        fdStepname.top = new FormAttachment(0, margin);
        fdStepname.right = new FormAttachment(100, 0);
        wStepname.setLayoutData(fdStepname);

        // Set the field name
        lfname = new Label( shell, SWT.RIGHT );
        lfname.setText( BaseMessages.getString( PKG, "JPostalPluginDialog.Fields.FieldName" ) );
        props.setLook( lfname );
        fdlFname = new FormData();
        fdlFname.left = new FormAttachment( 0, 0 );
        fdlFname.right = new FormAttachment( middle, -margin );
        fdlFname.top = new FormAttachment( wStepname, 15 );
        lfname.setLayoutData( fdlFname );

        extractCombo = new CCombo( shell, SWT.BORDER );
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
        fdStep = new FormData();
        fdStep.left = new FormAttachment( middle, 0 );
        fdStep.top = new FormAttachment( wStepname, 15 );
        fdStep.right = new FormAttachment( 100, 0 );
        extractCombo.setLayoutData( fdStep );

        //house
        wHouseName = new Label(shell, SWT.RIGHT);
        wHouseName.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Output.House"));
        props.setLook(wHouseName);
        fdlHouseName = new FormData();
        fdlHouseName.left = new FormAttachment(0, 0);
        fdlHouseName.top = new FormAttachment(lfname, 15);
        fdlHouseName.right = new FormAttachment(middle, -margin);
        wHouseName.setLayoutData(fdlHouseName);
        wHouseOut = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wHouseOut.setText("");
        wHouseOut.addModifyListener(lsMod);
        props.setLook(wHouseOut);
        fdlHouseOut = new FormData();
        fdlHouseOut.left = new FormAttachment(middle, 0);
        fdlHouseOut.top = new FormAttachment(lfname, 15);
        fdlHouseOut.right = new FormAttachment(100, 0);
        wHouseOut.setLayoutData(fdlHouseOut);

        //addr
        wAddrName = new Label(shell, SWT.RIGHT);
        wAddrName.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Address"));
        props.setLook(wAddrName);
        fdlAddrName = new FormData();
        fdlAddrName.left = new FormAttachment(0, 0);
        fdlAddrName.top = new FormAttachment(wHouseName, 15);
        fdlAddrName.right = new FormAttachment(middle, -margin);
        wAddrName.setLayoutData(fdlAddrName);
        wAddrOut = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wAddrOut.setText("");
        wAddrOut.addModifyListener(lsMod);
        props.setLook(wAddrOut);
        fdlAddrOut = new FormData();
        fdlAddrOut.left = new FormAttachment(middle, 0);
        fdlAddrOut.top = new FormAttachment(wHouseName, 15);
        fdlAddrOut.right = new FormAttachment(100, 0);
        wAddrOut.setLayoutData(fdlAddrOut);

        //addr2
        wAddr2Name = new Label(shell, SWT.RIGHT);
        wAddr2Name.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Address2"));
        props.setLook(wAddr2Name);
        fdlAddr2Name = new FormData();
        fdlAddr2Name.left = new FormAttachment(0, 0);
        fdlAddr2Name.top = new FormAttachment(wAddrName, 15);
        fdlAddr2Name.right = new FormAttachment(middle, -margin);
        wAddr2Name.setLayoutData(fdlAddr2Name);
        wAddr2Out = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wAddr2Out.setText("");
        wAddr2Out.addModifyListener(lsMod);
        props.setLook(wAddr2Out);
        fdlAddr2Out = new FormData();
        fdlAddr2Out.left = new FormAttachment(middle, 0);
        fdlAddr2Out.top = new FormAttachment(wAddrName, 15);
        fdlAddr2Out.right = new FormAttachment(100, 0);
        wAddr2Out.setLayoutData(fdlAddr2Out);

        //city
        wCityName = new Label(shell, SWT.RIGHT);
        wCityName.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Output.City"));
        props.setLook(wCityName);
        fdlCityName = new FormData();
        fdlCityName.left = new FormAttachment(0, 0);
        fdlCityName.top = new FormAttachment(wAddr2Name, 15);
        fdlCityName.right = new FormAttachment(middle, -margin);
        wCityName.setLayoutData(fdlCityName);
        wCityOut = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wCityOut.setText("");
        wCityOut.addModifyListener(lsMod);
        props.setLook(wCityOut);
        fdlCityOut = new FormData();
        fdlCityOut.left = new FormAttachment(middle, 0);
        fdlCityOut.top = new FormAttachment(wAddr2Name, 15);
        fdlCityOut.right = new FormAttachment(100, 0);
        wCityOut.setLayoutData(fdlCityOut);

        //state
        wStateName = new Label(shell, SWT.RIGHT);
        wStateName.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Output.State"));
        props.setLook(wStateName);
        fdlStateName = new FormData();
        fdlStateName.left = new FormAttachment(0, 0);
        fdlStateName.top = new FormAttachment(wCityName, 15);
        fdlStateName.right = new FormAttachment(middle, -margin);
        wStateName.setLayoutData(fdlStateName);
        wStateOut = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wStateOut.setText("");
        wStateOut.addModifyListener(lsMod);
        props.setLook(wCityOut);
        fdlStateOut = new FormData();
        fdlStateOut.left = new FormAttachment(middle, 0);
        fdlStateOut.top = new FormAttachment(wCityName, 15);
        fdlStateOut.right = new FormAttachment(100, 0);
        wStateOut.setLayoutData(fdlStateOut);

        //zip
        wZipName = new Label(shell, SWT.RIGHT);
        wZipName.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Zip"));
        props.setLook(wZipName);
        fdlZipName = new FormData();
        fdlZipName.left = new FormAttachment(0, 0);
        fdlZipName.top = new FormAttachment(wStateName, 15);
        fdlZipName.right = new FormAttachment(middle, -margin);
        wZipName.setLayoutData(fdlZipName);
        wZipOut = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wZipOut.setText("");
        wZipOut.addModifyListener(lsMod);
        props.setLook(wZipOut);
        fdlZipOut = new FormData();
        fdlZipOut.left = new FormAttachment(middle, 0);
        fdlZipOut.top = new FormAttachment(wStateName, 15);
        fdlZipOut.right = new FormAttachment(100, 0);
        wZipOut.setLayoutData(fdlZipOut);

        //path to NER
        wNerName = new Label(shell, SWT.RIGHT);
        wNerName.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Zip"));
        props.setLook(wZipName);
        fdlNerName = new FormData();
        fdlNerName.left = new FormAttachment(0, 0);
        fdlNerName.top = new FormAttachment(wZipName, 15);
        fdlNerName.right = new FormAttachment(middle, -margin);
        wNerName.setLayoutData(fdlNerName);
        wNerOut = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wNerOut.setText("");
        wNerOut.addModifyListener(lsMod);
        props.setLook(wZipOut);
        fdlNerOut = new FormData();
        fdlNerOut.left = new FormAttachment(middle, 0);
        fdlNerOut.top = new FormAttachment(wZipName, 15);
        fdlNerOut.right = new FormAttachment(100, 0);
        wNerOut.setLayoutData(fdlNerOut);


        //path to libpostal
        wLpName = new Label(shell, SWT.RIGHT);
        wLpName.setText(BaseMessages.getString(PKG, "JPostalPluginDialog.Output.LpPath"));
        props.setLook(wLpName);
        fdlLpName = new FormData();
        fdlLpName.left = new FormAttachment(0, 0);
        fdlLpName.top = new FormAttachment(wNerName, 15);
        fdlLpName.right = new FormAttachment(middle, -margin);
        wLpName.setLayoutData(fdlLpName);
        wLpOut = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wLpOut.setText("");
        wLpOut.addModifyListener(lsMod);
        props.setLook(wLpOut);
        fdlLpOut = new FormData();
        fdlLpOut.left = new FormAttachment(middle, 0);
        fdlLpOut.top = new FormAttachment(wNerName, 15);
        fdlLpOut.right = new FormAttachment(100, 0);
        wLpOut.setLayoutData(fdlZipOut);


        //add a flag for using ner
        wUserNerName = new Label(shell,SWT.RIGHT);
        wUserNerName.setText(BaseMessages.getString(PKG,"JPostalPluginDialog.Output.NERModel"));
        props.setLook(wUserNerName);
        fdlUseNerName = new FormData();
        fdlUseNerName.left = new FormAttachment(0, 0);
        fdlUseNerName.top = new FormAttachment(wLpName, 15);
        fdlUseNerName.right = new FormAttachment(middle, -margin);
        wUserNerName.setLayoutData(fdlUseNerName);
        wuseNer = new Button(shell, SWT.CHECK);
        props.setLook(wuseNer);
        fdlUseNer = new FormData();
        fdlUseNer.left = new FormAttachment(middle, 0);
        fdlUseNer.top = new FormAttachment(wLpName, 15);
        fdlUseNer.right = new FormAttachment(100, 0);
        wuseNer.setLayoutData(fdlUseNer);

        // OK and cancel buttons
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
        setButtonPositions(new Button[]{wOK, wCancel}, margin, wUserNerName);

        // Add listeners for cancel and OK
        lsCancel = new Listener() {
            public void handleEvent(Event e) {
                cancel();
            }
        };
        lsOK = new Listener() {
            public void handleEvent(Event e) {
                ok();
            }
        };
        wCancel.addListener(SWT.Selection, lsCancel);
        wOK.addListener(SWT.Selection, lsOK);

        // default listener (for hitting "enter")
        lsDef = new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
                ok();
            }
        };
        wuseNer.addSelectionListener(lsDef);
        wHouseOut.addSelectionListener(lsDef);
        wStepname.addSelectionListener(lsDef);
        wHouseOut.addSelectionListener(lsDef);
        wAddrOut.addSelectionListener(lsDef);
        wAddr2Out.addSelectionListener(lsDef);
        wCityOut.addSelectionListener(lsDef);
        wStateOut.addSelectionListener(lsDef);
        wZipOut.addSelectionListener(lsDef);
        wNerOut.addSelectionListener(lsDef);
        wLpOut.addSelectionListener(lsDef);

        // Detect X or ALT-F4 or something that kills this window and cancel the dialog properly
        shell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {
                cancel();
            }
        });

        // Set/Restore the dialog size based on last position on screen
        // The setSize() method is inherited from BaseStepDialog
        setSize();

        // populate the dialog with the values from the meta object
        getData();

        // restore the changed flag to original value, as the modify listeners fire during dialog population
        meta.setChanged(changed);

        // open dialog and enter event loop
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
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
        wHouseOut.setText(Const.NVL(meta.getHouseOutField(), ""));
        wuseNer.setSelection(meta.isNer());
        wLpOut.setText(Const.NVL(meta.getLpPath(), ""));
        wNerOut.setText(Const.NVL(meta.getNerPath(),""));
        wStepname.setFocus();
    }


    /**
    * Called when the user cancels the dialog.
    */
    private void cancel() {
        // The "stepname" variable will be the return value for the open() method.
        // Setting to null to indicate that dialog was cancelled.
        stepname = null;
        //may still need to set the extraction index
        meta.setExtractIndex(extractCombo.getSelectionIndex());
        // Restoring original "changed" flag on the met aobject
        meta.setChanged( changed );
        // close the SWT dialog window
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
        String houseFieldName = Const.NVL(wHouseOut.getText(), null);
        boolean isNer = wuseNer.getSelection();
        String lpPath = wLpOut.getText();
        String nerPath = wNerOut.getText();

        meta.setExtractField(extractField);
        meta.setAddressOutField(addressFieldName);
        meta.setAddress2OutField(address2FieldName);
        meta.setCityOutField(cityFieldName);
        meta.setStateOutField(stateFieldName);
        meta.setZipOutField(zipFieldName);
        meta.setHouseOutField(houseFieldName);
        meta.setNer(isNer);
        meta.setExtractIndex(extractCombo.getSelectionIndex());
        meta.setLpPath(lpPath);
        meta.setNerPath(nerPath);
        dispose();
    }
}
