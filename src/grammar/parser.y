%{
%}

%token SEMI DOT COMMA STAR EQUALS
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT CLASS INTERFACE THROWS
%token PARENOPEN PARENCLOSE SQUAREOPEN SQUARECLOSE BRACKETOPEN BRACKETCLOSE
%token JAVADOCSTART JAVADOCEND JAVADOCNEWLINE JAVADOCTAGMARK
%token CODEBLOCK STRING

%token <sval> IDENTIFIER JAVADOCTOKEN

%type <sval> fullidentifier dotidentifier

%%

file: packages imports class;

fullidentifier: IDENTIFIER { $$ = $1; }
	| IDENTIFIER dotidentifier { $$ = $1 + "|" + $2; }
	| IDENTIFIER dotidentifier DOT STAR { $$ = $$ = $1 + "|" + $2 + ".*"; }
	;

dotidentifier:
	| dotidentifier DOT IDENTIFIER { $$ = $1 + "|" + $3; }
	;

packages: | package;
package: PACKAGE fullidentifier SEMI { debug("package", null); } ;

imports: | imports import;
import: IMPORT fullidentifier SEMI { debug("import", null); } ;

class: SEMI;

%%

private Lexer lexer;

public Parser(Lexer lexer) {
	this.lexer = lexer;
}

/**
 * Parse file. Return true if successful.
 */
public boolean parse() {
	return yyparse() == 0;
}

public void setDebug(boolean debug) {
	this.yydebug = debug;
}

private int yylex() {
	try {
		final int result = lexer.yylex();
		return result;
	}
	catch(java.io.IOException e) {
		return 0;
	}
}

private void yyerror(String msg) {
	System.out.println(":::::::::::::ERROR: " + msg);
}

private void debug(String type, String val) {
	System.out.println(":::::::::::::GOT: " + type + " [" + val + "]");
}