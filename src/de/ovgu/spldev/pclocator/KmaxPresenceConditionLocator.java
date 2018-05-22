package de.ovgu.spldev.pclocator;

import java.util.HashMap;
import java.util.Map;

public class KmaxPresenceConditionLocator extends DeduceNotFoundPresenceConditionLocator {
    KmaxFileGrepper kmaxFileGrepper;

    public String getName() {
        return super.getName() + " + Kmax";
    }

    KmaxPresenceConditionLocator(Implementation implementation, Options options, KmaxFileGrepper kmaxFileGrepper) {
        super(implementation, options);
        this.kmaxFileGrepper = kmaxFileGrepper;
    }

    protected HashMap<Integer, PresenceCondition> modifyPresenceConditions
            (HashMap<Integer, PresenceCondition> locatedPresenceConditions, String[] lineContents) {
        locatedPresenceConditions = super.modifyPresenceConditions(locatedPresenceConditions, lineContents);

        for (Map.Entry<Integer, PresenceCondition> entry : locatedPresenceConditions.entrySet()) {
            PresenceCondition modifiedPresenceCondition = kmaxFileGrepper.modifyPresenceCondition(entry.getValue());
            modifiedPresenceCondition.history()
                    .include(entry.getValue())
                    .include(kmaxFileGrepper)
                    .add("This presence condition combines the presence condition from the parser above " +
                            "and the presence condition located by Kmax.");
            locatedPresenceConditions.put(entry.getKey(), modifiedPresenceCondition);
        }

        return locatedPresenceConditions;
    }
}
