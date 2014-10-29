package de.tu.darmstadt.lt.ner.preprocessing;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.pipeline.SimplePipeline.runPipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.cleartk.classifier.CleartkSequenceAnnotator;
import org.cleartk.classifier.crfsuite.CRFSuiteStringOutcomeDataWriter;
import org.cleartk.classifier.jar.DefaultSequenceDataWriterFactory;
import org.cleartk.classifier.jar.DirectoryDataWriterFactory;
import org.cleartk.classifier.jar.GenericJarClassifierFactory;
import org.cleartk.util.cr.FilesCollectionReader;
import org.uimafit.factory.JCasFactory;
import org.uimafit.pipeline.SimplePipeline;

import de.tu.darmstadt.lt.ner.annotator.NERAnnotator;
import de.tu.darmstadt.lt.ner.reader.NERReader;
import de.tu.darmstadt.lt.ner.writer.EvaluatedNERWriter;
import de.tu.darmstadt.lt.ner.writer.SentenceToCRFTestFileWriter;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;

public class TrainNERModel
{
    private static final Logger LOG = Logger.getLogger(TrainNERModel.class.getName());

    public static void writeModel(File NER_TagFile, File modelDirectory, String language)
        throws ResourceInitializationException, UIMAException, IOException
    {
        runPipeline(
                FilesCollectionReader.getCollectionReaderWithSuffixes(
                        NER_TagFile.getAbsolutePath(), NERReader.CONLL_VIEW, NER_TagFile.getName()),
                createPrimitiveDescription(NERReader.class),
                createPrimitiveDescription(NERAnnotator.class,
                        NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
                        modelDirectory.getAbsolutePath() + "/feature.xml",
                        CleartkSequenceAnnotator.PARAM_IS_TRAINING, true,
                        DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY, modelDirectory,
                        DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
                        CRFSuiteStringOutcomeDataWriter.class));
    }

    public static void trainModel(File modelDirectory)
        throws Exception
    {
        org.cleartk.classifier.jar.Train.main(modelDirectory.getAbsolutePath());
    }

    public static void classifyTestFile(File aClassifierJarPath, File testPosFile, File outputFile,
            File aNodeResultFile, ArrayList<Integer> aSentencesIds, String language)
        throws ResourceInitializationException, UIMAException, IOException
    {

        runPipeline(
                FilesCollectionReader.getCollectionReaderWithSuffixes(
                        testPosFile.getAbsolutePath(), NERReader.CONLL_VIEW, testPosFile.getName()),
                createPrimitiveDescription(NERReader.class),
                createPrimitiveDescription(SnowballStemmer.class, SnowballStemmer.PARAM_LANGUAGE,
                        language),
                createPrimitiveDescription(NERAnnotator.class,
                        NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
                        aClassifierJarPath.getAbsolutePath() + "/feature.xml",
                        GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                        aClassifierJarPath.getAbsolutePath() + "/model.jar"),
                createPrimitiveDescription(EvaluatedNERWriter.class,
                        EvaluatedNERWriter.OUTPUT_FILE, outputFile, EvaluatedNERWriter.IS_GOLD,
                        false, EvaluatedNERWriter.NOD_OUTPUT_FILE, aNodeResultFile,
                        EvaluatedNERWriter.SENTENCES_ID, aSentencesIds));
    }

    /**
     * This is a helper method, can be called from NoD. If you use a DKPro tokenizer during
     * training, this mehtod use the same tokenizer available in DKPro,
     *
     * @param aFileNam
     *            the name of the sentence document file in the form ID \t HASH \t SENTENCE
     * @return
     * @throws UIMAException
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static void sentenceToCRFFormat(String aSentenceFileName, String aCRFFileName)
        throws UIMAException, IllegalArgumentException, IOException
    {
        SimplePipeline.runPipeline(JCasFactory.createJCas(),createPrimitiveDescription(SentenceToCRFTestFileWriter.class,
                SentenceToCRFTestFileWriter.SENTENCE_FILE_NAME, aSentenceFileName,
                SentenceToCRFTestFileWriter.CRF_TEST_FILE_NAME, aCRFFileName));
    }

    public static void main(String[] args)
        throws Exception
    {
        String usage = "USAGE: java -jar germanner.jar (f OR ft OR t) modelDir (trainFile OR testFile) "
                + "where f means training mode, t means testing mode, modelDir is model directory, trainFile is a training file,  and "
                + "testFile is a Test file";
        long start = System.currentTimeMillis();

        ChangeColon c = new ChangeColon();

        String language = "de";
        try {
            if (!(args[0].equals("f") || args[0].equals("t") || args[0].equals("ft"))
                    || !new File(args[1]).exists() || !new File(args[2]).exists()) {
                LOG.error(usage);
                System.exit(1);
            }
            if (args[0].equals("ft") && !new File(args[3]).exists()) {
                LOG.error(usage);
                System.exit(1);
            }
            File modelDirectory = new File(args[1] + "/");
            modelDirectory.mkdirs();

            File outputFile = new File(modelDirectory, "res.txt");
            if (args[0].equals("f")) {
                c.run(args[2], args[2] + ".c");
                writeModel(new File(args[2] + ".c"), modelDirectory, language);
                trainModel(modelDirectory);
            }
            else if (args[0].equals("ft")) {
                c.run(args[2], args[2] + ".c");
                c.run(args[3], args[3] + ".c");
                writeModel(new File(args[2] + ".c"), modelDirectory, language);
                trainModel(modelDirectory);
                classifyTestFile(modelDirectory, new File(args[3] + ".c"), outputFile, null, null,
                        language);
            }
            else {
                c.run(args[2], args[2] + ".c");
                classifyTestFile(modelDirectory, new File(args[2] + ".c"), outputFile, null, null,
                        language);
            }
            long now = System.currentTimeMillis();
            UIMAFramework.getLogger().log(Level.INFO, "Time: " + (now - start) + "ms");
        }
        catch (Exception e) {
            LOG.error(usage);
            e.printStackTrace();
        }

    }
}