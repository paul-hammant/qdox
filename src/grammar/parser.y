%{
	import java.util.*;
%}

%token SEMI DOT COMMA STAR EQUALS
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT NATIVE STRICTFP SYNCHRONIZED TRANSIENT VOLATILE
%token CLASS INTERFACE THROWS EXTENDS IMPLEMENTS
%token PARENOPEN PARENCLOSE SQUAREOPEN SQUARECLOSE BRACKETOPEN BRACKETCLOSE
%token JAVADOCSTART JAVADOCEND JAVADOCNEWLINE JAVADOCTAGMARK
%token CODEBLOCK STRING

%token <sval> IDENTIFIER JAVADOCTOKEN

%type <sval> fullidentifier dotidentifier modifier

%%






file: | file filepart;
filepart: package | import | javadoc | class;

fullidentifier: IDENTIFIER dotidentifier { $$ = $1 + $2; };
dotidentifier: { $$ = ""; }
	| dotidentifier DOT IDENTIFIER { $$ = $1 + '.' + $3; }
	| dotidentifier DOT STAR { $$ = $1 + ".*"; }
	;

modifier: PUBLIC { $$ = "public"; }
	| PROTECTED { $$ = "protected"; }
	| PRIVATE { $$ = "private"; }
	| STATIC { $$ = "static"; }
	| FINAL { $$ = "final"; }
	| ABSTRACT { $$ = "abstract"; }
	| NATIVE { $$ = "native"; }
	| SYNCHRONIZED { $$ = "synchronized"; }
	| VOLATILE { $$ = "volatile"; }
	;

package: PACKAGE fullidentifier SEMI { builder.addPackage($2); };

import: IMPORT fullidentifier SEMI { builder.addImport($2); };

javadoc: JAVADOCSTART javadocdescription javadoctags JAVADOCEND;
javadocdescription: javadoctokens { builder.addJavaDoc(buffer()); }
javadoctokens: | javadoctokens javadoctoken;
javadoctoken: JAVADOCNEWLINE | JAVADOCTOKEN { textBuffer.append($1); textBuffer.append(' '); };
javadoctags: | javadoctags javadoctag;
javadoctag: JAVADOCTAGMARK JAVADOCTOKEN javadoctokens { builder.addJavaDocTag($2, buffer()); };

class: classdefinition PARENOPEN classparts PARENCLOSE;
classdefinition: classmodifiers classorinterface IDENTIFIER extends implements { cls.name = $3; builder.addClass(cls); cls = new Builder.ClassDef(); }
classmodifiers: | classmodifiers modifier { cls.modifiers.add($2); };
classorinterface: CLASS | INTERFACE { cls.isInterface = true; };
extends: | EXTENDS extendslist;
extendslist: fullidentifier { cls.extendz.add($1); } | extendslist COMMA fullidentifier { cls.extendz.add($3); };
implements: | IMPLEMENTS implementslist;
implementslist: fullidentifier { cls.implementz.add($1); } | implementslist COMMA fullidentifier { cls.implementz.add($3); };

classparts: | classparts classpart;
classpart: javadoc | method;

method: methodmodifiers fullidentifier IDENTIFIER BRACKETOPEN params BRACKETCLOSE exceptions methodend { mth.returns = $2; mth.name = $3; builder.addMethod(mth); mth = new Builder.MethodDef(); };
methodmodifiers: | methodmodifiers modifier { mth.modifiers.add($2); };
exceptions: | THROWS exceptionlist;
exceptionlist: IDENTIFIER { mth.exceptions.add($1); } | exceptionlist COMMA IDENTIFIER { mth.exceptions.add($3); };
methodend: SEMI | CODEBLOCK;

params: | param | params COMMA param;
param: parammodifiers IDENTIFIER IDENTIFIER { par.name = $3; par.type = $2; mth.params.add(par); par = new Builder.ParamDef(); };
parammodifiers: | parammodifiers modifier { par.modifiers.add($2); };

%%

private Lexer lexer;
private Builder builder;
private StringBuffer textBuffer = new StringBuffer();
private Builder.ClassDef cls = new Builder.ClassDef();
private Builder.MethodDef mth = new Builder.MethodDef();
private Builder.ParamDef par = new Builder.ParamDef();

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
