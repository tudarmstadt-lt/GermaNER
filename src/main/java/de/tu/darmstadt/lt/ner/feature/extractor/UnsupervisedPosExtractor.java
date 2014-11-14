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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.function.FeatureFunction;


public class UnsupervisedPosExtractor implements FeatureFunction {

	File unPosFile=new File("unsupervised_pos");

	static Map<String, String> uns_pos = new HashMap<String, String>();
	static int i=0;

	public UnsupervisedPosExtractor() {

	}

	public static final String DEFAULT_NAME = "UnsupervisedPos";

	@Override
    public List<Feature> apply(Feature feature) {
		if(i==0)
		{
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					ClassLoader.getSystemResourceAsStream("data/"+unPosFile.getName()), "UTF8"));
			String input;
			while((input=br.readLine())!=null)
			{
				String[] _sep = input.split("\\t");
				String[] sep = _sep[2].split("\\s");
				for(int i=0;i<sep.length;i++)
				{
					if(sep.length!=1) {
                        sep[i]=sep[i].substring(0,sep[i].length()-1);
                    }

					uns_pos.put(sep[i],_sep[0]);

				}
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		i++;
		}


		Object featureValue = feature.getValue();

		if (featureValue == null) {
            return Collections.emptyList();
        }
        else if (featureValue instanceof String) {

			String value = featureValue.toString();
			if (value == null || value.length() == 0) {
                return Collections.emptyList();
            }

			String output=uns_pos.get(value);
			if(output!=null)
			{
				return Collections.singletonList(new Feature("UnSuperPOS",
									output));
			}

			return Collections.singletonList(new Feature("UnSuperPOS","none"));

		}
        else {
            return Collections.emptyList();
        }
	}

}
