package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public class IndentBufferTest extends TestCase {

	private IndentBuffer buffer;

	public IndentBufferTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		buffer = new IndentBuffer();
	}

	public void testNoIndentation() throws Exception {
    buffer.write("A string");
		buffer.newline();
		buffer.write("more string");
		buffer.write('s');
		buffer.newline();
		String expected = ""
			+ "A string\n"
			+ "more strings\n";
		assertEquals(expected, buffer.toString());
	}

	public void testIndentation() throws Exception {
    buffer.write("Line1");
		buffer.newline();
		buffer.indent();
		buffer.write("Indent1");
		buffer.newline();
		buffer.write("Indent2");
		buffer.write(" more");
		buffer.newline();
		buffer.deindent();
		buffer.write("Line2");
		buffer.newline();
		String expected = ""
			+ "Line1\n"
			+ "\tIndent1\n"
			+ "\tIndent2 more\n"
			+ "Line2\n";
		assertEquals(expected, buffer.toString());
	}
}
