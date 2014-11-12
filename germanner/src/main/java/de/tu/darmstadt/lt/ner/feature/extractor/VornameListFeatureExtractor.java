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

public class VornameListFeatureExtractor implements FeatureFunction {

	File vornameListFile=new File("vornameList.txt");
	static Map<String, String> vnf = new HashMap<String, String>();
	static int i=0;

	public VornameListFeatureExtractor() {

	}

	public static final String DEFAULT_NAME = "VornameNameList";

	public List<Feature> apply(Feature feature) {

		if(i==0)
		{
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					ClassLoader.getSystemResourceAsStream("data/vornameList.txt"), "UTF8"));
			String input;
			while((input=br.readLine())!=null)
			{
				String []sep=input.split("\\t");
				vnf.put(sep[0],"true");
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

			if(vnf.get(value)!=null) {
                return Collections.singletonList(new Feature("VN", "true"));
            }

			return Collections.singletonList(new Feature("VN", "false"));

		}
        else {
            return Collections.emptyList();
        }
	}

}
