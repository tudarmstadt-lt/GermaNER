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
package de.tu.darmstadt.lt.ner.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import de.tu.darmstadt.lt.ner.feature.variables.ClarkPosInduction;
import de.tu.darmstadt.lt.ner.feature.variables.FreeBaseFeature;
import de.tu.darmstadt.lt.ner.feature.variables.PositionFeature;
import de.tu.darmstadt.lt.ner.preprocessing.Configuration;
import de.tu.darmstadt.lt.ner.types.GoldNamedEntity;
import de.tu.darmstadt.lt.ner.util.GenerateNgram;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class NewsleakInit
    extends JCasAnnotator_ImplBase
{

    public static final String CONLL_VIEW = "ConnlView";

    private Map<String, String> freebaseMap = new HashMap<String, String>();


    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
    }

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
    	
        if (Configuration.useFreeBase) {
            try {
                useFreaBase();
            }
            catch (Exception e) {
                // TODO
            }
        }

        if (Configuration.useClarkPosInduction) {
            try {
                useClarkPosInduction();
            }
            catch (Exception e) {
                // TODO
            }
        }
        
        for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
        	if (Configuration.useFreeBase) {
                getngramBasedFreebaseList(sentence.getCoveredText());
            }
        	 int positionIndex = 0;
        	for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
        		 if (Configuration.usePosition) {
                     PositionFeature.posistion.add(positionIndex);
                     positionIndex++;
                 }
        	}
        	       	
        	
        }
    }

    private void getngramBasedFreebaseList(String sentence)
    {
        // do 1-5 gram freebase checklists
        outer: for (String sentToken : sentence.trim().split(" ")) {
            for (int i = 5; i > 0; i--) {
                try {
                    for (String nGramToken : GenerateNgram.generateNgramsUpto(
                            sentence, i)) {
                        if (nGramToken.split(" ").length == 0 && !nGramToken.equals(sentToken)) {
                            continue;
                        }
                        if (nGramToken.contains(sentToken) && freebaseMap.get(nGramToken) != null) {
                            if (nGramToken.startsWith(sentToken)) {
                                FreeBaseFeature.freebaseFeature.add("B-"
                                        + freebaseMap.get(nGramToken));
                                continue outer;
                            }
                            else {
                                FreeBaseFeature.freebaseFeature.add("I-"
                                        + freebaseMap.get(nGramToken));
                                continue outer;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    FreeBaseFeature.freebaseFeature.add("none");
                    continue outer;
                }
            }
            FreeBaseFeature.freebaseFeature.add("none");
        }
    }


    private void useFreaBase()
        throws Exception
    {

        BufferedReader reader = (BufferedReader) getReader("freebase_2502.txt3");
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                StringTokenizer st = new StringTokenizer(line, "\t");
                freebaseMap.put(st.nextToken(), st.nextToken());
            }
            catch (Exception e) {
               // System.out.println("Warning: check if the freebase list file is correct, Some entries are wrong. " + e.getMessage());
            }
        }
    }

    public void useClarkPosInduction()
        throws Exception
    {
        BufferedReader reader = (BufferedReader) getReader("clark10m256");
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                String[] sample = line.split("\\t");
                String word = sample[0];
                // the class is mostly at the end of the column
                String wordClass = sample[sample.length - 1];
                ClarkPosInduction.posInduction.put(word, wordClass);
            }
            catch (Exception e) {
               // System.out.println("Warning: check if the clark POS induction list file is correct "
                 //       + e.getMessage());
            }
        }

        reader.close();
    }

    public Reader getReader(String aName)
        throws IOException
    {

        InputStream is = ClassLoader.getSystemResourceAsStream("data.zip");
        ZipInputStream zis = new ZipInputStream(is);

        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            if (entry.toString().equals(aName)) {
                return new BufferedReader(new InputStreamReader(zis));

            }
            entry = zis.getNextEntry();
        }
        return null;
    }
}