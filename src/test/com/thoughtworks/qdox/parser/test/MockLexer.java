// Generated MockObject for com.thoughtworks.qdox.parser.Lexer. Do not edit!

package com.thoughtworks.qdox.parser.test;

import java.lang.*;
import java.io.IOException;

import com.thoughtworks.qdox.parser.*;
import com.mockobjects.*;
import mockmaker.ReturnValues;

public class MockLexer implements Lexer, Verifiable {

	// Methods for lex()

	private ExpectationCounter myLexCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Lexer Lex");
	private ReturnValues myActualLexReturnValues = new ReturnValues(false);

	public int lex() {
		myLexCalls.inc();


		Object result = myActualLexReturnValues.getNext();
		return ((Integer)result).intValue();
	}

	public void setExpectedLexCalls(int calls) {
		myLexCalls.setExpected(calls);
	}

	public void setupLex(int value){
		myActualLexReturnValues.add(new Integer(value));
	}

	public void addExpectedLexValues(){
	}

	// Methods for text()

	private ExpectationCounter myTextCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Lexer Text");
	private ReturnValues myActualTextReturnValues = new ReturnValues(false);

	public java.lang.String text() {
		myTextCalls.inc();


		Object result = myActualTextReturnValues.getNext();
		return (java.lang.String)result;
	}

	public void setExpectedTextCalls(int calls) {
		myTextCalls.setExpected(calls);
	}

	public void setupText(java.lang.String value){
		myActualTextReturnValues.add(value);
	}

	public void addExpectedTextValues(){
	}


	// Verify method

	public void verify() {
		myLexCalls.verify();
		myTextCalls.verify();
	}

}
