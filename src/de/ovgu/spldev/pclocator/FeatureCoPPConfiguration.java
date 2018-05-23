package de.ovgu.spldev.pclocator;

import de.ovgu.spldev.featurecopp.splmodel.FeatureModule;
import org.chocosolver.solver.variables.IntVar;

import java.util.*;
import java.util.stream.Stream;

public class FeatureCoPPConfiguration extends Configuration {
    HashMap<String, String> macroTable;
    HashMap<String, Boolean> booleanTable = new HashMap<>();
    Set<String> interestingFeatureNames;

    public FeatureCoPPConfiguration(HashMap<String, IntVar> macros, Set<String> interestingFeatureNames) {
        macroTable = FeatureModule.FeatureOccurrence.makeMacroTable(macros);
        this.interestingFeatureNames = interestingFeatureNames;
        macros.forEach((feature, var) -> booleanTable.put(feature, var.isBool()));
    }

    private ArrayList<String> getExpressionStrings() {
        ArrayList<String> exprs = new ArrayList<>();
        for (Map.Entry<String, String> entry : macroTable.entrySet())
            exprs.add(booleanTable.get(entry.getKey()) ? entry.getKey() : entry.toString());
        return exprs;
    }

    public String toHumanString() {
        return "[" + String.join(", ", getExpressionStrings()) + "]";
    }

    public String toFlagsString() {
        return joinToFlags(getExpressionStrings().stream());
    }

    public String toConfigString() {
        ArrayList<String> exprs = new ArrayList<>();
        Set<String> disabledFeatures = new HashSet<>(interestingFeatureNames);

        // The macro table contains all features that should be enabled, so disabled features
        // can be inferred by subtracting the enabled features from the interesting features.
        for (Map.Entry<String, String> entry : macroTable.entrySet()) {
            if (booleanTable.get(entry.getKey())) {
                exprs.add(entry.getKey() + "=y");
                disabledFeatures.remove(entry.getKey());
            } else
                exprs.add(entry.toString());
        }

        return String.join("\n", Stream.concat(
                exprs.stream(),
                disabledFeatures.stream().map(feature -> feature + "=n")
        ).toArray(String[]::new)) + "\n";
    }
}
