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

	private void pushState(int newState) {
		stateStack[stateDepth++] = yy_lexical_state;
		yybegin(newState);
	}

	private void popState() {
		yybegin(stateStack[--stateDepth]);
	}

%}

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
	\r|\n|\r\n         { javaDocNewLine = true; javaDocStartedContent = false; return Parser.JAVADOCNEWLINE; }
	"*"                { if (javaDocStartedContent) return Parser.JAVADOCTOKEN; }
	[^ \t\r\n\*@][^ \t\r\n]* { javaDocStartedContent = true; return Parser.JAVADOCTOKEN; }
	"@"                { if (javaDocNewLine) return Parser.JAVADOCTAGMARK; }
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
	\r|\n|\r\n         { popState(); }
}

<MULTILINECOMMENT> {
	"*/"               { popState(); }
}

.|\n                 { }
