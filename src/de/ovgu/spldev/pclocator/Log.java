package de.ovgu.spldev.pclocator;

import org.apache.commons.lang3.text.WordUtils;

import java.io.PrintStream;
import java.util.*;

public class Log {
    static ArrayList<Entry> entries = new ArrayList<>();
    static HashMap<Object, History> allHistory = new HashMap<>();
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

    public static class History implements Iterable<String> {
        Object key;
        ArrayList<Object> objects = new ArrayList<>();

        static class ReferenceBegin {}
        static class ReferenceEnd {}

        History(Object key) {
            this.key = key;
        }

        // adds a String entry to the history
        public History add(String format, Object... args) {
            if (format == null)
                return this;
            objects.add(String.format(format, (Object[]) args));
            return this;
        }

        // includes another object's complete history
        public History include(Object key) {
            if (key == null)
                return this;
            if (key instanceof String)
                throw new RuntimeException("including a string's history is not allowed");
            objects.add(history(key));
            return this;
        }

        // references another object's complete history (with indent)
        public History reference(Object key) {
            if (key == null)
                return this;
            if (key instanceof String)
                throw new RuntimeException("referring to a string's history is not allowed");
            objects.add(new ReferenceBegin());
            objects.add(history(key));
            objects.add(new ReferenceEnd());
            return this;
        }

        // adopted from https://codereview.stackexchange.com/q/32827
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                Stack<Iterator<Object>> iteratorStack = new Stack<>();
                String nextHistory;
                boolean nextValid = false;

                {
                    iteratorStack.push(objects.iterator());
                }

                private boolean moveToNext() {
                    while (!iteratorStack.empty()) {
                        if (!iteratorStack.peek().hasNext()) {
                            iteratorStack.pop();
                            continue;
                        }

                        Object next = iteratorStack.peek().next();

                        if (next instanceof String) {
                            nextHistory = (String) next;
                            return true;
                        } else if (next instanceof History)
                            iteratorStack.push(((Iterable) next).iterator());
                        else if (next instanceof ReferenceBegin || next instanceof ReferenceEnd) ;
                        else
                            throw new RuntimeException("invalid history type " + next.getClass());
                    }
                    return false;
                }

                public boolean hasNext() {
                    if (!nextValid) nextValid = moveToNext();
                    return nextValid;
                }

                public String next() {
                    if (!hasNext())
                        throw new NoSuchElementException();
                    nextValid = false;
                    return key + ": " + nextHistory;
                }
            };
        }

        protected String getHeader(int level) {
            return "";
        }

        protected ArrayList<Object> getObjects(int level) {
            return objects;
        }

        protected String getFooter(int level) {
            return "";
        }

        String indent(int level) {
            return String.join("", Collections.nCopies(level, " |  "));
        }

        // recursively builds an explanation string
        protected String toString(int level, boolean isReferenced) {
            StringBuilder str = new StringBuilder(isReferenced ? getHeader(level) : "");
            boolean isReferencing = false;
            String currentIndent = indent(level);

            for (Object obj : getObjects(level)) {
                if (obj instanceof String) {
                    String entry = String.join(
                            "\n" + currentIndent,
                            WordUtils.wrap(obj.toString(), 80 - currentIndent.length()).split("\n"));
                    str.append(currentIndent).append(key).append("\n")
                            .append(currentIndent).append(entry).append("\n\n");
                } else if (obj instanceof History)
                    str.append(((History) obj).toString(level + (isReferencing ? 1 : 0), isReferencing));
                else if (obj instanceof ReferenceBegin)
                    isReferencing = true;
                else if (obj instanceof ReferenceEnd)
                    isReferencing = false;
                else
                    throw new RuntimeException("invalid history type " + obj.getClass());
            }

            return str.append(isReferenced ? getFooter(level) : "").toString();
        }

        public String toString() {
            return toString(0, true).trim();
        }
    }

    public static class PresenceConditionHistory extends History {
        Integer line;

        PresenceConditionHistory(Object key) {
            super(key);
        }

        PresenceConditionHistory(Object key, Integer line) {
            super(key);
            this.line = line;
        }

        protected String getHeader(int level) {
            if (line != null)
                return String.format("%sLocating presence condition in line %d.\n\n", indent(level), line);
            return "";
        }

        protected ArrayList<Object> getObjects(int level) {
            return objects;
        }

        protected String getFooter(int level) {
            String str;
            if (!((PresenceCondition) key).isPresent())
                str = line != null
                        ? "The presence condition in line " + line + " could not be located."
                        : "The presence condition could not be located.";
            else
                str = line != null
                        ? "Located the presence condition " + key + " in line " + line + "."
                        : "Located the presence condition " + key + ".";
            return String.format("%s%s\n\n", indent(level), str);
        }

        public Integer getLine() {
            return line;
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

    public static History history(PresenceCondition key, Integer line) {
        return allHistory.computeIfAbsent(key, k -> new PresenceConditionHistory(k, line));
    }

    public static History history(PresenceCondition key) {
        return history(key, null);
    }

    public static History history(Object key) {
        return allHistory.computeIfAbsent(key, k -> new History(k));
    }

    public static void print(PrintStream stream) {
        for (Entry entry : entries)
            entry.print(stream);
    }

    public static void print() {
        print(System.err);
    }
}
