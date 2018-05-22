package de.ovgu.spldev.pclocator;

import java.util.HashMap;

public class LineSupplier {
    private int[] lines;
    HashMap<Integer, PresenceCondition> locatedPresenceConditions;
    private int i = 0;

    LineSupplier(int[] lines, HashMap<Integer, PresenceCondition> locatedPresenceConditions) {
        this.lines = lines;
        this.locatedPresenceConditions = locatedPresenceConditions;
    }

    int next() {
        int line = i < lines.length ? lines[i++] : -1;
        if (line != -1)
            locatedPresenceConditions.put(line, TypeChefPresenceCondition.getNotFound(line));
        return line;
    }
}
