package de.tu.darmstadt.lt.lqa.webapp.preprocessing;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.pipeline.SimplePipeline.runPipeline;
import java.io.File;
import java.io.IOException;
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
import de.tu.darmstadt.lt.lqa.webapp.annotator.NERAnnotator;
import de.tu.darmstadt.lt.lqa.webapp.reader.NERReader;
import de.tu.darmstadt.lt.lqa.webapp.writer.EvaluatedNERWriter;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;

public class TrainNERModel
{
	public static void writeModel(File NER_TagFile, String featureExtractionDirectory, String modelDirectory, String language) throws ResourceInitializationException, UIMAException, IOException
	{
		runPipeline(
				FilesCollectionReader.getCollectionReaderWithSuffixes(
						NER_TagFile.getAbsolutePath(),
						NERReader.CONLL_VIEW,
						NER_TagFile.getName()
				),
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
						CRFSuiteStringOutcomeDataWriter.class
				)
		);
	}

	public static void trainModel(String modelDirectory) throws Exception
	{
		org.cleartk.classifier.jar.Train.main(modelDirectory);
	}

	public static void classifyTestFile(String modelDirectory, String featureExtractionDirectory, File testPosFile, String language, File outputFile) throws ResourceInitializationException, UIMAException, IOException
	{
		runPipeline(
				FilesCollectionReader.getCollectionReaderWithSuffixes(
						testPosFile.getAbsolutePath(),
						NERReader.CONLL_VIEW,
						testPosFile.getName()
				),
				createPrimitiveDescription(NERReader.class),
				createPrimitiveDescription(
						SnowballStemmer.class,
						SnowballStemmer.PARAM_LANGUAGE, language
				),
				createPrimitiveDescription(NERAnnotator.class,
						NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
						featureExtractionDirectory + "feature.xml",
						GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
						modelDirectory + "model.jar"
				),
				createPrimitiveDescription(
						EvaluatedNERWriter.class,
						EvaluatedNERWriter.OUTPUT_FILE, outputFile
				)
		);
	}

	public static void main(String[] args) throws Exception
	{
		long start = System.currentTimeMillis();
		String modelDirectory = "src/main/resources/model/";
		String featureExtractionDirectory = "./";
		String language = "en";
		
		/*//make it for pos
		File NER_TagFile = new File("training_file");
		File testNERFile = new File("testing_file");
		File outputFile1 = new File("ner_de_sample_train");
		File outputFile2 = new File("ner_de_sample_test")*/
		
		
		File NER_TagFile = new File("ner_de_sample_train");
		File testNERFile = new File("ner_de_sample_test");
		File outputFile = new File("src/main/resources/NER/res.txt");
		new File(modelDirectory).mkdirs();
	
		//make for pos
		writeModel(NER_TagFile, featureExtractionDirectory, modelDirectory, language);
		trainModel(modelDirectory);
		classifyTestFile(modelDirectory, featureExtractionDirectory, testNERFile, language, outputFile);
		long now = System.currentTimeMillis();
		UIMAFramework.getLogger().log(Level.INFO, "Time: " + (now - start) + "ms");
		System.out.println("Time: " + (now - start) + "ms");
		
		
		
	}
}