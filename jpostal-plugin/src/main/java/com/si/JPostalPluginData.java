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
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.libpostal.libpostal_address_parser_options_t;
import org.bytedeco.libpostal.libpostal_address_parser_response_t;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.bytedeco.libpostal.global.postal.*;


public class JPostalPluginData extends BaseStepData implements StepDataInterface{

    public RowMetaInterface outputRowMeta;

    private AbstractSequenceClassifier classifier;
    private boolean setup1 = false;
    private boolean setup2 = false;
    private boolean setup3 = false;
    private boolean isLibPostalInitialized = false;
    private libpostal_address_parser_options_t options = null;


    public JPostalPluginData() {
        super();
    }


    public AbstractSequenceClassifier getClassifier() {
        return classifier;
    }

    public void initClassifier(String nerPath) throws IOException, ClassNotFoundException {
        classifier = CRFClassifier.getClassifier(nerPath);
    }

    public boolean setupAddressParser(String lpPath){
        if(!setup1 || !setup2 || !setup3) {
            if (lpPath != null && isLibPostalInitialized == false) {
                setup1 = libpostal_setup_datadir(lpPath);
                setup2 = libpostal_setup_parser_datadir(lpPath);
                setup3 = libpostal_setup_language_classifier_datadir(lpPath);
                isLibPostalInitialized = true;
                options = libpostal_get_address_parser_default_options();
            }
        }
        return isAddressParserSetup();
    }

    public boolean isAddressParserSetup(){
        return (setup1 && setup2 && setup3);
    }

    public String[][] parseAddress(String text) throws UnsupportedEncodingException {
        BytePointer address = new BytePointer(text, "UTF-8");
        libpostal_address_parser_response_t response = libpostal_parse_address(address, options);
        int count = (int) response.num_components();
        String[] labels = new String[count];
        String[] values = new String[count];
        String[][] lvArr = new String[2][labels.length];
        for(int i = 0; i < count; i++){
            BytePointer btp = response.labels(i);
            BytePointer btp2 = response.components(i);
            String label = btp.getString();
            String val = btp2.getString();
            labels[i] = label;
            values[i] = val;
            btp.deallocate();
            btp2.deallocate();
            btp = null;
            btp2 = null;
        }
        lvArr[0] = labels;
        lvArr[1] = values;
        address.deallocate();
        response.deallocate();
        address = null;
        response = null;
        return lvArr;
    }

    public void teardownAddressParser(){
        classifier = null;
        if(options != null) {
            options.deallocate();
            options = null;
        }
        libpostal_teardown();
        libpostal_teardown_parser();
        libpostal_teardown_language_classifier();
        setup1 = false;
        setup2 = false;
        setup3 = false;
        isLibPostalInitialized = false;
        System.gc();
    }
}
