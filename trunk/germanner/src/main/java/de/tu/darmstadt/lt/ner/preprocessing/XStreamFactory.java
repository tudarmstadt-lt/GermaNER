package de.tu.darmstadt.lt.ner.preprocessing;

import java.util.ArrayList;

import org.cleartk.ml.feature.FeatureCollection;
import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Bag;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Covered;
import org.cleartk.ml.feature.extractor.CleartkExtractor.FirstCovered;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Following;
import org.cleartk.ml.feature.extractor.CleartkExtractor.LastCovered;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Ngram;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Preceding;
import org.cleartk.ml.feature.extractor.CoveredTextExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.extractor.TypePathExtractor;
import org.cleartk.ml.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.ml.feature.function.CharacterNgramFeatureFunction;
import org.cleartk.ml.feature.function.FeatureFunctionExtractor;
import org.cleartk.ml.feature.function.LowerCaseFeatureFunction;
import org.cleartk.ml.feature.function.NumericTypeFeatureFunction;

import com.thoughtworks.xstream.XStream;

public class XStreamFactory
{
    public static XStream createXStream()
    {
        // define alias so the xml file can be read easier
        XStream xstream = new XStream();
        xstream.alias("list", ArrayList.class);

        xstream.alias("TypePathExtractor", TypePathExtractor.class);
        xstream.alias("FeatureCollection", FeatureCollection.class);


        xstream.alias("Bag", Bag.class);
        xstream.alias("Preceding", Preceding.class);
        xstream.alias("Following", Following.class);
        xstream.alias("Covered", Covered.class);
        xstream.alias("FirstCovered", FirstCovered.class);
        xstream.alias("LastCovered", LastCovered.class);
        xstream.alias("Ngram", Ngram.class);

        xstream.alias("CleartkExtractor", CleartkExtractor.class);
        xstream.alias("Covered", Covered.class);
        xstream.alias("Following", Following.class);
        xstream.alias("Preceding", Preceding.class);
        xstream.alias("CoveredTextExtractor", CoveredTextExtractor.class);
        xstream.alias("FeatureExtractor1", FeatureExtractor1.class);
        xstream.alias("TypePathExtractor", TypePathExtractor.class);
        xstream.alias("CapitalTypeFeatureFunction", CapitalTypeFeatureFunction.class);
        xstream.alias("CharacterNgramFeatureFunction", CharacterNgramFeatureFunction.class);
        xstream.alias("FeatureFunctionExtractor", FeatureFunctionExtractor.class);
        xstream.alias("LowerCaseFeatureFunction", LowerCaseFeatureFunction.class);
        xstream.alias("NumericTypeFeatureFunction", NumericTypeFeatureFunction.class);

        return xstream;
    }
}