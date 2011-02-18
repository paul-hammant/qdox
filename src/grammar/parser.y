%{
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.parser.*;
import com.thoughtworks.qdox.parser.expression.*;
import com.thoughtworks.qdox.parser.structs.*;
import com.thoughtworks.qdox.model.Annotation; //@todo remove
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
%}

%token SEMI DOT DOTDOTDOT COMMA STAR PERCENT EQUALS ANNOSTRING ANNOCHAR SLASH PLUS MINUS
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT NATIVE STRICTFP SYNCHRONIZED TRANSIENT VOLATILE
%token CLASS INTERFACE ENUM ANNOINTERFACE THROWS EXTENDS IMPLEMENTS SUPER DEFAULT
%token BRACEOPEN BRACECLOSE SQUAREOPEN SQUARECLOSE PARENOPEN PARENCLOSE
%token LESSTHAN GREATERTHAN LESSEQUALS GREATEREQUALS
%token LESSTHAN2 GREATERTHAN2 GREATERTHAN3
%token EXCLAMATION AMPERSAND2 VERTLINE2 EQUALS2 NOTEQUALS
%token TILDE AMPERSAND VERTLINE CIRCUMFLEX
%token VOID
%token QUERY COLON AT
%token JAVADOCSTART JAVADOCEND JAVADOCEOL
%token CODEBLOCK PARENBLOCK
%token BYTE SHORT INT LONG CHAR FLOAT DOUBLE BOOLEAN

// strongly typed tokens/types
%token <sval> IDENTIFIER JAVADOCTAG JAVADOCLINE
%token <sval> BOOLEAN_LITERAL
%token <sval> INTEGER_LITERAL
%token <sval> LONG_LITERAL
%token <sval> FLOAT_LITERAL
%token <sval> DOUBLE_LITERAL
%token <sval> CHAR_LITERAL
%token <sval> STRING_LITERAL
%token <ival> VERTLINE2 AMPERSAND2 VERTLINE CIRCUMFLEX AMPERSAND EQUALS2 NOTEQUALS
%token <ival> LESSTHAN GREATERTHAN LESSEQUALS GREATEREQUALS LESSTHAN2 GREATERTHAN2 GREATERTHAN3
%token <ival> PLUS MINUS STAR SLASH PERCENT TILDE EXCLAMATION
%type <sval> name
%type <sval> PrimitiveType NumericType IntegralType FloatingPointType
%type <type> Wildcard WildcardBoundsOpt
%type <annoval> value expression literal annotation arrayInitializer
%type <annoval> conditionalExpression conditionalOrExpression conditionalAndExpression inclusiveOrExpression exclusiveOrExpression andExpression
%type <annoval> equalityExpression relationalExpression shiftExpression additiveExpression multiplicativeExpression
%type <annoval> unaryExpression unaryExpressionNotPlusMinus primary
%type <ival> dims
%type <sval> fullidentifier modifier typedeclspecifier typename memberend
%type <ival> dimensions
%type <bval> varargs
%type <type> type arrayidentifier classtype typearg

%%


// ----- TOP LEVEL

// A file consists of 0-n fileparts...
file: | file { line = lexer.getLine(); } filepart;

// And a filepart is a package/import statement, javadoc comment, or class declaration.
filepart: annotation | package | import | javadoc | class | enum | SEMI;

// Package statement
package: PACKAGE fullidentifier SEMI { builder.addPackage(new PackageDef($2, line)); };

// Import statement
import: IMPORT fullidentifier SEMI { builder.addImport($2); } |
		IMPORT STATIC fullidentifier SEMI { builder.addImport($3); };


// ----- JAVADOC

javadoclist: 
    javadoc |
    javadoclist javadoc;

javadoc: JAVADOCSTART javadocdescription javadoctags JAVADOCEND;

javadocdescription: 
    javadoctokens { 
        builder.addJavaDoc(buffer()); 
    };

javadoctokens: | javadoctokens javadoctoken;

