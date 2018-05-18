package de.ovgu.spldev.pclocator;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.ovgu.spldev.featurecopp.splmodel.FeatureTree;

public class FeatureCoPPPresenceCondition extends PresenceCondition {
    private FeatureTree featureTree;

    public static FeatureCoPPPresenceCondition TRUE, FALSE;

    static {
        FeatureTree featureTree = new FeatureTree();
        featureTree.setKeyword("#if");
        featureTree.setRoot(new FeatureTree.IntLiteral(null, null, "1"));
        TRUE = new FeatureCoPPPresenceCondition(featureTree);
    }

    static {
        FeatureTree featureTree = new FeatureTree();
        featureTree.setKeyword("#if");
        featureTree.setRoot(new FeatureTree.IntLiteral(null, null, "0"));
        FALSE = new FeatureCoPPPresenceCondition(featureTree);
    }

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

    public PresenceCondition not() {
        throw new UnsupportedOperationException("not implemented for FeatureCoPP");
    }

    public PresenceCondition and(PresenceCondition presenceCondition) {
        if (!isPresent() && !presenceCondition.isPresent())
            return NOT_FOUND;
        else if (!isPresent())
            return presenceCondition;
        else if (!presenceCondition.isPresent())
            return this;
        if (!(presenceCondition instanceof FeatureCoPPPresenceCondition))
            throw new UnsupportedOperationException("can not \"and\" a TypeChef PC and a FeatureCoPP PC");

        FeatureTree andFeatureTree = new FeatureTree();
        andFeatureTree.setRoot(new FeatureTree.LogAnd(
                featureTree.getRoot(),
                ((FeatureCoPPPresenceCondition) presenceCondition).featureTree.getRoot(),
                "&&"));

        return new FeatureCoPPPresenceCondition(andFeatureTree);
    }
}