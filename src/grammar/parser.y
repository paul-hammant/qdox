%{
%}

%token SEMI DOT COMMA STAR EQUALS
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT CLASS INTERFACE THROWS
%token PARENOPEN PARENCLOSE SQUAREOPEN SQUARECLOSE BRACKETOPEN BRACKETCLOSE
%token JAVADOCSTART JAVADOCEND JAVADOCNEWLINE JAVADOCTAGMARK JAVADOCTOKEN
%token IDENTIFIER CODEBLOCK STRING

%%

file: packages imports class;

fullidentifier: IDENTIFIER | dotidentifier;
dotidentifier: | dotidentifier DOT IDENTIFIER;

packages: | package;
package: PACKAGE fullidentifier SEMI;

imports: | imports import;
import: IMPORT fullidentifier SEMI;

class: SEMI;

%%

private Lexer lexer;

public Parser(Lexer lexer) {
	this.lexer = lexer;
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
	System.out.println("ERROR: " + msg);
}