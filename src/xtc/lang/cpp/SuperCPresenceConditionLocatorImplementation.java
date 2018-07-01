package xtc.lang.cpp;

import de.ovgu.spldev.pclocator.LineSupplier;
import de.ovgu.spldev.pclocator.PresenceCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Does not actually parse the C code, only uses configuration-preserving preprocessing.
 */
public class SuperCPresenceConditionLocatorImplementation extends LegacySuperCPresenceConditionLocatorImplementation {
    private LineSupplier lineSupplier;
    private int line;

    public String getName() {
        return "SuperC";
    }

    void locatePresenceConditionRevised(LinkedList<PresenceConditionManager.PresenceCondition> parents, Syntax syntax, PresenceConditionManager presenceConditionManager) {
        if (line == -1)
            return;

        // Just as with TypeChef, we advance the token stream and the lines
        // simultaneously to avoid quadratic complexity.
        if (syntax.hasLocation() && syntax.getLocation().file.equals(_filePath.toString())) {
            line = lineSupplier.catchUp(syntax.getLocation().line);
            if (syntax.getLocation().line == line) {
                PresenceConditionManager.PresenceCondition presenceCondition = presenceConditionManager.new PresenceCondition(true);
                for (PresenceConditionManager.PresenceCondition parent : parents)
                    presenceCondition = presenceCondition.and(parent);
                putPresenceCondition(line, presenceCondition, presenceConditionManager);
                line = lineSupplier.next();
            }
        }
    }

    protected ArrayList<String> buildArgs(int[] lines) {
        ArrayList<String> args = new ArrayList<>();
        // Configuration-preserving preprocessing is sufficient for the challenge!
        args.add("-E");
        args.addAll(super.buildArgs(lines));
        return args;
    }

    public HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines) {
        lineSupplier = new LineSupplier(lines, null);
        line = lineSupplier.next();
        return super.locatePresenceConditions(filePath, lines);
    }
}
