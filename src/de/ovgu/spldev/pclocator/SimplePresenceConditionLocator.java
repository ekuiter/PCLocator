package de.ovgu.spldev.pclocator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SimplePresenceConditionLocator implements AnnotatedFile.FileAnnotator {
    public interface Implementation {
        /**
         * Locates presence conditions for given lines in a file.
         * The lines are expected to be unique and in ascending order.
         */
        HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines);
        String getName();
        void setOptions(Options options);
    }

    public static class Options {
        private String[] includeDirectories;

        public Options() {
            this.includeDirectories = new String[]{};
        }

        public Options(String[] includeDirectories) {
            this.includeDirectories = includeDirectories;
        }

        public String[] getIncludeDirectories() {
            return includeDirectories;
        }
    }

    protected Implementation _implementation;
    protected Measurement _lastMeasurement;

    SimplePresenceConditionLocator(Implementation implementation, Options options) {
        _implementation = implementation;
        implementation.setOptions(options);
    }

    public String getName() {
        return _implementation.getName();
    }

    private static String getLineContent(String filePath, int line) {
        try (Stream<String> lineContentsStream = Files.lines(Paths.get(filePath))) {
            String[] lineContents = lineContentsStream.toArray(String[]::new);
            if (line < 1 || line > lineContents.length)
                return null;
            else
                return lineContents[line - 1];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String validateFilePath(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile())
            throw new RuntimeException("\"" + filePath + "\" is not a valid file");
        return filePath;
    }

    private static String validateLine(String filePath, int line) {
        String lineContent = getLineContent(filePath, line);
        if (lineContent == null)
            throw new RuntimeException("\"" + line + "\" is not a valid line number");
        return lineContent;
    }

    protected boolean lineNotAvailable(String lineContent) {
        return false;
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
                    .forEach(line -> locatedPresenceConditions.put(line, PresenceCondition.NOT_FOUND));

            return modifyPresenceConditions(locatedPresenceConditions, lineContents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PresenceCondition locatePresenceCondition(String filePath, int line) {
        validateLine(filePath, line);
        int[] lines = new int[]{line};
        return locatePresenceConditions(validateFilePath(filePath)).get(line);
    }

    public PresenceCondition locatePresenceCondition(String filePath, String lineString) {
        int line;
        try {
            line = Integer.parseInt(lineString);
        } catch (NumberFormatException e) {
            throw new RuntimeException("\"" + lineString + "\" is not a valid line number");
        }

        return locatePresenceCondition(filePath, line);
    }

    public static boolean isValidLocation(String location) {
        return location.split(":").length >= 2;
    }

    private static String[] getLocationParts(String location) {
        String[] parts = location.split(":");
        if (parts.length < 2)
            throw new RuntimeException("\"" + location + "\" is not a valid location of form <file>:<line>");
        return parts;
    }

    public PresenceCondition locatePresenceCondition(String location) {
        String[] parts = getLocationParts(location);
        String filePath = String.join(":", Arrays.copyOf(parts, parts.length - 1));
        String lineString = parts[parts.length - 1];

        return locatePresenceCondition(filePath, lineString);
    }

    public static String getFilePathFromLocation(String location) {
        String filePath;
        if (isValidLocation(location)) {
            String[] parts = getLocationParts(location);
            filePath = String.join(":", Arrays.copyOf(parts, parts.length - 1));
        } else
            filePath = location;
        return validateFilePath(filePath);
    }

    public HashMap<Integer, PresenceCondition> annotate(String filePath) {
        return locatePresenceConditions(filePath);
    }

    public Measurement getLastMeasurement() {
        return _lastMeasurement;
    }
}