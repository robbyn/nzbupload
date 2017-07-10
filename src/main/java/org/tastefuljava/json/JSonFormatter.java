package org.tastefuljava.json;

import java.io.Closeable;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class JSonFormatter implements JSonHandler, Closeable {
    private static final char HEX[] = "0123456789ABCDEF".toCharArray();

    private DecimalFormat decimalFormat;
    private final PrintWriter out;
    private final boolean format;
    private boolean bol;
    private boolean boa;
    private boolean boo;
    private int level = 0;

    public JSonFormatter(PrintWriter out, boolean format) {
        this.out = out;
        this.format = format;
    }

    @Override
    public void close() {
        out.close();
    }

    @Override
    public void startObject() {
        indent();
        out.print('{');
        println();
        boo = true;
        ++level;
    }

    @Override
    public void endObject() {
        --level;
        println();
        indent();
        out.print('}');
    }

    @Override
    public void startField(String name) {
        if (!boo) {
            out.print(',');
            println();
        }
        indent();
        handleString(name);
        out.print(':');
    }

    @Override
    public void endField(String name) {
        boo = false;
    }

    @Override
    public void startArray() {
        indent();
        out.print('[');
        println();
        boa = true;
        ++level;
    }

    @Override
    public void endArray() {
        --level;
        println();
        indent();
        out.print(']');
    }

    @Override
    public void startElement() {
        if (!boa) {
            out.print(',');
            println();
        }
    }

    @Override
    public void endElement() {
        boa = false;
    }

    @Override
    public void handleNull() {
        indent();
        out.print("null");
    }

    @Override
    public void handleBoolean(boolean value) {
        indent();
        out.print(value ? "true" : "false");
    }

    @Override
    public void handleNumber(double value) {
        indent();
        out.print(getDecimalFormat().format(value));
    }

    @Override
    public void handleString(String value) {
        indent();
        out.print('"');
        for (char c: value.toCharArray()) {
            switch (c) {
                case '\\':
                    out.print("\\\\");
                    break;
                case '\"':
                    out.print("\\\"");
                    break;
                case '\b':
                    out.print("\\b");
                    break;
                case '\f':
                    out.print("\\f");
                    break;
                case '\n':
                    out.print("\\n");
                    break;
                case '\r':
                    out.print("\\r");
                    break;
                case '\t':
                    out.print("\\t");
                    break;
                default:
                    if (c >= 32 && c <= 127) {
                        out.print(c);
                    } else {
                        out.print("\\u");
                        printHex(c, 4);
                    }
            }
        }
        out.print('"');
    }

    private DecimalFormat getDecimalFormat() {
        if (decimalFormat == null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            decimalFormat = new DecimalFormat("0.####");
            decimalFormat.setDecimalFormatSymbols(symbols);
        }
        return decimalFormat;
    }

    private void printHex(int value, int digits) {
        char chars[] = new char[digits];
        for (int i = digits; --i >= 0; ) {
            chars[i] = HEX[value % 16];
            value /= 16;
        }
        for (char c: chars) {
            out.print(c);
        }
    }

    private void println() {
        if (format && !bol) {
            out.println();
            bol = true;
        }
    }

    private void indent() {
        if (format && bol) {
            for (int i = 0; i < level; ++i) {
                out.print("    ");
            }
            bol = level == 0;
        }
    }
}
