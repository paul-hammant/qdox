package com.thoughtworks.qdox.model.util;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class TagParameterParser {

	/**
	 * Create a StreamTokenizer suitable for parsing the tag text. 
	 */
	static StreamTokenizer makeTokenizer(String input) {
		StreamTokenizer tokenizer = 
			new StreamTokenizer(new StringReader(input));
		tokenizer.resetSyntax();
		tokenizer.wordChars('A','Z');
		tokenizer.wordChars('a','z');
		tokenizer.wordChars('0','9');
		tokenizer.wordChars('-','-');
		tokenizer.wordChars('_','_');
		tokenizer.wordChars('.','.');
		tokenizer.quoteChar('\'');
		tokenizer.quoteChar('"');
		tokenizer.whitespaceChars(' ',' ');
		return tokenizer;
	}
	
	public static Map parse(String input) {
		Map paramMap = new HashMap();
		StreamTokenizer tokenizer = 
			new StreamTokenizer(new StringReader(input));
		try {
			while (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
				String key = tokenizer.sval;
				if (tokenizer.nextToken() != '=') {
					break;
				}
				switch (tokenizer.nextToken()) {
					case StreamTokenizer.TT_WORD:
					case '"':
					case '\'':
						paramMap.put(key, tokenizer.sval);
					default:
						break;
				}
			}
		} catch (IOException e) {
			// ignore
		}
		return paramMap;
	}
	
}
