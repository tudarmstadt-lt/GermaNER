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
package de.tu.darmstadt.lt.ner.preprocessing;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.crfsuite.CrfSuiteStringOutcomeDataWriter;
import org.cleartk.ml.jar.DefaultSequenceDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import org.cleartk.util.cr.FilesCollectionReader;

import de.tu.darmstadt.lt.ner.annotator.NERAnnotator;
import de.tu.darmstadt.lt.ner.reader.NERReader;
import de.tu.darmstadt.lt.ner.writer.EvaluatedNERWriter;
import de.tu.darmstadt.lt.ner.writer.SentenceToCRFTestFileWriter;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;

public class TrainNERModel
{
    private static final Logger LOG = Logger.getLogger(TrainNERModel.class.getName());
    static File modelDirectory;
    static InputStream configIs;
    static Properties prop;

    public static void initNERModel()
        throws IOException
    {
        configIs = ClassLoader.getSystemResourceAsStream("config.properties");
        prop = new Properties();
        loadConfig();
    }

    /**
     *
     * @param NER_TagFile
     * @param modelDirectory
     *            = the directory where the training model will be saved/or found
     * @param language
     *            = the language of the document, de for German and en for English
     * @param createPos
     *            = if bulletin MatePOS tagger is to be used
     * @param freebaseListFile
     *            = use freebase lists as a feature
     * @param usePosition
     *            = use the position of the token as a feature
     * @param suffixCLass
     *            = if a file to match common suffixes to a given class is given
     * @throws ResourceInitializationException
     * @throws UIMAException
     * @throws IOException
     */
    public static void writeModel(File NER_TagFile, File modelDirectory, String language)
        throws UIMAException, IOException
    {
        runPipeline(
                FilesCollectionReader.getCollectionReaderWithSuffixes(
                        NER_TagFile.getAbsolutePath(), NERReader.CONLL_VIEW, NER_TagFile.getName()),
                createEngine(NERReader.class),
                createEngine(NERAnnotator.class, NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
                        modelDirectory.getAbsolutePath() + "/feature.xml",
                        CleartkSequenceAnnotator.PARAM_IS_TRAINING, true,
                        DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
                        modelDirectory.getAbsolutePath(),
                        DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
                        CrfSuiteStringOutcomeDataWriter.class));

    }

    public static void trainModel(File modelDirectory)
        throws Exception
    {
        org.cleartk.ml.jar.Train.main(modelDirectory.getAbsolutePath());
    }

    public static void classifyTestFile(File aClassifierJarPath, File testPosFile, File outputFile,
            File aNodeResultFile, List<Integer> aSentencesIds, String language)
        throws UIMAException, IOException
    {
        runPipeline(
                FilesCollectionReader.getCollectionReaderWithSuffixes(
                        testPosFile.getAbsolutePath(), NERReader.CONLL_VIEW, testPosFile.getName()),
                createEngine(NERReader.class),
                createEngine(SnowballStemmer.class, SnowballStemmer.PARAM_LANGUAGE, language),
                createEngine(NERAnnotator.class, NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
                        aClassifierJarPath.getAbsolutePath() + "/feature.xml",
                        NERAnnotator.FEATURE_FILE, aClassifierJarPath.getAbsolutePath(),
                        GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                        aClassifierJarPath.getAbsolutePath() + "/model.jar"),
                createEngine(EvaluatedNERWriter.class, EvaluatedNERWriter.OUTPUT_FILE, outputFile,
                        EvaluatedNERWriter.IS_GOLD, false, EvaluatedNERWriter.NOD_OUTPUT_FILE,
                        aNodeResultFile, EvaluatedNERWriter.SENTENCES_ID, aSentencesIds));
    }
    
