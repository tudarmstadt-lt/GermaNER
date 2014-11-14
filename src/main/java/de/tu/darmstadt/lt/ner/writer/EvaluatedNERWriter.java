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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.tu.darmstadt.lt.ner.types.GoldNamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class EvaluatedNERWriter
    extends JCasConsumer_ImplBase
{
    public static final String OUTPUT_FILE = "OutputFile";
    @ConfigurationParameter(name = OUTPUT_FILE, mandatory = true)
    private File OutputFile = null;

    public static final String NOD_OUTPUT_FILE = "nodOutputFile";
    @ConfigurationParameter(name = NOD_OUTPUT_FILE, mandatory = false)
    private File nodOutputFile = null;

    public static final String IS_GOLD = "isGold";
    @ConfigurationParameter(name = IS_GOLD, mandatory = false)
    private boolean isGold = false;

    public static final String SENTENCES_ID = "sentencesId";
    @ConfigurationParameter(name = SENTENCES_ID, mandatory = false)
    private List<String> sentencesId = null;

    public static final String LF = System.getProperty("line.separator");
    public static final String TAB = "\t";
    private static final String ORG = "ORG";
    private static final String PER = "PER";
    private static final String B_ORG = "B-ORG";
    private static final String B_PER = "B-PER";
    private static final String I_ORG = "I-ORG";
    private static final String I_PER = "I-PER";
    private static final String TYPE_SEP = "$";
    private static final String ENT_SEP = ",";

    @Override
    public void process(JCas jCas)
        throws AnalysisEngineProcessException
    {
        try {
            FileWriter outputWriter = new FileWriter(OutputFile);
            Map<Sentence, Collection<NamedEntity>> sentencesNER = JCasUtil.indexCovered(jCas,
                    Sentence.class, NamedEntity.class);

            FileWriter nodOutputWriter = null;
            if (nodOutputFile != null) {
                nodOutputWriter = new FileWriter(nodOutputFile);
            }
            int sentenceIndex = 0;

            List<Sentence> sentences = new ArrayList<Sentence>(sentencesNER.keySet());
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

                List<String> personSb = new ArrayList<String>();
                List<String> orgSb = new ArrayList<String>();
                String prevNeType = "O";
                String namedEntity = "";

                for (NamedEntity neAnnotation : sentencesNER.get(sentence)) {

                    String text = neAnnotation.getCoveredText().replace(" ", "");
                    String neType = neAnnotation.getValue();

                    StringBuilder sb = new StringBuilder();
                    sb.append(text);
                    sb.append(" ");
                    if (isGold) {
                        sb.append(JCasUtil.selectCovered(jCas, GoldNamedEntity.class, neAnnotation)
                                .get(0).getNamedEntityType());
                    }
                    sb.append(" ");
                    sb.append(neAnnotation.getValue());
                    sb.append(LF);
                    outputWriter.write(sb.toString());

                    // save the sentence-ne file for NoD
                    if (nodOutputFile != null) {
                        if (neType.equals("O")) {
                            if (prevNeType == "O") {
                                continue;
                            }
                            else if (prevNeType.equals(PER)) {
                                personSb.add(namedEntity);
                                prevNeType = "O";
                                namedEntity = "";
                            }
                            else {
                                orgSb.add(namedEntity.trim());
                                prevNeType = "O";
                                namedEntity = "";
                            }

                        }
                        // if ORG is followed by PER
                        else if (prevNeType.equals(ORG)
                                && (neType.equals(B_PER) || neType.equals(I_PER))) {
                            orgSb.add(namedEntity.trim());
                            prevNeType = PER;
                            namedEntity = text;
                        }
                        // if PER is followed by ORG
                        else if (prevNeType.equals(PER)
                                && (neType.equals(B_ORG) || neType.equals(I_ORG))) {
                            personSb.add(namedEntity);
                            prevNeType = ORG;
                            namedEntity = text;
                        }
                        else if (neType.equals(B_PER) || neType.equals(I_PER)) {
                            prevNeType = PER;
                            namedEntity = namedEntity.trim() + " " + text;
                        }
                        else if (neType.equals(B_ORG) || neType.equals(I_ORG)) {
                            prevNeType = ORG;
                            namedEntity = namedEntity.trim() + " " + text;
                        }
                        else {
                            prevNeType = "O";
                        }

                    }
                }
                outputWriter.write(LF);
                if (nodOutputFile != null && sentencesId != null) {
                    nodOutputWriter.write(sentencesId.get(sentenceIndex) + TAB
                            + (personSb.size() == 0 ? "O" : listNames(personSb)) + TAB + TYPE_SEP
                            + TAB + (orgSb.size() == 0 ? "O" : listNames(orgSb)) + LF);
                }
                sentenceIndex++;
            }
            outputWriter.close();
            if (nodOutputWriter != null) {
                nodOutputWriter.close();
            }

            getContext().getLogger().log(Level.INFO,
                    "Output written to: " + OutputFile.getAbsolutePath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String listNames(List<String> aNameLists)
    {

        return StringUtils.join(aNameLists, ENT_SEP).replace("\'", "");
    }
}