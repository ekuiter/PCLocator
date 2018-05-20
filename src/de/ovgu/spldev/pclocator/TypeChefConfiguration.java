package de.ovgu.spldev.pclocator;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class TypeChefConfiguration extends Configuration {
    private Collection<SingleFeatureExpr> enabledFeatures, disabledFeatures;

    public TypeChefConfiguration(Tuple2<List<SingleFeatureExpr>, List<SingleFeatureExpr>> satisfiableAssignment) {
        enabledFeatures = JavaConverters.asJavaCollection(satisfiableAssignment._1());
        disabledFeatures = JavaConverters.asJavaCollection(satisfiableAssignment._2());
    }

    private TypeChefConfiguration() {
        enabledFeatures = Collections.emptyList();
        disabledFeatures = Collections.emptyList();
    }

    public final static TypeChefConfiguration getNotFound(PresenceCondition presenceCondition) {
        TypeChefConfiguration notFound = new TypeChefConfiguration() {
            public String toString() {
                return "?";
            }

            public boolean isPresent() {
                return false;
            }
        };
        Log.history(notFound)
                .include(presenceCondition)
                .add("No satisfying configuration found. " +
                "This means that either no presence condition has been located " +
                "or that no satisfying configuration exists.");
        return notFound;
    }

    public boolean isPresent() {
        return true;
    }

    private String[] getFeatureNames(Collection<SingleFeatureExpr> features) {
        return features.stream().map(SingleFeatureExpr::feature).toArray(String[]::new);
    }

    public String[] getEnabledFeatureNames() {
        return getFeatureNames(enabledFeatures);
    }

    public String[] getDisabledFeatureNames() {
        return getFeatureNames(disabledFeatures);
    }

    public TypeChefPresenceCondition toPresenceCondition() {
        FeatureExpr featureExpr = FeatureExprFactory.True();
        for (SingleFeatureExpr enabledFeature : enabledFeatures)
            featureExpr = featureExpr.and(enabledFeature);
        for (SingleFeatureExpr disabledFeature : disabledFeatures)
            featureExpr = featureExpr.andNot(disabledFeature);
        return new TypeChefPresenceCondition(featureExpr);
    }

    public String toString() {
        return Arrays.toString(getEnabledFeatureNames());
    }
}
