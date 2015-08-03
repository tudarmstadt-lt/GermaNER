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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
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
import de.tudarmstadt.ukp.dkpro.core.matetools.MatePosTagger;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;

public class TrainNERModel
{
    private static final Logger LOG = Logger.getLogger(TrainNERModel.class.getName());

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
        AnalysisEngine matePosTagger = createEngine(MatePosTagger.class,
                MatePosTagger.PARAM_LANGUAGE, language);
        if (Configuration.useMatePosTagger) {
            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(
                            NER_TagFile.getAbsolutePath(), NERReader.CONLL_VIEW,
                            NER_TagFile.getName()),
                    createEngine(NERReader.class),
                    matePosTagger,
                    createEngine(NERAnnotator.class, NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
                            modelDirectory.getAbsolutePath() + "/feature.xml",
                            CleartkSequenceAnnotator.PARAM_IS_TRAINING, true,
                            DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
                            modelDirectory.getAbsolutePath(),
                            DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
                            CrfSuiteStringOutcomeDataWriter.class));
        }
        else {
            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(
                            NER_TagFile.getAbsolutePath(), NERReader.CONLL_VIEW,
                            NER_TagFile.getName()),
                    createEngine(NERReader.class),
                    createEngine(NERAnnotator.class, NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
                            modelDirectory.getAbsolutePath() + "/feature.xml",
                            CleartkSequenceAnnotator.PARAM_IS_TRAINING, true,
                            DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
                            modelDirectory.getAbsolutePath(),
                            DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
                            CrfSuiteStringOutcomeDataWriter.class));
        }

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
        AnalysisEngine matePosTagger = createEngine(MatePosTagger.class,
                MatePosTagger.PARAM_LANGUAGE, language);
        if (Configuration.useMatePosTagger) {
            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(
                            testPosFile.getAbsolutePath(), NERReader.CONLL_VIEW,
                            testPosFile.getName()),
                    createEngine(NERReader.class),
                    matePosTagger,
                    createEngine(SnowballStemmer.class, SnowballStemmer.PARAM_LANGUAGE, language),
                    createEngine(NERAnnotator.class, NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
                            aClassifierJarPath.getAbsolutePath() + "/feature.xml",
                            NERAnnotator.FEATURE_FILE, aClassifierJarPath.getAbsolutePath(),
                            GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                            aClassifierJarPath.getAbsolutePath() + "/model.jar"),
                    createEngine(EvaluatedNERWriter.class, EvaluatedNERWriter.OUTPUT_FILE,
                            outputFile, EvaluatedNERWriter.IS_GOLD, false,
                            EvaluatedNERWriter.NOD_OUTPUT_FILE, aNodeResultFile,
                            EvaluatedNERWriter.SENTENCES_ID, aSentencesIds));

        }
        else {
            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(
                            testPosFile.getAbsolutePath(), NERReader.CONLL_VIEW,
                            testPosFile.getName()),
                    createEngine(NERReader.class),
                    createEngine(SnowballStemmer.class, SnowballStemmer.PARAM_LANGUAGE, language),
                    createEngine(NERAnnotator.class, NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
                            aClassifierJarPath.getAbsolutePath() + "/feature.xml",
                            NERAnnotator.FEATURE_FILE, aClassifierJarPath.getAbsolutePath(),
                            GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                            aClassifierJarPath.getAbsolutePath() + "/model.jar"),
                    createEngine(EvaluatedNERWriter.class, EvaluatedNERWriter.OUTPUT_FILE,
                            outputFile, EvaluatedNERWriter.IS_GOLD, false,
                            EvaluatedNERWriter.NOD_OUTPUT_FILE, aNodeResultFile,
                            EvaluatedNERWriter.SENTENCES_ID, aSentencesIds));
        }
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

    public static void main(String[] args)
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        String usage = "USAGE: java -jar germanner.jar (f OR ft OR t) modelDir (trainFile OR testFile) [options] "
                + "where f means training mode, t means testing mode, modelDir is model directory, trainFile is a training file,  and "
                + "testFile is a Test file. \n options include \n"
                + " -s=> use poitions as a feature(default false), \n  -d filename => use the file specified as freeBase list feature"
                + "-x list of suffix file ==> comma separated with the suffix and the class the suffix falls, example ..stadt TAB location,\n"
                + "-pr filename => use an unsupervised tag lists file to train with Pretree, \n"
                + " -pt  filename => use Pretree trained on a tarining data or simialr file, TAB Separated \n"
                + "-cpi filename,  Use the Alex Clark's POS induction result as a feature, TAB Separated";
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

            // if the model directory is empty, use the one from the jar!
            if (args[0].equals("t") && !new File(modelDirectory, "model.jar").exists()) {
                IOUtils.copyLarge(ClassLoader.getSystemResourceAsStream("model/model.jar"),
                        new FileOutputStream(new File(modelDirectory, "model.jar")));
                IOUtils.copyLarge(ClassLoader.getSystemResourceAsStream("model/MANIFEST.MF"),
                        new FileOutputStream(new File(modelDirectory, "MANIFEST.MF")));

                IOUtils.copyLarge(ClassLoader.getSystemResourceAsStream("feature/feature.xml"),
                        new FileOutputStream(new File(modelDirectory, "feature.xml")));
            }

            File outputFile = new File(modelDirectory, "res.txt");
            // read different configs
            List<String> argList = Arrays.asList(args);
            if (argList.contains("-p")) {
                Configuration.useMatePosTagger = true;
            }
            // get the freebase file list
            String freebaseList = null;
            if (argList.contains("-d") && argList.get(argList.indexOf("-d") + 1) != null) {
                if (!new File(argList.get(argList.indexOf("-d") + 1)).exists()) {
                    LOG.error(usage);
                    System.exit(1);
                }
                freebaseList = argList.get(argList.indexOf("-d") + 1);
            }

            // use positions as a feature
            boolean usePosition = false;
            if (argList.contains("-s")) {
                usePosition = true;
            }
            String suffixClassFile = null;
            if (argList.contains("-x")) {
                suffixClassFile = argList.get(argList.indexOf("-x") + 1);
            }

            String preTreeUnsupos = null;
            if (argList.contains("-pr")) {
                preTreeUnsupos = argList.get(argList.indexOf("-pr") + 1);
            }
            String pretreeTrain = null;
            if (argList.contains("-pt")) {
                pretreeTrain = argList.get(argList.indexOf("-pt") + 1);
            }

            String clarkPosInduction = null;
            if (argList.contains("-cpi")) {
                clarkPosInduction = argList.get(argList.indexOf("-cpi") + 1);
            }

            Configuration.freebaseList = freebaseList;
            Configuration.usePosition = usePosition;
            Configuration.useUnsuposPretree = preTreeUnsupos;
            Configuration.useSuffixClass = suffixClassFile;
            Configuration.useClarkPosInduction = clarkPosInduction;

            if (args[0].equals("f")) {
                c.run(args[2], args[2] + ".c");
                System.out.println("Start model generation");
                writeModel(new File(args[2] + ".c"), modelDirectory, language);
                System.out.println("Start model generation -- done");
                System.out.println("Start training");
                trainModel(modelDirectory);
                System.out.println("Start training ---done");
            }
            else if (args[0].equals("ft")) {
                c.run(args[2], args[2] + ".c");
                c.run(args[3], args[3] + ".c");
                System.out.println("Start model generation");
                writeModel(new File(args[2] + ".c"), modelDirectory, language);
                System.out.println("Start model generation -- done");
                System.out.println("Start training");
                trainModel(modelDirectory);
                System.out.println("Start training ---done");
                System.out.println("Start testing");
                classifyTestFile(modelDirectory, new File(args[3] + ".c"), outputFile, null, null,
                        language);
                System.out.println("Start testing ---done");
            }
            else {
                c.run(args[2], args[2] + ".c");
                System.out.println("Start testing");
                classifyTestFile(modelDirectory, new File(args[2] + ".c"), outputFile, null, null,
                        language);
                System.out.println("Start testing ---done");
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
}