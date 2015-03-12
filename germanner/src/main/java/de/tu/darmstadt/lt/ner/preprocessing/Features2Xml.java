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
import org.cleartk.ml.feature.extractor.TypePathExtractor;
import org.cleartk.ml.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.ml.feature.function.CharacterNgramFeatureFunction;
import org.cleartk.ml.feature.function.CharacterNgramFeatureFunction.Orientation;
import org.cleartk.ml.feature.function.FeatureFunctionExtractor;

import com.thoughtworks.xstream.XStream;

import de.tu.darmstadt.lt.ner.MyFeatureFunctionExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.FreeBaseFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.PositionFeatureExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.SimilarWord1Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.SimilarWord2Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.SimilarWord3Extractor;
import de.tu.darmstadt.lt.ner.feature.extractor.UnsupervisedPosExtractor;
import de.tu.darmstadt.lt.ner.feature.extractor.VornameListFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class Features2Xml
{
    public static void generateFeatureExtractors(String filename)
        throws IOException
    {
        CharacterNgramFeatureFunction.Orientation fromLeft = Orientation.LEFT_TO_RIGHT;
        CharacterNgramFeatureFunction.Orientation fromRight = Orientation.RIGHT_TO_LEFT;
        List<FeatureExtractor1<Token>> tokenFeatureExtractors;
        tokenFeatureExtractors = new ArrayList<FeatureExtractor1<Token>>();

        // German Word feature
        tokenFeatureExtractors.add(new FeatureFunctionExtractor<Token>(
                new CoveredTextExtractor<Token>()));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new CoveredTextExtractor<Token>(), new Preceding(2)));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new CoveredTextExtractor<Token>(), new Following(2)));


        // Capital Type Feature Function
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CapitalTypeFeatureFunction()), new Preceding(2)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new CapitalTypeFeatureFunction()));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CapitalTypeFeatureFunction()), new Following(2)));

        // Prefix(1) Feature
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromLeft, 0, 1)), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(),
                new CharacterNgramFeatureFunction(fromLeft, 0, 1)));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromLeft, 0, 1)), new Following(1)));

        // Prefix(2) Feature
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromLeft, 0, 2)), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(),
                new CharacterNgramFeatureFunction(fromLeft, 0, 2)));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromLeft, 0, 2)), new Following(1)));

        // Prefix(3) Feature
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromLeft, 0, 3)), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(),
                new CharacterNgramFeatureFunction(fromLeft, 0, 3)));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromLeft, 0, 3)), new Following(1)));

        // Suffix(1) Feature
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromRight, 0, 1)), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new CharacterNgramFeatureFunction(fromRight, 0,
                        1)));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromRight, 0, 1)), new Following(1)));

        // Suffix(2) Feature
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromRight, 0, 2)), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new CharacterNgramFeatureFunction(fromRight, 0,
                        2)));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromRight, 0, 2)), new Following(1)));

        // Suffix(3) Feature
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromRight, 0, 3)), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new CharacterNgramFeatureFunction(fromRight, 0,
                        3)));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new CharacterNgramFeatureFunction(fromRight, 0, 3)), new Following(1)));

        // Vorname List Feature
        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new VornameListFeatureExtractor()));

        // Unsupervised POS tag

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new UnsupervisedPosExtractor()), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new UnsupervisedPosExtractor()));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new UnsupervisedPosExtractor()), new Following(1)));

        // SpanVNL Feature
        // Position Feature
        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new PositionFeatureExtractor()));
        // FreeBase Feature
        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new FreeBaseFeatureExtractor()));

        // SimilarWord1 Feature
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new SimilarWord1Extractor()), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new SimilarWord1Extractor()));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new SimilarWord1Extractor()), new Following(1)));

        // SimilarWord2 Feature
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new SimilarWord2Extractor()), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new SimilarWord2Extractor()));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new SimilarWord2Extractor()), new Following(1)));

        // SimilarWord3 Feature
        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new SimilarWord3Extractor()), new Preceding(1)));

        tokenFeatureExtractors.add(new MyFeatureFunctionExtractor(
                new CoveredTextExtractor<Token>(), new SimilarWord3Extractor()));

        tokenFeatureExtractors.add(new CleartkExtractor<Token, Token>(Token.class,
                new MyFeatureFunctionExtractor(new CoveredTextExtractor<Token>(),
                        new SimilarWord3Extractor()), new Following(1)));
        tokenFeatureExtractors.add(new TypePathExtractor<Token>(Token.class, "pos/PosValue"));

        XStream xstream = XStreamFactory.createXStream();
        String x = xstream.toXML(tokenFeatureExtractors);
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