package de.tu.darmstadt.lt.lqa.webapp.feature.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.classifier.feature.function.FeatureFunction;
import org.cleartk.classifier.feature.function.CapitalTypeFeatureFunction.CapitalType;
import org.cleartk.classifier.feature.util.CaseUtil;

public class SimilarWord1Extractor implements FeatureFunction {

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
			
			try {
				//BufferedReader br=new BufferedReader(new FileReader("200k_2d_wordlists","UTF-8"));
				BufferedReader br = new BufferedReader(
						   new InputStreamReader(
				                      new FileInputStream(new File("200k_2d_wordlists")), "UTF8"));
				String input;
				while((input=br.readLine())!=null)
				{
					String[] sep=input.split("\\t");
					if(value.equals(sep[0]))
					{
						return Collections.singletonList(new Feature("SIMWO",sep[1]));
					}
				}
				br.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Collections.singletonList(new Feature(null,"NA"));
			/*
			String lowerCaseValue = value.toLowerCase();
			String upperCaseValue = value.toUpperCase();
			if (lowerCaseValue.equals(upperCaseValue))
				return Collections.emptyList();

			if (value.equals(value.toLowerCase())) {
				return Collections.singletonList(new Feature(featureName,
						CapitalType.ALL_LOWERCASE.toString()));
			} else if (value.equals(value.toUpperCase())) {
				return Collections.singletonList(new Feature(featureName,
						CapitalType.ALL_UPPERCASE.toString()));
			}

			if (CaseUtil.isInitialUppercase(value)) {
				return Collections.singletonList(new Feature(featureName,
						CapitalType.INITIAL_UPPERCASE.toString()));
			}

			return Collections.singletonList(new Feature(featureName,
					CapitalType.MIXED_CASE.toString()));*/
		} else
			return Collections.emptyList();
	}

}
