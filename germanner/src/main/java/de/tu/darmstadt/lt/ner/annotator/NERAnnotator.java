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
package de.tu.darmstadt.lt.ner.annotator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.Instance;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import com.thoughtworks.xstream.XStream;

import de.tu.darmstadt.lt.ner.preprocessing.XStreamFactory;
import de.tu.darmstadt.lt.ner.types.GoldNamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class NERAnnotator
    extends CleartkSequenceAnnotator<String>
{
    public static final String PARAM_FEATURE_EXTRACTION_FILE = "FeatureExtractionFile";

    /**
     * if a feature extraction/context extractor filename is given the xml file is parsed and the
     * features are used, otherwise it will not be used
     */
    @ConfigurationParameter(name = PARAM_FEATURE_EXTRACTION_FILE, mandatory = false)
    private String featureExtractionFile = null;


    public static final String FEATURE_FILE = "FeatureFile";

    /**
     * if a feature extraction/context extractor filename is given the xml file is parsed and the
     * features are used, otherwise it will not be used
     */
    @ConfigurationParameter(name = FEATURE_FILE, mandatory = false)
    private String classifierJarDir = null;

    private List<FeatureExtractor1<Annotation>> featureExtractors;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        // load the settings from a file
        // initialize the XStream if a xml file is given:
        XStream xstream = XStreamFactory.createXStream();
        featureExtractors = (List<FeatureExtractor1<Annotation>>) xstream.fromXML(new File(
                featureExtractionFile));
    }

    @Override
    public void process(JCas jCas)
        throws AnalysisEngineProcessException
    {
        Map<Sentence, Collection<Token>> sentencesTokens = JCasUtil.indexCovered(jCas,
                Sentence.class, Token.class);

        Map<Integer, List<Instance<String>>> sentencesInstances = new LinkedHashMap<Integer, List<Instance<String>>>();
        int index = 0;
        for (Sentence sentence : sentencesTokens.keySet()) {
            List<Instance<String>> instances = new ArrayList<Instance<String>>();
            for (Token token : sentencesTokens.get(sentence)) {
                Instance<String> instance = new Instance<String>();
                for (FeatureExtractor1<Annotation> extractor : this.featureExtractors) {
                    instance.addAll(extractor.extract(jCas, token));
                }

                if (this.isTraining()) {
                    GoldNamedEntity goldNE = JCasUtil.selectCovered(jCas, GoldNamedEntity.class,
                            token).get(0);
                    instance.setOutcome(goldNE.getNamedEntityType());
                }

                // add the instance to the list !!!
                instances.add(instance);
            }

            // differentiate between training and classifying
            if (this.isTraining()) {
                this.dataWriter.write(instances);
            }
            else {
                sentencesInstances.put(index, instances);
                index++;
            }
        }
        File featureFile = null;
        if (classifierJarDir != null) {
            featureFile = new File(classifierJarDir,"crfsuite");
        }
        List<String> namedEntities = this.classify(sentencesInstances, featureFile);
        try {
            FileUtils.copyFile(featureFile, new File(featureFile.getAbsolutePath()+".test"));
        }
        catch (IOException e) {
        }
        int i = 0;
        for (Sentence sentence : sentencesTokens.keySet()) {
            for (Token token : sentencesTokens.get(sentence)) {
                NamedEntity namedEntity = new NamedEntity(jCas, token.getBegin(), token.getEnd());
                namedEntity.setValue(namedEntities.get(i));
                namedEntity.addToIndexes();

                i++;
            }
            i++;
        }
    }
}