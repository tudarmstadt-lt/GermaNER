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


public class SimilarWord3Extractor implements FeatureFunction {

	File simWord=new File("200k_2d_wordlists");
	static Map<String, String> simWord3 = new HashMap<String, String>();
	static int i=0;

	public SimilarWord3Extractor() throws IOException {
		 //read
	}

	public static final String DEFAULT_NAME = "SimilarWord1";

	@Override
    public List<Feature> apply(Feature feature) {

		if(i==0)
		{
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					ClassLoader.getSystemResourceAsStream("data/"+simWord.getName()), "UTF8"));
			String input;
			while((input=br.readLine())!=null)
			{
				String []sep=input.split("\\t");
				simWord3.put(sep[0],sep[3]);
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

			String output;
			output=simWord3.get(value);
			//System.out.println("Size:"+i);
			if(output!=null)
			{
				return Collections.singletonList(new Feature("SIMWO3",
						output));
			}
			return Collections.singletonList(new Feature("SIMWO3", "NA"));

		}
        else {
            return Collections.emptyList();
        }
	}

}
