package de.ovgu.spldev.pclocator;

import java.io.PrintStream;

public class TrackErrorStream extends PrintStream {
    boolean seenError = false;


    public TrackErrorStream(PrintStream defaultErr) {
        super(defaultErr);
    }

    public void print(boolean b) {
        super.print(b);
        seenError(b);
    }
    
    public void print(char c) {
        super.print(c);
        seenError(c);
    }

    public void print(int i) {
        super.print(i);
        seenError(i);
    }

    public void print(long l) {
        super.print(l);
        seenError(l);
    }

    public void print(float f) {
        super.print(f);
        seenError(f);
    }

    public void print(double d) {
        super.print(d);
        seenError(d);
    }

    public void print(char[] s) {
        super.print(s);
        seenError(s);
    }

    public void print(String s) {
        super.print(s);
        seenError(s);

    }

    public void print(Object obj) {
        super.print(obj);
        seenError(obj);

    }
    
    public PrintStream append(CharSequence csq, int start, int end) {
        seenError(csq);
        return super.append(csq, start, end);
    }

    private void seenError(Object obj) {
        seenError = true;
    }

    public boolean seenError() {
        return seenError;
    }
}