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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.Instance;
import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import de.tu.darmstadt.lt.ner.preprocessing.GermaNERMain;
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

    private List<FeatureExtractor1<Token>> featureExtractors;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        // load feature settings from a configuration file or from an xml file
        // if (GermaNERMain.getPropFile() != null) {
        try {
            GermaNERMain.loadConfig();
            featureExtractors = GetFeaturesFromConfigFile.getFeatures(GermaNERMain.getPropFile());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // }
        /*
         * else { // load the settings from a file // initialize the XStream if a xml file is given:
         * XStream xstream = XStreamFactory.createXStream(); featureExtractors =
         * (List<FeatureExtractor1<Token>>) xstream .fromXML(new File(featureExtractionFile)); }
         */

    }

    @Override
    public void process(JCas jCas)
        throws AnalysisEngineProcessException
    {
        Map<Sentence, Collection<Token>> sentencesTokens = JCasUtil.indexCovered(jCas,
                Sentence.class, Token.class);
        List<Sentence> sentences = new ArrayList<Sentence>(sentencesTokens.keySet());
        // sort sentences by sentence
        Collections.sort(sentences, new Comparator<Sentence>()
        {
            @Override
            public int compare(Sentence arg0, Sentence arg1)
            {
                return arg0.getBegin() - arg1.getBegin();
            }
        });

        Map<Integer, List<Instance<String>>> sentencesInstances = new LinkedHashMap<Integer, List<Instance<String>>>();
        List<Sentence> sentenceList = new ArrayList<>();
        int index = 0;
        int it = 1;
        for (Sentence sentence : sentences) {
            List<Instance<String>> instances = new ArrayList<Instance<String>>();
            for (Token token : sentencesTokens.get(sentence)) {
                Instance<String> instance = new Instance<String>();
                for (FeatureExtractor1<Token> extractor : this.featureExtractors) {
                    if (extractor instanceof CleartkExtractor) {
                        instance.addAll((((CleartkExtractor) extractor).extractWithin(jCas, token,
                                sentence)));
                    }
                    else {
                        instance.addAll(extractor.extract(jCas, token));
                    }
                }

                if (this.isTraining()) {
                    GoldNamedEntity goldNE = JCasUtil
                            .selectCovered(jCas, GoldNamedEntity.class, token).get(0);
                    instance.setOutcome(goldNE.getNamedEntityType());
                }

                // add the instance to the list !!!
                instances.add(instance);
            }

            // differentiate between training and classifying
            if (this.isTraining()) {
                this.dataWriter.write(instances);
            }
            // do tagging every 10,000 sentences, in favour of memory consumption
            else if (index > 0 && index % 10000 == 0) {
                File featureFile = null;
                if (classifierJarDir != null) {
                    featureFile = new File(classifierJarDir, "crfsuite");
                }
                sentencesInstances.put(index, instances);
                sentenceList.add(sentence);

                classify(jCas, sentencesTokens, sentencesInstances, sentenceList, index, it,
                        featureFile);

                System.out.println(it * index + " sentences are classified");
                it++;
                // re-initialize for next iteration
                sentenceList.clear();
                sentencesInstances.clear();
                index = 0;

            }
            else {
                sentencesInstances.put(index, instances);
                sentenceList.add(sentence);
                index++;
            }
        }
        // the last portion of the sentences
        if (!this.isTraining() && index > 0) {
            File featureFile = null;
            if (classifierJarDir != null) {
                featureFile = new File(classifierJarDir, "crfsuite");
            }
            classify(jCas, sentencesTokens, sentencesInstances, sentenceList, index, it,
                    featureFile);
        }
    }

    private void classify(JCas jCas, Map<Sentence, Collection<Token>> sentencesTokens,
            Map<Integer, List<Instance<String>>> sentencesInstances, List<Sentence> sentenceList,
            int index, int it, File featureFile)
                throws CleartkProcessingException
    {
        List<String> namedEntities = this.classify(sentencesInstances, featureFile);
        try {
            FileUtils.copyFile(featureFile,
                    new File(featureFile.getAbsolutePath() + ".test" + it * index));
        }
        catch (IOException e) {
        }
        int i = 0;
        for (Sentence s : sentenceList) {
            for (Token token : sentencesTokens.get(s)) {
                NamedEntity namedEntity = new NamedEntity(jCas, token.getBegin(), token.getEnd());
                namedEntity.setValue(namedEntities.get(i));
                namedEntity.addToIndexes();

                i++;
            }
            i++;
        }
    }
}