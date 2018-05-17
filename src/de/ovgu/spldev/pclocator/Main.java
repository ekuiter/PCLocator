package de.ovgu.spldev.pclocator;

public class Main {
    public static void main(String[] args) {
        try {
            new PresenceConditionLocatorShell().run(args);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
