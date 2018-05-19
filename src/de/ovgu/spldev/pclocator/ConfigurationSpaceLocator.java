package de.ovgu.spldev.pclocator;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationSpaceLocator implements AnnotatedFile.FileAnnotator {
    private SimplePresenceConditionLocator presenceConditionLocator;
    private String dimacsFilePath, timeLimit;
    protected Measurement _lastMeasurement;

    public ConfigurationSpaceLocator(SimplePresenceConditionLocator presenceConditionLocator, String dimacsFilePath, String timeLimit) {
        this.presenceConditionLocator = presenceConditionLocator;
        this.dimacsFilePath = dimacsFilePath;
        this.timeLimit = timeLimit;
    }

    public String getName() {
        return presenceConditionLocator.getName() + " config";
    }

    public HashMap<Integer, ConfigurationSpace> annotate(String filePath) {
        HashMap<Integer, ConfigurationSpace> locatedConfigurations = new HashMap<>();

        Measurement begin = new Measurement();
        HashMap<Integer, PresenceCondition> locatedPresenceConditions = presenceConditionLocator.annotate(filePath);
        for (Map.Entry<Integer, PresenceCondition> entry : locatedPresenceConditions.entrySet())
            locatedConfigurations.put(entry.getKey(), entry.getValue().getSatisfyingConfigurationSpace(dimacsFilePath, timeLimit));
        _lastMeasurement = new Measurement().difference(begin);

        return locatedConfigurations;
    }

    public Measurement getLastMeasurement() {
        return _lastMeasurement;
    }
}
