package de.tu.darmstadt.lt.ner.feature.extractor;

import java.util.Collections;
import java.util.List;

import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.function.FeatureFunction;

import de.tu.darmstadt.lt.ner.PositionFeature;

public class PositionFeatureExtractor
    implements FeatureFunction
{

    public PositionFeatureExtractor()
    {

    }

    public static final String DEFAULT_NAME = "PositionFeature";

    @Override
    public List<Feature> apply(Feature feature)
    {
        Object featureValue = feature.getValue();
        try {
            if (featureValue == null) {
                return Collections.emptyList();
            }
            else if (featureValue instanceof String) {

                int k = PositionFeature.pos.remove();

                String value = featureValue.toString();
                if (value == null || value.length() == 0) {
                    return Collections.emptyList();
                }

                return Collections.singletonList(new Feature("Position", Integer.toString(k)));
            }
            else {
                return Collections.emptyList();
            }
        }
        catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
