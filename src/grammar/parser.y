%{
import com.thoughtworks.qdox.parser.*;
import com.thoughtworks.qdox.parser.structs.*;
import java.io.IOException;
%}

%token SEMI DOT COMMA STAR EQUALS
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT NATIVE STRICTFP SYNCHRONIZED TRANSIENT VOLATILE
%token CLASS INTERFACE THROWS EXTENDS IMPLEMENTS SUPER
%token BRACEOPEN BRACECLOSE SQUAREOPEN SQUARECLOSE PARENOPEN PARENCLOSE LESSTHAN GREATERTHAN AMPERSAND QUERY
%token JAVADOCSTART JAVADOCEND
%token CODEBLOCK STRING

// stringly typed tokens/types
%token <sval> IDENTIFIER JAVADOCTAG JAVADOCTOKEN
%type <sval> fullidentifier modifier classtype
%type <ival> dimensions
%type <type> type arrayidentifier

%%


// ----- TOP LEVEL

// A file consists of 0-n fileparts...
file: | file { line = lexer.getLine(); } filepart;

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
     JAVADOCTOKEN { 
        textBuffer.append($1); textBuffer.append(' '); 
    };

javadoctags: | javadoctags javadoctag;

javadoctag: 
    JAVADOCTAG { line = lexer.getLine(); } 
    javadoctokens {
        builder.addJavaDocTag(new TagDef($1.substring(1), buffer(), line)); 
    };


// ----- COMMON TOKENS

// A fullidentifier is "a", "a.b", "a.b.c", "a.b.*", etc...
fullidentifier: 
    IDENTIFIER { $$ = $1; } |
    fullidentifier DOT IDENTIFIER { $$ = $1 + '.' + $3; } |
    fullidentifier DOT STAR { $$ = $1 + ".*"; };

arrayidentifier: 
    IDENTIFIER dimensions {
        $$ = new TypeDef($1,$2); 
    };

dimensions: 
    /* empty */ { $$ = 0; }
|   dimensions SQUAREOPEN SQUARECLOSE {
        $$ = $1 + 1; 
    };

// Modifiers to methods, fields, classes, interfaces, parameters, etc...
modifier:
    PUBLIC          { $$ = "public"; } |
    PROTECTED       { $$ = "protected"; } |
    PRIVATE         { $$ = "private"; } |
    STATIC          { $$ = "static"; } |
    FINAL           { $$ = "final"; } |
    ABSTRACT        { $$ = "abstract"; } |
    NATIVE          { $$ = "native"; } |
    SYNCHRONIZED    { $$ = "synchronized"; } |
    VOLATILE        { $$ = "volatile"; } |
    TRANSIENT       { $$ = "transient"; } |
    STRICTFP        { $$ = "strictfp"; } ;

modifiers: | modifiers modifier { modifiers.add($2); };


// ----- TYPES 

type: 
    classtype dimensions {
        $$ = new TypeDef($1,$2); 
    };

classtype: 
    fullidentifier opt_typearguments {
        $$ = $1; 
    };

opt_typearguments: | LESSTHAN typearglist GREATERTHAN;

typearglist:
    typearg |
    typearglist COMMA typearg;

typearg:
    type |
    QUERY |
    QUERY EXTENDS type |
    QUERY SUPER type;

opt_typeparams: | LESSTHAN typeparamlist GREATERTHAN;

typeparamlist:
    typeparam |
    typeparamlist COMMA typeparam;

typeparam: 
    IDENTIFIER |
    IDENTIFIER EXTENDS typeboundlist;

typeboundlist:
    type | 
    typeboundlist AMPERSAND type;

// ----- CLASS

class: 
    classdefinition BRACEOPEN members BRACECLOSE { 
        builder.endClass(); 
    };

classdefinition: 
    modifiers classorinterface IDENTIFIER opt_typeparams opt_extends opt_implements {
        cls.lineNumber = line;
        cls.modifiers.addAll(modifiers); modifiers.clear(); 
        cls.name = $3;
        builder.beginClass(cls); 
        cls = new ClassDef(); 
    };

classorinterface: 
    CLASS | 
    INTERFACE { cls.isInterface = true; };

opt_extends: | EXTENDS extendslist;

extendslist: 
    classtype { cls.extendz.add($1); } |
    extendslist COMMA classtype { cls.extendz.add($3); };

opt_implements: | IMPLEMENTS implementslist;

implementslist: 
    classtype { cls.implementz.add($1); } | 
    implementslist COMMA classtype { cls.implementz.add($3); };

members: | members { line = lexer.getLine(); } member;

member:
    javadoc | 
    fields | 
    method |
    constructor |
    modifiers CODEBLOCK | // static block
    class | 
    SEMI;

memberend: SEMI | CODEBLOCK;


// ----- FIELD

fields:
    modifiers type arrayidentifier {
        fieldType = $2;
        makeField($3);
    }
    extrafields memberend {
        modifiers.clear();
    };
  
extrafields: | 
    extrafields COMMA { line = lexer.getLine(); } arrayidentifier {
        makeField($4);
    };


// ----- METHOD

method:
    modifiers type IDENTIFIER methoddef memberend {
        mth.lineNumber = line;
        mth.modifiers.addAll(modifiers); modifiers.clear(); 
        mth.returns = $2.name; mth.dimensions = $2.dimensions;
        mth.name = $3;
        builder.addMethod(mth);
        mth = new MethodDef(); 
    };

constructor:
    modifiers IDENTIFIER methoddef memberend {
        mth.lineNumber = line;
        mth.modifiers.addAll(modifiers); modifiers.clear(); 
        mth.constructor = true; mth.name = $2; 
        builder.addMethod(mth);
        mth = new MethodDef(); 
    };

methoddef: PARENOPEN opt_params PARENCLOSE opt_exceptions;

opt_exceptions: | THROWS exceptionlist;

exceptionlist: 
    fullidentifier { mth.exceptions.add($1); } | 
    exceptionlist COMMA fullidentifier { mth.exceptions.add($3); };

opt_params: | paramlist;

paramlist: 
    param | 
    paramlist COMMA param;

param: 
    opt_parammodifiers type arrayidentifier { 
        param.name = $3.name; 
        param.type = $2.name; 
        param.dimensions = $2.dimensions + $3.dimensions; 
        mth.params.add(param); param = new FieldDef(); 
    };

opt_parammodifiers: | 
    opt_parammodifiers modifier { param.modifiers.add($2); };


%%

private Lexer lexer;
private Builder builder;
private StringBuffer textBuffer = new StringBuffer();
private ClassDef cls = new ClassDef();
private MethodDef mth = new MethodDef();
private FieldDef param = new FieldDef();
private java.util.Set modifiers = new java.util.HashSet();
private TypeDef fieldType;
private int line;

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
    throw new ParseException(msg, lexer.getLine(), lexer.getColumn());
}

private class Value {
    String sval;
    int ival;
    TypeDef type;
}

private void makeField(TypeDef field) {
    FieldDef fd = new FieldDef();
    fd.lineNumber = line;
    fd.modifiers.addAll(modifiers); 
    fd.type = fieldType.name; 
    fd.dimensions = fieldType.dimensions + field.dimensions;
    fd.name = field.name;
    builder.addField(fd);
}
            
