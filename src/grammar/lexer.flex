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
import java.util.*;

%%

// class and lexer definitions
%class JFlexLexer
%public
%implements JavaLexer
%byaccj
%unicode
%line
%column

%init{
  java.io.InputStream qdoxProperties = this.getClass().getClassLoader().getResourceAsStream( "qdox.properties" );
  if( qdoxProperties != null )
  {
     Properties props = new Properties();
     try
     {
         props.load( qdoxProperties );
         stateStack = new int[ parseValue( props.getProperty( "lexer.statestack.size" ), stateStack.length ) ];
     }
     catch ( java.io.IOException e )
     {
         // failed to load qdoxProperties
     }
     finally 
     {
        try
        {
          qdoxProperties.close();
        }
        catch( java.io.IOException e )
        {
          // noop, we did our best
        }
     }
  }
%init}

%{
	private java.io.Writer writer;
	private List<CommentHandler> commentHandlers = new ArrayList<CommentHandler>();

    private int classDepth = 0;
    private int parenDepth = 0;
    private int nestingDepth = 0;
    private int annotationDepth = 0;
    private int assignmentDepth = 0;
    private int stateDepth = 0;
    private int codeblockDepth = 0;
    private int[] stateStack = new int[10];
    private int braceMode = CODEBLOCK;
    private int parenMode = -1;
    private int annotatedElementLine = 0;
    private StringBuffer codeBody = new StringBuffer(8192);
    private boolean newMode;
    private boolean bracketMode;
    private boolean anonymousMode;
    private boolean enumConstantMode;
    private boolean appendingToCodeBody;

	private void write() {
		write( text() );
	}
 
	private void write( String text ) {
    	try {
            if( writer != null ) {
                writer.write( text );
            }
        }
        catch( java.io.IOException ioe ) {}
	}

    public String text() {
        return yytext();
    }

    public int lex() throws java.io.IOException {
    	if(annotatedElementLine < 0)
    	{
    	  annotatedElementLine = 0;
    	}
        return yylex();
    }
    
    public int getLine() {
        return ( annotatedElementLine == 0 ? yyline + 1 : Math.abs(annotatedElementLine) );
    }

    public int getColumn() {
        return yycolumn + 1;
    }

    private void pushState(int newState) {
        stateStack[stateDepth++] = zzLexicalState;
        yybegin(newState);
    }

    private void popState() {
        yybegin(stateStack[--stateDepth]);
    }
    
    public String getCodeBody(){
        String s = codeBody.toString();
        codeBody = new StringBuffer(8192);
        return s;
    }
    
    public void addCommentHandler(CommentHandler handler) {
      this.commentHandlers.add(handler);
    }
    
    private int parseValue( String value, int defaultValue )
    {
      int result;
      try 
      {
        result = Integer.parseInt( value );
      }
      catch( NumberFormatException e )
      {
        result = defaultValue;
      }
      return result;
    }
    
    private void markAnnotatedElementLine()
    {
      if( annotatedElementLine <= 0 )
      {
        annotatedElementLine = getLine();
      }
    }

    private void resetAnnotatedElementLine()
    {
      annotatedElementLine = - Math.abs(annotatedElementLine);
    }
    
    public JFlexLexer( java.io.Reader reader, java.io.Writer writer ) {
       this( reader );
       this.writer = writer;
  	}

    public JFlexLexer( java.io.InputStream stream, java.io.Writer writer ) {
       this( stream );
       this.writer = writer;
  	}

%}

