package de.ovgu.spldev.pclocator;

public abstract class Configuration {
    abstract public String toString();

    public Log.History history() {
        return Log.history(this);
    }
}
