package de.ovgu.spldev.pclocator;

import de.ovgu.spldev.featurecopp.splmodel.FeatureTree;

public class FeatureCoPPPresenceCondition extends PresenceCondition {
    private FeatureTree featureTree;

    public FeatureCoPPPresenceCondition(FeatureTree featureTree) {
        this.featureTree = featureTree;
    }

    private FeatureCoPPPresenceCondition() {
        this.featureTree = null;
    }

    public static FeatureCoPPPresenceCondition getNotFound(Integer line) {
        FeatureCoPPPresenceCondition notFound = new FeatureCoPPPresenceCondition() {
            public String toString() {
                return "?";
            }

            public boolean isPresent() {
                return false;
            }
        };
        notFound.history(line).add(notFoundHistory);
        return notFound;
    }

    public static FeatureCoPPPresenceCondition getNotFound() {
        return getNotFound(null);
    }

    public static FeatureCoPPPresenceCondition getTrue(Integer line) {
        FeatureTree featureTree = new FeatureTree();
        featureTree.setKeyword("#if");
        featureTree.setRoot(new FeatureTree.IntLiteral(null, null, "1"));
        FeatureCoPPPresenceCondition presenceCondition = new FeatureCoPPPresenceCondition(featureTree);
        presenceCondition.history().add(trueHistory);
        return presenceCondition;
    }

    public static FeatureCoPPPresenceCondition getTrue() {
        return getTrue(null);
    }

    public static FeatureCoPPPresenceCondition getFalse(Integer line) {
        FeatureTree featureTree = new FeatureTree();
        featureTree.setKeyword("#if");
        featureTree.setRoot(new FeatureTree.IntLiteral(null, null, "0"));
        FeatureCoPPPresenceCondition presenceCondition = new FeatureCoPPPresenceCondition(featureTree);
        presenceCondition.history().add(falseHistory);
        return presenceCondition;
    }

    public static FeatureCoPPPresenceCondition getFalse() {
        return getFalse(null);
    }

    public static FeatureCoPPPresenceCondition fromDNF(String formula) {
        if (formula.equals("1"))
            return getTrue();
        else if (formula.equals("0"))
            return getFalse();

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

    public PresenceCondition clone() {
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
            return ConfigurationSpace.getNotFound(this);
        FeatureCoPPConfigurationSpace configurationSpace = new FeatureCoPPConfigurationSpace(this, dimacsFilePath, timeLimit);
        configurationSpace.history()
                .add("This configuration space has been located by FeatureCoPP " +
                        "from the presence condition and the given feature model. " +
                        "Below is some context for how the presence condition has been located: ")
                .reference(this);
        return configurationSpace;
    }

    public FeatureTree getFeatureTree() {
        return featureTree;
    }

    public FeatureCoPPPresenceCondition and(FeatureCoPPPresenceCondition that) {
        if (!isPresent() || !that.isPresent())
            return getNotFound(getLine() == null ? that.getLine() : getLine());

        FeatureTree andFeatureTree = new FeatureTree();
        andFeatureTree.setRoot(new FeatureTree.LogAnd(featureTree.getRoot(), that.featureTree.getRoot(), "&&"));

        FeatureCoPPPresenceCondition presenceCondition = new FeatureCoPPPresenceCondition(andFeatureTree);
        presenceCondition.history(getLine() == null ? that.getLine() : getLine());
        return presenceCondition;
    }
}