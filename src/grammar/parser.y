%{
import com.thoughtworks.qdox.parser.*;
import com.thoughtworks.qdox.parser.structs.*;
import java.io.IOException;
%}

%token SEMI DOT COMMA STAR EQUALS
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT NATIVE STRICTFP SYNCHRONIZED TRANSIENT VOLATILE
%token CLASS INTERFACE THROWS EXTENDS IMPLEMENTS
%token BRACEOPEN BRACECLOSE SQUAREOPEN SQUARECLOSE PARENOPEN PARENCLOSE
%token JAVADOCSTART JAVADOCEND JAVADOCNEWLINE
%token CODEBLOCK STRING ASSIGNMENT

// stringly typed tokens/types
%token <sval> IDENTIFIER JAVADOCTAG JAVADOCTOKEN
%type <sval> fullidentifier modifier arrayidentifier arrayfullidentifier paramarrayidentifier paramarrayfullidentifier

%%


// ----- TOP LEVEL

// A file consists of 0-n fileparts...
file: | file filepart;

// And a filepart is a package/import statement, javadoc comment, or class declaration.
filepart: package | import | javadoc | class;

// Package statement
package: PACKAGE fullidentifier SEMI { builder.addPackage($2); };

// Import statement
import: IMPORT fullidentifier SEMI { builder.addImport($2); };


// ----- JAVADOC

javadoc: JAVADOCSTART javadocdescription javadoctags JAVADOCEND;

javadocdescription: 
	javadoctokens { 
		builder.addJavaDoc(buffer()); 
	};

javadoctokens: | javadoctokens javadoctoken;

javadoctoken: 
	JAVADOCNEWLINE |
 	JAVADOCTOKEN { 
		textBuffer.append($1); textBuffer.append(' '); 
	};

javadoctags: | javadoctags javadoctag;

javadoctag: 
	JAVADOCTAG javadoctokens {
		builder.addJavaDocTag($1.substring(1), buffer(), lexer.getLine()); 
	};


// ----- COMMON TOKENS

// A fullidentifier is "a", "a.b", "a.b.c", "a.b.*", etc...
fullidentifier: 
	IDENTIFIER { $$ = $1; } |
 	fullidentifier DOT IDENTIFIER { $$ = $1 + '.' + $3; } |
 	fullidentifier DOT STAR { $$ = $1 + ".*"; };

// like IDENTIFIER and fullidentifer except they can contain arrays[]
arrayidentifier: IDENTIFIER arrayparts { $$ = $1; };
arrayfullidentifier: fullidentifier arrayparts { $$ = $1; };
arrayparts: | arrayparts SQUAREOPEN SQUARECLOSE { dimensions++; };

// Modifiers to methods, fields, classes, interfaces, parameters, etc...
modifier:
	PUBLIC		{ $$ = "public"; } |
 	PROTECTED 	{ $$ = "protected"; } |
 	PRIVATE 	{ $$ = "private"; } |
 	STATIC 		{ $$ = "static"; } |
 	FINAL 		{ $$ = "final"; } |
 	ABSTRACT 	{ $$ = "abstract"; } |
 	NATIVE 		{ $$ = "native"; } |
 	SYNCHRONIZED { $$ = "synchronized"; } |
 	VOLATILE 	{ $$ = "volatile"; } |
 	TRANSIENT 	{ $$ = "transient"; } |
 	STRICTFP 	{ $$ = "strictfp"; } ;

modifiers: | modifiers modifier { modifiers.add($2); };


// ----- CLASS

class: 
	classdefinition BRACEOPEN members BRACECLOSE { 
		builder.endClass(); 
	};

classdefinition: 
	modifiers classorinterface IDENTIFIER extends implements { 
		cls.modifiers.addAll(modifiers); modifiers.clear(); 
		cls.name = $3; 
		builder.beginClass(cls); 
		cls = new ClassDef(); 
	};

classorinterface: 
	CLASS | 
	INTERFACE { cls.isInterface = true; };

extends: | EXTENDS extendslist;

extendslist: 
	fullidentifier { cls.extendz.add($1); } | 
	extendslist COMMA fullidentifier { cls.extendz.add($3); };

implements: | IMPLEMENTS implementslist;

implementslist: 
	fullidentifier { cls.implementz.add($1); } | 
	implementslist COMMA fullidentifier { cls.implementz.add($3); };

members: | members member;

member:
	javadoc | 
	field | 
	method |
	constructor |
	modifiers CODEBLOCK | // static block
	class | 
	SEMI;

memberend: SEMI | CODEBLOCK | ASSIGNMENT;


// ----- FIELD

field:
	modifiers arrayfullidentifier arrayidentifier extraidentifiers memberend {
		fld.modifiers.addAll(modifiers); 
		modifiers.clear(); 
		fld.type = $2; fld.name = $3; fld.dimensions = getDimensions(); 
		builder.addField(fld); 
		fld = new FieldDef(); 
	};

extraidentifiers: | extraidentifiers COMMA fullidentifier;


// ----- METHOD

method:
	modifiers arrayfullidentifier arrayidentifier methoddef memberend {
		mth.modifiers.addAll(modifiers); modifiers.clear(); 
		mth.returns = $2; mth.name = $3; mth.dimensions = getDimensions(); 
		builder.addMethod(mth); mth = new MethodDef(); 
	};

constructor:
	modifiers arrayidentifier methoddef memberend { 
			mth.modifiers.addAll(modifiers); modifiers.clear(); 
			mth.constructor = true; mth.name = $2; 
			builder.addMethod(mth); mth = new MethodDef(); 
	};

methoddef: PARENOPEN params PARENCLOSE exceptions;

exceptions: | THROWS exceptionlist;

exceptionlist: 
	fullidentifier { mth.exceptions.add($1); } | 
	exceptionlist COMMA fullidentifier { mth.exceptions.add($3); };

// formal parameters
params: | param paramlist;
paramlist: | paramlist COMMA param;

param: 
	parammodifiers paramarrayfullidentifier paramarrayidentifier { 
		fld.name = $3; fld.type = $2; fld.dimensions = getParamDimensions(); 
		mth.params.add(fld); fld = new FieldDef(); 
	};

parammodifiers: | 
	parammodifiers modifier { fld.modifiers.add($2); };

paramarrayidentifier: IDENTIFIER paramarrayparts { $$ = $1; };
paramarrayfullidentifier: fullidentifier paramarrayparts { $$ = $1; };
paramarrayparts: | paramarrayparts SQUAREOPEN SQUARECLOSE { paramDimensions++; };


%%

private Lexer lexer;
private Builder builder;
private StringBuffer textBuffer = new StringBuffer();
private ClassDef cls = new ClassDef();
private MethodDef mth = new MethodDef();
private FieldDef fld = new FieldDef();
private java.util.Set modifiers = new java.util.HashSet();
private int dimensions, paramDimensions;

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
		yylval = new Value();
		yylval.sval = lexer.text();
		return result;
	}
	catch(IOException e) {
		return 0;
	}
}

private void yyerror(String msg) {
	// TODO: Implement error handling
}

private class Value {
	String sval;
}

private int getDimensions() {
	int r = dimensions;
	dimensions = 0;
	return r;
}

private int getParamDimensions() {
	int r = paramDimensions;
	paramDimensions = 0;
	return r;
}
