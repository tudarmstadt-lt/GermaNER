/*******************************************************************************
 * Copyright 2014
 * FG Language Technology
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tu.darmstadt.lt.ner.feature.extractor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.function.FeatureFunction;

import de.tu.darmstadt.lt.ner.reader.NERReader;

public class TemplateBinaryFeatureExtractor
    implements FeatureFunction
{

    static List<String> list = new ArrayList<>();
    static int i = 0;

    public TemplateBinaryFeatureExtractor()
        throws IOException
    {
        // read
    }

    public static final String DEFAULT_NAME = "BINARYFEATURE";

    @Override
    public List<Feature> apply(Feature feature)
    {

        if (i == 0) {
            BufferedReader br;
            try {
                NERReader reader = new NERReader();
                // ListFileName - assumption is to have a file where every feature is presented in a single line
                br = (BufferedReader) reader.getReader("listFile.tsv");
                String input;
                
                // if the features are separated anything than new line, change this code
                while ((input = br.readLine()) != null) {                 
                  if (input.trim().isEmpty()){
                      continue;
                  }
                  list.add(input.trim());
                }
                br.close();
            }
            catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            i++;
        }

        Object featureValue = feature.getValue();

        if (featureValue == null) {
            return Collections.singletonList(new Feature("BINARYFEATURE", "false"));
        }
        String value = featureValue.toString();
        if (value == null || value.length() == 0) {
            return Collections.singletonList(new Feature("BINARYFEATURE", "false"));
        }

        if (list.contains(value)) {
            return Collections.singletonList(new Feature("BINARYFEATURE", "true"));
        }
        return Collections.singletonList(new Feature("BINARYFEATURE", "false"));

    }

}
