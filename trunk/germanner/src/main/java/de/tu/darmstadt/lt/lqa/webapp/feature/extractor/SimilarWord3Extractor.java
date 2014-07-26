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

public class SimilarWord3Extractor implements FeatureFunction {

	File simWord=new File("200k_2d_wordlists");

	public SimilarWord3Extractor() {
		 
	}

	public static final String DEFAULT_NAME = "SimilarWord3";

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
						simWord), "UTF8"));

				while ((input = br.readLine()) != null) {
					String[] sep = input.split("\\t");
					if (value.equals(sep[0])) {
						//br.close();
						return Collections.singletonList(new Feature("SIMWO3",
								sep[3]));
					}
					//br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Collections.singletonList(new Feature("SIMWO3", "NA"));
			 
		} else
			return Collections.emptyList();
	}

}
