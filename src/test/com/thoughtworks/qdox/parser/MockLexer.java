package com.thoughtworks.qdox.parser;

import org.jmock.expectation.ReturnObjectList;

class MockLexer implements Lexer {

    private ReturnObjectList textReturn = new ReturnObjectList("text");
    private ReturnObjectList lexReturn = new ReturnObjectList("lex");

    public void setupText(String value) {
        textReturn.addObjectToReturn(value);
    }

    public void setupLex(int value) {
        lexReturn.addObjectToReturn(value);
    }

    public int lex() {
        return ((Integer) lexReturn.nextReturnObject()).intValue();
    }

    public String text() {
        return (String) textReturn.nextReturnObject();
    }

    public int getLine() {
        return -1;
    }

    public int getColumn() {
        return -1;
    }

}