Eol                             = \r|\n|\r\n
WhiteSpace                      = {Eol} | [ \t\f]
CommentChar                     = ( [^ \t\r\n*] | "*"+ [^ \t\r\n/*] )
DecimalNumeral                  = ( [0-9] | [1-9] [_0-9]* [0-9] )
Digits                          = ( [0-9] | [0-9] [_0-9]* [0-9]   )
HexDigits                       = ( [0-9a-fA-F] | [0-9a-fA-F] [_0-9a-fA-F]* [0-9a-fA-F] )
HexNumeral                      = ( "0" [xX] {HexDigits} )
OctalNumeral                    = ( "0" [_0-7]* [0-7] )
BinaryNumeral                   = ( "0" [bB] ( [01] | [01] [_01]* [01] ) )
IntegerLiteral			        = ( {DecimalNumeral} | {BinaryNumeral} | {HexNumeral} | {OctalNumeral} ) ([lL])?
Exponent				        = [eE] [+-]? {DecimalNumeral}
FloatingPointLiteral            = ( {DecimalFloatingPointLiteral} | {HexadecimalFloatingPointLiteral} )
DecimalFloatingPointLiteral	    = ( {Digits} ("." {Digits})? ({Exponent})? ([dDfF])? ) |
						          ( "." {Digits} ({Exponent})? ([dDfF])?) |
						          ( {Digits} {Exponent} ([dDfF])?) |
						          ( {Digits} ({Exponent} )? ([dDfF]) )
BinaryExponent                  = [pP] [+-]? ({DecimalNumeral})+					          
HexSignificand                  = ( {HexNumeral} "."? ) |
                                  ( "0" [xX] ( {HexDigits} )? "." ( {HexDigits} ) )
HexadecimalFloatingPointLiteral = {HexSignificand} {BinaryExponent} ([dDfF])?
UnicodeChar                     = \\u[a-fA-F0-9]{4}						  
Id						        = ([:jletter:]|{UnicodeChar}) ([:jletterdigit:]|{UnicodeChar})*
JavadocEnd                      = "*"+ "/"

%state JAVADOC JAVADOCTAG JAVADOCLINE CODEBLOCK PARENBLOCK ASSIGNMENT STRING CHAR SINGLELINECOMMENT MULTILINECOMMENT  ANNOTATION ANNOSTRING ANNOCHAR ARGUMENTS NAME 
%state ANNOTATIONTYPE ENUM MODULE TYPE ANNOTATIONNOARG ATANNOTATION
%state NAME_OR_MODIFIER

%%
<YYINITIAL> {
    "open"              { if ( classDepth == 0 )
                          {
                            return Parser.OPEN;
                          }
                          else
                          {
                            return Parser.IDENTIFIER;
                          } 
                        }
    "module"            { if ( classDepth == 0 )
                          {
                            pushState(MODULE);
                            pushState(NAME);
                            return Parser.MODULE;
                          }
                          else
                          {
                            return Parser.IDENTIFIER;
                          } 
                        }
}
<NAME_OR_MODIFIER> {
    {Id} / {WhiteSpace}* "."  { popState(); pushState(NAME); return Parser.IDENTIFIER; }
    {Id} / {WhiteSpace}* ";"  { popState(); return Parser.IDENTIFIER; }
    "static"                  { return Parser.STATIC; }
    "transitive"              { return Parser.TRANSITIVE; }
}
<ANNOTATIONNOARG> {
  {WhiteSpace} { popState(); }
}
<YYINITIAL, ANNOTATIONNOARG, ANNOTATIONTYPE, ENUM, NAME, TYPE> {
    "."                 { return Parser.DOT; }
    "..."               { return Parser.DOTDOTDOT; }
    ","                 { return Parser.COMMA; }
    "*"                 { popState(); return Parser.STAR; }

    "package"           { markAnnotatedElementLine(); pushState(NAME);
                          return Parser.PACKAGE; }
    "import"            { pushState(NAME);
                          return Parser.IMPORT; }
    "public"            { markAnnotatedElementLine(); return Parser.PUBLIC; }
    "protected"         { markAnnotatedElementLine(); return Parser.PROTECTED; }
    "private"           { markAnnotatedElementLine(); return Parser.PRIVATE; }
    "static"            { markAnnotatedElementLine(); return Parser.STATIC; }
    "final"             { markAnnotatedElementLine(); return Parser.FINAL; }
    "abstract"          { markAnnotatedElementLine(); return Parser.ABSTRACT; }
    "native"            { markAnnotatedElementLine(); return Parser.NATIVE; }
    "strictfp"          { markAnnotatedElementLine(); return Parser.STRICTFP; }
    "synchronized"      { markAnnotatedElementLine(); return Parser.SYNCHRONIZED; }
    "transient"         { markAnnotatedElementLine(); return Parser.TRANSIENT; }
    "volatile"          { markAnnotatedElementLine(); return Parser.VOLATILE; }
    "throws"            { return Parser.THROWS; }
    "extends"           { return Parser.EXTENDS; }
    "implements"        { return Parser.IMPLEMENTS; }
    "super"             { return Parser.SUPER; }
    "new"               { return Parser.NEW; }

    "["                 { nestingDepth++; return Parser.SQUAREOPEN; }
    "]"                 { nestingDepth--; return Parser.SQUARECLOSE; }
    ")"                 { nestingDepth--; return Parser.PARENCLOSE; }
    "<"                 { return Parser.LESSTHAN; }
    ">"                 { return Parser.GREATERTHAN; }
    "&"                 { return Parser.AMPERSAND; }
    "?"                 { return Parser.QUERY; }

    "@" {WhiteSpace}* "interface" {
        markAnnotatedElementLine();
      	classDepth++;
        braceMode = ANNOTATIONTYPE;
        pushState(NAME);
        return Parser.ANNOINTERFACE;
	  }

    "class"             {
        markAnnotatedElementLine();
        classDepth++;
        braceMode = TYPE;
        pushState(NAME);
        return Parser.CLASS; 
    }
    
    "interface"         { 
        markAnnotatedElementLine();
        classDepth++;
        braceMode = TYPE;
        pushState(NAME);
        return Parser.INTERFACE;
    }
    
    "enum"              {
        markAnnotatedElementLine();
        classDepth++;
        braceMode = ENUM;
        pushState(NAME);
        return Parser.ENUM;
    }
    "@"                 {
        markAnnotatedElementLine();
        pushState(ATANNOTATION);
        return Parser.AT;
    }
    "{"                 {
        resetAnnotatedElementLine();
        if(braceMode >= 0) {
          if(braceMode == ENUM) {
            enumConstantMode = true;
          } else if (braceMode == CODEBLOCK) {
              getCodeBody(); /* reset codebody */
              appendingToCodeBody = true;
          }
          pushState(braceMode);
          braceMode = -1;
          yypushback(1); /* (re)enter brace in right mode */
        }
        else {
          nestingDepth++;
          if (enumConstantMode && yystate() == ENUM)
          {
            braceMode = TYPE;
          }
          else 
          {
            braceMode = CODEBLOCK;
          }
          return Parser.BRACEOPEN;
        }
    }
    "}"  { 
        nestingDepth--;
        classDepth--;
        popState();
        if ( yystate() == ENUM && enumConstantMode)
        {
          braceMode = TYPE;
        }
        else
        {
          braceMode = CODEBLOCK;
        }
        return Parser.BRACECLOSE; 
    }

    "/**" ~"*/" {
      for( CommentHandler handler: commentHandlers ) {
        handler.onComment( text(), getLine(), getColumn() );
      }
    }

    "=" {WhiteSpace}* { 
        assignmentDepth = nestingDepth;
        getCodeBody(); /* reset codebody */
        appendingToCodeBody = true;
        pushState(ASSIGNMENT);
    }
}
<ATANNOTATION>
{
    "."                       { return Parser.DOT; }
    {Id} / {WhiteSpace}* "."  { return Parser.IDENTIFIER; }
    {Id} / {WhiteSpace}* "("  { parenMode = ANNOTATION; getCodeBody(); /* reset codebody */; popState(); resetAnnotatedElementLine(); return Parser.IDENTIFIER; }
    {Id}                      { resetAnnotatedElementLine(); popState(); return Parser.IDENTIFIER; }
}

