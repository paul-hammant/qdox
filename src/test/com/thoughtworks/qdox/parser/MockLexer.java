package com.thoughtworks.qdox.parser;

import com.mockobjects.ReturnObjectList;

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
		return ((Integer)lexReturn.nextReturnObject()).intValue();
	}

	public String text() {
		return (String)textReturn.nextReturnObject();
	}

}