javadoctoken: 
    JAVADOCLINE {
        appendToBuffer($1);
    } 

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
    modifiers javadoc |
    ;


//--------------------------------------------------------------------------------
// ANNOTATIONS
//--------------------------------------------------------------------------------

annotation:
    AT name 
    { 
    	AnnoDef annotation = new AnnoDef();
    	annotation.typeDef = new TypeDef($2);
    	annotation.lineNumber = lexer.getLine();
    	annotationStack.addFirst(annotation);
    }
    annotationParensOpt
    {
    	AnnoDef annotation = annotationStack.removeFirst();
    	if(annotationStack.isEmpty()) {
	    	builder.addAnnotation(annotation);
    	}
    	$$ = annotation;
    };
    
annotationParensOpt:
	|
	PARENOPEN value PARENCLOSE { annotationStack.getFirst().args.put("value", $2); } |
	PARENOPEN valuePairs PARENCLOSE |
	PARENOPEN PARENCLOSE;
    
valuePairs:
    valuePair |
    valuePairs COMMA valuePair;
    
valuePair:
    IDENTIFIER EQUALS value { annotationStack.getFirst().args.put($1, $3); };
    
arrayInitializer:
    {
    	annoValueListStack.add(annoValueList);
    	annoValueList = new LinkedList(); 
    }
    BRACEOPEN valuesOpt BRACECLOSE
    {
    	$$ = new AnnotationValueList(annoValueList);
    	annoValueList = annoValueListStack.remove(annoValueListStack.size() - 1);
    };
    
valuesOpt:
    |
    values;    
    
values:
	value { annoValueList.add($1); } |
	values COMMA value { annoValueList.add($3); };
    
value:
    expression |
    annotation |
    arrayInitializer ;

expression:
	conditionalExpression ;
	
conditionalExpression:
	conditionalOrExpression |
	conditionalOrExpression QUERY expression COLON expression { $$ = new AnnotationQuery($1, $3, $5); };

conditionalOrExpression:
    conditionalAndExpression |
	conditionalOrExpression VERTLINE2 conditionalAndExpression { $$ = new AnnotationLogicalOr($1, $3); };

conditionalAndExpression:
    inclusiveOrExpression |
	conditionalAndExpression AMPERSAND2 inclusiveOrExpression { $$ = new AnnotationLogicalAnd($1, $3); };

inclusiveOrExpression:
    exclusiveOrExpression |
    inclusiveOrExpression VERTLINE exclusiveOrExpression { $$ = new AnnotationOr($1, $3); };

exclusiveOrExpression:
	andExpression |
	exclusiveOrExpression CIRCUMFLEX andExpression { $$ = new AnnotationExclusiveOr($1, $3); };

andExpression:
    equalityExpression |
    andExpression AMPERSAND equalityExpression { $$ = new AnnotationAnd($1, $3); };

equalityExpression:
    relationalExpression |
    equalityExpression EQUALS2 relationalExpression { $$ = new AnnotationEquals($1, $3); } |
    equalityExpression NOTEQUALS relationalExpression { $$ = new AnnotationNotEquals($1, $3); };

relationalExpression:
	shiftExpression |
	relationalExpression LESSEQUALS shiftExpression { $$ = new AnnotationLessEquals($1, $3); } |
	relationalExpression GREATEREQUALS shiftExpression { $$ = new AnnotationGreaterEquals($1, $3); } |
	relationalExpression LESSTHAN shiftExpression { $$ = new AnnotationLessThan($1, $3); } |
	relationalExpression GREATERTHAN shiftExpression { $$ = new AnnotationGreaterThan($1, $3); };
	
shiftExpression:
	additiveExpression |
	shiftExpression LESSTHAN2 additiveExpression { $$ = new AnnotationShiftLeft($1, $3); } |
	shiftExpression GREATERTHAN3 additiveExpression { $$ = new AnnotationUnsignedShiftRight($1, $3); } |
	shiftExpression GREATERTHAN2 additiveExpression { $$ = new AnnotationShiftRight($1, $3); };

