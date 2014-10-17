package de.tu.darmstadt.lt.ner.feature.extractor;

import java.util.Collections;
import java.util.List;

import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.function.FeatureFunction;

import de.tu.darmstadt.lt.ner.FreeBaseFeature;

public class FreeBaseFeatureExtractor
    implements FeatureFunction
{

    public FreeBaseFeatureExtractor()
    {

    }

    public static final String DEFAULT_NAME = "FreebaseFeature";

    @Override
    public List<Feature> apply(Feature feature)
    {
        try {
            Object featureValue = feature.getValue();
            if (featureValue == null) {
                return Collections.emptyList();
            }
            else if (featureValue instanceof String) {

                String k = FreeBaseFeature.pos.remove();

                String value = featureValue.toString();
                if (value == null || value.length() == 0) {
                    return Collections.emptyList();
                }

                return Collections.singletonList(new Feature("FreeBase", k));

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
