package com.thoughtworks.qdox.parser;

/**
 * Thrown to indicate an error during parsing
 */
public class ParseException extends RuntimeException {

    private int line;
    private int column;
    
    public ParseException(String message, int line, int column) {
        super(message + " @[" + line + "," + column + "]");
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
