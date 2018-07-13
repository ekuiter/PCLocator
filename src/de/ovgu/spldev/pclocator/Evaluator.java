package de.ovgu.spldev.pclocator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Evaluator {
    private final static String CC = "gcc";
    private static Set<Integer> mergeEquivalent = new HashSet<>(), mergeSuperCSubSpace = new HashSet<>(),
            mergeTypeChefSubSpace = new HashSet<>(), mergeDisjointOrOverlapping = new HashSet<>();

    public static void addMergeEquivalent(int line) {
        mergeEquivalent.add(line);
    }

    public static void addMergeSuperCSubSpace(int line) {
        mergeSuperCSubSpace.add(line);
    }

    public static void addMergeTypeChefSubSpace(int line) {
        mergeTypeChefSubSpace.add(line);
    }

    public static void addMergeDisjointOrOverlapping(int line) {
        mergeDisjointOrOverlapping.add(line);
    }

    private void preprocess(PresenceConditionLocator presenceConditionLocator, Location location, Configuration configuration, boolean isLegacy, PresenceConditionLocator.Options options) {
        Path path = Paths.get(location.getFilePath()),
                originalDirectory = Paths.get("challenge/original_" + (isLegacy ? "legacy_" : "") + presenceConditionLocator.getName())
                        .resolve(path.getParent()),
                preprocessedDirectory = Paths.get("challenge/" + (isLegacy ? "legacy_" : "") + presenceConditionLocator.getName())
                        .resolve(path.getParent());
        String originalFileName = originalDirectory.resolve(path.getFileName().toString().replaceAll("\\.c$", "")) + "_" + location.getLine() + ".c",
                preprocessedFileName = preprocessedDirectory.resolve(path.getFileName().toString().replaceAll("\\.c$", "")) + "_" + location.getLine() + ".c";
        originalDirectory.toFile().mkdirs();
        preprocessedDirectory.toFile().mkdirs();

        // Assuming a shell environment with gcc for evaluation.
        ArrayList<String> command = new ArrayList<>();
        command.add("cp");
        command.add("\"" + location.getFilePath() + "\"");
        command.add("\"" + originalFileName + "\"");
        command.add("&&");
        command.add(CC);
        if (options.getIncludeDirectories() != null)
            for (String includeDirectory : options.getIncludeDirectories()) {
                command.add("-I");
                command.add(includeDirectory);
            }
        if (options.getPlatformHeaderFilePath() != null) {
            command.add("-include");
            command.add(options.getPlatformHeaderFilePath());
        }
        command.add("-E");
        command.add(configuration.toFlagsString());
        command.add("\"" + location.getFilePath() + "\"");
        command.add(">");
        command.add("\"" + preprocessedFileName + "\"");

        try {
            System.out.println(String.join(" ", command));
            Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", String.join(" ", command)});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int hasLocation(Set<Integer> set, Location location) {
        return set.contains(location.getLine()) ? 1 : 0;
    }

    public void run(PresenceConditionLocator presenceConditionLocator, Location location, String dimacsFilePath, boolean isLegacy, PresenceConditionLocator.Options options) {
        String name = presenceConditionLocator.getName();
        PresenceCondition presenceCondition = null;
        Configuration configuration = null;
        Measurement begin = new Measurement();

        try {
            presenceCondition = presenceConditionLocator.locatePresenceCondition(location);
        } catch (Exception e) {
            Log.error("could not locate presence condition for %s: %s", name, e);
        }

        if (presenceCondition != null)
            try {
                ConfigurationSpace configurationSpace = presenceCondition.getSatisfyingConfigurationSpace(dimacsFilePath, 1, null);
                for (Configuration _configuration : configurationSpace)
                    configuration = _configuration;
            } catch (Exception e) {
                Log.error("could not locate satisfying configuration for %s: %s", name, e);
            }

        if (configuration != null)
            preprocess(presenceConditionLocator, location, configuration, isLegacy, options);

        Measurement measurement = new Measurement().difference(begin);
        int Nall = 1;
        int Nmissed = configuration == null ? 1 : 0;
        String Ncorrect = configuration == null ? "0" : "?", Nwrong = configuration == null ? "0" : "?"; // has to be determined manually
        int isTrue = presenceCondition == null || (!presenceCondition.toString().equals("True") && !presenceCondition.toString().equals("1")) ? 0 : 1;

        // locator,location,time,Nall,Nmissed,Ncorrect,Nwrong,mergeEquivalent,mergeSuperCSubSpace,mergeTypeChefSubSpace,mergeDisjointOrOverlapping,isTrue
        System.out.println(name + "," + location + "," + measurement.time + "," + Nall + "," + Nmissed + "," + Ncorrect + "," + Nwrong +
                "," + hasLocation(mergeEquivalent, location) + "," + hasLocation(mergeSuperCSubSpace, location) + "," +
                hasLocation(mergeTypeChefSubSpace, location) + "," + hasLocation(mergeDisjointOrOverlapping, location) + "," + isTrue);
    }
}
