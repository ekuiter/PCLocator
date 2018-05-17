package de.ovgu.spldev.pclocator;

import de.fosd.typechef.featureexpr.*;
import de.fosd.typechef.lexer.FeatureExprLib;
import scala.Function1;
import scala.Option;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.io.Source;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PresenceCondition {
    abstract public String toString();

    abstract public boolean isPresent();

    abstract public boolean isBoolean();

    abstract protected FeatureExpr getFeatureExpr();

    public final static PresenceCondition TRUE =
            new PresenceCondition() {
                public String toString() {
                    return "true";
                }

                public boolean isPresent() {
                    return true;
                }

                public boolean isBoolean() {
                    return true;
                }

                protected FeatureExpr getFeatureExpr() {
                    return FeatureExprFactory.True();
                }
            };

    public final static PresenceCondition NOT_FOUND =
            new PresenceCondition() {
                public String toString() {
                    return "";
                }

                public boolean isPresent() {
                    return false;
                }

                public boolean isBoolean() {
                    return true;
                }

                protected FeatureExpr getFeatureExpr() {
                    return null;
                }
            };

    public static PresenceCondition fromDNF(String formula) {
        if (formula.equals("1"))
            return TRUE;
        else if (formula.equals("0"))
            return new TypeChefPresenceCondition(FeatureExprFactory.False());

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

    public boolean equivalentTo(PresenceCondition other) {
        FeatureExpr featureExpr = getFeatureExpr(), otherFeatureExpr = other.getFeatureExpr();
        if (featureExpr == null || otherFeatureExpr == null)
            return featureExpr == otherFeatureExpr;
        if (featureExpr == NOT_FOUND || otherFeatureExpr == NOT_FOUND)
            return featureExpr == otherFeatureExpr;
        if (featureExpr == TRUE || otherFeatureExpr == TRUE)
            return featureExpr == otherFeatureExpr;
        return featureExpr.equivalentTo(otherFeatureExpr);
    }

    public void print() {
        if (this.toString().length() == 0)
            System.err.println("not found");
        else
            System.out.println(this);
    }

    private static FeatureModelFactory featureModelFactory = FeatureExprLib.featureModelFactory();
    private static Function1<String, String> translateNames = name -> name;
    private static boolean autoAddVariables = false;

    private static Tuple3<Map<String, Object>, List<List<Object>>, Object> loadDimacsData(Source dimacsFileSource) {
        try {
            // We need to pass "interesting features" to getSatisfiableAssignment below.
            // We assume these are the features used in the DIMACS file. To get these,
            // we call loadDimacsData which is protected, so first we make it accessible.
            Method loadDimacsDataMethod = FeatureModelFactory.class.getDeclaredMethod(
                    "loadDimacsData", Source.class, Function1.class, boolean.class);
            return (Tuple3<Map<String, Object>, List<List<Object>>, Object>)
                    loadDimacsDataMethod.invoke(featureModelFactory, dimacsFileSource, translateNames, autoAddVariables);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public static Tuple3<Map<String, Object>, List<List<Object>>, Object> loadDimacsData(String dimacsFilePath) {
        return loadDimacsData(Source.fromFile(dimacsFilePath, "UTF-8"));
    }

    private static Set<SingleFeatureExpr> getInterestingFeatures(Source dimacsFileSource) {
        return JavaConverters
                .mapAsJavaMap(loadDimacsData(dimacsFileSource)._1())
                .keySet() // get variables (= features)
                .stream()
                .map(FeatureExprFactory::createDefinedExternal) // convert to SingleFeatureExpr
                .collect(Collectors.toSet());
    }

    public static Set<SingleFeatureExpr> getInterestingFeatures(String dimacsFilePath) {
        return getInterestingFeatures(Source.fromFile(dimacsFilePath, "UTF-8"));
    }

    public TypeChefConfiguration getSatisfyingConfiguration(String dimacsFilePath, boolean preferDisabledFeatures) {
        if (!isPresent())
            return TypeChefConfiguration.NOT_FOUND;

        Source dimacsFileSource = Source.fromFile(dimacsFilePath, "UTF-8");
        FeatureModel featureModel = featureModelFactory.createFromDimacsFile(dimacsFileSource, translateNames, autoAddVariables);
        Set<SingleFeatureExpr> interestingFeatures = getInterestingFeatures(dimacsFileSource.reset());

        Option<Tuple2<List<SingleFeatureExpr>, List<SingleFeatureExpr>>> satisfiableAssignment =
                getFeatureExpr().getSatisfiableAssignment(featureModel,
                        JavaConverters.asScalaSet(interestingFeatures).toSet(), preferDisabledFeatures);

        return !satisfiableAssignment.isEmpty()
                ? new TypeChefConfiguration(satisfiableAssignment.get())
                : TypeChefConfiguration.NOT_FOUND;
    }

    public TypeChefConfiguration getSatisfyingConfiguration(String dimacsFilePath) {
        return getSatisfyingConfiguration(dimacsFilePath, true);
    }

    public ConfigurationSpace getSatisfyingConfigurationSpace(String dimacsFilePath, String timeLimit) {
        return new TypeChefConfigurationSpace(this, dimacsFilePath, timeLimit);
    }

    public static PresenceCondition fromFeatureExpr(FeatureExpr featureExpr) {
        return new TypeChefPresenceCondition(featureExpr);
    }

    public PresenceCondition not() {
        if (!isPresent())
            throw new RuntimeException("can not \"not\" constrain a PC which is not present");

        return fromFeatureExpr(getFeatureExpr().not());
    }

    public PresenceCondition and(PresenceCondition presenceCondition) {
        if (!isPresent() && !presenceCondition.isPresent())
            return NOT_FOUND;
        else if (!isPresent())
            return presenceCondition;
        else if (!presenceCondition.isPresent())
            return this;

        return fromFeatureExpr(getFeatureExpr().and(presenceCondition.getFeatureExpr()));
    }
}
