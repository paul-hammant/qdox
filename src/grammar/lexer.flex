// class headers
package com.thoughtworks.qdox.parser.impl;
import com.thoughtworks.qdox.parser.*;
%%

// class and lexer definitions
%class JFlexLexer
%public
%implements Lexer
%byaccj
%unicode
%line

%{

	private int classDepth = 0;
	private int braceDepth = 0;
	private int stateDepth = 0;
	private int[] stateStack = new int[10];
	private boolean javaDocNewLine;
	private boolean javaDocStartedContent;

	public String text() {
		return yytext();
	}

	public int lex() throws java.io.IOException {
		return yylex();
	}
	
	public int getLine() {
		return yyline;
	}

	private void pushState(int newState) {
		stateStack[stateDepth++] = yy_lexical_state;
		yybegin(newState);
	}

	private void popState() {
		yybegin(stateStack[--stateDepth]);
	}

%}

Eol                     = \r|\n|\r\n
CommentChar             = ( [^ \t\r\n*] | "*"+ [^ \t\r\n/] )

%state JAVADOC CODEBLOCK ASSIGNMENT STRING CHAR SINGLELINECOMMENT MULTILINECOMMENT

%%

<YYINITIAL> {
	";"                { return Parser.SEMI; }
	"."                { return Parser.DOT; }
	","                { return Parser.COMMA; }
	"*"                { return Parser.STAR; }

	"package"          { return Parser.PACKAGE; }
	"import"           { return Parser.IMPORT; }
	"public"           { return Parser.PUBLIC; }
	"protected"        { return Parser.PROTECTED; }
	"private"          { return Parser.PRIVATE; }
	"static"           { return Parser.STATIC; }
	"final"            { return Parser.FINAL; }
	"abstract"         { return Parser.ABSTRACT; }
	"native"           { return Parser.NATIVE; }
	"strictfp"         { return Parser.STRICTFP; }
	"synchronized"     { return Parser.SYNCHRONIZED; }
	"transient"        { return Parser.TRANSIENT; }
	"volatile"         { return Parser.VOLATILE; }
	"throws"           { return Parser.THROWS; }
	"extends"          { return Parser.EXTENDS; }
	"implements"       { return Parser.IMPLEMENTS; }

	"["                { return Parser.SQUAREOPEN; }
	"]"                { return Parser.SQUARECLOSE; }
	"("                { return Parser.PARENOPEN; }
	")"                { return Parser.PARENCLOSE; }

	"class"            {
		classDepth++;
		return Parser.CLASS; 
	}
	"interface"        { 
		classDepth++;
		return Parser.INTERFACE; 
	}

	"{"                {
		braceDepth++;
		if (braceDepth == classDepth + 1) {
			pushState(CODEBLOCK);
		}
		else {
			return Parser.BRACEOPEN;
		}
	}
	"}"                { 
		braceDepth--;
		if (braceDepth == classDepth - 1) {
			classDepth--;
		}
		return Parser.BRACECLOSE; 
	}

	"/**"              { pushState(JAVADOC); javaDocNewLine = true; return Parser.JAVADOCSTART; }
	"="                { pushState(ASSIGNMENT); }
	[A-Za-z_0-9]*      { return Parser.IDENTIFIER; }

}

<JAVADOC> {
	"*/"               { popState(); return Parser.JAVADOCEND; }
	^[ \t]*\*/[^/*]    { /* ignore */ }
	{Eol}              { javaDocNewLine = true; return Parser.JAVADOCNEWLINE; }
	{CommentChar}* "*"+ / [ \t\r\n] {
                return Parser.JAVADOCTOKEN;
        }
	{CommentChar}+ { 
                int token = Parser.JAVADOCTOKEN;
                if (javaDocNewLine && yycharat(0) == '@') {
                        token = Parser.JAVADOCTAG;
                }
                javaDocNewLine = false;
                return token;
        }
}

<CODEBLOCK> {
	"{"                { braceDepth++; }
	"}"                {
		braceDepth--;
		if (braceDepth == classDepth) {
			popState();
			return Parser.CODEBLOCK;
		}
	}
}

<ASSIGNMENT> {
	";"                { 
	    if (braceDepth == classDepth) { 
			popState(); 
			return Parser.ASSIGNMENT; 
		} 
	}
	"{"                { braceDepth++; }
	"}"                { braceDepth--; }
}

<ASSIGNMENT, CODEBLOCK, YYINITIAL> {
	"\""               { pushState(STRING); }
	\'                 { pushState(CHAR); }
	"//"               { pushState(SINGLELINECOMMENT); }
	"/*"               { pushState(MULTILINECOMMENT); }
}

<STRING> {
  "\""               { popState(); }
  "\\\""             { }
  "\\\\"             { }
}

<CHAR> {
  \'                 { popState(); }
  "\\'"              { }
  "\\\\"             { }
}

<SINGLELINECOMMENT> {
	{Eol}        { popState(); }
}

<MULTILINECOMMENT> {
	"*/"               { popState(); }
}

.|\n                 { }
