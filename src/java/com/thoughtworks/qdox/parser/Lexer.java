package com.thoughtworks.qdox.parser;

import java.io.IOException;

public interface Lexer {

	int lex() throws IOException;

	String text();

}
