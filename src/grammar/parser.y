%{
	import java.util.*;
%}

%token SEMI DOT COMMA STAR EQUALS
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT CLASS INTERFACE THROWS
%token PARENOPEN PARENCLOSE SQUAREOPEN SQUARECLOSE BRACKETOPEN BRACKETCLOSE
%token JAVADOCSTART JAVADOCEND JAVADOCNEWLINE JAVADOCTAGMARK
%token CODEBLOCK STRING

%token <sval> IDENTIFIER JAVADOCTOKEN

%type <sval> fullidentifier dotidentifier

%%






file: packages imports javadoc class;

fullidentifier: IDENTIFIER dotidentifier { $$ = $1 + $2; };
dotidentifier: { $$ = ""; }
	| dotidentifier DOT IDENTIFIER { $$ = $1 + '.' + $3; }
	| dotidentifier DOT STAR { $$ = $1 + ".*"; }
	;


packages: | package;
package: PACKAGE fullidentifier SEMI { builder.addPackage($2); };


imports: | imports import;
import: IMPORT fullidentifier SEMI { builder.addImport($2); };


javadoc: | javadoc JAVADOCSTART javadocdescription javadoctags JAVADOCEND;
javadocdescription: javadoctokens { builder.addJavaDoc(buffer()); }
javadoctokens: | javadoctokens javadoctoken;
javadoctoken: JAVADOCNEWLINE | JAVADOCTOKEN { textBuffer.append($1); textBuffer.append(' '); };
javadoctags: | javadoctags javadoctag;
javadoctag: JAVADOCTAGMARK JAVADOCTOKEN javadoctokens { builder.addJavaDocTag($2, buffer()); };

class: classdefinition PARENOPEN PARENCLOSE;
classdefinition: IDENTIFIER CLASS { builder.addClass($1, Collections.EMPTY_SET, null, Collections.EMPTY_SET, false); }




%%

private Lexer lexer;
private Builder builder;
private StringBuffer textBuffer = new StringBuffer();

private String buffer() {
	if (textBuffer.length() > 0) textBuffer.deleteCharAt(textBuffer.length() - 1);
	String result = textBuffer.toString();
	textBuffer.setLength(0);
	return result;
}

public Parser(Lexer lexer, Builder builder) {
	this.lexer = lexer;
	this.builder = builder;
}

/**
 * Parse file. Return true if successful.
 */
public boolean parse() {
	return yyparse() == 0;
}

private int yylex() {
	try {
		final int result = lexer.lex();
		yylval = new ParserVal(lexer.text());
		return result;
	}
	catch(java.io.IOException e) {
		return 0;
	}
}

private void yyerror(String msg) {
	builder.error(msg);
}
