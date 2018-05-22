package de.ovgu.spldev.pclocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PresenceConditionEquivalenceChecker implements AnnotatedFile.FileAnnotator {
    private String name;
    private PresenceConditionLocator presenceConditionLocatorA, presenceConditionLocatorB;

    PresenceConditionEquivalenceChecker(String name,
                                        PresenceConditionLocator presenceConditionLocatorA,
                                        PresenceConditionLocator presenceConditionLocatorB) {
        this.name = name;
        this.presenceConditionLocatorA = presenceConditionLocatorA;
        this.presenceConditionLocatorB = presenceConditionLocatorB;
    }

    public String getName() {
        return name;
    }

    public HashMap<Integer, String> annotate(String filePath) {
        HashMap<Integer, PresenceCondition>
                locatedPresenceConditionsA = presenceConditionLocatorA.annotate(filePath),
                locatedPresenceConditionsB = presenceConditionLocatorB.annotate(filePath);
        HashMap<Integer, String> annotations = new HashMap<>();

        for (Map.Entry<Integer, PresenceCondition> entry : locatedPresenceConditionsA.entrySet()) {
            ArrayList<String> messages = new ArrayList<>();
            int line = entry.getKey();
            PresenceCondition rawPresenceConditionA = entry.getValue(),
                    rawPresenceConditionB = locatedPresenceConditionsB.get(line);
            if (!rawPresenceConditionA.isPresent() && !rawPresenceConditionB.isPresent()) ;
            else if (rawPresenceConditionA.isPresent() ^ rawPresenceConditionB.isPresent())
                messages.add("not equivalent");
            else if (!(entry.getValue() instanceof TypeChefPresenceCondition) ||
                    !(locatedPresenceConditionsB.get(line) instanceof TypeChefPresenceCondition))
                messages.add("can not compare");
            else {
                TypeChefPresenceCondition presenceConditionA = (TypeChefPresenceCondition) rawPresenceConditionA,
                        presenceConditionB = (TypeChefPresenceCondition) rawPresenceConditionB;
                if (!presenceConditionA.equivalentTo(presenceConditionB))
                    messages.add("not equivalent");
            }
            if (rawPresenceConditionA instanceof TypeChefPresenceCondition && !((TypeChefPresenceCondition) rawPresenceConditionA).isBoolean() ||
                    rawPresenceConditionB instanceof TypeChefPresenceCondition && !((TypeChefPresenceCondition) rawPresenceConditionB).isBoolean())
                messages.add("not Boolean");
            annotations.put(line, String.join(", ", messages));
        }

        return annotations;
    }

    public Measurement getLastMeasurement() {
        return null;
    }
}
