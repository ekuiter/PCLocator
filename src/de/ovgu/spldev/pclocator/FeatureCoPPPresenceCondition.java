package de.ovgu.spldev.pclocator;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.ovgu.spldev.featurecopp.splmodel.FeatureTree;

public class FeatureCoPPPresenceCondition extends PresenceCondition {
    private FeatureTree featureTree;

    public FeatureCoPPPresenceCondition(FeatureTree featureTree) {
        this.featureTree = featureTree;
    }

    public String toString() {
        return featureTree != null
                ? featureTree.featureExprToString().replace("defined", "").replace(" ", "")
                : "unknown";
    }

    public boolean isPresent() {
        return featureTree != null;
    }

    public boolean isBoolean() {
        return true;
    }

    protected FeatureExpr getFeatureExpr() {
        throw new UnsupportedOperationException("not implemented for FeatureCoPP");
    }

    public ConfigurationSpace getSatisfyingConfigurationSpace(String dimacsFilePath, String timeLimit) {
        return new FeatureCoPPConfigurationSpace(this, dimacsFilePath, timeLimit);
    }

    public FeatureTree getFeatureTree() {
        return featureTree;
    }
}