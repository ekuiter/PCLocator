package de.ovgu.spldev.pclocator;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import scala.Option;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;

import java.util.Set;

/**
 * A feature expression for a line of code.
 */
public class TypeChefPresenceCondition extends PresenceCondition {
    private FeatureExpr featureExpr;
    boolean warned = false;

    public TypeChefPresenceCondition(FeatureExpr featureExpr) {
        this.featureExpr = featureExpr;
    }

    protected TypeChefPresenceCondition() {
        this.featureExpr = null;
    }

    public static TypeChefPresenceCondition getNotFound(Integer line) {
        TypeChefPresenceCondition notFound = new TypeChefPresenceCondition() {
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

    public static TypeChefPresenceCondition getNotFound() {
        return getNotFound(null);
    }

    public static TypeChefPresenceCondition getTrue(Integer line) {
        TypeChefPresenceCondition presenceCondition = new TypeChefPresenceCondition(FeatureExprFactory.True());
        presenceCondition.history(line).add(trueHistory);
        return presenceCondition;
    }

    public static TypeChefPresenceCondition getTrue() {
        return getTrue(null);
    }

    public static TypeChefPresenceCondition getFalse(Integer line) {
        TypeChefPresenceCondition presenceCondition = new TypeChefPresenceCondition(FeatureExprFactory.False());
        presenceCondition.history(line).add(falseHistory);
        return presenceCondition;
    }

    public static TypeChefPresenceCondition getFalse() {
        return getFalse(null);
    }

    public static TypeChefPresenceCondition fromDNF(String formula) {
        if (formula.equals("1"))
            return getTrue();
        else if (formula.equals("0"))
            return getFalse();

        FeatureExpr featureExpr = FeatureExprFactory.False();

        String[] clauses = formula.split(" \\|\\| ");
        for (String clause : clauses) {
            FeatureExpr subExpr = FeatureExprFactory.True();
            String[] literals = clause.split(" && ");
            for (String literal : literals) {
                FeatureExpr varExpr = FeatureExprFactory.createDefinedExternal(literal.replaceAll("^!", ""));
                subExpr = subExpr.and(literal.startsWith("!") ? varExpr.not() : varExpr);
            }
            featureExpr = featureExpr.or(subExpr);
        }

        return new TypeChefPresenceCondition(featureExpr);
    }

    public PresenceCondition clone() {
        if (!isPresent())
            return getNotFound(getLine());
        TypeChefPresenceCondition presenceCondition = new TypeChefPresenceCondition(featureExpr);
        presenceCondition.history(getLine());
        return presenceCondition;
    }

    public String toString() {
        return featureExpr != null
                ? featureExpr.toString().replace("def", "").replace(" ", "")
                : "unknown";
    }

    public boolean isPresent() {
        return featureExpr != null;
    }

    public boolean isBoolean() {
        return true;
    }

    protected FeatureExpr getFeatureExpr() {
        return featureExpr;
    }

    public boolean equivalentTo(TypeChefPresenceCondition other) {
        FeatureExpr featureExpr = getFeatureExpr(), otherFeatureExpr = other.getFeatureExpr();
        if (featureExpr == null || otherFeatureExpr == null)
            return featureExpr == otherFeatureExpr;
        return featureExpr.equivalentTo(otherFeatureExpr);
    }

    public boolean implies(TypeChefPresenceCondition other) {
        FeatureExpr featureExpr = getFeatureExpr(), otherFeatureExpr = other.getFeatureExpr();
        if (otherFeatureExpr == null)
            return true; // a formula consisting of nothing is implied by everything
        else if (featureExpr == null)
            return otherFeatureExpr.isTautology(); // a formula is implied from nothing if it is a tautology
        return featureExpr.implies(otherFeatureExpr).isTautology();
    }

    private TypeChefConfiguration getSatisfyingConfiguration(String dimacsFilePath, boolean preferDisabledFeatures, FeatureModel featureModel, Set<SingleFeatureExpr> interestingFeatures) {
        if (!isPresent())
            return TypeChefConfiguration.getNotFound(this);

        Option<Tuple2<List<SingleFeatureExpr>, List<SingleFeatureExpr>>> satisfiableAssignment =
                getFeatureExpr().getSatisfiableAssignment(
                        featureModel, JavaConverters.asScalaSet(interestingFeatures).toSet(), preferDisabledFeatures);

        TypeChefConfiguration satisfyingConfiguration =
                !satisfiableAssignment.isEmpty()
                        ? new TypeChefConfiguration(satisfiableAssignment.get())
                        : TypeChefConfiguration.getNotFound(this);
        if (!satisfiableAssignment.isEmpty())
            satisfyingConfiguration.history()
                    .add("This satisfying configuration has been located by TypeChef " +
                            "from the presence condition and the given feature model. " +
                            "Below is some context for how the presence condition has been located: ")
                    .reference(this);
        return satisfyingConfiguration;
    }

    private void warnIgnoringNonBoolean() {
        if (!warned)
            history().add("This presence condition has non-Boolean sub-expressions. " +
                    "These sub-expressions will be ignored when deriving configurations " +
                    "because they are not supported by the SAT solver.");
        warned = true;
    }

    public TypeChefConfiguration getSatisfyingConfiguration(String dimacsFilePath) {
        DimacsFileReader dimacsFileReader = new DimacsFileReader(dimacsFilePath);
        return getSatisfyingConfiguration(dimacsFilePath, true, dimacsFileReader.getFeatureModel(), dimacsFileReader.getInterestingFeatures());
    }

    public TypeChefConfiguration getSatisfyingConfiguration(String dimacsFilePath, FeatureModel featureModel, Set<SingleFeatureExpr> interestingFeatures) {
        return getSatisfyingConfiguration(dimacsFilePath, true, featureModel, interestingFeatures);
    }

    public TypeChefPresenceCondition not() {
        if (!isPresent())
            return getNotFound(getLine());

        if (!isBoolean())
            warnIgnoringNonBoolean();

        return new TypeChefPresenceCondition(getFeatureExpr().not());
    }

    public TypeChefPresenceCondition and(TypeChefPresenceCondition that) {
        if (!isPresent() || !that.isPresent())
            return getNotFound(getLine() == null ? that.getLine() : getLine());

        if (!isBoolean())
            warnIgnoringNonBoolean();
        if (!that.isBoolean())
            that.warnIgnoringNonBoolean();

        TypeChefPresenceCondition presenceCondition = new TypeChefPresenceCondition(getFeatureExpr().and(that.getFeatureExpr()));
        presenceCondition.history(getLine() == null ? that.getLine() : getLine());
        return presenceCondition;
    }

    public ConfigurationSpace getSatisfyingConfigurationSpace(String dimacsFilePath, Integer limit, String timeLimit) {
        if (!isPresent())
            return ConfigurationSpace.getNotFound(this);
        TypeChefConfigurationSpace configurationSpace = new TypeChefConfigurationSpace(this, dimacsFilePath, limit, timeLimit);
        configurationSpace.history()
                .add("This configuration space has been located by TypeChef " +
                        "from the presence condition and the given feature model. " +
                        "Below is some context for how the presence condition has been located: ")
                .reference(this);
        return configurationSpace;
    }
}
