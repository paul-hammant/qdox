// class headers
package net.sf.qdox.parser;
%%

// class and lexer definitions
%class JFlexLexer
%public
%implements Lexer
%byaccj

%{

	private int parenDepth = 0, stateDepth = 0;
	private int[] stateStack = new int[2];

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
	"class"            { return Parser.CLASS; }
	"interface"        { return Parser.INTERFACE; }
	"throws"           { return Parser.THROWS; }
	"extends"          { return Parser.EXTENDS; }
	"implements"       { return Parser.IMPLEMENTS; }

//	"["                { return Parser.SQUAREOPEN; }
//	"]"                { return Parser.SQUARECLOSE; }
	"("                { return Parser.BRACKETOPEN; }
	")"                { return Parser.BRACKETCLOSE; }

	"{"                {
		parenDepth++;
		if (parenDepth == 2) {
			pushState(CODEBLOCK);
		}
		else {
			return Parser.PARENOPEN;
		}
	}
	"}"                { parenDepth--; return Parser.PARENCLOSE; }

	"/**"              { pushState(JAVADOC); return Parser.JAVADOCSTART; }
	"="                { pushState(ASSIGNMENT); }
	[A-Za-z_0-9]*      { return Parser.IDENTIFIER; }

}

<JAVADOC> {
	"*/"               { popState(); return Parser.JAVADOCEND; }
	\r|\n|\r\n         { return Parser.JAVADOCNEWLINE; }
	[^ \t\r\n\*@]*     { return Parser.JAVADOCTOKEN; }
	"@"                { return Parser.JAVADOCTAGMARK; }
}

<CODEBLOCK> {
	"{"                { parenDepth++; }
	"}"                {
		parenDepth--;
		if (parenDepth == 1) {
			popState();
			return Parser.CODEBLOCK;
		}
	}
}

<ASSIGNMENT> {
	";"                { if (parenDepth <= 1) { popState(); return Parser.ASSIGNMENT; } }
	"{"                { parenDepth++; }
	"}"                { parenDepth--; }
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