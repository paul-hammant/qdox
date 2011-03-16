%{
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

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.structs.TagDef;
%}

%token JAVADOCSTART JAVADOCEND

// strongly typed tokens/types
%token <sval> JAVADOCTAG JAVADOCLINE
%%
javadoc: JAVADOCSTART javadocdescription_opt javadoctags_opt JAVADOCEND;

javadocdescription_opt:
                      | javadocdescription;

javadocdescription: javadoctokens 
                    { 
                      builder.addJavaDoc(buffer()); 
                    };

javadoctokens_opt:
                 | javadoctokens;
                 
javadoctokens: javadoctoken
             | javadoctokens javadoctoken;

javadoctoken: JAVADOCLINE 
              {
                appendToBuffer($1);
              }; 

javadoctags_opt: 
               | javadoctags;
               
javadoctags: javadoctag
           | javadoctags javadoctag;

javadoctag: JAVADOCTAG 
            { 
              line = lexer.getLine(); 
            } 
            javadoctokens_opt 
            {
              builder.addJavaDocTag(new TagDef($1.substring(1), buffer(), line)); 
            };

%%

private Lexer lexer;
private Builder builder;

private int line;
private int column;
private boolean debugLexer;

private StringBuffer textBuffer = new StringBuffer();

public DefaultJavaCommentParser(Lexer lexer, Builder builder) {
    this.lexer = lexer;
    this.builder = builder;
}

public void setDebugParser(boolean debug) {
    yydebug = debug;
}

public void setDebugLexer(boolean debug) {
    debugLexer = debug;
}

private void appendToBuffer(String word) {
    if (textBuffer.length() > 0) {
        char lastChar = textBuffer.charAt(textBuffer.length() - 1);
        if (!Character.isWhitespace(lastChar)) {
            textBuffer.append(' ');
        }
    }
    textBuffer.append(word);
}

private String buffer() {
    String result = textBuffer.toString().trim();
    textBuffer.setLength(0);
    return result;
}

public boolean parse() {
    return yyparse() == 0;
}

private int yylex() {
    try {
        final int result = lexer.lex();
        yylval = new DefaultJavaCommentParserVal();
        yylval.sval = lexer.text();
        if (debugLexer) {
            System.err.println("Token: " + yyname[result] + " \"" + yylval.sval + "\"");
        }
        return result;
    }
    catch(IOException e) {
        return 0;
    }
}

private void yyerror(String msg) {
    throw new ParseException(msg, lexer.getLine(), lexer.getColumn());
}
    