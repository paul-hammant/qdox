package com.thoughtworks.qdox.model.util;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TagParser {
    
    /**
     * Create a StreamTokenizer suitable for parsing the tag text. 
     */
    static StreamTokenizer makeTokenizer(String tagValue) {
        StreamTokenizer tokenizer = 
            new StreamTokenizer(new StringReader(tagValue));
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
    
    /**
     * Extract a Map of named parameters  
     */
    public static Map parseNamedParameters(String tagValue) {
        Map paramMap = new HashMap();
        StreamTokenizer tokenizer = makeTokenizer(tagValue);
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

    /**
     * Extract an array of positional parameters  
     */
    public static String[] parseWords(String tagValue) {
        StreamTokenizer tokenizer = makeTokenizer(tagValue);
        ArrayList wordList = new ArrayList();
        try {
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                if (tokenizer.sval == null) {
                    wordList.add(Character.toString((char)tokenizer.ttype));
                } else {
                    wordList.add(tokenizer.sval);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error tokenizing tag");
        }
        String[] wordArray = new String[wordList.size()];
        wordList.toArray(wordArray);
        return wordArray;
    }
    
}
