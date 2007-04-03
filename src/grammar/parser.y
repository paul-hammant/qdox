%{
import com.thoughtworks.qdox.parser.*;
import com.thoughtworks.qdox.parser.structs.*;
import java.io.IOException;
import java.util.LinkedList;
%}

%token SEMI DOT DOTDOTDOT COMMA STAR EQUALS ANNOSTRING ANNOCHAR SLASH PLUS MINUS
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT NATIVE STRICTFP SYNCHRONIZED TRANSIENT VOLATILE
%token CLASS INTERFACE ENUM ANNOINTERFACE THROWS EXTENDS IMPLEMENTS SUPER DEFAULT
%token BRACEOPEN BRACECLOSE SQUAREOPEN SQUARECLOSE PARENOPEN PARENCLOSE LESSTHAN GREATERTHAN AMPERSAND QUERY AT
%token JAVADOCSTART JAVADOCEND JAVADOCEOL
%token CODEBLOCK PARENBLOCK

// strongly typed tokens/types
%token <sval> IDENTIFIER JAVADOCTAG JAVADOCTOKEN ANNOTATION
%token <sval> BOOLEAN_LITERAL INTEGER_LITERAL FLOAT_LITERAL
%type <sval> fullidentifier modifier classtype typedeclspecifier typename memberend
%type <sval> annotationValueConstant annotationSymConstant
%type <oval> annoElementValue
%type <ival> dimensions
%type <bval> varargs
%type <type> type arrayidentifier

%%


// ----- TOP LEVEL

// A file consists of 0-n fileparts...
file: | file { line = lexer.getLine(); } filepart;

// And a filepart is a package/import statement, javadoc comment, or class declaration.
filepart: package | import | javadoc | class | enum | SEMI;

// Package statement
package: PACKAGE fullidentifier SEMI { builder.addPackage($2); };

// Import statement
import: IMPORT fullidentifier SEMI { builder.addImport($2); } |
		IMPORT STATIC fullidentifier SEMI { builder.addImport($3); };


// ----- JAVADOC

javadoc: JAVADOCSTART javadocdescription javadoctags JAVADOCEND;

javadocdescription: 
    javadoctokens { 
        builder.addJavaDoc(buffer()); 
    };

javadoctokens: | javadoctokens javadoctoken;

