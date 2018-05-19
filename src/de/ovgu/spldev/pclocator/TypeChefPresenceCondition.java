package de.ovgu.spldev.pclocator;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import scala.Option;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;

/**
 * A feature expression for a line of code.
 */
public class TypeChefPresenceCondition extends PresenceCondition {
    private FeatureExpr featureExpr;
    boolean warned = false;

    public static TypeChefPresenceCondition TRUE = new TypeChefPresenceCondition(FeatureExprFactory.True());
    public static TypeChefPresenceCondition FALSE = new TypeChefPresenceCondition(FeatureExprFactory.False());

    public static TypeChefPresenceCondition NOT_FOUND =
            new TypeChefPresenceCondition() {
                public String toString() {
                    return "?";
                }

                public boolean isPresent() {
                    return false;
                }
            };

    public TypeChefPresenceCondition(FeatureExpr featureExpr) {
        this.featureExpr = featureExpr;
    }

    protected TypeChefPresenceCondition() {
        this.featureExpr = null;
    }

    public static TypeChefPresenceCondition fromDNF(String formula) {
        if (formula.equals("1"))
            return TRUE;
        else if (formula.equals("0"))
            return FALSE;

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

    public TypeChefConfiguration getSatisfyingConfiguration(String dimacsFilePath, boolean preferDisabledFeatures) {
        if (!isPresent())
            return TypeChefConfiguration.NOT_FOUND;

        DimacsFileReader dimacsFileReader = new DimacsFileReader(dimacsFilePath);
        Option<Tuple2<List<SingleFeatureExpr>, List<SingleFeatureExpr>>> satisfiableAssignment =
                getFeatureExpr().getSatisfiableAssignment(
                        dimacsFileReader.getFeatureModel(),
                        JavaConverters.asScalaSet(dimacsFileReader.getInterestingFeatures()).toSet(),
                        preferDisabledFeatures);

        return !satisfiableAssignment.isEmpty()
                ? new TypeChefConfiguration(satisfiableAssignment.get())
                : TypeChefConfiguration.NOT_FOUND;
    }

    private void warnIgnoringNonBoolean() {
        if (!warned)
            Log.warning("ignoring non-Boolean expressions in presence condition %s", this);
        warned = true;
    }

    public TypeChefConfiguration getSatisfyingConfiguration(String dimacsFilePath) {
        return getSatisfyingConfiguration(dimacsFilePath, true);
    }

    public TypeChefPresenceCondition not() {
        if (!isPresent())
            return NOT_FOUND;

        if (!isBoolean())
            warnIgnoringNonBoolean();

        return new TypeChefPresenceCondition(getFeatureExpr().not());
    }

    public TypeChefPresenceCondition and(TypeChefPresenceCondition that) {
        if (!isPresent() || !that.isPresent())
            return NOT_FOUND;

        if (!isBoolean())
            warnIgnoringNonBoolean();
        if (!that.isBoolean())
            that.warnIgnoringNonBoolean();

        return new TypeChefPresenceCondition(getFeatureExpr().and(that.getFeatureExpr()));
    }

    public ConfigurationSpace getSatisfyingConfigurationSpace(String dimacsFilePath, String timeLimit) {
        if (!isPresent())
            return ConfigurationSpace.NOT_FOUND;
        return new TypeChefConfigurationSpace(this, dimacsFilePath, timeLimit);
    }
}
