package de.tu.darmstadt.lt.lqa.webapp.preprocessing;

import java.util.ArrayList;
import org.cleartk.classifier.feature.FeatureCollection;
import org.cleartk.classifier.feature.WindowFeature;
import org.cleartk.classifier.feature.WindowNGramFeature;
import org.cleartk.classifier.feature.extractor.ContextExtractor;
import org.cleartk.classifier.feature.extractor.ContextExtractor.Bag;
import org.cleartk.classifier.feature.extractor.ContextExtractor.Covered;
import org.cleartk.classifier.feature.extractor.ContextExtractor.FirstCovered;
import org.cleartk.classifier.feature.extractor.ContextExtractor.Following;
import org.cleartk.classifier.feature.extractor.ContextExtractor.LastCovered;
import org.cleartk.classifier.feature.extractor.ContextExtractor.Ngram;
import org.cleartk.classifier.feature.extractor.ContextExtractor.Preceding;
import org.cleartk.classifier.feature.extractor.WindowExtractor;
import org.cleartk.classifier.feature.extractor.WindowNGramExtractor;
import org.cleartk.classifier.feature.extractor.simple.SpannedTextExtractor;
import org.cleartk.classifier.feature.extractor.simple.TypePathExtractor;
import org.cleartk.classifier.feature.proliferate.CapitalTypeProliferator;
import org.cleartk.classifier.feature.proliferate.CharacterNGramProliferator;
import org.cleartk.classifier.feature.proliferate.NumericTypeProliferator;
import org.cleartk.classifier.feature.proliferate.ProliferatingExtractor;
import com.thoughtworks.xstream.XStream;

public class XStreamFactory
{
	public static XStream createXStream()
	{
		// define alias so the xml file can be read easier
		XStream xstream = new XStream();
		
		// org.cleartk.classifier.feature.*
		xstream.alias("TypePathExtractor", TypePathExtractor.class);
		xstream.alias("WindowFeature", WindowFeature.class);
		xstream.alias("FeatureCollection", FeatureCollection.class);
		xstream.alias("WindowNGramFeature", WindowNGramFeature.class);
		
		// org.cleartk.classifier.feature.extractor.*
		xstream.alias("WindowExtractor", WindowExtractor.class);
		xstream.alias("WindowNGramExtractor", WindowNGramExtractor.class);
		xstream.alias("ContextExtractor", ContextExtractor.class);
		
		// within ContextExtractor
		xstream.alias("Bag", Bag.class);
		xstream.alias("Preceding", Preceding.class);
		xstream.alias("Following", Following.class);
		xstream.alias("Covered", Covered.class);
		xstream.alias("FirstCovered", FirstCovered.class);
		xstream.alias("LastCovered", LastCovered.class);
		xstream.alias("Ngram", Ngram.class);
		
		// org.cleartk.classifier.feature.proliferate.
		xstream.alias("ProliferatingExtractor", ProliferatingExtractor.class);
		xstream.alias("CharacterNGramProliferator", CharacterNGramProliferator.class);
		xstream.alias("NumericTypeProliferator", NumericTypeProliferator.class);
		xstream.alias("CapitalTypeProliferator", CapitalTypeProliferator.class);
		xstream.alias("SpannedTextExtractor", SpannedTextExtractor.class);
		xstream.alias("list", ArrayList.class);
		
		return xstream;
	}
}