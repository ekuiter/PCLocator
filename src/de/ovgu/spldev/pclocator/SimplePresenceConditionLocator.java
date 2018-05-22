package de.ovgu.spldev.pclocator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SimplePresenceConditionLocator extends PresenceConditionLocator {
    protected Implementation _implementation;
    protected Measurement _lastMeasurement;

    SimplePresenceConditionLocator(Implementation implementation, Options options) {
        _implementation = implementation;
        implementation.setOptions(options);
    }

    public String getName() {
        return _implementation.getName();
    }

    protected boolean lineNotAvailable(String lineContent) {
        return false;
    }

    protected String lineNotAvailableHistory() {
        return null;
    }

    protected HashMap<Integer, PresenceCondition> modifyPresenceConditions
            (HashMap<Integer, PresenceCondition> locatedPresenceConditions, String[] lineContents) {
        return locatedPresenceConditions;
    }

    protected String modifyFilePath(String filePath) {
        return filePath;
    }

    protected void cleanUpFilePath() {
    }

    protected HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines) {
        Measurement begin = new Measurement();
        HashMap<Integer, PresenceCondition> locatedPresenceConditions;
        try {
            locatedPresenceConditions = _implementation.locatePresenceConditions(modifyFilePath(filePath), lines);
        } finally {
            cleanUpFilePath();
        }
        _lastMeasurement = new Measurement().difference(begin);
        return locatedPresenceConditions;
    }

    protected HashMap<Integer, PresenceCondition> locatePresenceConditions(String _filePath) {
        final String filePath = validateFilePath(_filePath);

        try (Stream<String> lineContentsStream = Files.lines(Paths.get(filePath))) {
            String[] lineContents = lineContentsStream.toArray(String[]::new);
            int[] lines = IntStream
                    .rangeClosed(1, lineContents.length)
                    .filter(line -> !lineNotAvailable(lineContents[line - 1]))
                    .toArray();

            HashMap<Integer, PresenceCondition> locatedPresenceConditions =
                    locatePresenceConditions(filePath, lines);

            IntStream
                    .rangeClosed(1, lineContents.length)
                    .filter(line -> lineNotAvailable(lineContents[line - 1]))
                    .forEach(line -> {
                        PresenceCondition notFound = PresenceCondition.getNotFound(line);
                        if (lineNotAvailableHistory() != null)
                            notFound.history().add(lineNotAvailableHistory());
                        locatedPresenceConditions.put(line, notFound);
                    });

            return modifyPresenceConditions(locatedPresenceConditions, lineContents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Measurement getLastMeasurement() {
        return _lastMeasurement;
    }
}