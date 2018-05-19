package de.ovgu.spldev.pclocator;

import java.io.PrintStream;
import java.util.ArrayList;

public class Log {
    static ArrayList<Entry> entries = new ArrayList<>();
    static final boolean LIVE = true;

    static class Entry {
        String type;
        String format;
        Object[] args;

        Entry(String type, String format, Object[] args) {
            this.type = type;
            this.format = format;
            this.args = args;
        }

        void print(PrintStream stream) {
            stream.format(type + ": %s\n", String.format(format, (Object[]) args));
        }

        void print() {
            print(System.err);
        }
    }

    public static void notice(String format, Object... args) {
        Entry entry = new Entry("NOTICE", format, args);
        if (LIVE)
            entry.print();
        entries.add(entry);
    }

    public static void warning(String format, Object... args) {
        Entry entry = new Entry("WARNING", format, args);
        if (LIVE)
            entry.print();
        entries.add(entry);
    }

    public static void error(String format, Object... args) {
        Entry entry = new Entry("ERROR", format, args);
        if (LIVE)
            entry.print();
        entries.add(entry);
    }

    public static void print(PrintStream stream) {
        for (Entry entry : entries)
            entry.print(stream);
    }

    public static void print() {
        print(System.err);
    }
}
