package de.tu.darmstadt.lt.ner.feature.extractor;

import java.util.Collections;
import java.util.List;

import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.function.FeatureFunction;

import de.tu.darmstadt.lt.ner.SttsFeature;
import de.tu.darmstadt.lt.ner.UnivPosFeature;

public class SttsFeatureExtractor implements FeatureFunction {


	public SttsFeatureExtractor() {
		 
	}

	public static final String DEFAULT_NAME = "SttsFeature";

	public List<Feature> apply(Feature feature) {
		String featureName = Feature
				.createName(DEFAULT_NAME, feature.getName());
		Object featureValue = feature.getValue();
		
	

		if (featureValue == null)
			return Collections.emptyList();
		else if (featureValue instanceof String) {
			
			String k=SttsFeature.pos.remove();
		 
			String value = featureValue.toString();
			if (value == null || value.length() == 0)
				return Collections.emptyList();

			return Collections.singletonList(new Feature("SttsPos",k));
			 
		} else
			return Collections.emptyList();
	}

}
