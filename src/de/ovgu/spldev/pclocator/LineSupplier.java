package de.ovgu.spldev.pclocator;

import java.util.HashMap;

public class LineSupplier {
    private int[] lines;
    HashMap<Integer, PresenceCondition> locatedPresenceConditions;
    private int i = 0, line = -1;

    LineSupplier(int[] lines) {
        this(lines, null);
    }

    public LineSupplier(int[] lines, HashMap<Integer, PresenceCondition> locatedPresenceConditions) {
        this.lines = lines;
        this.locatedPresenceConditions = locatedPresenceConditions;
    }

    public int next() {
        line = i < lines.length ? lines[i++] : -1;
        if (line != -1 && locatedPresenceConditions != null)
            locatedPresenceConditions.put(line, TypeChefPresenceCondition.getNotFound(line));
        return line;
    }

    public int catchUp(int lineInOtherIterator) {
        while (line < lineInOtherIterator)
            if ((line = next()) == -1)
                break;
        return line;
    }
}
