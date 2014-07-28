package de.tu.darmstadt.lt.ner.preprocessing;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.pipeline.SimplePipeline.runPipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
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

import de.tu.darmstadt.lt.ner.annotator.NERAnnotator;
import de.tu.darmstadt.lt.ner.reader.NERReader;
import de.tu.darmstadt.lt.ner.writer.EvaluatedNERWriter;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;

public class TrainNERModel {
	private static final Logger LOG = Logger.getLogger(TrainNERModel.class
			.getName());

	public static void writeModel(File NER_TagFile,
			String featureExtractionDirectory, String modelDirectory,
			String language) throws ResourceInitializationException,
			UIMAException, IOException {
		runPipeline(
				FilesCollectionReader.getCollectionReaderWithSuffixes(
						NER_TagFile.getAbsolutePath(), NERReader.CONLL_VIEW,
						NER_TagFile.getName()),
				createPrimitiveDescription(NERReader.class),
				createPrimitiveDescription(
						NERAnnotator.class,
						NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
						featureExtractionDirectory + "feature.xml",
						CleartkSequenceAnnotator.PARAM_IS_TRAINING,
						true,
						DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
						modelDirectory,
						DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
						CRFSuiteStringOutcomeDataWriter.class));
	}

	public static void trainModel(String modelDirectory) throws Exception {
		org.cleartk.classifier.jar.Train.main(modelDirectory);
	}

	public static void classifyTestFile(String aClassifierJarPath,
			String featureExtractionDirectory, File testPosFile,
			String language, File outputFile)
			throws ResourceInitializationException, UIMAException, IOException {
		runPipeline(
				FilesCollectionReader.getCollectionReaderWithSuffixes(
						testPosFile.getAbsolutePath(), NERReader.CONLL_VIEW,
						testPosFile.getName()),
				createPrimitiveDescription(NERReader.class),
				createPrimitiveDescription(SnowballStemmer.class,
						SnowballStemmer.PARAM_LANGUAGE, language),
				createPrimitiveDescription(NERAnnotator.class,
						NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
						featureExtractionDirectory + "feature.xml",
						GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
						aClassifierJarPath + "model.jar"),
				createPrimitiveDescription(EvaluatedNERWriter.class,
						EvaluatedNERWriter.OUTPUT_FILE, outputFile));
	}

	public static void main(String[] args) throws Exception {
		String usage = "USAGE: java -jar germanner.jar (f OR ft OR t) modelDir (trainFile OR testFile) "
				+ "where f means training mode, t means testing mode, modelDir is model directory, trainFile is a training file,  and "
				+ "testFile is a Test file";
		long start = System.currentTimeMillis();

		ChangeColon c = new ChangeColon();

		
		String language = "de";
		File outputFile = new File("./res.txt");
		try {
			if (!(args[0].equals("f") || args[0].equals("t") || args[0]
					.equals("ft"))
					|| !new File(args[1]).exists()
					|| !new File(args[2]).exists()) {
				LOG.error(usage);
				System.exit(1);
			}
			if (args[0].equals("ft") && !new File(args[3]).exists()) {
				LOG.error(usage);
				System.exit(1);
			}
			String modelDirectory = args[1] + "/";
			new File(modelDirectory).mkdirs();

			IOUtils.copyLarge(
					ClassLoader.getSystemResourceAsStream("model/model.jar"),
					new FileOutputStream(new File(modelDirectory, "model.jar")));
			IOUtils.copyLarge(ClassLoader
					.getSystemResourceAsStream("model/MANIFEST.MF"),
					new FileOutputStream(
							new File(modelDirectory, "MANIFEST.MF")));

			if (args[0].equals("f")) {
				c.run(args[2], args[2] + ".c");
				writeModel(new File(args[2] + ".c"),
						modelDirectory, modelDirectory, language);
				trainModel(modelDirectory);
			} else if (args[0].equals("ft")) {
				c.run(args[2], args[2] + ".c");
				c.run(args[3], args[3] + ".c");
				writeModel(new File(args[2] + ".c"),
						modelDirectory, modelDirectory, language);
				trainModel(modelDirectory);
				classifyTestFile(modelDirectory, modelDirectory,
						new File(args[3] + ".c"), language, outputFile);
			} else {
				c.run(args[2], args[2] + ".c");
				classifyTestFile(modelDirectory, modelDirectory,
						new File(args[2] + ".c"), language, outputFile);
			}
			long now = System.currentTimeMillis();
			UIMAFramework.getLogger().log(Level.INFO,
					"Time: " + (now - start) + "ms");
		} catch (Exception e) {
			LOG.error(usage);
			e.printStackTrace();
		}

	}
}