additiveExpression:
	multiplicativeExpression |
	additiveExpression PLUS multiplicativeExpression { $$ = new AnnotationAdd($1, $3); } |
	additiveExpression MINUS multiplicativeExpression { $$ = new AnnotationSubtract($1, $3); };

multiplicativeExpression:
    unaryExpression |
	multiplicativeExpression STAR unaryExpression { $$ = new AnnotationMultiply($1, $3); } |
	multiplicativeExpression SLASH unaryExpression { $$ = new AnnotationDivide($1, $3); } |
	multiplicativeExpression PERCENT unaryExpression { $$ = new AnnotationRemainder($1, $3); };
	
unaryExpression:
    PLUS unaryExpression { $$ = new AnnotationPlusSign($2); } |
    MINUS unaryExpression { $$ = new AnnotationMinusSign($2); } |
	unaryExpressionNotPlusMinus;

unaryExpressionNotPlusMinus:
	TILDE unaryExpression { $$ = new AnnotationNot($2); } |
	EXCLAMATION unaryExpression { $$ = new AnnotationLogicalNot($2); } |
	primary;
    	
primary:
    PARENOPEN PrimitiveType PARENCLOSE unaryExpression { $$ = new AnnotationCast(builder.createType($2, 0), $4); } |
	PARENOPEN PrimitiveType dims PARENCLOSE unaryExpression { $$ = new AnnotationCast(builder.createType($2, $3), $5); } |
    PARENOPEN name dims PARENCLOSE unaryExpressionNotPlusMinus { $$ = new AnnotationCast(builder.createType($2, $3), $5); } |
	PARENOPEN name PARENCLOSE unaryExpressionNotPlusMinus { $$ = new AnnotationCast(builder.createType($2, 0), $4); } |
    PARENOPEN expression PARENCLOSE { $$ = new AnnotationParenExpression($2); } |
    literal |
    PrimitiveType dims DOT CLASS { $$ = new AnnotationTypeRef(builder.createType($1, 0)); } |
    PrimitiveType DOT CLASS { $$ = new AnnotationTypeRef(builder.createType($1, 0)); } |
    name DOT CLASS { $$ = new AnnotationTypeRef(builder.createType($1, 0)); } |
    name dims DOT CLASS { $$ = new AnnotationTypeRef(builder.createType($1, 0)); } |
    name { $$ = new AnnotationFieldRef($1); };
	
dims:
    SQUAREOPEN SQUARECLOSE { $$ = 1; } |
    dims SQUAREOPEN SQUARECLOSE { $$ = $1 + 1; };
	
name:
    IDENTIFIER |
    name DOT IDENTIFIER { $$ = $1 + "." + $3; };    
    
literal:
    DOUBLE_LITERAL { $$ = new AnnotationConstant(toDouble($1), $1); } |
    FLOAT_LITERAL { $$ = new AnnotationConstant(toFloat($1), $1); } |
    LONG_LITERAL { $$ = new AnnotationConstant(toLong($1), $1); } |
    INTEGER_LITERAL { $$ = new AnnotationConstant(toInteger($1), $1); } |
    BOOLEAN_LITERAL { $$ = new AnnotationConstant(toBoolean($1), $1); } |
    CHAR_LITERAL { String s = lexer.getCodeBody(); $$ = new AnnotationConstant(toChar(s), s); } |
    STRING_LITERAL { String s = lexer.getCodeBody(); $$ = new AnnotationConstant(toString(s), s); };
        
PrimitiveType:
	NumericType |
    BOOLEAN { $$ = "boolean"; };

NumericType:
	IntegralType |
	FloatingPointType;
	
IntegralType:
    BYTE { $$ = "byte"; } |
    SHORT { $$ = "short"; } |
    INT { $$ = "int"; } |
    LONG { $$ = "long"; } |
    CHAR { $$ = "char"; };

FloatingPointType:
    FLOAT { $$ = "float"; } |
    DOUBLE { $$ = "double"; };
        

