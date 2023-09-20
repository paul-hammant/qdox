package com.thoughtworks.qdox.model.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Iterator;
import java.util.Map;

public class TagParserTest {

    //---( Parse into named parameters )---

    @Test
    public void testEmptyInputContainsNoNamedParameters() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("");
        Assertions.assertNotNull(paramMap);
        Assertions.assertTrue(paramMap.isEmpty());
    }

    @Test
    public void testMyUnderstandingOfStreamTokenizer() throws IOException {
        String input = 
            "x=y 'foo' \" bar \" 234\t'multi\\\nline'\n" + 
            "dotted.words hypen-ated under_scored";
        StreamTokenizer tokenizer = TagParser.makeTokenizer(input);
        Assertions.assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        Assertions.assertEquals("x", tokenizer.sval);
        Assertions.assertEquals('=', tokenizer.nextToken());
        Assertions.assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        Assertions.assertEquals("y", tokenizer.sval);
        Assertions.assertEquals('\'', tokenizer.nextToken());
        Assertions.assertEquals("foo", tokenizer.sval);
        Assertions.assertEquals('"', tokenizer.nextToken());
        Assertions.assertEquals(" bar ", tokenizer.sval);
        Assertions.assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        Assertions.assertEquals("234", tokenizer.sval);
        Assertions.assertEquals('\'', tokenizer.nextToken());
        Assertions.assertEquals("multi\nline", tokenizer.sval);
        Assertions.assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        Assertions.assertEquals("dotted.words", tokenizer.sval);
        Assertions.assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        Assertions.assertEquals("hypen-ated", tokenizer.sval);
        Assertions.assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        Assertions.assertEquals("under_scored", tokenizer.sval);
    }

    @Test
    public void testCanParseSimpleNamedParameters() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x=foo");
        Assertions.assertNotNull(paramMap);
        Assertions.assertEquals(1, paramMap.size());
        Assertions.assertEquals("foo", paramMap.get("x"));
    }

    @Test
    public void testCanParseMultipleNamedParameters() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x=foo y=bar");
        Assertions.assertNotNull(paramMap);
        Assertions.assertEquals(2, paramMap.size());
        Assertions.assertEquals("foo", paramMap.get("x"));
        Assertions.assertEquals("bar", paramMap.get("y"));
    }

    @Test
    public void testNamedParametersAreReturnedInOrderDeclared() {
        Map<String, String> paramMap = TagParser.parseNamedParameters(
            "x=foo y=bar a=orses b=oney"
        );
        Assertions.assertEquals(4, paramMap.size());
        Iterator<String> keyIterator = paramMap.keySet().iterator();
        Assertions.assertEquals("x", keyIterator.next());
        Assertions.assertEquals("y", keyIterator.next());
        Assertions.assertEquals("a", keyIterator.next());
        Assertions.assertEquals("b", keyIterator.next());
    }

    @Test
    public void testNamedParameterValuesCanBeSingleQuoted() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x='foo' y='bar fly'");
        Assertions.assertNotNull(paramMap);
        Assertions.assertEquals(2, paramMap.size());
        Assertions.assertEquals("foo", paramMap.get("x"));
        Assertions.assertEquals("bar fly", paramMap.get("y"));
    }

    @Test
    public void testNamedParameterValuesCanBeDoubleQuoted() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x=\"'foo'\" y=\"bar fly\"");
        Assertions.assertNotNull(paramMap);
        Assertions.assertEquals(2, paramMap.size());
        Assertions.assertEquals("'foo'", paramMap.get("x"));
        Assertions.assertEquals("bar fly", paramMap.get("y"));
    }

    @Test
    public void testNamedParametersCanHaveSpacesAroundEquals() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x = foo y = 'bar'");
        Assertions.assertNotNull(paramMap);
        Assertions.assertEquals(2, paramMap.size());
        Assertions.assertEquals("foo", paramMap.get("x"));
        Assertions.assertEquals("bar", paramMap.get("y"));
    }

    @Test
    public void testNamedParameterNamesCanContainHypensEtc() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x.a = '1'  x-b = '2'  x_c = '3'");
        Assertions.assertNotNull(paramMap);
        Assertions.assertEquals(3, paramMap.size());
        Assertions.assertEquals("1", paramMap.get("x.a"));
        Assertions.assertEquals("2", paramMap.get("x-b"));
        Assertions.assertEquals("3", paramMap.get("x_c"));
    }

    @Test
    public void testNamedParameterValuesCanBeNumeric() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x=1 y=2");
        Assertions.assertNotNull(paramMap);
        Assertions.assertEquals(2, paramMap.size());
        Assertions.assertEquals("1", paramMap.get("x"));
        Assertions.assertEquals("2", paramMap.get("y"));
    }

    @Test
    public void testNamedParameterValuesCanSpanLinesIfBackslashIsUsed() {
        Map<String, String> paramMap = TagParser.parseNamedParameters("x='multiline\\\nvalue'");
        Assertions.assertNotNull(paramMap);
        Assertions.assertEquals(1, paramMap.size());
        Assertions.assertEquals("multiline\nvalue", paramMap.get("x"));
    }

    //---( Parse into "words": positional parameters )---

    @Test
    public void testEmptyInputContainsNoWords() {
        String[] words = TagParser.parseWords("");
        Assertions.assertEquals(0, words.length);
    }

    @Test
    public void testCanSplitIntoWordsOnWhitespace() {
        String[] words = TagParser.parseWords("a b c");
        Assertions.assertEquals(3, words.length);
        Assertions.assertEquals("a", words[0]);
        Assertions.assertEquals("b", words[1]);
        Assertions.assertEquals("c", words[2]);
    }

    @Test
    public void testWordsCanContainHyphensEtc() {
        String[] words = TagParser.parseWords("a.b c-d e_f");
        Assertions.assertEquals(3, words.length);
        Assertions.assertEquals("a.b", words[0]);
        Assertions.assertEquals("c-d", words[1]);
        Assertions.assertEquals("e_f", words[2]);
    }

    @Test
    public void testMostPunctuationAreSeparateWords() {
        String[] words = TagParser.parseWords("a=c");
        Assertions.assertEquals(3, words.length);
        Assertions.assertEquals("a", words[0]);
        Assertions.assertEquals("=", words[1]);
        Assertions.assertEquals("c", words[2]);
    }

    @Test
    public void testShouldParseMultiLineParameters() {
        String[] words = TagParser.parseWords("\n" +
                "a=c\n" +
                "e=g\n");

        Assertions.assertEquals(6, words.length);
        Assertions.assertEquals("a", words[0]);
        Assertions.assertEquals("=", words[1]);
        Assertions.assertEquals("c", words[2]);
        Assertions.assertEquals("e", words[3]);
        Assertions.assertEquals("=", words[4]);
        Assertions.assertEquals("g", words[5]);

    }

    //for QDOX-173
    @Test
    public void testGenericsSupport() {
    	String[] words = TagParser.parseWords("<K>  The Key");
    	Assertions.assertEquals("<K>", words[0]);
    	Assertions.assertEquals("The", words[1]);
    	Assertions.assertEquals("Key", words[2]);
    }

}
