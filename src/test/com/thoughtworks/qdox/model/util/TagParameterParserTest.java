package com.thoughtworks.qdox.model.util;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Map;

import com.thoughtworks.qdox.model.util.TagParameterParser;

import junit.framework.TestCase;

public class TagParameterParserTest extends TestCase {

	public TagParameterParserTest(String name) {
		super(name);
	}

	public void testEmptyInputReturnsEmptyMap() {
		Map paramMap = TagParameterParser.parse("");
		assertNotNull(paramMap);
		assertTrue(paramMap.isEmpty());	
	}

	public void testMyUnderstandingOfStreamTokenizer() throws IOException {
		String input = 
			"x=y 'foo' \" bar \" 234 " + 
			"dotted.words hypen-ated under_scored";
		StreamTokenizer tokenizer = TagParameterParser.makeTokenizer(input);
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
		assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());	
		assertEquals("dotted.words", tokenizer.sval);
		assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());	
		assertEquals("hypen-ated", tokenizer.sval);
		assertEquals(StreamTokenizer.TT_WORD, tokenizer.nextToken());	
		assertEquals("under_scored", tokenizer.sval);
	}
	
	public void testParseOneParameterWithNoSpaces() {
		Map paramMap = TagParameterParser.parse("x=foo");
		assertNotNull(paramMap);
		assertEquals(1, paramMap.size());	
		assertEquals("foo", paramMap.get("x"));	
	}

	public void testParseTwoParametersWithNoSpaces() {
		Map paramMap = TagParameterParser.parse("x=foo y=bar");
		assertNotNull(paramMap);
		assertEquals(2, paramMap.size());	
		assertEquals("foo", paramMap.get("x"));	
		assertEquals("bar", paramMap.get("y"));	
	}

	public void testParseOneSingleQuotedParameter() {
		Map paramMap = TagParameterParser.parse("x='foo'");
		assertNotNull(paramMap);
		assertEquals(1, paramMap.size());	
		assertEquals("foo", paramMap.get("x"));	
	}

	public void testParseTwoSingleQuotedParameters() {
		Map paramMap = TagParameterParser.parse("x='foo' y='bar'");
		assertNotNull(paramMap);
		assertEquals(2, paramMap.size());	
		assertEquals("foo", paramMap.get("x"));	
		assertEquals("bar", paramMap.get("y"));	
	}

	public void testParseQuotedParametersContainingSpaces() {
		Map paramMap = TagParameterParser.parse("x=' bar fly '");
		assertNotNull(paramMap);
		assertEquals(1, paramMap.size());	
		assertEquals(" bar fly ", paramMap.get("x"));	
	}

	public void testParseDoubleQuotedParameters() {
		Map paramMap = TagParameterParser.parse("x=\"foo\" y=\"'bar'\"");
		assertNotNull(paramMap);
		assertEquals(2, paramMap.size());	
		assertEquals("foo", paramMap.get("x"));	
		assertEquals("'bar'", paramMap.get("y"));	
	}

	public void testParseWithSpacesAroundEquals() {
		Map paramMap = TagParameterParser.parse("x = foo y = 'bar'");
		assertNotNull(paramMap);
		assertEquals(2, paramMap.size());	
		assertEquals("foo", paramMap.get("x"));	
		assertEquals("bar", paramMap.get("y"));	
	}

}
