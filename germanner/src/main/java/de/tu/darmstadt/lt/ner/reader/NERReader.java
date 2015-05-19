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
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import de.tu.darmstadt.lt.ner.FreeBaseFeature;
import de.tu.darmstadt.lt.ner.PositionFeature;
import de.tu.darmstadt.lt.ner.PretreeFeature;
import de.tu.darmstadt.lt.ner.SuffixClassFeature;
import de.tu.darmstadt.lt.ner.types.GoldNamedEntity;
import de.tu.darmstadt.lt.ner.util.GenerateNgram;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class NERReader
    extends JCasAnnotator_ImplBase
{
    public static final String FREE_BASE_LIST = "freebaseList";

    /**
     * A file containing freebase lists of tokens
     */
    @ConfigurationParameter(name = FREE_BASE_LIST, mandatory = false)
    private String freebaseList = null;

    public static final String USE_POSITION = "usePosition";

    /**
     * A file containing freebase lists of tokens
     */
    @ConfigurationParameter(name = USE_POSITION, mandatory = false)
    private boolean usePosition = false;

    public static final String USE_PRETREE = "usePretree";

    /**
     * A file containing freebase lists of tokens
     */
    @ConfigurationParameter(name = USE_PRETREE, mandatory = false)
    private String usePretree = null;

    public static final String USE_SUFFIX_CLASS = "useSuffixClass";

    /**
     * A file containing freebase lists of tokens
     */
    @ConfigurationParameter(name = USE_SUFFIX_CLASS, mandatory = false)
    private String useSuffixClass = null;

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
        if (freebaseList != null) {
            try {
                freebaseFileToMap(freebaseList);
            }
            catch (Exception e) {
                // TODO
            }
        }

        if (useSuffixClass != null) {
            try {
                suffixClassToMap(useSuffixClass);
            }
            catch (Exception e) {
                // TODO
            }
        }

        if (usePretree != null) {
            try {
                trainPretree(usePretree);
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
                    if (freebaseList != null) {
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

                if (usePosition) {
                    PositionFeature.posistion.add(positionIndex);
                    positionIndex++;
                }
                if (useSuffixClass != null) {
                    boolean found = false;
                    for (String suffix : suffixClassMap.keySet()) {
                        if (word.endsWith(suffix)) {
                            SuffixClassFeature.suffixCLass.add(suffixClassMap.get(suffix));
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        SuffixClassFeature.suffixCLass.add("NA");
                    }
                }

                docText.append(word);
                sentenceSb.append(word + " ");

                // if (!word.matches("^(\\p{Punct}).*")) {
                token = new Token(docView, idx, idx + word.length());
                NamedEntityTag = new GoldNamedEntity(docView, idx, idx + word.length());
                // sw=new SimilarWord1(docView, idx, idx + word.length());
                docText.append(" ");
                idx++;
                // }
                /*
                 * else { if ((docText.length() - word.length()) > 0 && (docText.charAt(idx -
                 * word.length()) == ' ')) { docText.deleteCharAt(idx - word.length()); idx--; }
                 * token = new Token(docView, idx, idx + word.length()); NamedEntityTag = new
                 * GoldNamedEntity(docView, idx, idx + word.length()); // sw=new
                 * SimilarWord1(docView, idx, idx + word.length()); }
                 */
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
            if (freebaseList != null) {
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

    private void freebaseFileToMap(String fileName)
        throws Exception
    {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
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
        reader.close();
    }

    private void dbPersonNameFileToMap(String fileName)
        throws Exception
    {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                String[] st = line.split("\t");
                freebaseMap.put(st[0], "Person");
            }
            catch (Exception e) {
                System.out.println("Check if the freebase person name list file is correct "
                        + e.getMessage());
            }
        }
        reader.close();
    }

    private void dbLocationNameFileToMap(String fileName)
        throws Exception
    {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                String[] st = line.split("\t");
                freebaseMap.put(st[0], "Location");
            }
            catch (Exception e) {
                System.out.println("Check if the freebase person name list file is correct "
                        + e.getMessage());
            }
        }
        reader.close();
    }

    private void suffixClassToMap(String fileName)
        throws Exception
    {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                StringTokenizer st = new StringTokenizer(line, "\t");
                suffixClassMap.put(st.nextToken(), st.nextToken());
            }
            catch (Exception e) {
                System.out
                        .println("Suffix class file is not correct. Make sure it is separated by TAB from the class, such as"
                                + " stadt TAB location " + e.getMessage());
            }
        }
        reader.close();
    }

    private void trainPretree(String fileName)
        throws Exception
    {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        int lines = 0;
        while ((line = reader.readLine()) != null) {
            try {
                String[] wordClass = line.split("\\t");
                String[] theWord = wordClass[2].split("\\s");
                for (int i = 0; i < theWord.length; i++) {
                    if (theWord.length != 1) {
                        theWord[i] = theWord[i].substring(0, theWord[i].length() - 1);
                    }
                    PretreeFeature.pretree.train(theWord[i], wordClass[0]);
                    if (lines % 10000 == 0) {
                        System.out.println(lines + " Pretree features trained");
                    }
                    lines++;
                }
            }
            catch (Exception e) {
                System.out.println("Check if the unspos list file is correct " + e.getMessage());
            }
            lines++;
        }

        reader.close();

        PretreeFeature.pretree.setThresh(0.1);
    }
}