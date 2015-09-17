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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.function.FeatureFunction;

import de.tu.darmstadt.lt.ner.reader.NERReader;

public class UperCasedTopicClass1FeatureExtractor
    implements FeatureFunction
{

    static Map<String, String> uperCaseTopicClass1 = new HashMap<String, String>();
    static int i = 0;

    public UperCasedTopicClass1FeatureExtractor()
        throws IOException
    {
        // read
    }

    public static final String DEFAULT_NAME = "UperCaseTopicClass1";

    @Override
    public List<Feature> apply(Feature feature)
    {

        if (i == 0) {
            BufferedReader br;
            try {
                NERReader reader = new NERReader();
                br = (BufferedReader) reader.getReader("uperCasetopicClaster.txt");
                String input;
                while ((input = br.readLine()) != null) {
                    String[] sep = input.split("\\t");
                    uperCaseTopicClass1.put(sep[0], sep[1]);
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
            // System.out.println("Size:"+simWord1.size());

            i++;
        }

        Object featureValue = feature.getValue();

        if (featureValue == null) {
            return Collections.singletonList(new Feature("UperCaseTopicClass1", "NA"));
        }
        String value = featureValue.toString();
        if (value == null || value.length() == 0) {
            return Collections.singletonList(new Feature("UperCaseTopicClass1", "NA"));
        }

        String output;
        output = uperCaseTopicClass1.get(value);
        // System.out.println("Size:"+i);
        if (output != null) {
            return Collections.singletonList(new Feature("UperCaseTopicClass1", output));
        }
        return Collections.singletonList(new Feature("UperCaseTopicClass1", "NA"));

    }

}
