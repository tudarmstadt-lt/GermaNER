package de.tu.darmstadt.lt.ner.annotator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.CoveredTextExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Following;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Preceding;
import org.cleartk.ml.feature.function.FeatureFunctionExtractor;

import de.tu.darmstadt.lt.ner.feature.extractor.CamelCaseFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.ClarkPosInductionFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.DBNachnamenListFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.DBPersonListFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.FreeBaseFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCapitalTypeFeatureFunction;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCharacterCategoryPatternFunction;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCharacterNgramFeatureFunction;
import de.tu.darmstadt.lt.ner.feature.extractor.PositionFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.SimilarWord1Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.SimilarWord2Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.SimilarWord3Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.SimilarWord4Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.TopicClass1FeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.TopicClass200Feature1Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.TopicClass500Feature1Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.TopicClass50Feature1Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.UperCasedTopicClass1FeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.VornameListFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCharacterCategoryPatternFunction.PatternType;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCharacterNgramFeatureFunction.Orientation;
import de.tu.darmstadt.lt.ner.feature.variables.MyFeatureFunctionExtractor;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class GetFeaturesFromConfigFile
{
    public static List<FeatureExtractor1<Token>> getFeatures(Properties aProp) throws IOException
    {
        LTCharacterNgramFeatureFunction.Orientation fromLeft = Orientation.LEFT_TO_RIGHT;
        LTCharacterNgramFeatureFunction.Orientation fromRight = Orientation.RIGHT_TO_LEFT;

        List<FeatureExtractor1<Token>> germaNERfeatures = new ArrayList<FeatureExtractor1<Token>>();
        if (aProp.getProperty("usePosition").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new PositionFeatureExtractor()));
        }

        if (aProp.getProperty("useFreeBase").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new FreeBaseFeatureExtractor()));
        }

        if (aProp.getProperty("useClarkPosInduction").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new ClarkPosInductionFeatureExtractor()));
        }

        if (aProp.getProperty("useWordFeature").equals("1")) {
            germaNERfeatures
                    .add(new FeatureFunctionExtractor<Token>(new CoveredTextExtractor<Token>()));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new CoveredTextExtractor<Token>(), new Preceding(2)));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new CoveredTextExtractor<Token>(), new Following(2)));
        }

        if (aProp.getProperty("useCapitalFeature").equals("1")) {
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCapitalTypeFeatureFunction()),
                    new Preceding(2)));
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new LTCapitalTypeFeatureFunction()));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCapitalTypeFeatureFunction()),
                    new Following(2)));
        }

        if (aProp.getProperty("usePreffix1Feature").equals("1")) {
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromLeft, 0, 1)),
                    new Preceding(1)));
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new LTCharacterNgramFeatureFunction(fromLeft, 0, 1)));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromLeft, 0, 1)),
                    new Following(1)));
        }

        if (aProp.getProperty("usePreffix2Feature").equals("1")) {
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromLeft, 0, 2)),
                    new Preceding(1)));
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new LTCharacterNgramFeatureFunction(fromLeft, 0, 2)));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromLeft, 0, 2)),
                    new Following(1)));
        }

        if (aProp.getProperty("usePreffix3Feature").equals("1")) {
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromLeft, 0, 3)),
                    new Preceding(1)));
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new LTCharacterNgramFeatureFunction(fromLeft, 0, 3)));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromLeft, 0, 3)),
                    new Following(1)));
        }

        if (aProp.getProperty("usePreffix4Feature").equals("1")) {
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromLeft, 0, 4)),
                    new Preceding(1)));
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new LTCharacterNgramFeatureFunction(fromLeft, 0, 4)));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromLeft, 0, 4)),
                    new Following(1)));
        }

        if (aProp.getProperty("useSuffix1Feature").equals("1")) {
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromRight, 0, 1)),
                    new Preceding(1)));
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new LTCharacterNgramFeatureFunction(fromRight, 0, 1)));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromRight, 0, 1)),
                    new Following(1)));
        }

        if (aProp.getProperty("useSuffix2Feature").equals("1")) {
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromRight, 0, 2)),
                    new Preceding(1)));
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new LTCharacterNgramFeatureFunction(fromRight, 0, 2)));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromRight, 0, 2)),
                    new Following(1)));

        }

        if (aProp.getProperty("useSuffix3Feature").equals("1")) {
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromRight, 0, 3)),
                    new Preceding(1)));
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new LTCharacterNgramFeatureFunction(fromRight, 0, 3)));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromRight, 0, 3)),
                    new Following(1)));
        }

        if (aProp.getProperty("useSuffix4Feature").equals("1")) {
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromRight, 0, 4)),
                    new Preceding(1)));
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new LTCharacterNgramFeatureFunction(fromRight, 0, 4)));
            germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                    new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                            new LTCharacterNgramFeatureFunction(fromRight, 0, 4)),
                    new Following(1)));
        }

        if (aProp.getProperty("useFirstNameFeature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new VornameListFeatureExtractor()));
        }

        if (aProp.getProperty("useSimilarWord1Feature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new SimilarWord1Extractor()));
        }

        if (aProp.getProperty("useSimilarWord2Feature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new SimilarWord2Extractor()));
        }

        if (aProp.getProperty("useSimilarWord3Feature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new SimilarWord3Extractor()));
        }

        if (aProp.getProperty("useSimilarWord4Feature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new SimilarWord4Extractor()));
        }

        if (aProp.getProperty("useCamelCaseFeature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new CamelCaseFeatureExtractor()));
        }
        
        if (aProp.getProperty("useDBPediaPersonListFeature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new DBNachnamenListFeatureExtractor()));
        }
        
        if (aProp.getProperty("useDBPediaLocationListFeature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new DBPersonListFeatureExtractor()));
        }
        
        if (aProp.getProperty("useTopicClass100Feature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new TopicClass1FeatureExtractor()));
        }
        
        if (aProp.getProperty("useTopicClass50Feature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new TopicClass50Feature1Extractor()));
        }
        
        if (aProp.getProperty("useTopicClass200Feature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new TopicClass200Feature1Extractor()));
        }
        
        if (aProp.getProperty("useTopicClass500Feature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new TopicClass500Feature1Extractor()));
        }
        
        if (aProp.getProperty("useTopicClassUpper100Feature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new UperCasedTopicClass1FeatureExtractor()));
        }
        
        if (aProp.getProperty("useCharacterCategoryFeature").equals("1")) {
            germaNERfeatures.add(LTCharacterCategoryPatternFunction
                    .<Token> createExtractor(PatternType.ONE_PER_CHAR));
            germaNERfeatures.add(LTCharacterCategoryPatternFunction
                    .<Token> createExtractor(PatternType.REPEATS_MERGED));
        }
        
        if (aProp.getProperty("useDBPediaPersonLastNameFeature").equals("1")) {
            germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                    new DBNachnamenListFeatureExtractor()));
        }
        
        return germaNERfeatures;

    }
}
