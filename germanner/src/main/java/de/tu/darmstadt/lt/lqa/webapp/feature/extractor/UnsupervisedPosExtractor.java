package de.tu.darmstadt.lt.lqa.webapp.feature.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.function.FeatureFunction;

public class UnsupervisedPosExtractor implements FeatureFunction {

	File unPosFile=new File("unsupervised_pos");

	public UnsupervisedPosExtractor() {
		 
	}

	public static final String DEFAULT_NAME = "SimilarWord1";

	public List<Feature> apply(Feature feature) {
		String featureName = Feature
				.createName(DEFAULT_NAME, feature.getName());
		Object featureValue = feature.getValue();
		
	

		if (featureValue == null)
			return Collections.emptyList();
		else if (featureValue instanceof String) {
		 
			String value = featureValue.toString();
			if (value == null || value.length() == 0)
				return Collections.emptyList();
			
			String input;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
						unPosFile), "UTF8"));
			

				while ((input = br.readLine()) != null) {
					String[] _sep = input.split("\\t");
					String[] sep = _sep[2].split("\\s");
					for(int i=0;i<sep.length;i++)
					{
						//System.out.println(sep[i]);
						if(sep.length!=1)
						sep[i]=sep[i].substring(0,sep[i].length()-1);
						//System.out.println(sep[i]);
						if(sep[i].equals(value))
						{
							return Collections.singletonList(new Feature("UnSuperPOS",
									_sep[0]));
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Collections.singletonList(new Feature("UnSuperPOS","none"));
			 
		} else
			return Collections.emptyList();
	}

}
