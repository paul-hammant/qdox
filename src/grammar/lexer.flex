// class headers
package net.sf.qdox.parser;
%%

// class and lexer definitions
%class Lexer
%byaccj
%line
%column

%{
	private int parenDepth = 0;
%}

%state JAVADOC CODEBLOCK STRING

%%

<YYINITIAL> {

	";"                { return Tokens.SEMI; }
	"."                { return Tokens.DOT; }
	","                { return Tokens.COMMA; }
	"*"                { return Tokens.STAR; }
	"="                { return Tokens.EQUALS; }

	"package"          { return Tokens.PACKAGE; }
	"import"           { return Tokens.IMPORT; }
	"public"           { return Tokens.PUBLIC; }
	"protected"        { return Tokens.PROTECTED; }
	"private"          { return Tokens.PRIVATE; }
	"static"           { return Tokens.STATIC; }
	"final"            { return Tokens.FINAL; }
	"abstract"         { return Tokens.ABSTRACT; }
	"class"            { return Tokens.CLASS; }
	"interface"        { return Tokens.INTERFACE; }
	"throws"           { return Tokens.THROWS; }

	"["                { return Tokens.SQUAREOPEN; }
	"]"                { return Tokens.SQUARECLOSE; }
	"("                { return Tokens.BRACKETOPEN; }
	")"                { return Tokens.BRACKETCLOSE; }

	"{"                {
		parenDepth++;
		if (parenDepth == 2) {
			yybegin(CODEBLOCK);
		}
		else {
			return Tokens.PARENOPEN;
		}
	}

	"}"                {
		parenDepth--;
		return Tokens.PARENCLOSE;
	}

	"\""               { yybegin(STRING); }

	/* comments */
	"//" [^\r\n]* \r|\n|\r\n? { }
	"/*" [^*] ~"*/"    { }

	/* javadoc */
	"/**"              { yybegin(JAVADOC); return Tokens.JAVADOCSTART; }

	[A-Za-z_0-9]*      { return Tokens.IDENTIFIER; }
}

<JAVADOC> {
	"*/"               { yybegin(YYINITIAL); return Tokens.JAVADOCEND; }
	\r|\n|\r\n         { return Tokens.JAVADOCNEWLINE; }
	[^ \t\r\n\*@]*     { return Tokens.JAVADOCTOKEN; }
	"@"                { return Tokens.JAVADOCTAGMARK; }
//	[ \t\*]* "@"       { return Tokens.JAVADOCTAGMARK; }
//	[ \t\*]*           { }
//	[^\*]*             { return Tokens.JAVADOCTOKEN; }
//	[^ \t\r\n("*/")@]* { return Tokens.JAVADOCTOKEN; }
}

<CODEBLOCK> {

	"{"                {
		parenDepth++;
	}

	"}"                {
		parenDepth--;
		if (parenDepth == 1) {
			yybegin(YYINITIAL);
			return Tokens.CODEBLOCK;
		}
	}

}

<STRING> {
  "\""               { yybegin(YYINITIAL); return Tokens.STRING; }
}

.|\n                       { }