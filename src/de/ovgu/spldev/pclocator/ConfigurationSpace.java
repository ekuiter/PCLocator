package de.ovgu.spldev.pclocator;

import java.util.Iterator;

public abstract class ConfigurationSpace implements Iterable<Configuration> {
    protected String dimacsFilePath;

    public ConfigurationSpace(String dimacsFilePath) {
        this.dimacsFilePath = dimacsFilePath;
    }

    abstract public Iterator<Configuration> iterator();

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Configuration configuration : this) {
            s.append(configuration).append('\n');
            if (s.length() > AnnotatedFile.columnWidth)
                break;
        }
        return s.toString().trim();
    }

    public void print() {
        for (Configuration configuration : this)
            System.out.println(configuration);
    }
}
