package de.tu.darmstadt.lt.ner.writer;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

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
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

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
        LineIterator sentIt = new LineIterator(new FileReader(new File(sentenceFileName)));


        StringBuilder sb = new StringBuilder();
        while (sentIt.hasNext()) {

            String line = sentIt.next().toString().trim();
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

        AnalysisEngine pipeline = createPrimitive(BreakIteratorSegmenter.class,
                BreakIteratorSegmenter.PARAM_CREATE_TOKENS, true,
                BreakIteratorSegmenter.PARAM_CREATE_SENTENCES, true);
        pipeline.process(jcas);

        // get the token from jcas and convert it to CRF test file format. one token per line, with
        // out gold.
        StringBuilder sbCRF = new StringBuilder();
        for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
            for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
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