<YYINITIAL, ANNOTATIONTYPE, TYPE> {
    ";"  {  resetAnnotatedElementLine();
            return Parser.SEMI; }
    "("  {
            nestingDepth++;
            if( parenMode >= 0 ) {
              annotationDepth = nestingDepth;
              pushState(parenMode);
              parenMode = -1;
            }
            return Parser.PARENOPEN;
          }
}
<MODULE> {
	"{"                 { return Parser.BRACEOPEN; }
    "}"                 { popState(); 
                          return Parser.BRACECLOSE; }

    ","                 { pushState(NAME); return Parser.COMMA; }
	";"                 { return Parser.SEMI; }
	
    "exports"           { pushState(NAME); return Parser.EXPORTS; }
    "opens"             { pushState(NAME); return Parser.OPENS; }
    "provides"          { pushState(NAME); return Parser.PROVIDES; }
    "requires"          { pushState(NAME_OR_MODIFIER); return Parser.REQUIRES; }
    "to"                { pushState(NAME); return Parser.TO; }
    "uses"              { pushState(NAME); return Parser.USES; }
    "with"              { pushState(NAME); return Parser.WITH; }
}
<ENUM> {
    ";"  { 
    		enumConstantMode = false; 
    		braceMode = CODEBLOCK; 
    		return Parser.SEMI;
    	 }
    "("  {
            nestingDepth++;
            if(parenMode >= 0) {
              annotationDepth = nestingDepth;
              pushState(parenMode);
              parenMode = -1;
              return Parser.PARENOPEN;
            }
            else if(enumConstantMode) 
            {  
              annotationDepth = nestingDepth;
              pushState(ARGUMENTS);
              return Parser.PARENOPEN;
            }
            else {
                return Parser.PARENOPEN;
            }
         }
}
<ENUM, TYPE> {
    "default"           { return Parser.DEFAULT; }
}
<ANNOTATIONTYPE> {
	"default"           { assignmentDepth = nestingDepth; appendingToCodeBody = true; pushState(ASSIGNMENT); }
}
<NAME> {
    {Id} / {WhiteSpace}* "."   { return Parser.IDENTIFIER; }
    {Id} / {WhiteSpace}* [;{(] { resetAnnotatedElementLine(); popState(); return Parser.IDENTIFIER; }
    {Id}                       { popState(); return Parser.IDENTIFIER; }
}
<YYINITIAL, ANNOTATIONNOARG, ANNOTATIONTYPE, ENUM, MODULE, TYPE> {
    {Id} { return Parser.IDENTIFIER;
         }
}
<CODEBLOCK> {
     "{"  { 
            if(codeblockDepth++ > 0 ) {
            codeBody.append('{');
            }  
          }
     "}"                 {
        if (--codeblockDepth == 0) {
            popState();
            appendingToCodeBody = false;
            braceMode = CODEBLOCK;
            return Parser.CODEBLOCK;
        } else {
            codeBody.append('}');
        }
    }
}

<ANNOTATION> {
	"{"                 { nestingDepth++; return Parser.BRACEOPEN; }
    "}"                 { nestingDepth--; return Parser.BRACECLOSE; }
}
<ARGUMENTS> {
	"{"                 { pushState(CODEBLOCK);
                          braceMode = -1;
                          yypushback(1); /* (re)enter brace in right mode */ }
    "}"                 { return Parser.BRACECLOSE; }
}

<ANNOTATION,ARGUMENTS> {
	"("                 { ++ nestingDepth; return Parser.PARENOPEN; }
    ")"                 { if( nestingDepth-- == annotationDepth) { popState(); } return Parser.PARENCLOSE; }
    "["                 { return Parser.SQUAREOPEN; }
    "]"                 { return Parser.SQUARECLOSE; }

	","                 { return Parser.COMMA; }
    "="                 { return Parser.EQUALS; }
    "*="                { return Parser.STAREQUALS; }
    "/="                { return Parser.SLASHEQUALS; }
    "%="                { return Parser.PERCENTEQUALS; }
    "+="                { return Parser.PLUSEQUALS; }
    "-="                { return Parser.MINUSEQUALS; }
    "<<="               { return Parser.LESSTHAN2EQUALS; }
    ">>="               { return Parser.GREATERTHAN2EQUALS; }
    ">>>="              { return Parser.GREATERTHAN3EQUALS; }
    "&="                { return Parser.AMPERSANDEQUALS; }
    "^="                { return Parser.CIRCUMFLEXEQUALS; }
    "|="                { return Parser.VERTLINEEQUALS; }
    
    "++"                { return Parser.PLUSPLUS; }
    "--"                { return Parser.MINUSMINUS; }

	"\""                { appendingToCodeBody=true; codeBody.append("\""); pushState(ANNOSTRING); }
    "\'"                { appendingToCodeBody=true; codeBody.append("\'"); pushState(ANNOCHAR); }

	"."                 { return Parser.DOT; }

    "?"                 { return Parser.QUERY; }
    "::"                { return Parser.COLONCOLON; }
    ":"                 { return Parser.COLON; }
    "<<"                { return Parser.LESSTHAN2; }
    ">>>"               { return Parser.GREATERTHAN3; }
    ">>"                { return Parser.GREATERTHAN2; }
    "=="                { return Parser.EQUALS2; }
    "!="                { return Parser.NOTEQUALS; }
    "<"                 { return Parser.LESSTHAN; }
    ">"                 { return Parser.GREATERTHAN; }
    "<="                { return Parser.LESSEQUALS; }
    ">="                { return Parser.GREATEREQUALS; }
    "*"                 { return Parser.STAR; }
    "/"                 { return Parser.SLASH; }
    "%"                 { return Parser.PERCENT; }
    "+"                 { return Parser.PLUS; }
    "-"                 { return Parser.MINUS; }
    
    "byte"              { return Parser.BYTE; }
    "char"              { return Parser.CHAR; }
    "short"             { return Parser.SHORT; }
    "int"               { return Parser.INT; }
    "long"              { return Parser.LONG; }
    "float"             { return Parser.FLOAT; }
    "double"            { return Parser.DOUBLE; }

    "&&"                { return Parser.AMPERSAND2; }
    "||"                { return Parser.VERTLINE2; }
    "!"                	{ return Parser.EXCLAMATION; }

    "&"                 { return Parser.AMPERSAND; }
    "|"                 { return Parser.VERTLINE; }
    "^"                	{ return Parser.CIRCUMFLEX; }
    "~"                 { return Parser.TILDE; }

	{IntegerLiteral}	{ return Parser.INTEGER_LITERAL; }
	{FloatingPointLiteral} { return Parser.FLOAT_LITERAL; }
	"true" | "false"	{ return Parser.BOOLEAN_LITERAL; }
	
	"class"				{ return Parser.CLASS; }
	
	"new"               { return Parser.NEW; }

	{Id}                { return Parser.IDENTIFIER; }

	"@" 				{ return Parser.AT; }
}

<ANNOSTRING> {
	"\""            { codeBody.append("\""); popState(); appendingToCodeBody=false; return Parser.STRING_LITERAL; }
	"\\\""          { codeBody.append("\\\""); }
	"\\\\"          { codeBody.append("\\\\"); }
}

<ANNOCHAR> {
	\'              { codeBody.append("\'"); popState(); appendingToCodeBody=false; return Parser.CHAR_LITERAL; }
	"\\'"           { codeBody.append("\\'"); }
	"\\\\"          { codeBody.append("\\\\"); }
}

<PARENBLOCK> {
    "("             { 
        nestingDepth++; 
        if (appendingToCodeBody) { codeBody.append("("); }
    }
    ")"             {
		nestingDepth--;
		if (appendingToCodeBody) { codeBody.append(")"); }
        if (nestingDepth == parenDepth) {
            popState();
        }
    }
}

<ASSIGNMENT> {
    ";"                 { 
        resetAnnotatedElementLine();
        if (nestingDepth == assignmentDepth) {
            appendingToCodeBody = true;
            newMode = false;
            popState(); 
            return Parser.SEMI; 
        } else {
            codeBody.append(';');
        }
    }
    ","                 {
        if (nestingDepth == assignmentDepth) {
            appendingToCodeBody = true;
            popState(); 
            return Parser.COMMA; 
        } else {
            codeBody.append(',');
        }
    }
    "{"                 {
        codeBody.append('{');
		anonymousMode = true;
		nestingDepth++;
    }
    "}"                 {
		codeBody.append('}');
        nestingDepth--;
    	if (nestingDepth==assignmentDepth) { 
            anonymousMode=false;
        }
    }

    "("                 { 
        codeBody.append('('); 
        parenDepth = nestingDepth++; 
        pushState(PARENBLOCK); 
    }
    ")"                 {
        codeBody.append(')');
        nestingDepth--; 
        if (nestingDepth < assignmentDepth) {
            appendingToCodeBody = true; 
            popState(); 
            return Parser.PARENCLOSE; 
        }
    }
    "["                 { codeBody.append('['); bracketMode = true; nestingDepth++; }
    "]"                 { codeBody.append(']'); bracketMode = false; nestingDepth--; }
    "new"               {
        codeBody.append("new");
        if (nestingDepth==assignmentDepth) {
            newMode=true;
        } 
    }
    "." [ \t\r\n]* / "<" {
      codeBody.append('.');
      newMode = true;
    }
    "<"                 {
        codeBody.append('<');
        if (!bracketMode && newMode && !anonymousMode) {
            nestingDepth++; 
        }
    }
    ">"                 {
        codeBody.append('>');
        if (!anonymousMode) {
	        if (!bracketMode && newMode) {
    	        nestingDepth--;
    	    	if (nestingDepth==assignmentDepth) { 
    	            newMode=false;
    	        }
        	}
        }
    }
}

<ASSIGNMENT, YYINITIAL, CODEBLOCK, PARENBLOCK, ENUM, ANNOTATIONTYPE, TYPE> {
    "\""                { if (appendingToCodeBody) { codeBody.append('"');  } pushState(STRING); }
    \'                  { if (appendingToCodeBody) { codeBody.append('\''); } pushState(CHAR); }
}

