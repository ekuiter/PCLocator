package de.ovgu.spldev.pclocator;

public class Main {
    public static void main(String[] args) {
        try {
            new Shell().run(args);
        } catch (Exception e) {
            Log.error("%s", e);
            System.exit(1);
        }
    }
}
