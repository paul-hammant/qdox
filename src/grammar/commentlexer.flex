package com.thoughtworks.qdox.parser.impl;

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

import com.thoughtworks.qdox.parser.*;
%%

// class and lexer definitions
%class DefaultJavaCommentLexer
%public
%implements Lexer
%byaccj
%unicode
%line
%column

%{
    private int lineOffset = 1;
    private int columnOffset =1;

    private int stateDepth = 0;
    private int[] stateStack = new int[10];

    private StringBuffer codeBody = new StringBuffer(8192);
    private boolean appendingToCodeBody;

    public int lex() throws java.io.IOException {
        return yylex();
    }

    public String text() {
        return yytext();
    }
    
    public int getLine() {
        return yyline + lineOffset;
    }
    
    public void setLineOffset(int lineOffset) {
      this.lineOffset = lineOffset;
    }

    public int getColumn() {
        return yycolumn + columnOffset;
    }
    
    public void setColumnOffset(int columnOffset) {
      this.columnOffset = columnOffset;
    }
    
    public String getCodeBody(){
        String s = codeBody.toString();
        codeBody = new StringBuffer(8192);
        return s;
    }
    
        private void pushState(int newState) {
        stateStack[stateDepth++] = zzLexicalState;
        yybegin(newState);
    }

    private void popState() {
        yybegin(stateStack[--stateDepth]);
    }
    
    private int peekState(int relative) {
      if(relative > stateDepth) {
        return -1;
      }
      else {
        return stateStack[stateDepth - relative];
      }
    }
%}

Eol                     = \r|\n|\r\n
JavadocEnd              = "*"+ "/"

%state JAVADOC JAVADOCCONTENT JAVADOCLINE JAVADOCTAG MULTILINECOMMENT SINGLELINECOMMENT

%%

<YYINITIAL> {
  "//"   { 
           codeBody.append( "//" );
           pushState( SINGLELINECOMMENT ); 
         }
  "/**/" { 
           codeBody.append( "/**/" ); 
         }
  "/**" [*]+ "/" { 
           yypushback(2);
           pushState(JAVADOC);
           return DefaultJavaCommentParser.JAVADOCSTART; 
         }
  "/*" [*]+ {
           pushState( JAVADOC );
           pushState( JAVADOCCONTENT );
           return DefaultJavaCommentParser.JAVADOCSTART;
         }
  "/*"   { 
           codeBody.append( "/*" );
           pushState( MULTILINECOMMENT ); 
         }
}

<JAVADOC> {
    "*"+ [ \t]* / "@" { 
                        pushState(JAVADOCTAG); 
                      }
    "*"+ [ \t]?       { 
                        pushState(JAVADOCLINE); 
                      }
    {JavadocEnd}      { 
                        popState(); 
                        return DefaultJavaCommentParser.JAVADOCEND;
                      }
    "@"               { 
                        yypushback(1);
                        pushState(JAVADOCTAG); 
                      }
    [^ \t\r]		  { 
                        yypushback(1); 
                        pushState(JAVADOCLINE); 
                      }
}
<JAVADOCCONTENT> {
    [ \t]*  "@"       { 
                        yypushback(1);
                        popState(); 
                        pushState(JAVADOCTAG); 
                      }
    [^ \t]  		  { 
                        yypushback(1); 
                        popState();
                        pushState(JAVADOCLINE); 
                      }
   {Eol}              {
                        popState();
                        return DefaultJavaCommentParser.JAVADOCLINE;
                      }
}

<JAVADOCLINE> {
  ~{Eol}                           { 
                                     popState(); 
                                     return DefaultJavaCommentParser.JAVADOCLINE; 
                                   }
  .* [^ \t*] / [ \t]* {JavadocEnd} { 
                                     popState(); 
                                     return DefaultJavaCommentParser.JAVADOCLINE;
                                   }
  {JavadocEnd}                     { 
                                     popState();
                                     popState();
                                     return DefaultJavaCommentParser.JAVADOCEND;
                                   }
}

<JAVADOCTAG> {
  "@" [^ \t\n\r]+ / {JavadocEnd} { 
                                   popState();
                                   return DefaultJavaCommentParser.JAVADOCTAG;
                                 }
  "@" [^ \t\n\r]+                { 
                                   return DefaultJavaCommentParser.JAVADOCTAG;
                                 }
  [ \t]+                         { 
                                   popState();
                                   pushState(JAVADOCLINE);
                                 }
  {Eol}                          { 
                                   popState();
                                   return DefaultJavaCommentParser.JAVADOCLINE;
                                 }
}

<MULTILINECOMMENT, JAVADOC> {
    "*/" { 
           codeBody.append("*/");
           popState();
         }
}

<SINGLELINECOMMENT> {
    {Eol} { 
            codeBody.append(yytext());
            popState();
          }
}

.|\r|\n|\r\n { 
               codeBody.append(yytext());
             }