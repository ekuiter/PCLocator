package de.ovgu.spldev.pclocator;

import java.io.File;
import java.io.IOException;
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

    private void preprocess(PresenceConditionLocator presenceConditionLocator, Location location, Configuration configuration, boolean isLegacy) {
        // Assuming a shell environment with gcc for evaluation.
        String directory = "challenge/" + (isLegacy ? "legacy_" : "") + presenceConditionLocator.getName();
        new File(directory).mkdirs();
        String command = CC + " -E " + configuration + " " + location.getFilePath() +
                " > " + directory + "/" + location.toString().replaceAll("[/:]", "_") + ".c";
        String[] shell = {"/bin/sh", "-c", command};

        try {
            Runtime.getRuntime().exec(shell);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int hasLocation(Set<Integer> set, Location location) {
        return set.contains(location.getLine()) ? 1 : 0;
    }

    public void run(PresenceConditionLocator presenceConditionLocator, Location location, String dimacsFilePath, boolean isLegacy) {
        String name = presenceConditionLocator.getName();
        PresenceCondition presenceCondition = null;
        Configuration configuration = null;
        Configuration.setFormatKind("flags");
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
            preprocess(presenceConditionLocator, location, configuration, isLegacy);

        Measurement measurement = new Measurement().difference(begin);
        int Nall = 1;
        int Nmissed = configuration == null ? 1 : 0;
        String Ncorrect = configuration == null ? "0" : "?", Nwrong = configuration == null ? "0" : "?"; // has to be determined manually

        // locator,location,time,Nall,Nmissed,Ncorrect,Nwrong,mergeEquivalent,mergeSuperCSubSpace,mergeTypeChefSubSpace,mergeDisjointOrOverlapping
        System.out.println(name + "," + location + "," + measurement.time + "," + Nall + "," + Nmissed + "," + Ncorrect + "," + Nwrong +
                "," + hasLocation(mergeEquivalent, location) + "," + hasLocation(mergeSuperCSubSpace, location) + "," +
                hasLocation(mergeTypeChefSubSpace, location) + "," + hasLocation(mergeDisjointOrOverlapping, location));
    }
}
