/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 23-Jan-2004
 * Time: 22:50:09
 */
package com.thoughtworks.qdox.parser.impl;

public class ParseException extends RuntimeException {
    private int line;
    private int column;

    public ParseException(String message, int line, int column) {
        super(message + " (" + line + "," + column + ")");
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}