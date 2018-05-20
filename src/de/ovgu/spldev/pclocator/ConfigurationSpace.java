package de.ovgu.spldev.pclocator;

import java.util.Collections;
import java.util.Iterator;

public abstract class ConfigurationSpace implements Iterable<Configuration> {
    protected String dimacsFilePath;

    public ConfigurationSpace(String dimacsFilePath) {
        this.dimacsFilePath = dimacsFilePath;
    }

    abstract public Iterator<Configuration> iterator();

    public static ConfigurationSpace getNotFound(PresenceCondition presenceCondition) {
        ConfigurationSpace notFound = new ConfigurationSpace(null) {
            public Iterator<Configuration> iterator() {
                return Collections.emptyIterator();
            }

            public String toString() {
                return "?";
            }

            public void print() {
                Log.error("no configuration space found");
            }
        };
        Log.history(notFound)
                .add("No configuration space found. " +
                        "This is because no presence condition has been located. " +
                        "Below is some context for how the presence condition has been located: ")
                .reference(presenceCondition);
        return notFound;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Configuration configuration : this) {
            s.append(configuration).append('\n');
            if (s.length() > AnnotatedFile.columnWidth)
                break;
        }
        return s.toString().trim();
    }

    public void print(boolean isExplain) {
        if (isExplain)
            System.out.println(history());
        else
            for (Configuration configuration : this)
                System.out.println(configuration);
    }

    public Log.History history() {
        return Log.history(this);
    }
}
