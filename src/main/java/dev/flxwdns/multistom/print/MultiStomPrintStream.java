package dev.flxwdns.multistom.print;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;

public final class MultiStomPrintStream extends PrintStream {
    public MultiStomPrintStream(@NotNull OutputStream out) {
        super(out);
    }

    /*public MultiStomPrintStream() {
        super(out);
    }*/
}
