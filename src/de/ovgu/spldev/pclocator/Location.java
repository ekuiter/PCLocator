package de.ovgu.spldev.pclocator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class Location {
    String filePath;
    int line;

    private void initialize(String filePath, String lineString) {
        validateFilePath(filePath);
        this.filePath = filePath;
        try {
            this.line = Integer.parseInt(lineString);
        } catch (NumberFormatException e) {
            throw new RuntimeException("\"" + lineString + "\" is not a valid line number");
        }
        validateLine(filePath, line);
    }

    public Location(String filePath, int line) {
        validateFilePath(filePath);
        validateLine(filePath, line);
        this.filePath = filePath;
        this.line = line;
    }

    public Location(String filePath, String lineString) {
        initialize(filePath, lineString);
    }

    public Location(String location) {
        String[] parts = getLocationParts(location);
        initialize(String.join(":", Arrays.copyOf(parts, parts.length - 1)), parts[parts.length - 1]);
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

    public String getFilePath() {
        return filePath;
    }

    public int getLine() {
        return line;
    }

    public String toString() {
        return filePath + ":" + line;
    }
}
