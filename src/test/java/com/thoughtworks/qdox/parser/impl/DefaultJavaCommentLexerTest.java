package com.thoughtworks.qdox.parser.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class DefaultJavaCommentLexerTest
{
    private DefaultJavaCommentLexer lexer;
    
    @Test
    public void testSingleLineComment() throws Exception {
        lexer = new DefaultJavaCommentLexer( new StringReader("// this is a single line comment"));
        lexAssert( 0 );
        assertEquals( "// this is a single line comment", lexer.getCodeBody() );
    }

    @Test
    public void testCompactMultiLineComment() throws Exception {
        lexer = new DefaultJavaCommentLexer( new StringReader("/**/"));
        lexAssert( 0 );
        assertEquals( "/**/", lexer.getCodeBody() );
    }

    @Test
    public void testCompactMultiLineComment2() throws Exception {
        lexer = new DefaultJavaCommentLexer( new StringReader("/***/"));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert( 0 );
    }

    @Test
    public void testCompactMultiLineComment3() throws Exception {
        lexer = new DefaultJavaCommentLexer( new StringReader("/*****/"));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/****");
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert( 0 );
    }

    @Test
    public void testSingleRowMultiLineComment() throws Exception {
        lexer = new DefaultJavaCommentLexer( new StringReader("/* multiline comment with one row */"));
        lexAssert( 0 );
        assertEquals( "/* multiline comment with one row */", lexer.getCodeBody() );
    }
    
    @Test
    public void testJavaDocComment() throws Exception {
        lexer = new DefaultJavaCommentLexer( new StringReader("/** multiline comment with one row */"));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "multiline comment with one row");
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert( 0 );
    }
    
    @Test
    public void testSingleTagJavaDoc() throws Exception {
        lexer = new DefaultJavaCommentLexer( new StringReader("/** @deprecated */"));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");
        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@deprecated");
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert( 0 );
    }

    @Test
    public void testDeprecatedJavaDoc() throws Exception {
        lexer = new DefaultJavaCommentLexer( new StringReader("/** @author John Doe */"));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");
        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@author");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "John Doe");
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert( 0 );
    }

    @Test
    public void testMultiTagJavaDoc() throws Exception {
        lexer = new DefaultJavaCommentLexer( new StringReader("/** @deprecated\n" + 
        		"  * @author John Doe */"));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");
        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@deprecated");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@author");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "John Doe");
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert( 0 );
    }
    
    @Test
    public void testDocletTags() throws Exception {
        String in = ""
                + "/**\n"
                + " * @hello world\n"
                + " * @a b c d\n"
                + " * @bye\n"
                + " * @bye:bye\n"
                + " */";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");

        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@hello");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "world\n");

        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@a");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "b c d\n");

        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@bye");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "\n");

        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@bye:bye");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "\n");

        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert(0);
    }
    
    @Test
    public void testOneLinerDocComment() throws Exception {
        String in = "/** @hello world */";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");

        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@hello");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "world");

        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert(0);
    }
    
    @Test
    public void testCompressedDocComment() throws Exception {
        String in = "/**@foo bar*/";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");
        
        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@foo");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "bar");
        
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert(0);
    }

    @Test
    public void testDeepJavadocTag() throws Exception {
        String in = "  /** *  *** * @m x \n" +
                "*/";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");
        
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "*  *** * @m x \n");
        
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert(0); 
    }

    @Test
    public void testDocCommentContainingAtSymbols() throws Exception {
        String in = ""
            + "/**\n"
            + " * joe@truemesh.com\n"
            + " * {@link here}.\n"
            + " * me @home\n"
            + " * geeks @ play\n"
            + " */";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");

        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "joe@truemesh.com\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "{@link here}.\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "me @home\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "geeks @ play\n");

        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert(0);
    }

    @Test
    public void testDocCommentContainingStars() throws Exception {
        String in = ""
                + "/**\n"
                + " * 5 * 4\n"
                + " * SELECT COUNT(*)\n"
                + " * **stars**everywhere** \n"
                + " */";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");

        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "5 * 4\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "SELECT COUNT(*)\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "**stars**everywhere** \n");

        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert(0);
    }
    
    @Test
    public void testExtraStarsAreIgnoredAtStartAndEnd() throws Exception {
        String in = ""
                + "/*****\n"
                + " * blah\n"
                + " *****/";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/*****");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "blah\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*****/");
        lexAssert(0);
    }
    
    @Test
    public void testExtraStarsCompressed() throws Exception {
        String in = ""
                + "/***blah***/";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/***");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "blah");
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "***/");
        lexAssert(0);
    }
    
    @Test
    public void testIgnoreStarPrefix() throws Exception {
        String in = ""
            + "/**\n"
            + " * simple\n"
            + "\t    * indented\n"
            + " *nospace\n"
            + " *** multistar\n"
            + " *\n"
            + " */";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");
        
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "simple\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "indented\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "nospace\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "multistar\n");
        lexAssert(DefaultJavaCommentParser.JAVADOCLINE, "\n");
        
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");
        lexAssert(0);
    }
    
    // QDOX-200
    @Test
    public void testCompactJavaDocTag() throws Exception {
        String in = "/** @foo*/";
        lexer = new DefaultJavaCommentLexer(new StringReader(in));
        lexAssert(DefaultJavaCommentParser.JAVADOCSTART, "/**");
        lexAssert(DefaultJavaCommentParser.JAVADOCTAG, "@foo");
        lexAssert(DefaultJavaCommentParser.JAVADOCEND, "*/");      
        lexAssert(0);
    }

    private void lexAssert(int lex) throws IOException {
        lexAssert( lex, "" );

    }
    private void lexAssert(int lex, String text) throws IOException {
        assertEquals(lex, lexer.lex());
        assertEquals(text, lexer.text());
    }
    
}
