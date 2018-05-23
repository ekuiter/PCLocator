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
import java.util.stream.Stream;

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

    public String toHumanString() {
        return Arrays.toString(getEnabledFeatureNames());
    }

    public String toFlagsString() {
        return joinToFlags(Stream.of(getEnabledFeatureNames()));
    }

    public String toConfigString() {
        // Unlike with the -D flags, here we also include disabled features to provide a hint to
        // "make oldconfig" on which features to exclude. All other features (non-Boolean features)
        // are not considered by our tool and will be filled in by "yes "" | make oldconfig".
        Stream<String> enabledFeatures = Stream.of(getEnabledFeatureNames()).map(feature -> feature + "=y"),
                disabledFeatures = Stream.of(getDisabledFeatureNames()).map(feature -> feature + "=n");

        // append a newline character so that multiple .configs are distinguishable when --limit 1 is not used
        return String.join("\n", Stream.concat(enabledFeatures, disabledFeatures).toArray(String[]::new)) + "\n";
    }
}
