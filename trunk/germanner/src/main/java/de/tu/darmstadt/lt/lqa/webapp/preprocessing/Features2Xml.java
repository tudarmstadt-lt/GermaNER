package de.tu.darmstadt.lt.lqa.webapp.preprocessing;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Following;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Preceding;
import org.cleartk.classifier.feature.extractor.simple.CoveredTextExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.classifier.feature.extractor.simple.TypePathExtractor;
import org.cleartk.classifier.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction;
import org.cleartk.classifier.feature.function.FeatureFunctionExtractor;
import org.cleartk.classifier.feature.function.LowerCaseFeatureFunction;
import org.cleartk.classifier.feature.function.NumericTypeFeatureFunction;

import com.thoughtworks.xstream.XStream;

import de.tu.darmstadt.lt.lqa.webapp.MyFeatureFunctionExtractor;
import de.tu.darmstadt.lt.lqa.webapp.feature.extractor.SimilarWord1Extractor;
import de.tu.darmstadt.lt.lqa.webapp.types.SimilarWord1;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class Features2Xml
{
	public static void generateFeatureExtractors(String filename) throws FileNotFoundException
	{
		CharacterNGramFeatureFunction.Orientation fromLeft = CharacterNGramFeatureFunction.Orientation.LEFT_TO_RIGHT;
		CharacterNGramFeatureFunction.Orientation fromRight = CharacterNGramFeatureFunction.Orientation.RIGHT_TO_LEFT;
		List<SimpleFeatureExtractor> tokenFeatureExtractors;
		tokenFeatureExtractors = new ArrayList<SimpleFeatureExtractor>();
		
		/*tokenFeatureExtractors.add(new CoveredTextExtractor());
		tokenFeatureExtractors.add(new FeatureFunctionExtractor(
			new CoveredTextExtractor(),
			new NumericTypeFeatureFunction(),
			new CapitalTypeFeatureFunction(),
			new LowerCaseFeatureFunction(),
			//new SimilarWord1Extractor(),
			new CharacterNGramFeatureFunction(fromLeft, 0, 2),
			new CharacterNGramFeatureFunction(fromLeft, 0, 4),
			new CharacterNGramFeatureFunction(fromRight, 0, 4),
			new CharacterNGramFeatureFunction(fromRight, 0, 2)
			
			)
		);*/
		/*tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new FeatureFunctionExtractor(
						new CoveredTextExtractor(),
						new CharacterNGramFeatureFunction(fromLeft, 0, 2),
						new CharacterNGramFeatureFunction(fromRight, 0, 4),
						new CharacterNGramFeatureFunction(fromRight, 0, 2)),
				new Preceding(2),
				new Preceding(1),
				new Following(2))
			);
		tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new CoveredTextExtractor(),
				new Preceding(1),
				new Preceding(1),
				new Following(2))
			);
		tokenFeatureExtractors.add(new TypePathExtractor(SimilarWord1.class,"Value"));*/
		/*tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new FeatureFunctionExtractor(
						new CoveredTextExtractor(),new SimilarWord1Extractor()),
				new Preceding(1),
				new Preceding(2),
				new Following(1),
				new Following(2)));
		/*tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new CoveredTextExtractor(),
				new Preceding(1),
				new Preceding(2),
				new Following(1),
				new Following(2))
			);*/
		/*tokenFeatureExtractors.add(new FeatureFunctionExtractor(
				new CoveredTextExtractor(),new SimilarWord1Extractor())
		);
		tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new MyFeatureFunctionExtractor(
						new CoveredTextExtractor(),new SimilarWord1Extractor()),
				new Preceding(1),
				//new Preceding(2),
				new Following(1)
				//new Following(2)
				));
		*/
		
		//German Word feature
		tokenFeatureExtractors.add(new FeatureFunctionExtractor(
				new CoveredTextExtractor()));
		
		tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new CoveredTextExtractor(),
				new Preceding(2)
				));
		
		tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new CoveredTextExtractor(),
				new Following(2)
				));
		
		//Capital Type Feature Function
		tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new MyFeatureFunctionExtractor(
						new CoveredTextExtractor(),new CapitalTypeFeatureFunction()),
				new Preceding(2)
				));
		
		tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
				new CoveredTextExtractor(),new CapitalTypeFeatureFunction())
		);
		
		tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new MyFeatureFunctionExtractor(
						new CoveredTextExtractor(),new CapitalTypeFeatureFunction()),
				new Following(2)
				));
		
		
		//Prefix(1) Feature
		tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new MyFeatureFunctionExtractor(
						new CoveredTextExtractor(),new CharacterNGramFeatureFunction(fromLeft, 0, 1)),
				new Preceding(1)
				));
		
		tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
				new CoveredTextExtractor(),new CharacterNGramFeatureFunction(fromLeft, 0, 1))
		);
		
		tokenFeatureExtractors.add(new CleartkExtractor(
				Token.class,
				new MyFeatureFunctionExtractor(
						new CoveredTextExtractor(),new CharacterNGramFeatureFunction(fromLeft, 0, 1)),
				new Following(1)
				));
		
		
		
		
		
		XStream xstream = XStreamFactory.createXStream();
		String x = xstream.toXML(tokenFeatureExtractors);
		x = removeLogger(x);
		PrintStream ps = new PrintStream(filename);
		ps.println(x);
		ps.close();
	}

	/**
	 * To make the xml file more readable remove the logger elements that are'nt
	 * needed
	 */
	private static String removeLogger(String x)
	{
		StringBuffer buffer = new StringBuffer();
		String[] lines = x.split("\n");
		boolean loggerFound = false;
		StringBuffer line = new StringBuffer();
		String LB = "<logger>";
		String LS = "<logger";
		String LE = "</logger>";
		
		for (String l : lines)
		{
			if (l.trim().contains(LB) && l.trim().contains(LE))
			{
				line.append(l.substring(0, l.indexOf(LB)));
				line.append(l.substring(l.indexOf(LE) + LE.length()));
			}
			else if (l.trim().contains(LS) && !l.trim().contains(LB))
                continue;
			else if (!loggerFound && l.trim().contains(LB) && !l.trim().contains(LE))
			{
				loggerFound = true;
				line.append(l.substring(0, l.indexOf(LB)));
			}
			else if (loggerFound && l.trim().contains(LE))
			{
				loggerFound = false;
				line.append(l.substring(l.indexOf(LE) + LE.length()));
			}
			else if (!loggerFound)
				line.append(l);

			if (!loggerFound)
			{
				if (!line.toString().trim().isEmpty())
				{
					buffer.append(line.toString());
					buffer.append("\n");
				}
				line = new StringBuffer();
			}
		}

		return buffer.toString();
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		String featureFileName = "./feature.xml";
		generateFeatureExtractors(featureFileName);
	}
}