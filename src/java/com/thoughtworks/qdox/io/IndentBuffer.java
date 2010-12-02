package com.thoughtworks.qdox.io;

public class IndentBuffer {

    private StringBuffer buffer = new StringBuffer();
    private int depth = 0;
    private boolean newLine;

    public void write(String s) {
        checkNewLine();
        buffer.append(s);
    }

    public void write(char s) {
        checkNewLine();
        buffer.append(s);
    }

    public void newline() {
        buffer.append('\n');
        newLine = true;
    }

    public void indent() {
        depth++;
    }

    public void deindent() {
        depth--;
    }

    public String toString() {
        return buffer.toString();
    }

    private void checkNewLine() {
        if (newLine) {
            for (int i = 0; i < depth; i++) buffer.append('\t');
            newLine = false;
        }
    }

}
