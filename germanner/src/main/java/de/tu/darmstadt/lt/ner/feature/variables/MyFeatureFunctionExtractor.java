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
package de.tu.darmstadt.lt.ner.feature.variables;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.function.FeatureFunction;

import com.google.common.base.Function;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * <br>
 * Copyright (c) 2007-2012, Regents of the University of Colorado <br>
 * All rights reserved.
 *
 *
 * @author Seid M. Yimam
 * @author Prabhakaran
 */
public class MyFeatureFunctionExtractor
    implements FeatureExtractor1<Token>
{

    public MyFeatureFunctionExtractor(FeatureExtractor1<Token> extractor,
            FeatureFunction... featureFunctions)
    {
        this.extractor = extractor;
        this.featureFunctions = featureFunctions;
    }

    @Override
    public List<Feature> extract(JCas jCas, Token focusAnnotation)
        throws CleartkExtractorException
    {
        List<Feature> features = new ArrayList<Feature>();
        List<Feature> baseFeatures = this.extractor.extract(jCas, focusAnnotation);
        // features.addAll(baseFeatures);
        for (Function<Feature, List<Feature>> featureFunction : this.featureFunctions) {
            features.addAll(apply(featureFunction, baseFeatures));
        }
        return features;
    }

    public static List<Feature> apply(Function<Feature, List<Feature>> featureFunction,
            List<Feature> features)
    {
        List<Feature> returnValues = new ArrayList<Feature>();
        for (Feature feature : features) {
            returnValues.addAll(featureFunction.apply(feature));
        }
        return returnValues;
    }

    private FeatureExtractor1<Token> extractor;

    private FeatureFunction[] featureFunctions;
}
