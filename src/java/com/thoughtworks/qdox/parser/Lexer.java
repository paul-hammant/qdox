package com.thoughtworks.qdox.parser;

import java.io.IOException;

/**
 * @mock
 */
public interface Lexer {

	int lex() throws IOException;

	String text();

}
