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

public class VornameListFeatureExtractor implements FeatureFunction {

	File vornameListFile=new File("vornameList.txt");

	public VornameListFeatureExtractor() {
		 
	}

	public static final String DEFAULT_NAME = "VornameNameList";

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
						vornameListFile), "UTF8"));
			

				while ((input = br.readLine()) != null) {
					String[] sep = input.split("\\t");
					if (value.equals(sep[0])) {
						//br.close();
						return Collections.singletonList(new Feature("VN",
								"true"));
					}
					//br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Collections.singletonList(new Feature("VN", "False"));
			 
		} else
			return Collections.emptyList();
	}

}
