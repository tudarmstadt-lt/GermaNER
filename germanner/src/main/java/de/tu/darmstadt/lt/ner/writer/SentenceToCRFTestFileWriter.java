package de.tu.darmstadt.lt.ner.writer;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;


/**
 * This is a helper Class, can be used from NoD. If you use a DKPro tokenizer during
 * training, this method use the same tokenizer available in DKPro,
 */
public class SentenceToCRFTestFileWriter
    extends JCasConsumer_ImplBase
{
    public static final String SENTENCE_FILE_NAME = "sentenceFileName";
    @ConfigurationParameter(name = SENTENCE_FILE_NAME, mandatory = true)
    private String sentenceFileName = null;

    public static final String CRF_TEST_FILE_NAME = "crfFileName";
    @ConfigurationParameter(name = CRF_TEST_FILE_NAME, mandatory = true)
    private String crfFileName = null;

    public static final String LF = System.getProperty("line.separator");


    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        try{
        LineIterator sentIt = FileUtils.lineIterator(new File(sentenceFileName),"UTF-8");


        StringBuilder sb = new StringBuilder();
        while (sentIt.hasNext()) {

            String line = sentIt.nextLine().toString().trim();
            if (line.equals("")) {
                continue;
            }
            String sentenceText = line.split("\t")[2]; // the first and second are id and hash
                                                       // values, not required
           /* Sentence sentence = new Sentence(jcas, index + 1, sentenceText.length() + index);
            sentence.addToIndexes();*/
            sb.append(sentenceText+"\n");
        }
        jcas.setDocumentText(sb.toString().trim());

        AnalysisEngine pipeline = createPrimitive(OpenNlpSegmenter.class,
                OpenNlpSegmenter.PARAM_LANGUAGE, "de");
        pipeline.process(jcas);

        // get the token from jcas and convert it to CRF test file format. one token per line, with
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
        catch(Exception e){
            //
        }
    }
}