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

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Following;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Preceding;
import org.cleartk.ml.feature.extractor.CoveredTextExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.function.FeatureFunctionExtractor;

import com.thoughtworks.xstream.XStream;

import de.tu.darmstadt.lt.ner.feature.extractor.CamelCaseFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.ClarkPosInductionFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.DBLocationListFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.DBNachnamenListFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.DBPersonListFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.FreeBaseFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCapitalTypeFeatureFunction;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCharacterCategoryPatternFunction;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCharacterCategoryPatternFunction.PatternType;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCharacterNgramFeatureFunction;
import de.tu.darmstadt.lt.ner.feature.extractor.LTCharacterNgramFeatureFunction.Orientation;
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
import de.tu.darmstadt.lt.ner.feature.variables.MyFeatureFunctionExtractor;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class Features2Xml
{
    public static void generateFeatureExtractors(String filename)
        throws IOException
    {
        LTCharacterNgramFeatureFunction.Orientation fromLeft = Orientation.LEFT_TO_RIGHT;
        LTCharacterNgramFeatureFunction.Orientation fromRight = Orientation.RIGHT_TO_LEFT;
        List<FeatureExtractor1<Token>> germaNERfeatures = new ArrayList<FeatureExtractor1<Token>>();

        // German Word feature
        germaNERfeatures
                .add(new FeatureFunctionExtractor<Token>(new CoveredTextExtractor<Token>()));

        germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                new CoveredTextExtractor<Token>(), new Preceding(2)));
        germaNERfeatures.add(new CleartkExtractor<Token, Token>(Token.class,
                new CoveredTextExtractor<Token>(), new Following(2)));

        // Capital Type Feature Function
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

        // Prefix(1) Feature
        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromLeft, 0, 1)),
                        new Preceding(1)));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new LTCharacterNgramFeatureFunction(fromLeft, 0, 1)));

        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromLeft, 0, 1)),
                        new Following(1)));

        // Prefix(2) Feature
        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromLeft, 0, 2)),
                        new Preceding(1)));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new LTCharacterNgramFeatureFunction(fromLeft, 0, 2)));

        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromLeft, 0, 2)),
                        new Following(1)));

        // Prefix(3) Feature
        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromLeft, 0, 3)),
                        new Preceding(1)));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new LTCharacterNgramFeatureFunction(fromLeft, 0, 3)));

        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromLeft, 0, 3)),
                        new Following(1)));
        // prefix(4)
        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromLeft, 0, 4)),
                        new Preceding(1)));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new LTCharacterNgramFeatureFunction(fromLeft, 0, 4)));

        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromLeft, 0, 4)),
                        new Following(1)));

        // Suffix(1) Feature
        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromRight, 0, 1)),
                        new Preceding(1)));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new LTCharacterNgramFeatureFunction(fromRight, 0, 1)));

        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromRight, 0, 1)),
                        new Following(1)));

        // Suffix(2) Feature
        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromRight, 0, 2)),
                        new Preceding(1)));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new LTCharacterNgramFeatureFunction(fromRight, 0, 2)));

        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromRight, 0, 2)),
                        new Following(1)));

        // Suffix(3) Feature
        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromRight, 0, 3)),
                        new Preceding(1)));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new LTCharacterNgramFeatureFunction(fromRight, 0, 3)));

        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromRight, 0, 3)),
                        new Following(1)));

        // Suffix (4) feature
        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromRight, 0, 4)),
                        new Preceding(1)));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new LTCharacterNgramFeatureFunction(fromRight, 0, 4)));

        germaNERfeatures
                .add(new CleartkExtractor<Token, Token>(Token.class,
                        new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                                new LTCharacterNgramFeatureFunction(fromRight, 0, 4)),
                        new Following(1)));

        // Vorname List Feature
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new VornameListFeatureExtractor()));

        // Position Feature
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new PositionFeatureExtractor()));
        // FreeBase Feature

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new FreeBaseFeatureExtractor()));

        // DT similar word
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new SimilarWord1Extractor()));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new SimilarWord2Extractor()));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new SimilarWord3Extractor()));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new SimilarWord4Extractor()));

        // camelcase an all upercase word
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new CamelCaseFeatureExtractor()));

        // DB Nachnamen
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new DBNachnamenListFeatureExtractor()));
        // DB Person list
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new DBPersonListFeatureExtractor()));
        // DB Location
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new DBLocationListFeatureExtractor()));

        // topic class features
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new TopicClass1FeatureExtractor()));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new TopicClass50Feature1Extractor()));
        
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new TopicClass200Feature1Extractor()));

        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new TopicClass500Feature1Extractor()));

        // topic class features - focused on upper case tokens
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new UperCasedTopicClass1FeatureExtractor()));

        // Character Category

        germaNERfeatures.add(LTCharacterCategoryPatternFunction
                .<Token> createExtractor(PatternType.ONE_PER_CHAR));
        germaNERfeatures.add(LTCharacterCategoryPatternFunction
                .<Token> createExtractor(PatternType.REPEATS_MERGED));

        // CLARK's POS induction feature
        germaNERfeatures.add(new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                new ClarkPosInductionFeatureExtractor()));

        XStream xstream = XStreamFactory.createXStream();
        String x = xstream.toXML(germaNERfeatures);
        x = removeLogger(x);
        PrintStream ps = new PrintStream(filename);
        ps.println(x);
        ps.close();
    }

    /**
     * To make the xml file more readable remove the logger elements that are'nt needed
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

        for (String l : lines) {
            if (l.trim().contains(LB) && l.trim().contains(LE)) {
                line.append(l.substring(0, l.indexOf(LB)));
                line.append(l.substring(l.indexOf(LE) + LE.length()));
            }
            else if (l.trim().contains(LS) && !l.trim().contains(LB)) {
                continue;
            }
            else if (!loggerFound && l.trim().contains(LB) && !l.trim().contains(LE)) {
                loggerFound = true;
                line.append(l.substring(0, l.indexOf(LB)));
            }
            else if (loggerFound && l.trim().contains(LE)) {
                loggerFound = false;
                line.append(l.substring(l.indexOf(LE) + LE.length()));
            }
            else if (!loggerFound) {
                line.append(l);
            }

            if (!loggerFound) {
                if (!line.toString().trim().isEmpty()) {
                    buffer.append(line.toString());
                    buffer.append("\n");
                }
                line = new StringBuffer();
            }
        }

        return buffer.toString();
    }

    public static void main(String[] args)
        throws IOException
    {

        String featureFileName = "src/main/resources/feature/feature.xml";
        generateFeatureExtractors(featureFileName);
    }
}