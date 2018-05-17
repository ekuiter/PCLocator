package de.ovgu.spldev.pclocator;

import java.io.OutputStream;
import java.io.PrintStream;

public class SilentStream extends PrintStream {
    public SilentStream() {
        super(new OutputStream() {
            public void write(int b) {
            }
        });
    }
}