package com.thoughtworks.qdox.parser;

/**
 * Thrown to indicate an error during parsing
 */
public class ParseException extends RuntimeException {

    private int line;
    private int column;
    private String errorMessage;

    public ParseException(String message, int line, int column) {
        errorMessage = message + " @[" + line + "," + column + "] in ";
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public void setSourceInfo(String sourceInfo) {
        errorMessage += sourceInfo;
    }

}
