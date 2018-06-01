package com.thoughtworks.qdox.parser;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;

public class LexerTest extends TestCase {

    private static Map<Integer, String> tokens;

    static {
        tokens = new HashMap<Integer, String>();
        Field[] tokenFlds = Parser.class.getDeclaredFields();
        for (int i = 0; i < tokenFlds.length; i++) {
            Field f = tokenFlds[i];
            try {
                if (!f.getName().startsWith("YY")) {
                    tokens.put(new Integer(f.getShort(Parser.class)), f.getName());
                }
            } catch (Exception e) {
                // we don't care!
            }
        }
    }

    public LexerTest(String s) {
        super(s);
    }

    public void testEmptyInput() throws Exception {
        String in = "";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(0, lexer);
    }

    public void testNewlines() throws Exception {
        String in = "DOS\r\nUNIX\nMAC\r";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.IDENTIFIER, "DOS", lexer);
        assertLex(Parser.IDENTIFIER, "UNIX", lexer);
        assertLex(Parser.IDENTIFIER, "MAC", lexer);
        assertLex(0, lexer);
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
        assertLex(Parser.IDENTIFIER, "X", lexer);
        assertLex(Parser.BRACEOPEN, lexer);
        assertLex(Parser.STATIC, lexer);
        assertLex(Parser.CODEBLOCK, lexer);
        assertLex(Parser.BRACECLOSE, lexer);
        assertLex(0, lexer);
    }

    public void testFieldAssignment() throws Exception {
        checkAssignment("x");
        checkAssignment("(map.isEmpty ? 1 : -1)");
        checkAssignment("0");
        checkAssignment("\"hello\"");
        checkAssignment("new Thingy()");
        checkAssignment("new Thingy(\"xxx\", 3, x.getBlah())");
        checkAssignment("new Thingy(\"xx;x\", 3, x.getBlah())");
        checkAssignment("StaticClass.intance()");
        checkAssignment("[1,2,3]");
        checkAssignment("/* , ; } */");
    }

    public void testAnonymousInnerClassAssignment() throws Exception {
        checkAssignment("new Thingifier() { void doThings(int x) { blah(); } }");
        checkAssignment("new Thingifier() { void doThings(int x) { a = \"aaa\"; } }");
    }

    public void testGenericTypeAssignment() throws Exception {
        // QDOX-77
        checkAssignment("new HashMap<String,Integer>");
    }

    public void testFieldsContainingLessThanOrGreaterThanInAssignment() throws Exception {
        // QDOX-71 - this is really important as it is common to see in all versions of the JDK.
        // Please don't disable this test -joe.
        checkAssignment("x < y");
        checkAssignment("x > y");
        checkAssignment("x << y");
        checkAssignment("x >> y");
        checkAssignment("x<y>z");
    }
    
    private void checkAssignment(String assignment) throws IOException {
        String in = ""
                + "class X { "
                + " int x = " + assignment + "; "
                + "} ";

        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.CLASS, lexer);
        assertLex(Parser.IDENTIFIER, "X", lexer);
        assertLex(Parser.BRACEOPEN, lexer);

        assertLex(Parser.IDENTIFIER, "int", lexer);
        assertLex(Parser.IDENTIFIER, "x", lexer);
        assertLex(Parser.SEMI, lexer);

        assertLex(Parser.BRACECLOSE, lexer);
        assertLex(0, lexer);
    }

    public void testMultipleFields() throws Exception {
        String in = ""
        + "class X { "
        + " int x, y = 2; "
        + "} ";

        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.CLASS, lexer);
        assertLex(Parser.IDENTIFIER, "X", lexer);
        assertLex(Parser.BRACEOPEN, lexer);

        assertLex(Parser.IDENTIFIER, "int", lexer);
        assertLex(Parser.IDENTIFIER, "x", lexer);
        assertLex(Parser.COMMA, lexer);
        assertLex(Parser.IDENTIFIER, "y", lexer);
        assertLex(Parser.SEMI, lexer);
        
        assertLex(Parser.BRACECLOSE, lexer);
        assertLex(0, lexer);
    }

    public void testSpecialCharsInIdentifier() throws Exception {
        String in = "a_b x$y z80";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.IDENTIFIER, "a_b", lexer);
        assertLex(Parser.IDENTIFIER, "x$y", lexer);
        assertLex(Parser.IDENTIFIER, "z80", lexer);
        assertLex(0, lexer);
    }

    public void testUnicodeInTest() throws Exception {
        checkAssignment("\"\u0000\""); 
    }

    public void testUnicodeInFile() throws Exception {
        Lexer lexer = new JFlexLexer( getClass().getResourceAsStream( "/com/thoughtworks/qdox/testdata/Unicode.java" ) );
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
        assertLex(Parser.SEMI, lexer);

        assertLex(Parser.BRACECLOSE, lexer);
        assertLex(0, lexer);
    }

    public void testUnicodeIdentifiers() throws Exception {
        // \u0391 == uppercase Greek "Alpha"
        assertSingleLex("\u0391", Parser.IDENTIFIER);
        // \u00f6 == lowercase o + diaeresis
        assertSingleLex("f\u00f6rnamn", Parser.IDENTIFIER); 
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
        assertLex(Parser.SEMI, lexer);
        assertLex(Parser.BRACECLOSE, lexer);
        assertLex(Parser.IDENTIFIER, "int", lexer);
        assertLex(Parser.IDENTIFIER, "y", lexer);
        assertLex(Parser.SEMI, lexer);
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
        checkAssignment("\"???????????\"");
        checkAssignment("'???????????'");
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
        assertLex(Parser.SEMI, lexer);
        assertLex(0, lexer);
    }

    public void testSingleQuoteInCharInAssignment() throws Exception {
        String in = "x = '\\'';";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.IDENTIFIER, "x", lexer);
        assertLex(Parser.SEMI, lexer);
        assertLex(0, lexer);
    }

    public void testStringWithDoubleQuotesIn() throws Exception {
        String in = "x = \"blah \\\" blah\";";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.IDENTIFIER, "x", lexer);
        assertLex(Parser.SEMI, lexer);
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

    public void testCommentsThatEndImmediately() throws Exception {
        String in = "/**/ class";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.CLASS, lexer);
        assertLex(0, lexer);
    }

    public void testCommentsWithQuotesInAssignment() throws Exception {
        String in = "a x = y /* don't do stuff*/;";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.IDENTIFIER, "a", lexer);
        assertLex(Parser.IDENTIFIER, "x", lexer);
        assertLex(Parser.SEMI, lexer);
        assertLex(0, lexer);

        in = "a z = \n"
                + "// thing's thing \n"
                + "0;";
        lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.IDENTIFIER, "a", lexer);
        assertLex(Parser.IDENTIFIER, "z", lexer);
        assertLex(Parser.SEMI, lexer);
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
        assertSingleLex("super", Parser.SUPER);
    }

    public void testTypeTokens() throws Exception {
        String in = "Map<? extends A & B, List<String>>";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.IDENTIFIER, "Map", lexer);
        assertLex(Parser.LESSTHAN, lexer);
        assertLex(Parser.QUERY, lexer);
        assertLex(Parser.EXTENDS, lexer);
        assertLex(Parser.IDENTIFIER, "A", lexer);
        assertLex(Parser.AMPERSAND, lexer);
        assertLex(Parser.IDENTIFIER, "B", lexer);
        assertLex(Parser.COMMA, lexer);
        assertLex(Parser.IDENTIFIER, "List", lexer);
        assertLex(Parser.LESSTHAN, lexer);
        assertLex(Parser.IDENTIFIER, "String", lexer);
        assertLex(Parser.GREATERTHAN, lexer);
        assertLex(Parser.GREATERTHAN, lexer);
        assertLex(0, lexer);
    }

    public void testAnnotationDeclarationTokens() throws Exception {
        String in = "" 
            + "public @interface Copyright {\n" 
            + "    int year();\n" 
            + "    String assignee() default \"The CodeHaus\";\n"
            + "}\n";
        Lexer lexer = new JFlexLexer(new StringReader(in));

        assertLex(Parser.PUBLIC, lexer);
        assertLex(Parser.ANNOINTERFACE, lexer);
        assertLex(Parser.IDENTIFIER, "Copyright", lexer);
        assertLex(Parser.BRACEOPEN, lexer);
        assertLex(Parser.IDENTIFIER, "int", lexer);
        assertLex(Parser.IDENTIFIER, "year", lexer);
        assertLex(Parser.PARENOPEN, lexer);
        assertLex(Parser.PARENCLOSE, lexer);
        assertLex(Parser.SEMI, lexer);
        assertLex(Parser.IDENTIFIER, "String", lexer);
        assertLex(Parser.IDENTIFIER, "assignee", lexer);
        assertLex(Parser.PARENOPEN, lexer);
        assertLex(Parser.PARENCLOSE, lexer);
        assertLex(Parser.SEMI, lexer);
        assertLex(Parser.BRACECLOSE, lexer);
        assertLex(0, lexer);
    }

    public void testAnnotationTokens() throws Exception {
        String in = ""
            + "@Copyright (year = 2004, month = \"Jan\")\n"
            + "@Note(\"Just ignore me\")\n" 
            + "public class LexerTest extends TestCase {}\n";
        Lexer lexer = new JFlexLexer(new StringReader(in));

        assertLex(Parser.AT, "@", lexer);
        assertLex(Parser.IDENTIFIER, "Copyright", lexer);
        assertLex(Parser.PARENOPEN, lexer);
        assertLex(Parser.IDENTIFIER, "year", lexer);
        assertLex(Parser.EQUALS, lexer);
        assertLex(Parser.INTEGER_LITERAL, "2004", lexer);
        assertLex(Parser.COMMA, lexer);
        assertLex(Parser.IDENTIFIER, "month", lexer);
        assertLex(Parser.EQUALS, lexer);
        assertLex(Parser.STRING_LITERAL, "\"", lexer);
        assertEquals("\"Jan\"", lexer.getCodeBody());
        assertLex(Parser.PARENCLOSE, lexer);
        assertLex(Parser.AT, "@", lexer);
        assertLex(Parser.IDENTIFIER, "Note", lexer);
        assertLex(Parser.PARENOPEN, lexer);
        assertLex(Parser.STRING_LITERAL, lexer);
        assertEquals( "\"Just ignore me\"", lexer.getCodeBody() );
        assertLex(Parser.PARENCLOSE, lexer);
        assertLex(Parser.PUBLIC, lexer);
        assertLex(Parser.CLASS, lexer);
        assertLex(Parser.IDENTIFIER, "LexerTest", lexer);
        assertLex(Parser.EXTENDS, lexer);
        assertLex(Parser.IDENTIFIER, "TestCase", lexer);
        assertLex(Parser.BRACEOPEN, lexer);
        assertLex(Parser.BRACECLOSE, lexer);
        assertLex(0, lexer);
    }

    public void testQDOX134_MoreAnnotationTokens() throws Exception {
        String in = ""
            + "@myTag name=TestClass attrs=Something1,Something2,Something3\n"
            + "public class LexerTest extends TestCase {}\n";
        Lexer lexer = new JFlexLexer(new StringReader(in));

        assertLex(Parser.AT, "@", lexer);
        assertLex(Parser.IDENTIFIER, "myTag", lexer);
        assertLex(Parser.IDENTIFIER, "name", lexer);
        assertLex(Parser.COMMA, lexer);
        assertLex(Parser.IDENTIFIER, "Something2", lexer);
        assertLex(Parser.COMMA, lexer);
        assertLex(Parser.IDENTIFIER, "Something3", lexer);
        assertLex(Parser.PUBLIC, lexer);
        assertLex(Parser.CLASS, lexer);
        assertLex(Parser.IDENTIFIER, "LexerTest", lexer);
        assertLex(Parser.EXTENDS, lexer);
        assertLex(Parser.IDENTIFIER, "TestCase", lexer);
        assertLex(Parser.BRACEOPEN, lexer);
        assertLex(Parser.BRACECLOSE, lexer);
        assertLex(0, lexer);
    }
    
    public void testAnnotationElementValueArrayInitializer() throws Exception {
    	String in = "@Endorsers({\"Children\", \"Unscrupulous dentists\"})\n" +
    			"public class Lollipop { }";
    	
    	Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.AT, "@", lexer);
        assertLex(Parser.IDENTIFIER, "Endorsers", lexer);
        assertLex(Parser.PARENOPEN, "(", lexer);
        assertLex(Parser.BRACEOPEN, "{", lexer);
        assertLex(Parser.STRING_LITERAL, "\"", lexer);
        assertEquals( "\"Children\"", lexer.getCodeBody() );
        assertLex(Parser.COMMA, ",", lexer);
        assertLex(Parser.STRING_LITERAL, "\"", lexer);
        assertEquals( "\"Unscrupulous dentists\"", lexer.getCodeBody() );
        assertLex(Parser.BRACECLOSE, "}", lexer);
        assertLex(Parser.PARENCLOSE, ")", lexer);
        assertLex(Parser.PUBLIC, "public", lexer);
        assertLex(Parser.CLASS, "class", lexer);
        assertLex(Parser.IDENTIFIER, "Lollipop", lexer);
        assertLex(Parser.BRACEOPEN, "{", lexer);
        assertLex(Parser.BRACECLOSE, "}", lexer);
        assertLex(0, lexer);
    }

    public void testEnumConstructor() throws Exception {
        String in = "enum Foo { a(\"hello\"); int someField; }";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.ENUM, lexer);
        assertLex(Parser.IDENTIFIER, "Foo", lexer);
        assertLex(Parser.BRACEOPEN, lexer);
        assertLex(Parser.IDENTIFIER, "a", lexer);
        assertLex(Parser.PARENOPEN, lexer);
        assertLex(Parser.STRING_LITERAL, "\"", lexer);
        assertEquals( "\"hello\"", lexer.getCodeBody() );
        assertLex(Parser.PARENCLOSE, lexer);
        assertLex(Parser.SEMI, lexer);
        assertLex(Parser.IDENTIFIER, "int", lexer);
        assertLex(Parser.IDENTIFIER, "someField", lexer);
        assertLex(Parser.SEMI, lexer);
        assertLex(Parser.BRACECLOSE, lexer);
    }

    public void testEnumWithMethods() throws Exception {
        String in = ""
                + "enum Animal {"
                + " DUCK { public void speak() { System.out.println(\"quack!\"); } }"
                + "}";
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.ENUM, lexer);
        assertLex(Parser.IDENTIFIER, "Animal", lexer);
        assertLex(Parser.BRACEOPEN, lexer);
        assertLex(Parser.IDENTIFIER, "DUCK", lexer);
        //assertLex(Parser.CODEBLOCK, lexer);
        assertLex( Parser.BRACEOPEN, lexer );
        assertLex(Parser.PUBLIC, lexer);
        assertLex(Parser.IDENTIFIER, "void", lexer);
        assertLex( Parser.IDENTIFIER, "speak", lexer );
        assertLex( Parser.PARENOPEN, lexer );
        assertLex( Parser.PARENCLOSE, lexer );
        assertLex( Parser.CODEBLOCK, lexer );
        assertLex( Parser.BRACECLOSE, lexer );
        
        assertLex(Parser.BRACECLOSE, lexer);
        assertLex( 0, lexer );
    }

    private void assertSingleLex(String in, short expectedLex) throws Exception {
        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(expectedLex, lexer);
        assertLex(0, lexer);
    }


    private void assertLex(int expectedToken, Lexer lexer) throws IOException {
        Object expected = tokens.get(new Integer(expectedToken));
        Object actual = tokens.get(new Integer(lexer.lex()));
        assertEquals(expected, actual);
    }

    private void assertLex(int expectedToken, String expectedText, Lexer lexer) throws IOException {
        assertLex(expectedToken, lexer);
        assertEquals(expectedText, lexer.text());
    }

    // for QDOX-140
    public void testNonAsciiMethodNameDoesNotCrashLexerButChewsUpUnicodeEscapedSequencesBadly() throws Exception {
        String in = ""
                + "interface X { "
                + "   void paramWithNonAsciis\\u00E4\\u00F6\\u00FC\\u00DF();"
                + "} ";

        Lexer lexer = new JFlexLexer(new StringReader(in));
        assertLex(Parser.INTERFACE, lexer);
        assertLex(Parser.IDENTIFIER, "X", lexer);
        assertLex(Parser.BRACEOPEN, lexer);
        assertLex(Parser.IDENTIFIER, "void", lexer);
        assertLex(Parser.IDENTIFIER, "paramWithNonAsciis\\u00E4\\u00F6\\u00FC\\u00DF", lexer);
        assertLex(Parser.PARENOPEN, lexer);
        assertLex(Parser.PARENCLOSE, lexer);
        assertLex(Parser.SEMI, lexer);
        assertLex(Parser.BRACECLOSE, lexer);
        assertLex(0, lexer);
    }
    
    //for QDOX-158
    public void testAnnotationWithMultipleParameters() throws Exception {
    	String in = 
    			"@MyFunction.MyInterface( prefix1 = \"abc\", prefix2 = \"abc\" )";
    	Lexer lexer = new JFlexLexer(new StringReader(in));
    	assertLex(Parser.AT, lexer);
    	assertLex(Parser.IDENTIFIER, "MyFunction", lexer);
    	assertLex(Parser.DOT, lexer);
    	assertLex(Parser.IDENTIFIER, "MyInterface", lexer);
    	assertLex(Parser.PARENOPEN, lexer);
    	assertLex(Parser.IDENTIFIER, "prefix1", lexer);
    	assertLex(Parser.EQUALS, lexer);
    	assertLex(Parser.STRING_LITERAL, lexer);
    	assertEquals( "\"abc\"", lexer.getCodeBody() );
    	assertLex(Parser.COMMA, lexer);
    	assertLex(Parser.IDENTIFIER, "prefix2", lexer);
    	assertLex(Parser.EQUALS, lexer);
    	assertLex(Parser.STRING_LITERAL, lexer);
    	assertEquals( "\"abc\"", lexer.getCodeBody() );
    	assertLex(Parser.PARENCLOSE, lexer);
    }

    public void testSimpleAnnotation() throws Exception {
    	String in = "@Override\n public boolean isReadOnly(final ELContext context)";
    	Lexer lexer = new JFlexLexer(new StringReader(in));
    	assertLex(Parser.AT, lexer);
    	assertLex(Parser.IDENTIFIER, "Override", lexer);
    	assertLex(Parser.PUBLIC, lexer);
    	assertLex(Parser.IDENTIFIER, "boolean", lexer);
    	assertLex(Parser.IDENTIFIER, "isReadOnly", lexer);
    	assertLex(Parser.PARENOPEN, lexer);
    	assertLex(Parser.FINAL, lexer);
    	assertLex(Parser.IDENTIFIER, "ELContext", lexer);
    	assertLex(Parser.IDENTIFIER, "context", lexer);
    	assertLex(Parser.PARENCLOSE, lexer);
    }
    
    public void testMultipleRowAnnotation()
        throws Exception
    {
        String in = "@JSFComponent\n  (name = \"h:inputHidden\")";
        Lexer lexer = new JFlexLexer( new StringReader( in ) );
        assertLex( Parser.AT, lexer );
        assertLex( Parser.IDENTIFIER, "JSFComponent", lexer );
        assertLex( Parser.PARENOPEN, lexer );
        assertLex( Parser.IDENTIFIER, "name", lexer );
        assertLex( Parser.EQUALS, lexer );
        assertLex( Parser.STRING_LITERAL, lexer );
        assertEquals( "\"h:inputHidden\"", lexer.getCodeBody() );
        assertLex( Parser.PARENCLOSE, lexer );
    }
    
    public void testEnumWithAnnotations() throws Exception {
    	String in = "class Foo {\n" +
		"public enum BasicType {\n" +
		"@XmlEnumValue(\"text\")\n" +
		"VALUE(\"value\"); }\n" +
		"}";  
    	Lexer lexer = new JFlexLexer(new StringReader(in));
    	assertLex(Parser.CLASS, lexer);
    	assertLex(Parser.IDENTIFIER, "Foo", lexer);
    	assertLex(Parser.BRACEOPEN, lexer);
    	assertLex(Parser.PUBLIC, lexer);
    	assertLex(Parser.ENUM, lexer);
    	assertLex(Parser.IDENTIFIER, "BasicType", lexer);
    	assertLex(Parser.BRACEOPEN, lexer);
    	assertLex(Parser.AT, lexer);
    	assertLex(Parser.IDENTIFIER, "XmlEnumValue", lexer);
    	assertLex(Parser.PARENOPEN, lexer);
    	assertLex(Parser.STRING_LITERAL, lexer);
    	assertEquals( "\"text\"", lexer.getCodeBody() );
    	assertLex(Parser.PARENCLOSE, lexer);
    	assertLex(Parser.IDENTIFIER, "VALUE", lexer);
        assertLex(Parser.PARENOPEN, lexer);
        assertLex(Parser.STRING_LITERAL, lexer);
        assertEquals( "\"value\"", lexer.getCodeBody() );
        assertLex(Parser.PARENCLOSE, lexer);
    	assertLex(Parser.SEMI, lexer);
    }
    
    // QDOX-242
    public void testDoubleValueAnnotation()
        throws Exception
    {
        String in = "@Rule( delta = 0.005 )";
        Lexer lexer = new JFlexLexer( new StringReader( in ) );
        assertLex( Parser.AT, lexer );
        assertLex( Parser.IDENTIFIER, "Rule", lexer );
        assertLex( Parser.PARENOPEN, lexer );
        assertLex( Parser.IDENTIFIER, "delta", lexer );
        assertLex( Parser.EQUALS, lexer );
        assertLex( Parser.FLOAT_LITERAL, "0.005", lexer );
        assertLex( Parser.PARENCLOSE, lexer );
        assertLex( 0, lexer );
    }
    
    // Github #31
    public void testParseEnumWithConstructor() throws Exception
    {
        String in = "public enum SomeEnum {\n" + 
            " VALUE1(\"hello\", 1, new String[]{\"hello\", \"world\"});\n" + 
            " SomeEnum(String string, int integer, String[] stringArray) {\n" + 
            " }\r\n" + 
            "}";
        
        Lexer lexer = new JFlexLexer( new StringReader( in ) );
        
        assertLex( Parser.PUBLIC, lexer );
        assertLex( Parser.ENUM, lexer );
        assertLex( Parser.IDENTIFIER, "SomeEnum", lexer );
        assertLex( Parser.BRACEOPEN, lexer );
        
        assertLex( Parser.IDENTIFIER, "VALUE1", lexer );
        assertLex( Parser.PARENOPEN, lexer );
        assertLex( Parser.STRING_LITERAL, "\"", lexer ); //???
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.INTEGER_LITERAL, "1", lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.NEW, lexer );
        assertLex( Parser.IDENTIFIER, "String", lexer );
        assertLex( Parser.SQUAREOPEN, lexer );
        assertLex( Parser.SQUARECLOSE, lexer );
        assertLex( Parser.CODEBLOCK, lexer );
        assertLex( Parser.PARENCLOSE, lexer );
        assertLex( Parser.SEMI, lexer );
        
        assertLex( Parser.IDENTIFIER, "SomeEnum", lexer );
        assertLex( Parser.PARENOPEN, lexer );
        assertLex( Parser.IDENTIFIER, "String", lexer );
        assertLex( Parser.IDENTIFIER, "string", lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.IDENTIFIER, "int", lexer );
        assertLex( Parser.IDENTIFIER, "integer", lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.IDENTIFIER, "String", lexer );
        assertLex( Parser.SQUAREOPEN, lexer );
        assertLex( Parser.SQUARECLOSE, lexer );
        assertLex( Parser.IDENTIFIER, "stringArray", lexer );
        assertLex( Parser.PARENCLOSE, lexer );
        
        assertLex( Parser.CODEBLOCK, lexer );
        assertLex( Parser.BRACECLOSE, lexer );
        
        assertLex( 0, lexer );
    }
    
    
    public void testModule()
        throws Exception
    {
        String in = "@Foo(1) @Foo(2) @Bar\n" + 
            "open module M.N {\n" + 
            "  requires A.B;\n" + 
            "  requires transitive C.D;\n" + 
            "  requires static E.F;\n" + 
            "  requires transitive static G.H;\n" + 
            "  exports P.Q;\n" + 
            "  exports R.S to T1.U1, T2.U2;\n" + 
            "  opens P.Q;\n" + 
            "  opens R.S to T1.U1, T2.U2;\n" + 
            "  uses V.W;\n" + 
            "  provides X.Y with Z1.Z2, Z3.Z4;\n" + 
            "}";
        Lexer lexer = new JFlexLexer( new StringReader( in ) );
        assertLex( Parser.AT, lexer );
        assertLex( Parser.IDENTIFIER, "Foo", lexer );
        assertLex( Parser.PARENOPEN, lexer );
        assertLex( Parser.INTEGER_LITERAL, "1", lexer );
        assertLex( Parser.PARENCLOSE, lexer );
        assertLex( Parser.AT, lexer );
        assertLex( Parser.IDENTIFIER, "Foo", lexer );
        assertLex( Parser.PARENOPEN, lexer );
        assertLex( Parser.INTEGER_LITERAL, "2", lexer );
        assertLex( Parser.PARENCLOSE, lexer );
        assertLex( Parser.AT, lexer );
        assertLex( Parser.IDENTIFIER, "Bar", lexer );
        
        assertLex( Parser.OPEN, lexer );
        assertLex( Parser.MODULE, lexer );
        assertLex( Parser.IDENTIFIER, "M", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "N", lexer );
        assertLex( Parser.BRACEOPEN, lexer );
        
        assertLex( Parser.REQUIRES, lexer );
        assertLex( Parser.IDENTIFIER, "A", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "B", lexer );
        assertLex( Parser.SEMI, lexer );
        
        assertLex( Parser.REQUIRES, lexer );
        assertLex( Parser.TRANSITIVE, lexer );
        assertLex( Parser.IDENTIFIER, "C", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "D", lexer );
        assertLex( Parser.SEMI, lexer );
        
        assertLex( Parser.REQUIRES, lexer );
        assertLex( Parser.STATIC, lexer );
        assertLex( Parser.IDENTIFIER, "E", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "F", lexer );
        assertLex( Parser.SEMI, lexer );
        
        assertLex( Parser.REQUIRES, lexer );
        assertLex( Parser.TRANSITIVE, lexer );
        assertLex( Parser.STATIC, lexer );
        assertLex( Parser.IDENTIFIER, "G", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "H", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.EXPORTS, lexer );
        assertLex( Parser.IDENTIFIER, "P", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "Q", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.EXPORTS, lexer );
        assertLex( Parser.IDENTIFIER, "R", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "S", lexer );
        assertLex( Parser.TO, lexer );
        assertLex( Parser.IDENTIFIER, "T1", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "U1", lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.IDENTIFIER, "T2", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "U2", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.OPENS, lexer );
        assertLex( Parser.IDENTIFIER, "P", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "Q", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.OPENS, lexer );
        assertLex( Parser.IDENTIFIER, "R", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "S", lexer );
        assertLex( Parser.TO, lexer );
        assertLex( Parser.IDENTIFIER, "T1", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "U1", lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.IDENTIFIER, "T2", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "U2", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.USES, lexer );
        assertLex( Parser.IDENTIFIER, "V", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "W", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.PROVIDES, lexer );
        assertLex( Parser.IDENTIFIER, "X", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "Y", lexer );
        assertLex( Parser.WITH, lexer );
        assertLex( Parser.IDENTIFIER, "Z1", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "Z2", lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.IDENTIFIER, "Z3", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "Z4", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.BRACECLOSE, lexer );
        assertLex( 0, lexer );
    }
    
    public void testModuleAsIdentifier() throws Exception
    {
        String in = ""
            + "package module.module;\n"
            + "import static module.module;\n"
            + "import module.module;\n"
            + "@module @module.module @module(module=module)\n"
            + "public class module extends module implements module,module {\n"
            + "  @module\n"
            + "  private module module;\n"
            + "  protected module module(module module) {}\n"
            + "}";
        Lexer lexer = new JFlexLexer( new StringReader( in ) );
        assertLex( Parser.PACKAGE, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "module", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.IMPORT, lexer );
        assertLex( Parser.STATIC, lexer );
        assertLex( Parser.IDENTIFIER, "module", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "module", lexer );
        assertLex( Parser.SEMI, lexer );
        
        assertLex( Parser.IMPORT, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "module", lexer );
        assertLex( Parser.SEMI, lexer );
        
        assertLex( Parser.AT, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.AT, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.AT, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.PARENOPEN, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.EQUALS, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.PARENCLOSE, lexer );

        assertLex( Parser.PUBLIC, lexer );
        assertLex( Parser.CLASS, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.EXTENDS, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.IMPLEMENTS, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.BRACEOPEN, lexer );

        assertLex( Parser.AT, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );

        assertLex( Parser.PRIVATE, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.IDENTIFIER, "module", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.PROTECTED, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.IDENTIFIER, "module", lexer );
        assertLex( Parser.PARENOPEN, lexer );
        assertLex( Parser.IDENTIFIER, "module",  lexer );
        assertLex( Parser.IDENTIFIER, "module", lexer );
        assertLex( Parser.PARENCLOSE, lexer );
        assertLex( Parser.CODEBLOCK, lexer );

        assertLex( Parser.BRACECLOSE, lexer );
        assertLex( 0, lexer );
    }
    
    public void testModuleModifiersAsIdentifier()
                    throws Exception
    {
        String in = "open module module.module {\n" + 
            "  requires requires;\n" + 
            "  requires requires.requires;\n" + 
            "  requires transitive transitive.transitive;\n" + 
            "  requires static requires.transitive;\n" + 
            "  requires transitive static requires.transitive;\n" + 
            "  exports exports;\n" + 
            "  exports exports.exports;\n" + 
            "  exports to.to to to.to, to.to;\n" + 
            "  opens opens;\n" + 
            "  opens opens.opens;\n" + 
            "  opens to.to to to.to, to.to;\n" + 
            "  uses uses;\n" + 
            "  uses uses.uses;\n" + 
            "  provides with.with with with.with, with.with;\n" + 
            "}";
        Lexer lexer = new JFlexLexer( new StringReader( in ) );
        
        assertLex( Parser.OPEN, lexer );
        assertLex( Parser.MODULE, lexer );
        assertLex( Parser.IDENTIFIER, "module", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "module", lexer );
        assertLex( Parser.BRACEOPEN, lexer );

        assertLex( Parser.REQUIRES, lexer );
        assertLex( Parser.IDENTIFIER, "requires", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.REQUIRES, lexer );
        assertLex( Parser.IDENTIFIER, "requires", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "requires", lexer );
        assertLex( Parser.SEMI, lexer );
        
        assertLex( Parser.REQUIRES, lexer );
        assertLex( Parser.TRANSITIVE, lexer );
        assertLex( Parser.IDENTIFIER, "transitive", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "transitive", lexer );
        assertLex( Parser.SEMI, lexer );
        
        assertLex( Parser.REQUIRES, lexer );
        assertLex( Parser.STATIC, lexer );
        assertLex( Parser.IDENTIFIER, "requires", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "transitive", lexer );
        assertLex( Parser.SEMI, lexer );
        
        assertLex( Parser.REQUIRES, lexer );
        assertLex( Parser.TRANSITIVE, lexer );
        assertLex( Parser.STATIC, lexer );
        assertLex( Parser.IDENTIFIER, "requires", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "transitive", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.EXPORTS, lexer );
        assertLex( Parser.IDENTIFIER, "exports", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.EXPORTS, lexer );
        assertLex( Parser.IDENTIFIER, "exports", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "exports", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.EXPORTS, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.TO, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.OPENS, lexer );
        assertLex( Parser.IDENTIFIER, "opens", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.OPENS, lexer );
        assertLex( Parser.IDENTIFIER, "opens", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "opens", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.OPENS, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.TO, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "to", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.USES, lexer );
        assertLex( Parser.IDENTIFIER, "uses", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.USES, lexer );
        assertLex( Parser.IDENTIFIER, "uses", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "uses", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.PROVIDES, lexer );
        assertLex( Parser.IDENTIFIER, "with", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "with", lexer );
        assertLex( Parser.WITH, lexer );
        assertLex( Parser.IDENTIFIER, "with", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "with", lexer );
        assertLex( Parser.COMMA, lexer );
        assertLex( Parser.IDENTIFIER, "with", lexer );
        assertLex( Parser.DOT, lexer );
        assertLex( Parser.IDENTIFIER, "with", lexer );
        assertLex( Parser.SEMI, lexer );

        assertLex( Parser.BRACECLOSE, lexer );
        assertLex( 0, lexer );
    }
}