    public static void classifyTestFile(File testPosFile, File outputFile,
            File aNodeResultFile, List<Integer> aSentencesIds, String language)
        throws UIMAException, IOException
    {
        initNERModel();
        setModelDir();
        runPipeline(
                FilesCollectionReader.getCollectionReaderWithSuffixes(
                        testPosFile.getAbsolutePath(), NERReader.CONLL_VIEW, testPosFile.getName()),
                createEngine(NERReader.class),
                createEngine(SnowballStemmer.class, SnowballStemmer.PARAM_LANGUAGE, language),
                createEngine(NERAnnotator.class, NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
                        modelDirectory.getAbsolutePath() + "/feature.xml",
                        NERAnnotator.FEATURE_FILE, modelDirectory.getAbsolutePath(),
                        GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                        modelDirectory.getAbsolutePath() + "/model.jar"),
                createEngine(EvaluatedNERWriter.class, EvaluatedNERWriter.OUTPUT_FILE, outputFile,
                        EvaluatedNERWriter.IS_GOLD, false, EvaluatedNERWriter.NOD_OUTPUT_FILE,
                        aNodeResultFile, EvaluatedNERWriter.SENTENCES_ID, aSentencesIds));
    }

    /**
     * This is a helper method, can be called from NoD. If you use a DKPro tokenizer during
     * training, this mehtod use the same tokenizer available in DKPro,
     *
     * @param sentences
     *            pure sentences
     * @return
     * @throws UIMAException
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static void sentenceToCRFFormat(List<String> sentences, String aCRFFileName,
            String aLanguage)
        throws UIMAException, IllegalArgumentException, IOException
    {
        SimplePipeline.runPipeline(
                JCasFactory.createJCas(),
                createEngine(SentenceToCRFTestFileWriter.class,
                        SentenceToCRFTestFileWriter.SENTENCE_ITERATOR, sentences,
                        SentenceToCRFTestFileWriter.CRF_TEST_FILE_NAME, aCRFFileName,
                        SentenceToCRFTestFileWriter.CRF_TEST_FILE_LANG, aLanguage));
    }

    public static void main(String[] arg)
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        String usage = "USAGE: java -jar germanner.jar [-cf config.properties] \n"
                + " [-trainf trainingFileName] -testf testFileName";
        long start = System.currentTimeMillis();

        ChangeColon c = new ChangeColon();

        List<String> argList = Arrays.asList(arg);
        try {

            if (argList.contains("-cf") && argList.get(argList.indexOf("-cf") + 1) != null) {
                if (!new File(argList.get(argList.indexOf("-cf") + 1)).exists()) {
                    LOG.error("Default configuration is read from the system\n");
                }
                else {
                    configIs = new FileInputStream(argList.get(argList.indexOf("-cf") + 1));
                }

            }

            if (argList.contains("-testf") && argList.get(argList.indexOf("-testf") + 1) != null) {
                if (!new File(argList.get(argList.indexOf("-testf") + 1)).exists()) {
                    LOG.error("There is no test file to tag");
                    System.exit(1);
                }
                Configuration.testFileName = argList.get(argList.indexOf("-testf") + 1);
            }

            if (argList.contains("-trainf") && argList.get(argList.indexOf("-trainf") + 1) != null) {
                if (!new File(argList.get(argList.indexOf("-trainf") + 1)).exists()) {
                    LOG.error("The system is running in tagging mode. No training data provided");
                }
                else {
                    Configuration.trainFileName = argList.get(argList.indexOf("-trainf") + 1);
                }
            }
            // load a properties file
            initNERModel();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        String language = "de";
        try {
            if (Configuration.testFileName == null) {
                LOG.error(usage);
                System.exit(1);
            }
            setModelDir();

            File outputFile = new File(modelDirectory, "result.tmp");

            if (Configuration.mode.equals("ft")
                    && (Configuration.trainFileName == null || Configuration.testFileName == null)) {
                LOG.error(usage);
                System.exit(1);
            }
            if (Configuration.mode.equals("f") && Configuration.trainFileName == null) {
                LOG.error(usage);
                System.exit(1);
            }
            if (Configuration.mode.equals("t") && Configuration.testFileName == null) {
                LOG.error(usage);
                System.exit(1);
            }

            if (Configuration.mode.equals("f") && Configuration.trainFileName != null) {
                c.normalize(Configuration.trainFileName, Configuration.trainFileName
                        + ".normalized");
                System.out.println("Start model generation");
                writeModel(new File(Configuration.trainFileName + ".normalized"), modelDirectory,
                        language);
                System.out.println("Start model generation -- done");
                System.out.println("Start training");
                trainModel(modelDirectory);
                System.out.println("Start training ---done");
            }
            else if (Configuration.mode.equals("ft") && Configuration.trainFileName != null
                    && Configuration.testFileName != null) {
                c.normalize(Configuration.trainFileName, Configuration.trainFileName
                        + ".normalized");
                c.normalize(Configuration.testFileName, Configuration.testFileName + ".normalized");
                System.out.println("Start model generation");
                writeModel(new File(Configuration.trainFileName + ".normalized"), modelDirectory,
                        language);
                System.out.println("Start model generation -- done");
                System.out.println("Start training");
                trainModel(modelDirectory);
                System.out.println("Start training ---done");
                System.out.println("Start tagging");
                classifyTestFile(modelDirectory, new File(Configuration.testFileName
                        + ".normalized"), outputFile, null, null, language);
                System.out.println("Start tagging ---done");

                // re-normalized the colon changed text
                c.deNormalize(outputFile.getAbsolutePath(),
                        new File(modelDirectory, "result.txt").getAbsolutePath());
            }
            else {
                c.normalize(Configuration.testFileName, Configuration.testFileName + ".normalized");
                System.out.println("Start tagging");
                classifyTestFile(modelDirectory, new File(Configuration.testFileName
                        + ".normalized"), outputFile, null, null, language);
                // re-normalized the colon changed text
                c.deNormalize(outputFile.getAbsolutePath(),
                        new File(modelDirectory, "result.txt").getAbsolutePath());

                System.out.println("Start tagging ---done");
            }
            long now = System.currentTimeMillis();
            UIMAFramework.getLogger().log(Level.INFO, "Time: " + (now - start) + "ms");
        }
        catch (Exception e) {
            LOG.error(usage);
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("NER tarin/test done in " + totalTime / 1000 + " seconds");

    }

    private static void setModelDir()
        throws IOException, FileNotFoundException
    {
        modelDirectory = (Configuration.modelDir == null || Configuration.modelDir.isEmpty()) ? new File(
                "output") : new File(Configuration.modelDir);
        modelDirectory.mkdirs();

        if (!new File(modelDirectory, "model.jar").exists()) {
            IOUtils.copyLarge(ClassLoader.getSystemResourceAsStream("model/model.jar"),
                    new FileOutputStream(new File(modelDirectory, "model.jar")));
        }
        if (!new File(modelDirectory, "MANIFEST.MF").exists()) {
            IOUtils.copyLarge(ClassLoader.getSystemResourceAsStream("model/MANIFEST.MF"),
                    new FileOutputStream(new File(modelDirectory, "MANIFEST.MF")));
        }
        if (!new File(modelDirectory, "feature.xml").exists()) {
            IOUtils.copyLarge(ClassLoader.getSystemResourceAsStream("feature/feature.xml"),
                    new FileOutputStream(new File(modelDirectory, "feature.xml")));
        }
    }

    private static void loadConfig()
        throws IOException
    {
        prop.load(configIs);
        Configuration.mode = prop.getProperty("mode");
        Configuration.useClarkPosInduction = prop.getProperty("useClarkPosInduction").equals("1") ? true
                : false;
        Configuration.usePosition = prop.getProperty("usePosition").equals("1") ? true : false;
        Configuration.useFreeBase = prop.getProperty("useFreebase").equals("1") ? true : false;
        Configuration.modelDir = prop.getProperty("modelDir");
    }
}