// ----- TYPES

type:
    classtype dimensions {
    	TypeDef td = $1;
    	td.dimensions = $2;
        $$ = td;
    };

classtype:
    typedeclspecifier LESSTHAN {
    		TypeDef td = new TypeDef($1,0);
    		td.actualArgumentTypes = new LinkedList();
    		$$ = typeStack.push(td);
    	} typearglist { 
    		$$ = typeStack.pop();
    	} GREATERTHAN {
         $$ = $5;
    } |
    typedeclspecifier {
        $$ = new TypeDef($1,0); 
    };

typedeclspecifier:
    typename |
    classtype DOT IDENTIFIER { $$ = $1.name + '.' + $3; };

typename: 
    IDENTIFIER |
    typename DOT IDENTIFIER { $$ = $1 + '.' + $3; }; 

typearglist:
    typearg { (typeStack.peek()).actualArgumentTypes.add($1);}|
    typearglist COMMA typearg { (typeStack.peek()).actualArgumentTypes.add($3);};

typearg:
    type |
    Wildcard;

Wildcard:
	QUERY { $$ = new WildcardTypeDef();} |
	QUERY WildcardBoundsOpt { $$ = $2;};
	
WildcardBoundsOpt:
    EXTENDS type { $$ = new WildcardTypeDef($2, "extends");} |
    SUPER type   { $$ = new WildcardTypeDef($2, "super");} ;

opt_typeparams: | typeparams;

typeparams: LESSTHAN { typeParams = new LinkedList(); } typeparamlist GREATERTHAN;

typeparamlist:
    typeparam |
    typeparamlist COMMA typeparam;

typeparam: 
    IDENTIFIER { typeParams.add(new TypeVariableDef($1)); } |
    IDENTIFIER EXTENDS { 
      typeVariable = new TypeVariableDef($1);
      typeVariable.bounds = new LinkedList();
    } typeboundlist {
      typeParams.add(typeVariable);
      typeVariable = null;
    };

typeboundlist:
    type { typeVariable.bounds.add($1); } | 
    typeboundlist AMPERSAND type { typeVariable.bounds.add($3); };

// ----- ENUM

enum: enum_definition BRACEOPEN enum_body BRACECLOSE {
  builder.endClass();
  fieldType = null;
  modifiers.clear();
};

enum_definition: modifiers ENUM IDENTIFIER opt_implements {
    cls.lineNumber = line;
    cls.modifiers.addAll(modifiers);
    cls.name = $3;
    cls.type = ClassDef.ENUM;
    builder.beginClass(cls);
    cls = new ClassDef();
    fieldType = new TypeDef($3, 0);
};

enum_body: enum_values | enum_values SEMI members;

enum_values: | enum_value | enum_value COMMA enum_values;

enum_value:
    javadoclist opt_annotations enum_constructor |
    opt_annotations enum_constructor;

enum_constructor:
    IDENTIFIER { makeField(new TypeDef($1, 0), ""); } |
    IDENTIFIER CODEBLOCK  { makeField(new TypeDef($1, 0), ""); } |
    IDENTIFIER PARENBLOCK { makeField(new TypeDef($1, 0), ""); } |
    IDENTIFIER PARENBLOCK CODEBLOCK { makeField(new TypeDef($1, 0), ""); };


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
        cls.typeParams = typeParams;
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
    modifiers CODEBLOCK { lexer.getCodeBody(); modifiers.clear(); };

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
    } | 
    extrafields COMMA javadoc { line = lexer.getLine(); } arrayidentifier {
        makeField($5, lexer.getCodeBody());
    };


// ----- METHOD

