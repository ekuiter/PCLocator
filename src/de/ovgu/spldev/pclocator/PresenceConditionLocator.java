package de.ovgu.spldev.pclocator;

import java.util.HashMap;

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

    public PresenceCondition locatePresenceCondition(Location location) {
        return locatePresenceConditions(location.getFilePath()).get(location.getLine());
    }

    public HashMap<Integer, PresenceCondition> annotate(String filePath) {
        return locatePresenceConditions(filePath);
    }

    abstract protected HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines);
    abstract protected HashMap<Integer, PresenceCondition> locatePresenceConditions(String _filePath);
}
