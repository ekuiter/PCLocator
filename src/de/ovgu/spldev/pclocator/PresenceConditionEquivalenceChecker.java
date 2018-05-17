package de.ovgu.spldev.pclocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PresenceConditionEquivalenceChecker implements AnnotatedFile.FileAnnotator {
    private String name;
    private SimplePresenceConditionLocator presenceConditionLocatorA, presenceConditionLocatorB;

    PresenceConditionEquivalenceChecker(String name,
                                        SimplePresenceConditionLocator presenceConditionLocatorA,
                                        SimplePresenceConditionLocator presenceConditionLocatorB) {
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
            int line = entry.getKey();
            PresenceCondition presenceConditionA = entry.getValue(),
                    presenceConditionB = locatedPresenceConditionsB.get(line);
            ArrayList<String> messages = new ArrayList<>();
            if (!presenceConditionA.equivalentTo(presenceConditionB))
                messages.add("not equivalent");
            if (!presenceConditionA.isBoolean() || !presenceConditionB.isBoolean())
                messages.add("not Boolean");
            annotations.put(line, String.join(", ", messages));
        }

        return annotations;
    }

    public Measurement getLastMeasurement() {
        return null;
    }
}
