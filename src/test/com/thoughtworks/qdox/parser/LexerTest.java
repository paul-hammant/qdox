package com.thoughtworks.qdox.parser;

import junit.framework.TestCase;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;

public class LexerTest extends TestCase {

	private static Map tokens;

	static {
		tokens = new HashMap();
		Field[] tokenFlds = Parser.class.getDeclaredFields();
		for (int i = 0; i < tokenFlds.length; i++) {
			Field f = tokenFlds[i];
			try {
				if (!f.getName().startsWith("YY")) {
					tokens.put(new Integer(f.getShort(Parser.class)), f.getName());
				}
			}
			catch (Exception e) {
				// we don't care!
			}
		}
	}

	public LexerTest(String s) {
		super(s);
	}

	public void testStaticBlock() throws Exception {
		String in = ""
			+ "class X { "
			+ " static { "
			+ "   something(); "
			+ " } "
			+ "} ";

		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.CLASS, lexer);
		assertLex(Parser.IDENTIFIER, lexer);
		assertLex(Parser.BRACEOPEN, lexer);
		assertLex(Parser.STATIC, lexer);
		assertLex(Parser.CODEBLOCK, lexer);
		assertLex(Parser.BRACECLOSE, lexer);
		assertLex(0, lexer);
	}

	public void testFieldAssignment() throws Exception {
		checkAssignment("0");
		checkAssignment("\"hello\"");
		checkAssignment("new Thingy()");
		checkAssignment("new Thingy(\"xxx\", 3, x.getBlah())");
		checkAssignment("new Thingy(\"xx;x\", 3, x.getBlah())");
		checkAssignment("StaticClass.intance()");
	}

	public void testAnonymousInnerClassAssignment() throws Exception {
		checkAssignment("new Thingifier() { void doThings(int x) { blah(); } }");
		checkAssignment("new Thingifier() { void doThings(int x) { a = \"aaa\"; } }");
	}

	private void checkAssignment(String assignment) throws IOException {
		String in = ""
			+ "class X { "
			+ " int x = " + assignment + "; "
			+ "} ";

		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.CLASS, lexer);
		assertLex(Parser.IDENTIFIER, lexer);
		assertLex(Parser.BRACEOPEN, lexer);

		assertLex(Parser.IDENTIFIER, "int", lexer);
		assertLex(Parser.IDENTIFIER, "x", lexer);
		assertLex(Parser.ASSIGNMENT, lexer);

		assertLex(Parser.BRACECLOSE, lexer);
		assertLex(0, lexer);
	}

	public void testUnicodeInTest() throws Exception {
		checkAssignment("\"\u0000\"");
	}

	public void testUnicodeInFile() throws Exception {
		Lexer lexer = new JFlexLexer(new FileReader("src/test/com/thoughtworks/qdox/testdata/Unicode.java"));
        assertLex(Parser.PACKAGE, lexer);
        assertLex(Parser.IDENTIFIER, lexer);
        assertLex(Parser.DOT, lexer);
        assertLex(Parser.IDENTIFIER, lexer);
        assertLex(Parser.DOT, lexer);
        assertLex(Parser.IDENTIFIER, lexer);
        assertLex(Parser.DOT, lexer);
        assertLex(Parser.IDENTIFIER, lexer);
        assertLex(Parser.SEMI, lexer);
        assertLex(Parser.CLASS, lexer);
		assertLex(Parser.IDENTIFIER, lexer);
		assertLex(Parser.BRACEOPEN, lexer);

		assertLex(Parser.IDENTIFIER, "String", lexer);
		assertLex(Parser.IDENTIFIER, "x", lexer);
		assertLex(Parser.ASSIGNMENT, lexer);

		assertLex(Parser.BRACECLOSE, lexer);
		assertLex(0, lexer);
	}

	public void testInnerClass() throws Exception {
		String in = ""
			+ "class X { "
			+ " class InnerClass { "
			+ "   int x = 1; "
			+ " } "
			+ " int y = 2; "
			+ "} ";

		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.CLASS, lexer);
		assertLex(Parser.IDENTIFIER, lexer);
		assertLex(Parser.BRACEOPEN, lexer);
		assertLex(Parser.CLASS, lexer);
		assertLex(Parser.IDENTIFIER, "InnerClass", lexer);
		assertLex(Parser.BRACEOPEN, lexer);
		assertLex(Parser.IDENTIFIER, "int", lexer);
		assertLex(Parser.IDENTIFIER, "x", lexer);
		assertLex(Parser.ASSIGNMENT, lexer);
		assertLex(Parser.BRACECLOSE, lexer);
		assertLex(Parser.IDENTIFIER, "int", lexer);
		assertLex(Parser.IDENTIFIER, "y", lexer);
		assertLex(Parser.ASSIGNMENT, lexer);
		assertLex(Parser.BRACECLOSE, lexer);
		assertLex(0, lexer);
	}

	public void testCurliesInStringsOrChars() throws Exception {

		checkAssignment("\"{\"");
		checkAssignment("\"}\"");
		checkAssignment("'}'");
		checkAssignment("'{'");

	}

	public void testDoubleBackSlashesInStringsOrChars() throws Exception {

		checkAssignment("\"\\\\\""); // x = "\\"
		checkAssignment("'\\\\'"); // x = '\\'

	}

	public void testFunnyCharsInStringsOrChars() throws Exception {
		checkAssignment("\"Åäöæøåüß§ÆØ\"");
		checkAssignment("'Åäöæøåüß§ÆØ'");
	}

	public void testQuoteInCharInCodeBlock() throws Exception {
		String in = "{'\"'}";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.CODEBLOCK, lexer);
		assertLex(0, lexer);
	}

	public void testDoubleQuotesInCharInAssignment() throws Exception {
		String in = "x = '\"';";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.IDENTIFIER, "x", lexer);
		assertLex(Parser.ASSIGNMENT, lexer);
		assertLex(0, lexer);
	}

	public void testSingleQuoteInCharInAssignment() throws Exception {
		String in = "x = '\\'';";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.IDENTIFIER, "x", lexer);
		assertLex(Parser.ASSIGNMENT, lexer);
		assertLex(0, lexer);
	}

	public void testStringWithDoubleQuotesIn() throws Exception {
		String in = "x = \"blah \\\" blah\";";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.IDENTIFIER, "x", lexer);
		assertLex(Parser.ASSIGNMENT, lexer);
		assertLex(0, lexer);
	}

	public void testCommentsWithSingleQuoteInCodeBlock() throws Exception {
		String in = "{ /* ' */ }";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.CODEBLOCK, lexer);
		assertLex(0, lexer);
	}

	public void testCommentsWithDoubleQuotesInCodeBlock() throws Exception {
		String in = "{ /* \" */ }";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.CODEBLOCK, lexer);
		assertLex(0, lexer);
	}

	public void testCommentsWithQuotesInAssignment() throws Exception {
		String in = "a x = y /* don't do stuff*/;";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.IDENTIFIER, "a", lexer);
		assertLex(Parser.IDENTIFIER, "x", lexer);
		assertLex(Parser.ASSIGNMENT, lexer);
		assertLex(0, lexer);

		in = "a z = \n"
			+ "// thing's thing \n"
			+ "0;";
		lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.IDENTIFIER, "a", lexer);
		assertLex(Parser.IDENTIFIER, "z", lexer);
		assertLex(Parser.ASSIGNMENT, lexer);
		assertLex(0, lexer);
	}

	public void testDocletTags() throws Exception {
		String in = ""
			+ "/**\n"
			+ " * @hello world\n"
			+ " * @a b c d\n"
			+ " * @bye\n"
			+ " * @bye:bye\n"
			+ " */";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.JAVADOCSTART, lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);

		assertLex(Parser.JAVADOCTAGMARK, lexer);
		assertLex(Parser.JAVADOCTOKEN, "hello", lexer);
		assertLex(Parser.JAVADOCTOKEN, "world", lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);

		assertLex(Parser.JAVADOCTAGMARK, lexer);
		assertLex(Parser.JAVADOCTOKEN, "a", lexer);
		assertLex(Parser.JAVADOCTOKEN, "b", lexer);
		assertLex(Parser.JAVADOCTOKEN, "c", lexer);
		assertLex(Parser.JAVADOCTOKEN, "d", lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);

		assertLex(Parser.JAVADOCTAGMARK, lexer);
		assertLex(Parser.JAVADOCTOKEN, "bye", lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);

		assertLex(Parser.JAVADOCTAGMARK, lexer);
		assertLex(Parser.JAVADOCTOKEN, "bye:bye", lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);

		assertLex(Parser.JAVADOCEND, lexer);
		assertLex(0, lexer);
	}

	public void testOneLinerDocletTag() throws Exception {
		String in = "/** @hello world */";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.JAVADOCSTART, lexer);

		assertLex(Parser.JAVADOCTAGMARK, lexer);
		assertLex(Parser.JAVADOCTOKEN, "hello", lexer);
		assertLex(Parser.JAVADOCTOKEN, "world", lexer);

		assertLex(Parser.JAVADOCEND, lexer);
		assertLex(0, lexer);
	}

	public void testCommentThatContainsAtSymbol() throws Exception {
		String in = ""
			+ "/**\n"
			+ " * joe@truemesh.com\n"
			+ " * {@link here}.\n"
			+ " */";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.JAVADOCSTART, lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);

		assertLex(Parser.JAVADOCTOKEN, "joe@truemesh.com", lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);
		assertLex(Parser.JAVADOCTOKEN, "{@link", lexer);
		assertLex(Parser.JAVADOCTOKEN, "here}.", lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);

		assertLex(Parser.JAVADOCEND, lexer);
		assertLex(0, lexer);
	}

	public void testCommentThatContainsStar() throws Exception {
		String in = ""
			+ "/**\n"
			+ " * 5 * 4\n"
			+ " * SELECT COUNT(*)\n"
			+ " */";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.JAVADOCSTART, lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);

		assertLex(Parser.JAVADOCTOKEN, "5", lexer);
		assertLex(Parser.JAVADOCTOKEN, "*", lexer);
		assertLex(Parser.JAVADOCTOKEN, "4", lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);
		assertLex(Parser.JAVADOCTOKEN, "SELECT", lexer);
		assertLex(Parser.JAVADOCTOKEN, "COUNT(*)", lexer);
		assertLex(Parser.JAVADOCNEWLINE, lexer);

		assertLex(Parser.JAVADOCEND, lexer);
		assertLex(0, lexer);
	}

	public void testArrayTokens() throws Exception {
		String in = "String[] []o[]";
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(Parser.IDENTIFIER, "String", lexer);
		assertLex(Parser.SQUAREOPEN, lexer);
		assertLex(Parser.SQUARECLOSE, lexer);
		assertLex(Parser.SQUAREOPEN, lexer);
		assertLex(Parser.SQUARECLOSE, lexer);
		assertLex(Parser.IDENTIFIER, "o", lexer);
		assertLex(Parser.SQUAREOPEN, lexer);
		assertLex(Parser.SQUARECLOSE, lexer);
		assertLex(0, lexer);
	}

    public void testKeyWords() throws Exception {
		assertSingleLex("package", Parser.PACKAGE);
		assertSingleLex("import", Parser.IMPORT);
		assertSingleLex("public", Parser.PUBLIC);
		assertSingleLex("protected", Parser.PROTECTED);
		assertSingleLex("private", Parser.PRIVATE);
		assertSingleLex("static", Parser.STATIC);
		assertSingleLex("final", Parser.FINAL);
		assertSingleLex("abstract", Parser.ABSTRACT);
		assertSingleLex("native", Parser.NATIVE);
		assertSingleLex("strictfp", Parser.STRICTFP);
		assertSingleLex("synchronized", Parser.SYNCHRONIZED);
		assertSingleLex("transient", Parser.TRANSIENT);
		assertSingleLex("volatile", Parser.VOLATILE);
		assertSingleLex("class", Parser.CLASS);
		assertSingleLex("interface", Parser.INTERFACE);
		assertSingleLex("throws", Parser.THROWS);
		assertSingleLex("extends", Parser.EXTENDS);
		assertSingleLex("implements", Parser.IMPLEMENTS);
	}

	private void assertSingleLex(String in, short expectedLex) throws Exception {
		Lexer lexer = new JFlexLexer(new StringReader(in));
		assertLex(expectedLex, lexer);
		assertLex(0, lexer);
	}


	private void assertLex(int expectedToken, Lexer lexer) throws IOException {
		Object expected = tokens.get(new Integer(expectedToken));
		Object actual = tokens.get(new Integer(lexer.lex()));
		assertEquals(expected,actual);
	}

	private void assertLex(int expectedToken, String expectedText, Lexer lexer) throws IOException {
		assertLex(expectedToken, lexer);
		assertEquals(expectedText, lexer.text());
	}

}
