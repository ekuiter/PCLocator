package de.ovgu.spldev.pclocator;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;

/**
 * A feature expression for a line of code.
 */
public class TypeChefPresenceCondition extends PresenceCondition {
    private FeatureExpr featureExpr;

    public static TypeChefPresenceCondition TRUE = new TypeChefPresenceCondition(FeatureExprFactory.True());
    public static TypeChefPresenceCondition FALSE = new TypeChefPresenceCondition(FeatureExprFactory.False());

    public TypeChefPresenceCondition(FeatureExpr featureExpr) {
        this.featureExpr = featureExpr;
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
}
