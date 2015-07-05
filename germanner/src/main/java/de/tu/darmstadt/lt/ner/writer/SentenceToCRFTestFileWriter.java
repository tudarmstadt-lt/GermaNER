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
package de.tu.darmstadt.lt.ner.writer;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
/*import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;*/

/**
 * This is a helper Class, can be used from NoD. If you use a DKPro tokenizer during training, this
 * method use the same tokenizer available in DKPro,
 */
public class SentenceToCRFTestFileWriter
        extends JCasConsumer_ImplBase
{
    public static final String SENTENCE_ITERATOR = "iterator";
    @ConfigurationParameter(name = SENTENCE_ITERATOR, mandatory = true)
    private List<String> sentences = null;

    public static final String CRF_TEST_FILE_NAME = "crfFileName";
    @ConfigurationParameter(name = CRF_TEST_FILE_NAME, mandatory = true)
    private String crfFileName = null;

    public static final String CRF_TEST_FILE_LANG = "crfTestFileLanguage";
    @ConfigurationParameter(name = CRF_TEST_FILE_LANG, mandatory = false)
    private String crfTestFileLanguage = "de";

    public static final String LF = System.getProperty("line.separator");



    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
        try {
            StringBuilder sb = new StringBuilder();
            int index  = 0;

            for (String l: sentences) {
                Sentence sentence = new Sentence(jcas, index, l.length() + index);
                sentence.addToIndexes();
                index  = index + l.length() + 1;
                sb.append(l + "\n");
            }

            jcas.setDocumentText(sb.toString().trim());

            //TODO replace this segmenter with open source segmenter, if applications like NoD is
            // to use this. OR
            // use the pre-gscl release
        /*    AnalysisEngine pipeline = createEngine(OpenNlpSegmenter.class,
                    OpenNlpSegmenter.PARAM_LANGUAGE, crfTestFileLanguage, OpenNlpSegmenter.PARAM_WRITE_SENTENCE,
                    false);
            pipeline.process(jcas);*/

            // get the token from jcas and convert it to CRF test file format. one token per line,
            // with
            // out gold.
            StringBuilder sbCRF = new StringBuilder();

            Map<Sentence, Collection<Token>> sentencesTokens = JCasUtil.indexCovered(jcas,
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

            for (Sentence sentence : sentences) {
                for (Token token : sentencesTokens.get(sentence)) {
                    sbCRF.append(token.getCoveredText() + LF);
                }
                sbCRF.append(LF);
            }

            IOUtils.write(sbCRF.toString(), new FileOutputStream(crfFileName), "UTF-8");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}