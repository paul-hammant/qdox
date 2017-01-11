package com.thoughtworks.qdox.model.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class TagParser {
    
    private TagParser()
    {
        // hide utility class constructor
    }
    
    /**
     * Create a StreamTokenizer suitable for parsing the tag text. 
     * 
     * @param tagValue the tag value
     * @return the tokenizer
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
        tokenizer.wordChars('<','<');
        tokenizer.wordChars('>','>');
        tokenizer.quoteChar('\'');
        tokenizer.quoteChar('"');
        tokenizer.whitespaceChars(' ',' ');
        tokenizer.whitespaceChars('\t','\t');
        tokenizer.whitespaceChars('\n','\n');
        tokenizer.whitespaceChars('\r','\r');
        tokenizer.eolIsSignificant(false);
        return tokenizer;
    }
    
    /**
     * Extract a Map of named parameters
     * 
     * @param tagValue the tag value
     * @return a Map with the parameter names and their values
     */
    public static Map<String, String> parseNamedParameters(String tagValue) {
        Map<String, String> paramMap = new LinkedHashMap<String, String>();
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
     * 
     * @param tagValue the tag value
     * @return an array with words
     */
    public static String[] parseWords(String tagValue) {
        StreamTokenizer tokenizer = makeTokenizer(tagValue);
        List<String> wordList = new ArrayList<String>();
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
        return wordList.toArray(new String[0]);
    }
    
    /**
     * Extract an array of parameters as name or name=value representation
     * 
     * @param tagValue the tag value
     * @return the names of the parameters
     * @since 1.11  
     */
    public static List<String> parseParameters(String tagValue) {
        StreamTokenizer tokenizer = makeTokenizer(tagValue);
        List<String> wordList = new LinkedList<String>();
        try {
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                StringBuilder param = new StringBuilder();
                if (tokenizer.sval != null) {
                    param.append( tokenizer.sval );
                }
                
                //check for parameterValues
                while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                    if(tokenizer.sval == null && ('=' == (char)tokenizer.ttype || ','== (char)tokenizer.ttype)){
                        param.append( Character.toString( (char)tokenizer.ttype ) );
                        
                        //file was parsed correctly, so this works.
                        tokenizer.nextToken();
                        param.append(tokenizer.sval); 
                    }
                    else {
                        tokenizer.pushBack(); break;
                    }
                }
                wordList.add(param.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error tokenizing tag");
        }
        return wordList;
    }
    
}
