package com.thoughtworks.qdox.model.util;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

public class TagParserTest extends TestCase {

    public TagParserTest(String name) {
        super(name);
    }

    //---( Parse into named parameters )---
    
    public void testEmptyInputContainsNoNamedParameters() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("");
        assertNotNull(paramMap);
        assertTrue(paramMap.isEmpty());
    }

    public void testMyUnderstandingOfStreamTokenizer() throws IOException {
        String input = 
            "x=y 'foo' \" bar \" 234\t'multi\\\nline'\n" + 
            "dotted.words hypen-ated under_scored";
        StreamTokenizer tokenizer = TagParser.makeTokenizer(input);
        assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("x", tokenizer.sval);
        assertEquals('=', tokenizer.nextToken());
        assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("y", tokenizer.sval);
        assertEquals('\'', tokenizer.nextToken());
        assertEquals("foo", tokenizer.sval);
        assertEquals('"', tokenizer.nextToken());
        assertEquals(" bar ", tokenizer.sval);
        assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("234", tokenizer.sval);
        assertEquals('\'', tokenizer.nextToken());
        assertEquals("multi\nline", tokenizer.sval);
        assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("dotted.words", tokenizer.sval);
        assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("hypen-ated", tokenizer.sval);
        assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("under_scored", tokenizer.sval);
    }

    public void testCanParseSimpleNamedParameters() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x=foo");
        assertNotNull(paramMap);
        assertEquals(1, paramMap.size());
        assertEquals("foo", paramMap.get("x"));
    }

    public void testCanParseMultipleNamedParameters() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x=foo y=bar");
        assertNotNull(paramMap);
        assertEquals(2, paramMap.size());
        assertEquals("foo", paramMap.get("x"));
        assertEquals("bar", paramMap.get("y"));
    }

    public void testNamedParametersAreReturnedInOrderDeclared() {
        Map<String, String> paramMap = TagParser.parseNamedParameters(
            "x=foo y=bar a=orses b=oney"
        );
        assertEquals(4, paramMap.size());
        Iterator<String> keyIterator = paramMap.keySet().iterator();
        assertEquals("x", keyIterator.next());
        assertEquals("y", keyIterator.next());
        assertEquals("a", keyIterator.next());
        assertEquals("b", keyIterator.next());
    }

    public void testNamedParameterValuesCanBeSingleQuoted() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x='foo' y='bar fly'");
        assertNotNull(paramMap);
        assertEquals(2, paramMap.size());
        assertEquals("foo", paramMap.get("x"));
        assertEquals("bar fly", paramMap.get("y"));
    }

    public void testNamedParameterValuesCanBeDoubleQuoted() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x=\"'foo'\" y=\"bar fly\"");
        assertNotNull(paramMap);
        assertEquals(2, paramMap.size());
        assertEquals("'foo'", paramMap.get("x"));
        assertEquals("bar fly", paramMap.get("y"));
    }

    public void testNamedParametersCanHaveSpacesAroundEquals() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x = foo y = 'bar'");
        assertNotNull(paramMap);
        assertEquals(2, paramMap.size());
        assertEquals("foo", paramMap.get("x"));
        assertEquals("bar", paramMap.get("y"));
    }

    public void testNamedParameterNamesCanContainHypensEtc() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x.a = '1'  x-b = '2'  x_c = '3'");
        assertNotNull(paramMap);
        assertEquals(3, paramMap.size());
        assertEquals("1", paramMap.get("x.a"));
        assertEquals("2", paramMap.get("x-b"));
        assertEquals("3", paramMap.get("x_c"));
    }

    public void testNamedParameterValuesCanBeNumeric() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x=1 y=2");
        assertNotNull(paramMap);
        assertEquals(2, paramMap.size());
        assertEquals("1", paramMap.get("x"));
        assertEquals("2", paramMap.get("y"));
    }

    public void testNamedParameterValuesCanSpanLinesIfBackslashIsUsed() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x='multiline\\\nvalue'");
        assertNotNull(paramMap);
        assertEquals(1, paramMap.size());
        assertEquals("multiline\nvalue", paramMap.get("x"));
    }

    //---( Parse into "words": positional parameters )---
    
    public void testEmptyInputContainsNoWords() {
        String[] words = TagParser.parseWords("");
        assertEquals(0, words.length);
    }

    public void testCanSplitIntoWordsOnWhitespace() {
        String[] words = TagParser.parseWords("a b c");
        assertEquals(3, words.length);
        assertEquals("a", words[0]);
        assertEquals("b", words[1]);
        assertEquals("c", words[2]);
    }
    
    public void testWordsCanContainHyphensEtc() {
        String[] words = TagParser.parseWords("a.b c-d e_f");
        assertEquals(3, words.length);
        assertEquals("a.b", words[0]);
        assertEquals("c-d", words[1]);
        assertEquals("e_f", words[2]);
    }
    
    public void testMostPunctuationAreSeparateWords() {
        String[] words = TagParser.parseWords("a=c");
        assertEquals(3, words.length);
        assertEquals("a", words[0]);
        assertEquals("=", words[1]);
        assertEquals("c", words[2]);
    }

    public void testShouldParseMultiLineParameters() {
        String[] words = TagParser.parseWords("\n" +
                "a=c\n" +
                "e=g\n");

        assertEquals(6, words.length);
        assertEquals("a", words[0]);
        assertEquals("=", words[1]);
        assertEquals("c", words[2]);
        assertEquals("e", words[3]);
        assertEquals("=", words[4]);
        assertEquals("g", words[5]);

    }

    //for QDOX-173
    public void testGenericsSupport() {
    	String[] words = TagParser.parseWords("<K>  The Key");
    	assertEquals("<K>", words[0]);
    	assertEquals("The", words[1]);
    	assertEquals("Key", words[2]);
    }

}
