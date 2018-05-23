package de.ovgu.spldev.pclocator;

import java.util.stream.Stream;

public abstract class Configuration {
    static String formatKind;

    public static void setFormatKind(Arguments args) {
        formatKind = args.getFormatKind();
    }

    public String toString() {
        if (formatKind.equals("human"))
            return toHumanString();
        else if (formatKind.equals("flags"))
            return toFlagsString();
        else if (formatKind.equals("config"))
            return toConfigString();
        else
            throw new RuntimeException("unknown format kind " + formatKind);
    }

    protected String joinToFlags(Stream<String> featureNamesStream) {
        return String.join(" ", featureNamesStream.map(feature -> "-D" + feature).toArray(String[]::new));
    }

    abstract public String toHumanString();
    abstract public String toFlagsString();
    abstract public String toConfigString();

    public Log.History history() {
        return Log.history(this);
    }
}
