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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
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

public class NERReader
    extends JCasAnnotator_ImplBase
{

    public static final String CONLL_VIEW = "ConnlView";
    private Logger logger = null;
    private Map<String, String> freebaseMap = new HashMap<String, String>();
    private Map<String, String> suffixClassMap = new HashMap<String, String>();

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        logger = context.getLogger();
    }

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        JCas docView;
        String tbText;
        try {
            docView = jcas.getView(CAS.NAME_DEFAULT_SOFA);
            tbText = jcas.getView(CONLL_VIEW).getDocumentText();
        }
        catch (CASException e) {
            throw new AnalysisEngineProcessException(e);
        }
        // a new sentence always starts with a new line
        if (tbText.charAt(0) != '\n') {
            tbText = "\n" + tbText;
        }

        String[] tokens = tbText.split("(\r\n|\n)");
        Sentence sentence = null;
        int idx = 0;
        Token token = null;
        GoldNamedEntity NamedEntityTag;
        String NamedEntity;
        boolean initSentence = false;
        StringBuffer docText = new StringBuffer();

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
        StringBuffer sentenceSb = new StringBuffer();

        int positionIndex = 0;
        for (String line : tokens) {

            // new sentence if there's a new line
            if (line.equals("")) {
                if (sentence != null && token != null) {
                    terminateSentence(sentence, token, docText);
                    docText.append("\n");
                    idx++;
                    if (Configuration.useFreeBase) {
                        getngramBasedFreebaseList(sentenceSb);
                    }
                    positionIndex = 0;
                }
                // init new sentence with the next recognized token
                initSentence = true;
                sentenceSb = new StringBuffer();
            }
            else {
                String[] tag = line.split("\\t");
                String word = tag[0];
                NamedEntity = tag[tag.length - 1];

                if (Configuration.usePosition) {
                    PositionFeature.posistion.add(positionIndex);
                    positionIndex++;
                }

                docText.append(word);
                sentenceSb.append(word + " ");

                // if (!word.matches("^(\\p{Punct}).*")) {
                token = new Token(docView, idx, idx + word.length());
                NamedEntityTag = new GoldNamedEntity(docView, idx, idx + word.length());
                // sw=new SimilarWord1(docView, idx, idx + word.length());
                docText.append(" ");
                idx++;

                // start new sentence
                if (initSentence) {
                    sentence = new Sentence(docView);
                    sentence.setBegin(token.getBegin());
                    initSentence = false;
                }
                // increment actual index of text
                idx += word.length();
                NamedEntityTag.setNamedEntityType(NamedEntity);

                // sw.setValue(tag[16]);

                // sw.addToIndexes();

                NamedEntityTag.addToIndexes();
                token.addToIndexes();

                logger.log(Level.FINE,
                        "Token: [" + docText.substring(token.getBegin(), token.getEnd()) + "]"
                                + token.getBegin() + "\t" + token.getEnd());
                logger.log(
                        Level.FINE,
                        "NamedEnity: ["
                                + docText.substring(NamedEntityTag.getBegin(),
                                        NamedEntityTag.getEnd()) + "]" + NamedEntityTag.getBegin()
                                + "\t" + NamedEntityTag.getEnd());
            }
        }
        if (!sentenceSb.toString().isEmpty()) {
            if (Configuration.useFreeBase) {
                getngramBasedFreebaseList(sentenceSb);
            }
        }
        System.out.println(FreeBaseFeature.freebaseFeature.size() + " Freebase entries found");
        if (sentence != null && token != null) {
            terminateSentence(sentence, token, docText);
        }

        docView.setSofaDataString(docText.toString(), "text/plain");
    }

    private void getngramBasedFreebaseList(StringBuffer sentenceSb)
    {
        // do 1-5 gram freebase checklists
        outer: for (String sentToken : sentenceSb.toString().trim().split(" ")) {
            for (int i = 5; i > 0; i--) {
                try {
                    for (String nGramToken : GenerateNgram.generateNgramsUpto(
                            sentenceSb.toString(), i)) {
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

    private void terminateSentence(Sentence sentence, Token token, StringBuffer docText)
    {
        sentence.setEnd(token.getEnd());
        sentence.addToIndexes();
        logger.log(Level.FINE,
                "Sentence:[" + docText.substring(sentence.getBegin(), sentence.getEnd()) + "]\t"
                        + sentence.getBegin() + "\t" + sentence.getEnd());
    }

    private void useFreaBase()
        throws Exception
    {

        BufferedReader reader = (BufferedReader) getReader("freebase_2502.txt3");
        String line;
        int lines = 0;
        while ((line = reader.readLine()) != null) {
            try {
                StringTokenizer st = new StringTokenizer(line, "\t");
                freebaseMap.put(st.nextToken(), st.nextToken());
                if (lines % 10000 == 0) {
                    System.out.println(lines);
                }
            }
            catch (Exception e) {
                System.out.println("Check if the freebase list file is correct " + e.getMessage());
            }
            lines++;
        }
    }

    public static void useClarkPosInduction()
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
                System.out.println("Check if the clark POS induction list file is correct "
                        + e.getMessage());
            }
        }

        reader.close();
    }

    @SuppressWarnings("resource")
    public static Reader getReader(String aName)
        throws IOException
    {
        URL url = ClassLoader.getSystemResource("data/data.zip");
        ZipFile zip = new ZipFile(url.getFile().replaceAll("%20", " "));

        InputStream is = ClassLoader.getSystemResourceAsStream("data/data.zip");
        ZipInputStream zis = new ZipInputStream(is);

        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            if (entry.toString().equals(aName)) {
                return new BufferedReader(new InputStreamReader(zip.getInputStream(entry), "UTF8"));
            }
            entry = zis.getNextEntry();
        }
        zip.close();
        return null;
    }
}