method:
    modifiers typeparams type IDENTIFIER {
        builder.beginMethod();
        mth.typeParams = typeParams;
    } methoddef dimensions opt_exceptions memberend {
        mth.lineNumber = line;
        mth.modifiers.addAll(modifiers); modifiers.clear(); 
        mth.returnType = $3;
        mth.dimensions = $7;
        mth.name = $4;
        mth.body = $9;
        builder.endMethod(mth);
        mth = new MethodDef(); 
    } |
    modifiers type IDENTIFIER {
      builder.beginMethod();
    } methoddef dimensions opt_exceptions memberend {
        mth.lineNumber = line;
        mth.modifiers.addAll(modifiers); modifiers.clear();
        mth.returnType = $2;
        mth.dimensions = $6;
        mth.name = $3;
        mth.body = $8;
        builder.endMethod(mth);
        mth = new MethodDef();
    };

constructor:
    modifiers IDENTIFIER {
        builder.beginMethod();
    } methoddef opt_exceptions memberend {
        mth.lineNumber = line;
        mth.modifiers.addAll(modifiers); modifiers.clear(); 
        mth.constructor = true; mth.name = $2;
        mth.body = $6;
        builder.endMethod(mth);
        mth = new MethodDef(); 
    } |
    modifiers typeparams IDENTIFIER {
        builder.beginMethod();
        mth.typeParams = typeParams;
    } methoddef opt_exceptions memberend {
        mth.lineNumber = line;
        mth.modifiers.addAll(modifiers); modifiers.clear(); 
        mth.constructor = true; mth.name = $3;
        mth.body = $7;
        builder.endMethod(mth);
        mth = new MethodDef(); 
    };

methoddef: PARENOPEN opt_params PARENCLOSE;

opt_exceptions: | THROWS exceptionlist;

exceptionlist: 
    classtype { mth.exceptions.add($1); } | 
    exceptionlist COMMA classtype { mth.exceptions.add($3); };

opt_params: | paramlist;

paramlist: 
    param | 
    paramlist COMMA param;

param: 
    opt_parammodifiers type varargs arrayidentifier {
        param.name = $4.name;
        param.type = $2;
        param.dimensions = $4.dimensions;
        param.isVarArgs = $3;
        builder.addParameter(param);
        param = new FieldDef();
    };

varargs:
    /* empty */ { $$ = false; } |
    DOTDOTDOT   { $$ = true; } ;

opt_annotations: | opt_annotations annotation;

opt_parammodifiers: |
    opt_parammodifiers modifier { param.modifiers.add($2); } |
    opt_parammodifiers annotation;

%%

private Lexer lexer;
private Builder builder;
private StringBuffer textBuffer = new StringBuffer();
private ClassDef cls = new ClassDef();
private MethodDef mth = new MethodDef();
private List<TypeVariableDef> typeParams = new LinkedList<TypeVariableDef>(); //for both JavaClass and JavaMethod
private LinkedList<AnnoDef> annotationStack = new LinkedList<AnnoDef>(); // Use LinkedList instead of Stack because it is unsynchronized 
private List<List<AnnotationValue>> annoValueListStack = new LinkedList<List<AnnotationValue>>(); // Use LinkedList instead of Stack because it is unsynchronized
private List<AnnotationValue> annoValueList = null;
private FieldDef param = new FieldDef();
private java.util.Set<String> modifiers = new java.util.HashSet<String>();
private TypeDef fieldType;
private TypeVariableDef typeVariable;
private Stack<TypeDef> typeStack = new Stack<TypeDef>();
private int line;
private int column;
private boolean debugLexer;

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
    AnnotationValue annoval;
}


private void makeField(TypeDef field, String body) {
    FieldDef fd = new FieldDef();
    fd.lineNumber = line;
    fd.modifiers.addAll(modifiers); 
    fd.name = field.name;
    fd.type = fieldType;
    fd.dimensions = field.dimensions;
    fd.body = body;
    builder.addField(fd);
}