<ASSIGNMENT, YYINITIAL, CODEBLOCK, PARENBLOCK, ENUM, ANNOTATIONTYPE, ANNOTATION, ATANNOTATION, ARGUMENTS, TYPE, NAME, MODULE > {
  "//"                { if (appendingToCodeBody) { codeBody.append("//"); } pushState(SINGLELINECOMMENT); }
  "/*"                { if (appendingToCodeBody) { codeBody.append("/*"); } pushState(MULTILINECOMMENT); }
  "/**/"              { if (appendingToCodeBody) { codeBody.append("/**/"); } }
}

<CODEBLOCK, ASSIGNMENT> { 
    .|{WhiteSpace}	    { codeBody.append(yytext()); }
}

<STRING> {
    "\""                { if (appendingToCodeBody) { codeBody.append('"');    } popState(); }
    "\\\""              { if (appendingToCodeBody) { codeBody.append("\\\""); } }
    "\\\\"              { if (appendingToCodeBody) { codeBody.append("\\\\"); } }
}

<CHAR> {
    \'                  { if (appendingToCodeBody) { codeBody.append('\'');    } popState(); }
    "\\'"               { if (appendingToCodeBody) { codeBody.append("\\'");  } }
    "\\\\"              { if (appendingToCodeBody) { codeBody.append("\\\\"); } }
}

<SINGLELINECOMMENT> {
    {Eol}               { if (appendingToCodeBody) { codeBody.append(yytext()); } popState(); }
}

<MULTILINECOMMENT> {
    "*/"                { if (appendingToCodeBody) { codeBody.append("*/"); } popState(); }
}

.|\r|\n|\r\n            { if (appendingToCodeBody) { codeBody.append(yytext()); } }
