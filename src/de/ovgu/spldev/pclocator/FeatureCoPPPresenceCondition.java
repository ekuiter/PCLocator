package de.ovgu.spldev.pclocator;

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

    public static FeatureCoPPPresenceCondition NOT_FOUND =
            new FeatureCoPPPresenceCondition() {
                public String toString() {
                    return "";
                }

                public boolean isPresent() {
                    return false;
                }
            };

    public FeatureCoPPPresenceCondition(FeatureTree featureTree) {
        this.featureTree = featureTree;
    }

    private FeatureCoPPPresenceCondition() {
        this.featureTree = null;
    }

    public static FeatureCoPPPresenceCondition fromDNF(String formula) {
        if (formula.equals("1"))
            return TRUE;
        else if (formula.equals("0"))
            return FALSE;

        FeatureTree.Node topNode = null;

        String[] clauses = formula.split(" \\|\\| ");
        for (String clause : clauses) {
            FeatureTree.Node subNode = null;
            String[] literals = clause.split(" && ");
            for (String literal : literals) {
                FeatureTree.Node macroNode = new FeatureTree.Macro(null, null, literal.replaceAll("^!", ""));
                macroNode.setEmbracedByParentheses();
                FeatureTree.Node varNode = new FeatureTree.Defined(null, macroNode, "defined");
                varNode = literal.startsWith("!") ? new FeatureTree.UnaryLogNeg(null, varNode, "!") : varNode;
                subNode = subNode == null ? varNode : new FeatureTree.LogAnd(subNode.clone(), varNode, "&&");
            }
            topNode = topNode == null ? subNode : new FeatureTree.LogOr(topNode.clone(), subNode, "||");
        }

        FeatureTree featureTree = new FeatureTree();
        if (topNode != null)
            topNode.setEmbracedByParentheses();
        featureTree.setRoot(topNode);
        return new FeatureCoPPPresenceCondition(featureTree);
    }

    public String toString() {
        return featureTree != null
                ? featureTree.featureExprToString().replace("defined", "").replace(" ", "")
                : "unknown";
    }

    public boolean isPresent() {
        return featureTree != null;
    }

    public ConfigurationSpace getSatisfyingConfigurationSpace(String dimacsFilePath, String timeLimit) {
        if (!isPresent())
            return ConfigurationSpace.EMPTY;
        return new FeatureCoPPConfigurationSpace(this, dimacsFilePath, timeLimit);
    }

    public FeatureTree getFeatureTree() {
        return featureTree;
    }

    public FeatureCoPPPresenceCondition and(FeatureCoPPPresenceCondition presenceCondition) {
        if (!isPresent() && !presenceCondition.isPresent())
            return NOT_FOUND;
        else if (!isPresent())
            return presenceCondition;
        else if (!presenceCondition.isPresent())
            return this;

        FeatureTree andFeatureTree = new FeatureTree();
        andFeatureTree.setRoot(new FeatureTree.LogAnd(featureTree.getRoot(), presenceCondition.featureTree.getRoot(), "&&"));

        return new FeatureCoPPPresenceCondition(andFeatureTree);
    }
}