private String convertString(String str) {
	StringBuffer buf = new StringBuffer();
	boolean escaped = false;
	int unicode = 0;
	int value = 0;
	int octal = 0;
	boolean consumed = false;
	
	for(int i = 0; i < str.length(); ++ i) {
		char ch = str.charAt( i );
		
		if(octal > 0) {
			if( value >= '0' && value <= '7' ) {
				value = ( value << 3 ) | Character.digit( ch, 8 );
				-- octal;
				consumed = true;
			}
			else {
				octal = 0;
			}
			
			if( octal == 0 ) {
				buf.append( (char) value );		
				value = 0;
			}
		}
		
		if(!consumed) {
			if(unicode > 0) {
				value = ( value << 4 ) | Character.digit( ch, 16 );
				
				-- unicode;
		
				if(unicode == 0) {
					buf.append( (char)value );
					value = 0;
				}
			}
			else if(escaped) {
				if(ch == 'u' || ch == 'U') {
					unicode = 4;
				}
				else if(ch >= '0' && ch <= '7') {
					octal = (ch > '3') ? 1 : 2;
					value = Character.digit( ch, 8 );
				}
				else {
					switch( ch ) {
						case 'b':
							buf.append('\b');
							break;
							
						case 'f':
							buf.append('\f');
							break;
							
						case 'n':
							buf.append('\n');
							break;
							
						case 'r':
							buf.append('\r');
							break;
							
						case 't':
							buf.append('\t');
							break;
							
						case '\'':
							buf.append('\'');
							break;
	
						case '\"':
							buf.append('\"');
							break;
	
						case '\\':
							buf.append('\\');
							break;
							
						default:
							yyerror( "Illegal escape character '" + ch + "'" );
					}
				}
				
				escaped = false;
			}
			else if(ch == '\\') {
				escaped = true;
			}
			else {
				buf.append( ch );
			}
		}
	}

	return buf.toString();
}

private Boolean toBoolean(String str) {
	str = str.trim();

	return new Boolean( str );
}

private Integer toInteger(String str) {
	str = str.trim();
	
	Integer result;
	
	if(str.startsWith("0x") || str.startsWith( "0X" ) ) {
		result = new Integer( Integer.parseInt( str.substring( 2 ), 16 ) );
	}
	else if(str.length() > 1 && str.startsWith("0") ) {
		result = new Integer( Integer.parseInt( str.substring( 1 ), 8 ) );
	}
	else {
		result = new Integer( str );
	}
	
	return result;
}

private Long toLong(String str) {
	str = str.trim();

	Long result;
	
	if( !str.endsWith("l") && !str.endsWith("L") ) {
		yyerror( "Long literal must end with 'l' or 'L'." );
	}
	
	int len = str.length() - 1;
	
	if(str.startsWith("0x") || str.startsWith( "0X" ) ) {
		result = new Long( Long.parseLong( str.substring( 2, len ), 16 ) );
	}
	else if(str.startsWith("0") ) {
		result = new Long( Long.parseLong( str.substring( 1, len ), 8 ) );
	}
	else {
		result = new Long( str.substring( 0, len ) );
	}

	return result;
}

private Float toFloat(String str) {
	str = str.trim();
	return new Float( str );
}

private Double toDouble(String str) {
	str = str.trim();

	if( !str.endsWith("d") && !str.endsWith("D") ) {
		yyerror( "Double literal must end with 'd' or 'D'." );
	}
	
	return new Double( str.substring( 0, str.length() - 1 ) );
}

/**
 * Convert a character literal into a character.
 */
private Character toChar(String str) {
	str = str.trim();

	if( !str.startsWith("'") && !str.endsWith("'") ) {
		yyerror("Character must be single quoted.");
	}

	String str2 = convertString( str.substring( 1, str.length() - 1 ) );
	
	if( str2.length() != 1) {
		yyerror("Only one character allowed in character constants.");
	}
	
	return new Character( str2.charAt( 0 ) );
}

/**
 * Convert a string literal into a string.
 */
private String toString(String str) {
	str = str.trim();

	if( str.length() < 2 && !str.startsWith("\"") && !str.endsWith("\"") ) {
		yyerror("String must be double quoted.");
	}

	String str2 = convertString( str.substring( 1, str.length() - 1 ) );
	return str2;
}