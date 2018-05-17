package de.ovgu.spldev.pclocator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class AnnotatedFile {
    public interface FileAnnotator<T> {
        String getName();
        Map<Integer, T> annotate(String filePath);
        Measurement getLastMeasurement();
    }

    String filePath;
    ArrayList<FileAnnotator> fileAnnotators = new ArrayList<>();
    HashMap<FileAnnotator, Map<Integer, Object>> annotations = null;
    HashMap<FileAnnotator, Measurement> measurements = null;
    public static int columnWidth = 30;

    AnnotatedFile(String filePath) {
        this.filePath = PresenceConditionLocator.validateFilePath(filePath);
    }

    public AnnotatedFile addFileAnnotator(FileAnnotator fileAnnotator) {
        if (annotations != null)
            throw new RuntimeException("file has already been annotated");
        fileAnnotators.add(fileAnnotator);
        return this;
    }

    public void annotate() {
        if (annotations == null) {
            annotations = new HashMap<>();
            measurements = new HashMap<>();

            for (FileAnnotator fileAnnotator : fileAnnotators) {
                try {
                    annotations.put(fileAnnotator, fileAnnotator.annotate(filePath));
                    measurements.put(fileAnnotator, fileAnnotator.getLastMeasurement());
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }
    }

    private void printDivider(boolean header) {
        StringBuffer s1 = new StringBuffer(), s2 = new StringBuffer();
        for (FileAnnotator fileAnnotator : fileAnnotators) {
            s1.append(String.format("%-30s | ", fileAnnotator.getName()));
            s2.append("--------------------------------+");
        }
        if (header)
            System.out.format("%4s | %-50s | %s\n", "#", "line of code", s1);
        System.out.format("-----+----------------------------------------------------+%s\n", s2);
    }

    public void print() {
        annotate();

        try (Stream<String> lineContentsStream = Files.lines(Paths.get(filePath))) {
            String[] lineContents = lineContentsStream.toArray(String[]::new);
            printDivider(true);

            for (int line = 1; line <= lineContents.length; line++) {
                String lineContent = lineContents[line - 1].replace('\t', ' ');
                StringBuffer s = new StringBuffer();
                for (FileAnnotator fileAnnotator : fileAnnotators) {
                    String annotationString;
                    try {
                        annotationString =
                                annotations.get(fileAnnotator) != null
                                        ? annotations.get(fileAnnotator).get(line).toString()
                                        : "";
                    } catch (Exception e) {
                        annotationString = e.toString();
                    }
                    s.append(String.format("%-30s | ",
                            annotationString.substring(0, Math.min(30, annotationString.length())).replace('\n', ' ')));
                }
                System.out.format("%4d | %-50s | %s\n", line,
                        lineContent.substring(0, Math.min(50, lineContent.length())), s);
            }

            printDivider(false);
            StringBuffer s = new StringBuffer();
            for (FileAnnotator fileAnnotator : fileAnnotators) {
                String measurementString =
                        measurements.get(fileAnnotator) != null ? measurements.get(fileAnnotator).toString() :
                        annotations.get(fileAnnotator) != null ? "" : "FAILED";
                s.append(String.format("%-30s | ",
                        measurementString.substring(0, Math.min(30, measurementString.length()))));
            }
            System.out.format("%4s | %-50s | %s\n", "", "", s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
