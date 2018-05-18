package de.ovgu.spldev.pclocator;

import de.ovgu.spldev.featurecopp.splmodel.FeatureModule;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeatureCoPPConfiguration extends Configuration {
    HashMap<String, String> macroTable;
    HashMap<String, Boolean> booleanTable = new HashMap<>();

    public FeatureCoPPConfiguration(HashMap<String, IntVar> macros) {
        macroTable = FeatureModule.FeatureOccurrence.makeMacroTable(macros);
        macros.forEach((feature, var) -> booleanTable.put(feature, var.isBool()));
    }

    public String toString() {
        ArrayList<String> exprs = new ArrayList<>();
        for (Map.Entry<String, String> entry : macroTable.entrySet())
            exprs.add(booleanTable.get(entry.getKey()) ? entry.getKey() : entry.toString());
        return "[" + String.join(", ", exprs) + "]";
    }
}
