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
import org.pentaho.di.ui.core.widget.LabelText;
import org.pentaho.di.ui.trans.step.BaseStepDialog;


public class JPostalPluginDialog extends BaseStepDialog implements StepDialogInterface{

    private static Class<?> PKG = JPostalPluginMeta.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$
    private Group wSettingsGroup;
    private FormData fdSettingsGroup;
    private Text wStepname;
    private CCombo extractCombo;
    private Button wuseNer;
    private LabelText wAddrOut;
    private LabelText wAddr2Out;
    private LabelText wCityOut;
    private LabelText wStateOut;
    private LabelText wZipOut;
    private LabelText wHouseOut;
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

        wHouseOut = new LabelText(shell, BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Address2"), null);
        props.setLook(wHouseOut);
        wHouseOut.addModifyListener(lsMod);
        FormData fdValName = new FormData();
        fdValName.left = new FormAttachment(0, 0);
        fdValName.right = new FormAttachment(100, 0);
        fdValName.top = new FormAttachment(wStepname, margin);
        wHouseOut.setLayoutData(fdValName);

        wAddrOut = new LabelText(shell, BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Address"), null);
        props.setLook(wAddrOut);
        wAddrOut.addModifyListener(lsMod);
        fdValName = new FormData();
        fdValName.left = new FormAttachment(0, 0);
        fdValName.right = new FormAttachment(100, 0);
        fdValName.top = new FormAttachment(wStepname, margin);
        wAddrOut.setLayoutData(fdValName);

        wAddr2Out = new LabelText(shell, BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Address2"), null);
        props.setLook(wAddr2Out);
        wAddr2Out.addModifyListener(lsMod);
        fdValName = new FormData();
        fdValName.left = new FormAttachment(0, 0);
        fdValName.right = new FormAttachment(100, 0);
        fdValName.top = new FormAttachment(wStepname, margin);
        wAddr2Out.setLayoutData(fdValName);

        wCityOut = new LabelText(shell, BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Address2"), null);
        props.setLook(wCityOut);
        wCityOut.addModifyListener(lsMod);
        fdValName = new FormData();
        fdValName.left = new FormAttachment(0, 0);
        fdValName.right = new FormAttachment(100, 0);
        fdValName.top = new FormAttachment(wStepname, margin);
        wCityOut.setLayoutData(fdValName);

        wStateOut = new LabelText(shell, BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Address2"), null);
        props.setLook(wStateOut);
        wStateOut.addModifyListener(lsMod);
        fdValName = new FormData();
        fdValName.left = new FormAttachment(0, 0);
        fdValName.right = new FormAttachment(100, 0);
        fdValName.top = new FormAttachment(wStepname, margin);
        wStateOut.setLayoutData(fdValName);

        wZipOut = new LabelText(shell, BaseMessages.getString(PKG, "JPostalPluginDialog.Output.Address2"), null);
        props.setLook(wZipOut);
        wZipOut.addModifyListener(lsMod);
        fdValName = new FormData();
        fdValName.left = new FormAttachment(0, 0);
        fdValName.right = new FormAttachment(100, 0);
        fdValName.top = new FormAttachment(wStepname, margin);
        wZipOut.setLayoutData(fdValName);

        //add a flag for using ner

        // OK and cancel buttons
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
        setButtonPositions(new Button[]{wOK, wCancel}, margin, wZipOut);

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
        wStepname.addSelectionListener(lsDef);
        wHouseOut.addSelectionListener(lsDef);
        wAddrOut.addSelectionListener(lsDef);
        wAddr2Out.addSelectionListener(lsDef);
        wCityOut.addSelectionListener(lsDef);
        wStateOut.addSelectionListener(lsDef);
        wZipOut.addSelectionListener(lsDef);

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
        //wuseNer.setSelection(meta.isNer());
        wStepname.setFocus();
    }


    /**
    * Called when the user cancels the dialog.
    */
    private void cancel() {
        // The "stepname" variable will be the return value for the open() method.
        // Setting to null to indicate that dialog was cancelled.
        stepname = null;
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
        //boolean isNer = wuseNer.getSelection();

        meta.setExtractField(extractField);
        meta.setAddressOutField(addressFieldName);
        meta.setAddress2OutField(address2FieldName);
        meta.setCityOutField(cityFieldName);
        meta.setStateOutField(stateFieldName);
        meta.setZipOutField(zipFieldName);
        meta.setHouseOutField(houseFieldName);
        //meta.setNer(isNer);

        dispose();
    }
}
