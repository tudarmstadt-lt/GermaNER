package de.tu.darmstadt.lt.lqa.webapp;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.uima.UIMAException;
import org.cleartk.classifier.jar.GenericJarClassifierFactory;
import org.json.simple.JSONArray;
import org.uimafit.pipeline.SimplePipeline;
import de.tu.darmstadt.lt.lqa.webapp.annotator.NERAnnotator;
import de.tu.darmstadt.lt.lqa.webapp.reader.SolrReader;
import de.tu.darmstadt.lt.lqa.webapp.writer.NER_LocWriter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;

public class SolrRetriever
{
	public static ArrayList<String> result = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public String queryDB(String q) throws UIMAException, IOException
	{
		String[] stopwords = { ".", ",", "/", "(", ")", "!", "?", "a", "about", "above", "above", "across", "after", "afterwards",
				"again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst",
				"amoungst", "amount",  "an", "and", "another", "any", "anyhow", "anyone", "anything","anyway", "anywhere", "are",
				"around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand",
				"behind", "being", "below", "beside", "besides", "between", "beyond", "both", "bottom","but", "by", "call", "can",
				"cannot", "can't", "co", "con", "could", "couldn't", "cry", "describe", "detail", "do", "done", "down", "due", "during",
				"each", "e.g.", "either", "else", "elsewhere", "enough", "etc.", "even", "ever", "every", "everyone", "everything",
				"everywhere", "except", "few", "for", "former", "formerly", "from", "front", "full", "further", "get", "give", "go",
				"had", "has", "hasn't", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers",
				"herself", "him", "himself", "his", "how", "however", "hundred", "i.e.", "if", "in", "inc.", "indeed", "interest",
				"into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many",
				"may", "me", "meanwhile", "might", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself",
				"namely", "neither", "never", "nevertheless", "next", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now",
				"nowhere", "of", "off", "often", "on", "once", "only", "onto", "or", "other", "others", "otherwise", "our", "ours",
				"ourselves", "out", "over", "own", "part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem",
				"seemed", "seeming", "seems", "serious", "several", "she", "should", "side", "since", "sincere", "so", "some", "somehow",
				"someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "take", "than", "that", "the", "their",
				"them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these",
				"they", "thick", "thin", "third", "this", "those", "though", "through", "throughout", "thru", "thus", "to", "together",
				"too", "top", "toward", "towards", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well",
				"were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein",
				"whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why",
				"will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the" };
		
		for (String stopword : stopwords)
			q = q.replace(" " + stopword + " ", "");
		
		SimplePipeline.runPipeline(
				createCollectionReader(
						SolrReader.class,
						SolrReader.DATAURL,
						"http://130.83.164.194:18500/solr/Wikipedia-English",
						SolrReader.QUERYWORD,
						q
				),
				createPrimitive(StanfordSegmenter.class),
				createPrimitive(
						NERAnnotator.class,
						NERAnnotator.PARAM_FEATURE_EXTRACTION_FILE,
						"/Users/Jonas/Documents/Studium/6. Semester/QA Watson/Project/de.tu-darmstadt.lt.lqa/feature.xml",
						GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
						"/Users/Jonas/Documents/Studium/6. Semester/QA Watson/Project/de.tu-darmstadt.lt.lqa/src/main/resources/model/model.jar"),
				createPrimitive(NER_LocWriter.class)
		);

		JSONArray retVal = new JSONArray();
		for (String s : result)
			retVal.add(s);

		String tmp = retVal.toString();
		return tmp;
	}
}