package de.ovgu.spldev.pclocator;

import java.io.File;
import java.io.IOException;

public class Evaluator {
    private final static String CC = "gcc";

    private void preprocess(PresenceConditionLocator presenceConditionLocator, Location location, Configuration configuration) {
        String directory = "challenge/" + presenceConditionLocator.getName();
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

    public void run(PresenceConditionLocator presenceConditionLocator, Location location, String dimacsFilePath) {
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
            preprocess(presenceConditionLocator, location, configuration);

        Measurement measurement = new Measurement().difference(begin);
        int Nall = 1;
        int Nmissed = configuration == null ? 1 : 0;
        String Ncorrect = configuration == null ? "0" : "?", Nwrong = configuration == null ? "0" : "?"; // has to be determined manually
        System.out.println(name + "," + location + "," + measurement.time + "," + Nall + "," + Nmissed + "," + Ncorrect + "," + Nwrong);
    }
}
