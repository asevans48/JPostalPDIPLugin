package com.si;

import org.bytedeco.javacpp.*;
import org.bytedeco.libpostal.*;
import static org.bytedeco.libpostal.global.postal.*;
import org.junit.jupiter.api.Test;


public class LibPostalTest {

    String fpath = System.getProperty("libpostal.data.dir");
    boolean setup1 = libpostal_setup_datadir(fpath);
    boolean setup2 = libpostal_setup_parser_datadir(fpath);
    boolean setup3 = libpostal_setup_language_classifier_datadir(fpath);

    public void StringShouldContainOrNotContainRoad() throws Exception{
        assert(fpath != null);
        assert(setup1 && setup2 && setup3);
        libpostal_address_parser_options_t options = libpostal_get_address_parser_default_options();
        for(int i = 0; i < 10000; i++) {
            boolean cont_road = false;
            BytePointer address = new BytePointer("781 Franklin Ave Crown Heights Brooklyn NYC NY 11216 USA", "UTF-8");
            libpostal_address_parser_response_t response = libpostal_parse_address(address, options);
            long count = response.num_components();
            for (int j = 0; j < count; j++) {
                String label = response.labels(j).getString();
                if(label.toUpperCase().equals("ROAD")){
                    cont_road = true;
                }
            }
            assert(cont_road);

            cont_road = false;
            address = new BytePointer("PO Box 123 Brooklyn NYC NY 11216 USA", "UTF-8");
            response = libpostal_parse_address(address, options);
            count = response.num_components();
            for (int j = 0; j < count; j++) {
                String label = response.labels(j).getString();
                if (label.toUpperCase().equals("ROAD")) {
                    cont_road = true;
                }
            }
            assert (!cont_road);
        }
        libpostal_teardown();
        libpostal_teardown_parser();
        libpostal_teardown_language_classifier();

    }
}
