// class headers
package net.sf.qdox.parser;
%%

// class and lexer definitions
%class Lexer
%byaccj
%line
%column

%state JAVADOC

%%

<YYINITIAL> {

	";"                { return Parser.SEMI; }
	"."                { return Parser.DOT; }
	","                { return Parser.COMMA; }
	"*"                { return Parser.STAR; }
	"="                { return Parser.EQUALS; }

	"package"          { return Parser.PACKAGE; }
	"import"           { return Parser.IMPORT; }
	"public"           { return Parser.PUBLIC; }
	"protected"        { return Parser.PROTECTED; }
	"private"          { return Parser.PRIVATE; }
	"static"           { return Parser.STATIC; }
	"final"            { return Parser.FINAL; }
	"abstract"         { return Parser.ABSTRACT; }
	"class"            { return Parser.CLASS; }
	"interface"        { return Parser.INTERFACE; }
	"throws"           { return Parser.THROWS; }

	"{"                { return Parser.PARENOPEN; }
	"}"                { return Parser.PARENCLOSE; }
	"["                { return Parser.SQUAREOPEN; }
	"]"                { return Parser.SQUARECLOSE; }
	"("                { return Parser.BRACKETOPEN; }
	")"                { return Parser.BRACKETCLOSE; }

	/* comments */
	"//" [^\r\n]* \r|\n|\r\n? { }
	"/*" [^*] ~"*/"    { }

	/* javadoc */
	"/**"              { yybegin(JAVADOC); return Parser.JAVADOCSTART; }

	[A-Za-z_0-9]*      { return Parser.IDENTIFIER; }
}

<JAVADOC> {
	"*/"               { yybegin(YYINITIAL); return Parser.JAVADOCEND; }
	\r|\n|\r\n         { return Parser.JAVADOCNEWLINE; }
	[ \t\*]* "@"       { return Parser.JAVADOCTAGMARK; }
	[^ \t\r\n("*/")@]* { return Parser.JAVADOCTOKEN; }
}

.|\n                       { }