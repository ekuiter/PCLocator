package de.ovgu.spldev.pclocator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public abstract class PresenceConditionLocator implements AnnotatedFile.FileAnnotator {
    public interface Implementation {
        /**
         * Locates presence conditions for given lines in a file.
         * The lines are expected to be unique and in ascending order.
         */
        HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines);
        String getName();
        Options getOptions();
        void setOptions(Options options);
        PresenceCondition getTrue();
        // returns at least one DNF from the given formula, at least of the type getTrue() returns
        PresenceCondition[] fromDNF(String formula);
    }

    public static class Options {
        private String[] includeDirectories;
        private String platformHeaderFilePath;

        public Options() {
            this.includeDirectories = new String[]{};
        }

        public Options(String[] includeDirectories, String platformHeaderFilePath) {
            this.includeDirectories = includeDirectories;
            this.platformHeaderFilePath = platformHeaderFilePath;
        }

        public String[] getIncludeDirectories() {
            return includeDirectories;
        }

        public String getPlatformHeaderFilePath() {
            return platformHeaderFilePath;
        }
    }

    protected static String getLineContent(String filePath, int line) {
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

    protected static String validateLine(String filePath, int line) {
        String lineContent = getLineContent(filePath, line);
        if (lineContent == null)
            throw new RuntimeException("\"" + line + "\" is not a valid line number");
        return lineContent;
    }

    public static boolean isValidLocation(String location) {
        return location.split(":").length >= 2;
    }

    protected static String[] getLocationParts(String location) {
        String[] parts = location.split(":");
        if (parts.length < 2)
            throw new RuntimeException("\"" + location + "\" is not a valid location of form <file>:<line>");
        return parts;
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

    public PresenceCondition locatePresenceCondition(String filePath, int line) {
        validateLine(filePath, line);
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

    public PresenceCondition locatePresenceCondition(String location) {
        String[] parts = getLocationParts(location);
        String filePath = String.join(":", Arrays.copyOf(parts, parts.length - 1));
        String lineString = parts[parts.length - 1];

        return locatePresenceCondition(filePath, lineString);
    }

    public HashMap<Integer, PresenceCondition> annotate(String filePath) {
        return locatePresenceConditions(filePath);
    }

    abstract protected HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines);
    abstract protected HashMap<Integer, PresenceCondition> locatePresenceConditions(String _filePath);
}
