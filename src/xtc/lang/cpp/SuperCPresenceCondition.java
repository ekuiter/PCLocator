package xtc.lang.cpp;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import net.sf.javabdd.BDD;
import xtc.lang.cpp.PresenceConditionManager.PresenceCondition;

import java.util.List;

/**
 * A presence condition for a line of code.
 */
public class SuperCPresenceCondition extends de.ovgu.spldev.pclocator.PresenceCondition {
    private PresenceCondition presenceCondition;
    private PresenceConditionManager presenceConditionManager;
    private boolean isBoolean = true;

    public SuperCPresenceCondition(PresenceCondition presenceCondition, PresenceConditionManager presenceConditionManager) {
        this.presenceCondition = presenceCondition;
        this.presenceConditionManager = presenceConditionManager;
    }

    public String toString() {
        return presenceCondition != null
                ? presenceCondition.toString().replace("defined", "").replace(" ", "")
                : "unknown";
    }

    public boolean isPresent() {
        return presenceCondition != null;
    }

    public boolean isBoolean() {
        getFeatureExpr();
        return isBoolean;
    }

    protected FeatureExpr getFeatureExpr() {
        if (presenceCondition == null)
            return null;

        BDD bdd = presenceCondition.getBDD();
        PresenceConditionManager.Variables vars = presenceConditionManager.getVariableManager();

        if (bdd.isOne())
            return FeatureExprFactory.True();
        else if (bdd.isZero())
            return FeatureExprFactory.False();

        List allsat = (List) bdd.allsat();
        FeatureExpr featureExpr = FeatureExprFactory.False();

        for (Object o : allsat) {
            byte[] sat = (byte[]) o;
            FeatureExpr subExpr = FeatureExprFactory.True();

            for (int i = 0; i < sat.length; i++)
                if (sat[i] >= 0) { // -1 = don't care
                    // SuperC wraps all variables in (defined ...) - remove with regex (ugly, but works).
                    String varName = null;
                    if (vars.getName(i).startsWith("(defined "))
                        varName = vars.getName(i).replaceFirst("\\(defined (.*)\\)", "$1");
                    else {
                        // this presence condition does not solely consist of Boolean features (defined ...)

                        isBoolean = false;
                        continue;
                    }
                    FeatureExpr varExpr = FeatureExprFactory.createDefinedExternal(varName);
                    subExpr = subExpr.and(sat[i] == 0 ? varExpr.not() : varExpr);
                }

            featureExpr = featureExpr.or(subExpr);
        }

        return featureExpr;
    }
}