javadoctoken: 
    JAVADOCTOKEN {
        appendToBuffer($1);
    } |
    JAVADOCEOL {
        textBuffer.append('\n');
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

modifiers:
    modifiers modifier { modifiers.add($2); } |
    modifiers annotation |
    ;


// ----- ANNOTATIONS

annotationValueConstant:
	FLOAT_LITERAL		{ $$ = $1; } |
	INTEGER_LITERAL		{ $$ = $1; } |
	BOOLEAN_LITERAL		{ $$ = $1; } |
	fullidentifier		{ $$ = $1; } |
	fullidentifier DOT CLASS { $$ = $1 + ".class"; } |
	ANNOSTRING			{
		// would prefer to set this as a returned token in flex... how?
		String str = lexer.getCodeBody();
		str = str.substring( 1, str.length() - 1 );
		$$ = str;
	} |
	ANNOCHAR			{
		String str = lexer.getCodeBody();
		str = str.substring( 1, str.length() - 1 );
		$$ = str;
	};

annotationSymConstant:
	LESSTHAN	{ $$ = "<"; } |
	GREATERTHAN	{ $$ = ">"; } |
	STAR		{ $$ = "*"; } |
	SLASH		{ $$ = "/"; } |
	PLUS		{ $$ = "+"; } |
	MINUS		{ $$ = "/"; };

annotationValueConstants:
	annotationValueConstant {
		annoConstants.add( $1 );
	} | annotationValueConstants annotationSymConstant annotationValueConstant {
		annoConstants.add( $2 );
		annoConstants.add( $3 );
	};

annotation:
	{ ano = new AnnoDef(); } annotationWork { builder.addAnnotation(ano); };

annotationWork:
	ANNOTATION {
        ano.lineNumber = line;
		ano.name = $1.substring(1).trim();
	} annoParens;

annoParens:
	|
	PARENOPEN annoParenContents PARENCLOSE;

annoParenContents:
	annoElementValue { ano.args.put( "value", $1 ); } |
	annoElementValuePairs;

annoElementValuePairs:
	annoElementValuePair |
	annoElementValuePairs COMMA annoElementValuePair;

annoElementValuePair:
	IDENTIFIER EQUALS annoElementValue { ano.args.put( $1, $3 ); };

annoElementValue:
	PARENOPEN annoElementValue PARENCLOSE { $$ = $2; } |
	annotationValueConstants { $$ = annoConstants; annoConstants = new LinkedList(); } |
	{	AnnoDef tmpAno = new AnnoDef();
		tmpAno.tempAnno = ano;
		ano = tmpAno;
	} annotationWork { $$ = ano; ano = ano.tempAnno; } |
	annoElementValueArrayInitializer { $$ = annoValues; annoValues = new LinkedList(); };

annoElementValueArrayInitializer:
    BRACEOPEN annoElementValues BRACECLOSE;

annoElementValues:
	annoElementValue { annoValues.add( $1 ); } |
	annoElementValues COMMA annoElementValue { annoValues.add( $3 ); };

// ----- TYPES

type:
    classtype opt_typearguments dimensions {
        $$ = new TypeDef($1,$3);
    };

classtype:
    typedeclspecifier opt_typearguments {
        $$ = $1; 
    };

typedeclspecifier:
    typename { $$ = $1; } |
    classtype DOT IDENTIFIER { $$ = $1 + '.' + $3; };

typename: 
    IDENTIFIER { $$ = $1; } |
    typename DOT IDENTIFIER { $$ = $1 + '.' + $3; }; 

opt_typearguments: | LESSTHAN typearglist GREATERTHAN;

typearglist:
    typearg |
    typearglist COMMA typearg;

typearg:
    type |
    QUERY |
    QUERY EXTENDS type |
    QUERY SUPER type;

opt_typeparams: | typeparams;

typeparams: LESSTHAN typeparamlist GREATERTHAN;

typeparamlist:
    typeparam |
    typeparamlist COMMA typeparam;

typeparam: 
    IDENTIFIER |
    IDENTIFIER EXTENDS typeboundlist;

typeboundlist:
    type | 
    typeboundlist AMPERSAND type;

// ----- ENUM

enum: enum_definition BRACEOPEN enum_body BRACECLOSE {
  builder.endClass();
};

enum_definition: modifiers ENUM IDENTIFIER opt_implements {
    cls.lineNumber = line;
    cls.modifiers.addAll(modifiers); modifiers.clear();
    cls.name = $3;
    cls.type = ClassDef.ENUM;
    builder.beginClass(cls);
    cls = new ClassDef();
};

enum_body: enum_values | enum_values SEMI members;

enum_values: | enum_value | enum_value COMMA enum_values;

enum_value:
    javadoc enum_constructor |
    opt_annotations enum_constructor;

enum_constructor:
    IDENTIFIER |
    IDENTIFIER CODEBLOCK |
    IDENTIFIER PARENBLOCK |
    IDENTIFIER PARENBLOCK CODEBLOCK;


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
    CLASS { cls.type = ClassDef.CLASS; } | 
    INTERFACE { cls.type = ClassDef.INTERFACE; } |
    ANNOINTERFACE { cls.type = ClassDef.ANNOTATION_TYPE; };

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
    static_block |
    class |
	enum |
    SEMI;

memberend:
    SEMI {
      $$ = "";
    }
    | CODEBLOCK {
	  $$ = lexer.getCodeBody();
    };

static_block:
    modifiers CODEBLOCK { modifiers.clear(); };

// ----- FIELD

fields:
    modifiers type arrayidentifier {
        fieldType = $2;
        makeField($3, lexer.getCodeBody());
    }
    extrafields memberend {
        modifiers.clear();
    };
  
extrafields: | 
    extrafields COMMA { line = lexer.getLine(); } arrayidentifier {
        makeField($4, lexer.getCodeBody());
    };


// ----- METHOD

method:
    modifiers typeparams type IDENTIFIER methoddef dimensions opt_exceptions memberend {
        mth.lineNumber = line;
        mth.modifiers.addAll(modifiers); modifiers.clear(); 
        mth.returns = $3.name;
        mth.dimensions = $6 + $3.dimensions; // return dimensions can be specified after return type OR after params
        mth.name = $4;
        mth.body = $8;
        builder.addMethod(mth);
        mth = new MethodDef(); 
    } |
    modifiers type IDENTIFIER methoddef dimensions opt_exceptions memberend {
        mth.lineNumber = line;
        mth.modifiers.addAll(modifiers); modifiers.clear();
        mth.returns = $2.name;
        mth.dimensions = $5 + $2.dimensions; // return dimensions can be specified after return type OR after params
        mth.name = $3;
        mth.body = $7;
        builder.addMethod(mth);
        mth = new MethodDef();
    };

constructor:
    modifiers IDENTIFIER methoddef opt_exceptions memberend {
        mth.lineNumber = line;
        mth.modifiers.addAll(modifiers); modifiers.clear(); 
        mth.constructor = true; mth.name = $2;
        mth.body = $5;
        builder.addMethod(mth);
        mth = new MethodDef(); 
    };

methoddef: PARENOPEN opt_params PARENCLOSE;

opt_exceptions: | THROWS exceptionlist;

exceptionlist: 
    fullidentifier { mth.exceptions.add($1); } | 
    exceptionlist COMMA fullidentifier { mth.exceptions.add($3); };

opt_params: | paramlist;

paramlist: 
    param | 
    paramlist COMMA param;

param: 
    opt_annotations opt_parammodifiers type varargs arrayidentifier {
        param.name = $5.name;
        param.type = $3.name;
        param.dimensions = $3.dimensions + $5.dimensions;
        param.isVarArgs = $4;
        mth.params.add(param);
        param = new FieldDef();
    };

varargs:
    /* empty */ { $$ = false; } |
    DOTDOTDOT   { $$ = true; } ;

opt_annotations: | opt_annotations annotation;

opt_parammodifiers: |
    opt_parammodifiers modifier { param.modifiers.add($2); };

%%

private Lexer lexer;
private Builder builder;
private StringBuffer textBuffer = new StringBuffer();
private ClassDef cls = new ClassDef();
private MethodDef mth = new MethodDef();
private AnnoDef ano = new AnnoDef();
private FieldDef param = new FieldDef();
private java.util.Set modifiers = new java.util.HashSet();
private TypeDef fieldType;
private int line;
private int column;
private boolean debugLexer;

private LinkedList annoConstants = new LinkedList();
private LinkedList annoValues = new LinkedList();

private void appendToBuffer(String word) {
    if (textBuffer.length() > 0) {
        char lastChar = textBuffer.charAt(textBuffer.length() - 1);
        if (!Character.isWhitespace(lastChar)) {
            textBuffer.append(' ');
        }
    }
    textBuffer.append(word);
}

private String buffer() {
    String result = textBuffer.toString().trim();
    textBuffer.setLength(0);
    return result;
}

public Parser(Lexer lexer, Builder builder) {
    this.lexer = lexer;
    this.builder = builder;
}

public void setDebugParser(boolean debug) {
    yydebug = debug;
}

public void setDebugLexer(boolean debug) {
    debugLexer = debug;
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
        if (debugLexer) {
            System.err.println("Token: " + yyname[result] + " \"" + yylval.sval + "\"");
        }
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
	Object oval;
    String sval;
    int ival;
	boolean bval;
    TypeDef type;
}

private void makeField(TypeDef field, String body) {
    FieldDef fd = new FieldDef();
    fd.lineNumber = line;
    fd.modifiers.addAll(modifiers); 
    fd.type = fieldType.name; 
    fd.dimensions = fieldType.dimensions + field.dimensions;
    fd.name = field.name;
    fd.body = body;
    builder.addField(fd);
}

