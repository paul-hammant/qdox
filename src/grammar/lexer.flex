// class headers
package net.sf.qdox.parser;
%%

// class and lexer definitions
%class JFlexLexer
%implements Lexer
%byaccj
%line
%column

%{
	private int parenDepth = 0;

	public String text() {
		return yytext();
	}

	public int lex() throws java.io.IOException {
		return yylex();
	}

%}

%state JAVADOC CODEBLOCK STRING

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

	"["                { return Parser.SQUAREOPEN; }
	"]"                { return Parser.SQUARECLOSE; }
	"("                { return Parser.BRACKETOPEN; }
	")"                { return Parser.BRACKETCLOSE; }

	"{"                {
		parenDepth++;
		if (parenDepth == 2) {
			yybegin(CODEBLOCK);
		}
		else {
			return Parser.PARENOPEN;
		}
	}

	"}"                {
		parenDepth--;
		return Parser.PARENCLOSE;
	}

	"\""               { yybegin(STRING); }

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
	[^ \t\r\n\*@]*     { return Parser.JAVADOCTOKEN; }
	"@"                { return Parser.JAVADOCTAGMARK; }
}

<CODEBLOCK> {

	"{"                {
		parenDepth++;
	}

	"}"                {
		parenDepth--;
		if (parenDepth == 1) {
			yybegin(YYINITIAL);
			return Parser.CODEBLOCK;
		}
	}

}

<STRING> {
  "\""               { yybegin(YYINITIAL); return Parser.STRING; }
}

.|\n                       { }