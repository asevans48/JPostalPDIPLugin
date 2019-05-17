package com.si;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

/**
 * Some basic test for the Stanford NER to see if we can adequately grab a location tag from the strings.
 *
 * @author aevans
 */
public class StanfordNERTest {

    String fpath = null;
    AbstractSequenceClassifier classifier = null;

    @Test
    public void InputShouldContainLocation() throws Exception {
        if(fpath == null) {
            fpath = new JPostalPluginData().getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            fpath = fpath + File.separator + "lib" + File.separator + "english.muc.7class.distsim.crf.ser.gz";
            classifier = CRFClassifier.getClassifier(fpath);
        }
        assert(fpath != null);
        String text = "The folder is in Fakeville";
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
        assert(contains_loc);
    }

    @Test
    public void InputShouldNotContainLocation() throws Exception{
        if(fpath == null) {
            fpath = new JPostalPluginData().getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            fpath = fpath + File.separator + "lib" + File.separator + "english.muc.7class.distsim.crf.ser.gz";
            classifier = CRFClassifier.getClassifier(fpath);
        }

        String text = "The folder is not in here but it is at our hospital";
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
        assert(!contains_loc);
    }
}
