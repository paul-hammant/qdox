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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
%}

%token SEMI DOT DOTDOTDOT COMMA STAR PERCENT EQUALS ANNOSTRING ANNOCHAR SLASH PLUS MINUS
%token STAREQUALS SLASHEQUALS PERCENTEQUALS PLUSEQUALS MINUSEQUALS LESSTHAN2EQUALS GREATERTHAN2EQUALS GREATERTHAN3EQUALS AMPERSANDEQUALS CIRCUMFLEXEQUALS VERTLINEEQUALS 
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT NATIVE STRICTFP SYNCHRONIZED TRANSIENT VOLATILE
%token CLASS INTERFACE ENUM ANNOINTERFACE THROWS EXTENDS IMPLEMENTS SUPER DEFAULT
%token BRACEOPEN BRACECLOSE SQUAREOPEN SQUARECLOSE PARENOPEN PARENCLOSE
%token LESSTHAN GREATERTHAN LESSEQUALS GREATEREQUALS
%token LESSTHAN2 GREATERTHAN2 GREATERTHAN3
%token EXCLAMATION AMPERSAND2 VERTLINE2 EQUALS2 NOTEQUALS
%token TILDE AMPERSAND VERTLINE CIRCUMFLEX
%token VOID
%token QUERY COLON AT
%token CODEBLOCK PARENBLOCK
%token BYTE SHORT INT LONG CHAR FLOAT DOUBLE BOOLEAN

// strongly typed tokens/types
%token <sval> IDENTIFIER
%token <sval> BOOLEAN_LITERAL
%token <sval> INTEGER_LITERAL
%token <sval> FLOAT_LITERAL
%token <sval> CHAR_LITERAL
%token <sval> STRING_LITERAL
%token <ival> VERTLINE2 AMPERSAND2 VERTLINE CIRCUMFLEX AMPERSAND EQUALS2 NOTEQUALS
%token <ival> LESSTHAN GREATERTHAN LESSEQUALS GREATEREQUALS LESSTHAN2 GREATERTHAN2 GREATERTHAN3
%token <ival> PLUS MINUS STAR SLASH PERCENT TILDE EXCLAMATION
%token <sval> EQUALS STAREQUALS SLASHEQUALS PERCENTEQUALS PLUSEQUALS MINUSEQUALS LESSTHAN2EQUALS GREATERTHAN2EQUALS GREATERTHAN3EQUALS AMPERSANDEQUALS CIRCUMFLEXEQUALS VERTLINEEQUALS
%type <type> PrimitiveType NumericType IntegralType FloatingPointType
%type <type> InterfaceType
%type <type> Wildcard
%type <annoval> Expression Literal Annotation ElementValue ElementValueArrayInitializer
%type <annoval> ConditionalExpression ConditionalOrExpression ConditionalAndExpression InclusiveOrExpression ExclusiveOrExpression AndExpression
%type <annoval> EqualityExpression RelationalExpression ShiftExpression AdditiveExpression MultiplicativeExpression
%type <annoval> UnaryExpression UnaryExpressionNotPlusMinus primary
%type <annoval> PostfixExpression CastExpression Assignment LeftHandSide AssignmentExpression
%type <ival> dims Dims_opt
%type <sval> AnyName TypeDeclSpecifier memberend AssignmentOperator
%type <type> Type ReferenceType VariableDeclaratorId ClassOrInterfaceType ActualTypeArgument

%%
// Source: Java Language Specification - Third Edition
//         The Java(TM) Language Specification - Java SE 7 Edition

// 7 Packages

// 7.3 Compilation Units
CompilationUnit: PackageDeclaration_opt ImportDeclarations_opt TypeDeclarations_opt;

// 7.4 Package Declarations
PackageDeclaration_opt:
                      | PackageDeclaration_opt PackageDeclaration;

PackageDeclaration: package
                  | Annotation;
                      
package: PACKAGE 
         { 
           line = lexer.getLine(); 
         } 
         AnyName /* =PackageName */SEMI 
         { 
           builder.addPackage(new PackageDef($3, line)); 
         };

// 7.5 Import Declarations
ImportDeclarations_opt: 
				      | ImportDeclarations_opt ImportDeclaration;

ImportDeclaration: SingleTypeImportDeclaration
                 | TypeImportOnDemandDeclaration
                 | SingleStaticImportDeclaration
                 | StaticImportOnDemandDeclaration;

// 7.5.1 Single-Type-Import Declaration
SingleTypeImportDeclaration: IMPORT AnyName /* =TypeName */ SEMI 
                             { 
                               builder.addImport( $2 ); 
                             };

// 7.5.2 Type-Import-on-Demand Declaration
TypeImportOnDemandDeclaration: IMPORT AnyName /* =PackageOrTypeName */ DOT STAR SEMI 
                               { 
                                 builder.addImport( $2 + ".*" ); 
                               };

// 7.5.3 Single Static Import Declaration
SingleStaticImportDeclaration: IMPORT STATIC AnyName /* =TypeName . Identifier */ SEMI 
                               { 
                                 builder.addImport( "static " + $3);
                               };

// 7.5.4 Static-Import-on-Demand Declaration             
StaticImportOnDemandDeclaration: IMPORT STATIC AnyName /* =TypeName */ DOT STAR SEMI 
                                 { 
                                   builder.addImport( "static " + $3 + ".*" ); 
                                 };

// 7.6 Top Level Type Declarations
TypeDeclarations_opt: 
                    | TypeDeclarations_opt 
                      { 
                        line = lexer.getLine(); 
                      } 
                      TypeDeclaration;

TypeDeclaration: ClassDeclaration
/*               | InterfaceDeclaration */
               | SEMI;

// 3 Lexical Structure

// 3.10 Literals
// NOTE: LONG_LITERAL and DOUBLE_LITERAL are not part of 
Literal: INTEGER_LITERAL
         { 
           $$ = new ConstantDef($1, Integer.class); 
         } 
       | FLOAT_LITERAL 
         { 
           $$ = new ConstantDef($1, Float.class); 
         } 
       | BOOLEAN_LITERAL 
         { 
           $$ = new ConstantDef($1, Boolean.class);
         } 
       | CHAR_LITERAL 
         {
           String s = lexer.getCodeBody(); 
           $$ = new ConstantDef(s, Character.class); 
         } 
       | STRING_LITERAL 
         { 
           String s = lexer.getCodeBody(); 
           $$ = new ConstantDef(s, String.class); 
         };

// 4 Types, Values, and Variables

// 4.1 The Kinds of Types and Values
Type: PrimitiveType
    | ReferenceType;
    
// 4.2 Primitive Types and Values        
PrimitiveType: NumericType
             | BOOLEAN 
               { 
                 $$ = new TypeDef("boolean"); 
               };

NumericType: IntegralType
           | FloatingPointType;
	
IntegralType: BYTE 
              { 
                $$ = new TypeDef("byte"); 
              } 
            | SHORT 
              { 
                $$ = new TypeDef("short"); 
              } 
            | INT 
              { 
                $$ = new TypeDef("int"); 
              } 
            | LONG 
              { 
                $$ = new TypeDef("long"); 
              } 
            | CHAR 
              { 
                $$ = new TypeDef("char");
              };

FloatingPointType: FLOAT 
                   {
                     $$ = new TypeDef("float");
                   }
                 | DOUBLE 
                   { 
                     $$ = new TypeDef("double");
                   };
                   
// 4.3 Reference Types and Values
// Actually
// ReferenceType: ClassOrInterfaceType | TypeVariable | ArrayType
// TypeVariable:  Identifier
// ArrayType:     Type [ ]
ReferenceType: ClassOrInterfaceType Dims_opt 
               {
                 TypeDef td = $1;
    	         td.setDimensions($2);
                 $$ = td;
               };
// Actually
// ClassOrInterfaceType: ClassType | InterfaceType;
// ClassType:            TypeDeclSpecifier TypeArguments_opt
// InterfaceType:        TypeDeclSpecifier TypeArguments_opt
// Parser can't see the difference  
ClassOrInterfaceType: TypeDeclSpecifier 
                      {
                        TypeDef td = new TypeDef($1,0);
                        $$ = typeStack.push(td);
                      }
                      TypeArguments_opt
                      {
                        $$ = typeStack.pop();
                      };
// Actually
// TypeDeclSpecifier: TypeName | ClassOrInterfaceType . Identifier
// TypeName:          Identifier | TypeName . Identifier
TypeDeclSpecifier: AnyName
                 | ClassOrInterfaceType DOT IDENTIFIER 
                   { 
                     $$ = $1.getName() + '.' + $3;
                   };
               
// 4.4 Type Variables
TypeParameter: IDENTIFIER 
               { 
                 typeVariable = new TypeVariableDef($1);
                 typeVariable.setBounds(new LinkedList<TypeDef>());
               }
               TypeBound_opt
               {
                 typeParams.add(typeVariable);
                 typeVariable = null;
               };

TypeBound_opt:
             | TypeBound;

TypeBound: EXTENDS ClassOrInterfaceType
		   {
		     typeVariable.setBounds(new LinkedList<TypeDef>());
		     typeVariable.getBounds().add($2);
		   }
		   AdditionalBoundList_opt;
		   
AdditionalBoundList_opt:
                       | AdditionalBoundList_opt AdditionalBound;		   

AdditionalBound: AMPERSAND ClassOrInterfaceType
                 { 
                   typeVariable.getBounds().add($2); 
                 };

// 4.5.1 Type Arguments and Wildcards
TypeArguments_opt:
                 | TypeArguments;

TypeArguments: LESSTHAN 
               {
                 typeStack.peek().setActualArgumentTypes(new LinkedList<TypeDef>());
               }
               ActualTypeArgumentList GREATERTHAN;

ActualTypeArgumentList: ActualTypeArgument 
                        { 
                          (typeStack.peek()).getActualArgumentTypes().add($1);
                        }
                      | ActualTypeArgumentList COMMA ActualTypeArgument 
                        { 
                          (typeStack.peek()).getActualArgumentTypes().add($3);
                        };

ActualTypeArgument: ReferenceType 
                  | Wildcard;
                  
// Actually Wildcard: ? WildcardBounds_Opt
// but it's weird to let an optional bound return a value
Wildcard: QUERY              
          { 
            $$ = new WildcardTypeDef(); 
          } 
        | QUERY EXTENDS ReferenceType 
          { 
            $$ = new WildcardTypeDef($3, "extends" ); 
          }
        | QUERY SUPER ReferenceType
          { 
            $$ = new WildcardTypeDef($3, "super" ); 
          };

// 6.5 Determining the Meaning of a Name
// PackageName | TypeName | ExpressionName | MethodName | PackageOrTypeName | AmbiguousName 
AnyName: IDENTIFIER { $$ = $1; } 
       | AnyName DOT IDENTIFIER { $$ = $1 + '.' + $3; };


// 15.8 Primary Expressions
//Primary: PrimaryNoNewArray
//       | ArrayCreationExpression;
       
//PrimaryNoNewArray: Literal
//                 | Type DOT CLASS
//                 | VOID DOT CLASS
//                 | THIS
//        ClassName.this
//        ( Expression )
//        ClassInstanceCreationExpression
//        FieldAccess
//        MethodInvocation
//        ArrayAccess
       
primary:
    Literal |
    PARENOPEN Expression PARENCLOSE { $$ = new ParenExpressionDef($2); } |
    PrimitiveType Dims_opt DOT CLASS { $$ = new TypeRefDef(new TypeDef($1.getName(), $2)); } |
    AnyName DOT CLASS { $$ = new TypeRefDef(new TypeDef($1, 0)); } |
    AnyName dims DOT CLASS { $$ = new TypeRefDef(new TypeDef($1, $2)); } |
    AnyName { $$ = new FieldRefDef($1); };
	
Dims_opt:  { $$ = 0; }
		| dims;	
dims:
    SQUAREOPEN SQUARECLOSE { $$ = 1; } |
    dims SQUAREOPEN SQUARECLOSE { $$ = $1 + 1; };

// 8 Classes

// 8.1.1 ClassModifier: Annotation public protected private abstract static final strictfp 
// 8.3.1 FieldModifier: Annotation public protected private static final transient volatile
// 8.4.1 VariableModifier: final Annotation
// 8.4.3 MethodModifier: Annotation public protected private abstract static final synchronized native strictfp
// 8.8.3 ConstructorModifier: Annotation public protected private

AnyModifiers_opt:
                | AnyModifiers_opt AnyModifier;

AnyModifier: Annotation 
           | PUBLIC          { modifiers.add("public"); }
           | PROTECTED       { modifiers.add("protected"); } 
           | PRIVATE         { modifiers.add("private"); }
           | STATIC          { modifiers.add("static"); }
           | FINAL           { modifiers.add("final"); }
           | ABSTRACT        { modifiers.add("abstract"); }
           | NATIVE          { modifiers.add("native"); }
           | SYNCHRONIZED    { modifiers.add("synchronized"); }
           | VOLATILE        { modifiers.add("volatile"); }
           | TRANSIENT       { modifiers.add("transient"); }
           | STRICTFP        { modifiers.add("strictfp"); } ;

// 8.1 Class Declaration
ClassDeclaration: NormalClassDeclaration	
                | EnumDeclaration;

NormalClassDeclaration: 
    AnyModifiers_opt /* =ClassModifiers_opt */ classorinterface /* =CLASS or =INTERFACE */ IDENTIFIER TypeParameters_opt opt_extends Interfaces_opt  {
        cls.setLineNumber(line);
        cls.getModifiers().addAll(modifiers); modifiers.clear(); 
        cls.setName( $3 );
        cls.setTypeParameters(typeParams);
        builder.beginClass(cls); 
        cls = new ClassDef(); 
    }
    ClassBody
    {
      builder.endClass(); 
    };    

// 8.1.1 Class Modifiers
// See: AnyModifiers

// 8.1.2 Generic Classes and Type Parameters
TypeParameters_opt: 
                  | TypeParameters;

TypeParameters: LESSTHAN 
                { 
                  typeParams = new LinkedList<TypeVariableDef>(); 
                } 
                TypeParameterList GREATERTHAN;

TypeParameterList: TypeParameter 
                 | TypeParameterList COMMA TypeParameter;
                 
// 8.1.5 Superinterfaces
Interfaces_opt:
              | Interfaces;
              
Interfaces:	IMPLEMENTS InterfaceTypeList;

InterfaceTypeList: InterfaceType { cls.getImplements().add($1); }
                 | InterfaceTypeList COMMA InterfaceType { cls.getImplements().add($3); };

// See ClassOrInterfaceType why like this
InterfaceType: ClassOrInterfaceType;

// 8.1.6 Class Body and Member Declarations
ClassBody: BRACEOPEN ClassBodyDeclarations_opt BRACECLOSE; 

// this is slighly different so we can get the correct linenumber
ClassBodyDeclarations_opt:
                         | ClassBodyDeclarations_opt
                           { 
                             line = lexer.getLine(); 
                           }
                           ClassBodyDeclaration;

ClassBodyDeclaration: ClassMemberDeclaration
/*                    | InstanceInitializer */
                    | StaticInitializer
                    | ConstructorDeclaration;

ConstructorDeclaration: constructor;
StaticInitializer: static_block;

ClassMemberDeclaration:	FieldDeclaration
                      | MethodDeclaration
                      | ClassDeclaration
/*                      | InterfaceDeclaration*/
                      |	SEMI;

classorinterface: 
    CLASS { cls.setType(ClassDef.CLASS); } | 
    INTERFACE { cls.setType(ClassDef.INTERFACE); } |
    ANNOINTERFACE { cls.setType(ClassDef.ANNOTATION_TYPE); };

opt_extends: | EXTENDS extendslist;

extendslist:
    ClassOrInterfaceType { cls.getExtends().add($1); } |
    extendslist COMMA ClassOrInterfaceType { cls.getExtends().add($3); };

static_block:
    AnyModifiers_opt CODEBLOCK { lexer.getCodeBody(); modifiers.clear(); };

// ----- FIELD

FieldDeclaration: 
    AnyModifiers_opt Type VariableDeclaratorId {
        fieldType = $2;
        makeField($3, lexer.getCodeBody(), false);
        builder.beginField(fd);
        builder.endField();
    }
    extrafields SEMI {
        modifiers.clear();
    };
  
extrafields: | 
    extrafields COMMA { line = lexer.getLine(); } VariableDeclaratorId {
        makeField($4, lexer.getCodeBody(), false);
        builder.beginField(fd);
        builder.endField();
    }; 

// 8.3 Field Declarations...
VariableDeclaratorId: IDENTIFIER Dims_opt 
                      {
                        $$ = new TypeDef($1,$2);
                      };

// 8.4 Method Declarations
MethodDeclaration: MethodHeader memberend /* =MethodBody*/ 
                   {
                     mth.setBody($2);
                     builder.endMethod(mth);
                     mth = new MethodDef();
                   };

MethodHeader: AnyModifiers_opt TypeParameters Type /* =ResultType */ IDENTIFIER PARENOPEN
              {
                builder.beginMethod();
                mth.setLineNumber(lexer.getLine());
                mth.getModifiers().addAll(modifiers); modifiers.clear();
                mth.setTypeParams(typeParams);
                mth.setReturnType($3);
                mth.setName($4);
              } 
              FormalParameterList_opt PARENCLOSE Dims_opt Throws_opt
              {
                mth.setDimensions($9);
              } 
            | AnyModifiers_opt Type /* =ResultType */ IDENTIFIER PARENOPEN 
              {
                builder.beginMethod();
                mth.setLineNumber(lexer.getLine());
                mth.getModifiers().addAll(modifiers); modifiers.clear();
                mth.setReturnType($2);
                mth.setName($3);
              } 
              FormalParameterList_opt PARENCLOSE Dims_opt Throws_opt 
              {
                mth.setDimensions($8);
              };

// 8.4.1 Formal Parameters
FormalParameterList_opt:
                       | FormalParameterList;
                       
FormalParameterList: LastFormalParameter
                   | FormalParameters COMMA LastFormalParameter;

FormalParameters: FormalParameter
                | FormalParameters COMMA FormalParameter;
                
FormalParameter:  AnyModifiers_opt /* =VariableModifiers_opt */ Type VariableDeclaratorId
                  {
                    param.setName($3.getName());
                    param.setType($2);
                    param.setDimensions($3.getDimensions());
                    param.setVarArgs(false);
                    param.getModifiers().addAll(modifiers); modifiers.clear();
                    builder.addParameter(param);
                    param = new FieldDef();
                  };

LastFormalParameter: AnyModifiers_opt /* =VariableModifiers_opt */ Type DOTDOTDOT VariableDeclaratorId  /* =VariableDeclaratorId */
                     {
                       param.setName($4.getName()); 
                       param.setType($2);
                       param.setDimensions($4.getDimensions());
                       param.setVarArgs(true);
                       param.getModifiers().addAll(modifiers); modifiers.clear();
                       builder.addParameter(param);
                       param = new FieldDef();
                     };
                   | FormalParameter;

// 8.4.6 Method Throws
Throws_opt:
          | THROWS ExceptionTypeList;

ExceptionTypeList: ClassOrInterfaceType /*ExceptionType*/
                   { 
                     mth.getExceptions().add($1); 
                   }
                 | ExceptionTypeList COMMA ClassOrInterfaceType /* =ExceptionType */
                   {
                     mth.getExceptions().add($3);
                   };
                   
// 8.4.7 Method Body

memberend: CODEBLOCK 
           {
	         $$ = lexer.getCodeBody();
           } 
         | SEMI 
           {
             $$ = "";
           };

// 8.8 Constructor Declarations
constructor: AnyModifiers_opt /* =ConstructorModifiers_opt */ IDENTIFIER PARENOPEN 
             {
               builder.beginConstructor();
               mth.setLineNumber(lexer.getLine());
               mth.getModifiers().addAll(modifiers); modifiers.clear();
               mth.setConstructor(true); 
               mth.setName($2);
             }
             FormalParameterList_opt PARENCLOSE Throws_opt memberend /* =MethodBody */ 
             {
               mth.setBody($8);
               builder.endConstructor(mth);
               mth = new MethodDef(); 
             }
           | AnyModifiers_opt /* =ConstructorModifiers_opt */ TypeParameters IDENTIFIER PARENOPEN 
             {
               builder.beginConstructor();
               mth.setLineNumber(lexer.getLine());
               mth.setTypeParams(typeParams);
               mth.getModifiers().addAll(modifiers); modifiers.clear();
               mth.setConstructor(true); 
               mth.setName($3);
             } 
             FormalParameterList_opt PARENCLOSE Throws_opt memberend /* =MethodBody */ 
             {
               mth.setBody($9);
               builder.endConstructor(mth);
               mth = new MethodDef(); 
             };
             
// 8.9 Enums
EnumDeclaration: AnyModifiers_opt /* =ClassModifiers_opt*/ ENUM IDENTIFIER Interfaces_opt 
                 { 
                   cls.setLineNumber(line);
                   cls.getModifiers().addAll(modifiers);
                   cls.setName( $3 );
                   cls.setType(ClassDef.ENUM);
                   builder.beginClass(cls);
                   cls = new ClassDef();
                   fieldType = new TypeDef($3, 0);
                 } 
                 EnumBody;

/* Specs say: { EnumConstants_opt ,_opt EnumBodyDeclarations_opt }
   The optional COMMA causes trouble for the parser
   For that reason the adjusted options of EnumConstants_opt, which will accept all cases 
*/
EnumBody: BRACEOPEN EnumConstants_opt EnumBodyDeclarations_opt BRACECLOSE 
          { 
            builder.endClass();
            fieldType = null;
            modifiers.clear();
          };

// 8.9 Enums Constants
/* See EnumBody for this slightly different code */
EnumConstants_opt:
                 | EnumConstants_opt COMMA
                 | EnumConstants_opt EnumConstant;
                 
EnumConstant: Annotations_opt IDENTIFIER Arguments_opt
              { 
                makeField( new TypeDef($2, 0), "", true );
                builder.beginField( fd );
              }
              ClassBody_opt
              {
                fd.setBody( lexer.getCodeBody() );
                builder.endField();
              };
         
Arguments_opt:
             | PARENOPEN ArgumentList_opt PARENCLOSE; //PARENBLOCK

ClassBody_opt:
             | ClassBody;

EnumBodyDeclarations_opt:
                        | SEMI ClassBodyDeclarations_opt;      
                        
// 9.7 Annotations

Annotations_opt: 
               | Annotations_opt Annotation;

Annotation /* = NormalAnnotation*/: AT AnyName /* =TypeName */ 
                                    {
                                      AnnoDef annotation = new AnnoDef( new TypeDef($2) );
                                      annotation.setLineNumber(lexer.getLine());
                                      annotationStack.addFirst(annotation);
                                    }
                                    annotationParensOpt
                                    {
                                      AnnoDef annotation = annotationStack.removeFirst();
                                      if(annotationStack.isEmpty()) 
                                      {
                                        builder.addAnnotation(annotation);
                                      }
                                      $$ = annotation;
                                    };
    
annotationParensOpt:
	               | PARENOPEN ElementValue PARENCLOSE 
	                 { 
	                   annotationStack.getFirst().getArgs().put("value", $2);
                     }
	               | PARENOPEN ElementValuePairs_opt PARENCLOSE;
  
ElementValuePairs_opt:
                     | ElementValuePairs;   

    
ElementValuePairs: ElementValuePair 
                 | ElementValuePairs COMMA ElementValuePair;
    
ElementValuePair: IDENTIFIER EQUALS ElementValue 
                  {
                    annotationStack.getFirst().getArgs().put($1, $3);
                  };

/* Specs say: { ElementValues_opt COMMA_opt }
   The optional COMMA causes trouble for the parser
   For that reason the adjusted options of ElementValues_opt, which will accept all cases
*/    
ElementValueArrayInitializer: {
                                annoValueListStack.add(annoValueList);
                                annoValueList = new LinkedList<ElemValueDef>();
                              }
                              BRACEOPEN ElementValues_opt BRACECLOSE
                              { 
                                $$ = new ElemValueListDef(annoValueList);
                                annoValueList = annoValueListStack.remove(annoValueListStack.size() - 1);
                              };
    
ElementValues_opt:
                 | ElementValues_opt ElementValue
                   { 
                     annoValueList.add($2); 
                   } 
                 | ElementValues_opt COMMA;    
    
ElementValue: ConditionalExpression 
            | Annotation 
            | ElementValueArrayInitializer;

// 15.9 Class Instance Creation Expressions
ArgumentList_opt:
                | ArgumentList;

ArgumentList: Expression
            | ArgumentList COMMA Expression;


// 15.14 Postfix Expressions
PostfixExpression: /* ExpressionName | PostIncrementExpression | PostDecrementExpression | */
                   primary;

// 15.15 Unary Operators
UnaryExpression: /* PreIncrementExpression | PreDecrementExpression | */
                 PLUS UnaryExpression  { $$ = new PlusSignDef($2); } 
               | MINUS UnaryExpression { $$ = new MinusSignDef($2); }
               | UnaryExpressionNotPlusMinus;

UnaryExpressionNotPlusMinus: PostfixExpression 
                           | TILDE UnaryExpression       { $$ = new NotDef($2); } 
                           | EXCLAMATION UnaryExpression { $$ = new LogicalNotDef($2); } 
                           | CastExpression;

// 15.16 Cast Expressions	
CastExpression: PARENOPEN PrimitiveType Dims_opt PARENCLOSE UnaryExpression   { $$ = new CastDef(new TypeDef($2.getName(), $3), $5); } 
              | PARENOPEN AnyName PARENCLOSE UnaryExpressionNotPlusMinus      { $$ = new CastDef(new TypeDef($2, 0), $4); }
              | PARENOPEN AnyName dims PARENCLOSE UnaryExpressionNotPlusMinus { $$ = new CastDef(new TypeDef($2, $3), $5); };

// 15.17 Multiplicative Operators
MultiplicativeExpression: UnaryExpression 
                        | MultiplicativeExpression STAR UnaryExpression    { $$ = new MultiplyDef($1, $3); } 
                        | MultiplicativeExpression SLASH UnaryExpression   { $$ = new DivideDef($1, $3); } 
                        | MultiplicativeExpression PERCENT UnaryExpression { $$ = new RemainderDef($1, $3); };

// 15.18 Additive Operators
AdditiveExpression:	MultiplicativeExpression 
                  |	AdditiveExpression PLUS MultiplicativeExpression  { $$ = new AddDef($1, $3); } 
                  |	AdditiveExpression MINUS MultiplicativeExpression { $$ = new SubtractDef($1, $3); };

// 15.19 Shift Operators
ShiftExpression: AdditiveExpression 
               | ShiftExpression LESSTHAN2 AdditiveExpression    { $$ = new ShiftLeftDef($1, $3); }
               | ShiftExpression GREATERTHAN3 AdditiveExpression { $$ = new UnsignedShiftRightDef($1, $3); } 
               | ShiftExpression GREATERTHAN2 AdditiveExpression { $$ = new ShiftRightDef($1, $3); };

// 15.20 Relational Operators
RelationalExpression: ShiftExpression 
                    | RelationalExpression LESSEQUALS ShiftExpression    
                      { 
                        $$ = new LessEqualsDef($1, $3);
                      } 
                    | RelationalExpression GREATEREQUALS ShiftExpression 
                      { 
                        $$ = new GreaterEqualsDef($1, $3); 
                      } 
                    | RelationalExpression LESSTHAN ShiftExpression      
                      { 
                        $$ = new LessThanDef($1, $3); 
                      } 
                    | RelationalExpression GREATERTHAN ShiftExpression   
                      { 
                        $$ = new GreaterThanDef($1, $3); 
                      };

// 15.21 Equality Operators
EqualityExpression: RelationalExpression 
                  | EqualityExpression EQUALS2 RelationalExpression   
                    { 
                      $$ = new EqualsDef($1, $3);
                    } 
                  | EqualityExpression NOTEQUALS RelationalExpression 
                    { 
                      $$ = new NotEqualsDef($1, $3); 
                    };

// 15.22 Bitwise and Logical Operators
InclusiveOrExpression: ExclusiveOrExpression 
                     | InclusiveOrExpression VERTLINE ExclusiveOrExpression 
                       { 
                         $$ = new OrDef($1, $3); 
                       };

ExclusiveOrExpression: AndExpression 
                     | ExclusiveOrExpression CIRCUMFLEX AndExpression 
                       { 
                         $$ = new ExclusiveOrDef($1, $3);
                       };

AndExpression: EqualityExpression 
             | AndExpression AMPERSAND EqualityExpression 
               { 
                 $$ = new AndDef($1, $3); 
               };

// 15.23 Conditional-And Operator &&
ConditionalAndExpression: InclusiveOrExpression 
                        | ConditionalAndExpression AMPERSAND2 InclusiveOrExpression 
                          { 
                            $$ = new LogicalAndDef($1, $3); 
                          };

// 15.24 Conditional-Or Operator ||
ConditionalOrExpression: ConditionalAndExpression 
                       | ConditionalOrExpression VERTLINE2 ConditionalAndExpression 
                         { 
                           $$ = new LogicalOrDef($1, $3);
                         };

// 15.25 Conditional Operator ? :	
ConditionalExpression: ConditionalOrExpression 
                     | ConditionalOrExpression QUERY Expression COLON ConditionalExpression 
                       { 
                         $$ = new QueryDef($1, $3, $5);
                       };
                       
// 15.26 Assignment Operators
AssignmentExpression: ConditionalExpression
                    | Assignment; 

Assignment: LeftHandSide AssignmentOperator AssignmentExpression
            {
              $$ = new AssignmentDef($1, $2, $3);
            };


// ExpressionName | FieldAccess
LeftHandSide: AnyName
              {
                $$ = new FieldRefDef($1);
              };
//            | ArrayAccess;
            
AssignmentOperator: EQUALS
                  | STAREQUALS
                  | SLASHEQUALS
                  | PERCENTEQUALS
                  | PLUSEQUALS
                  | MINUSEQUALS
                  | LESSTHAN2EQUALS
                  | GREATERTHAN2EQUALS
                  | GREATERTHAN3EQUALS
                  | AMPERSANDEQUALS
                  | CIRCUMFLEXEQUALS
                  | VERTLINEEQUALS;

// 15.27 Expression
Expression: AssignmentExpression;

%%

private JavaLexer lexer;
private Builder builder;
private StringBuffer textBuffer = new StringBuffer();
private ClassDef cls = new ClassDef();
private MethodDef mth = new MethodDef();
private FieldDef fd;
private List<TypeVariableDef> typeParams = new LinkedList<TypeVariableDef>(); //for both JavaClass and JavaMethod
private LinkedList<AnnoDef> annotationStack = new LinkedList<AnnoDef>(); // Use LinkedList instead of Stack because it is unsynchronized 
private List<List<ElemValueDef>> annoValueListStack = new LinkedList<List<ElemValueDef>>(); // Use LinkedList instead of Stack because it is unsynchronized
private List<ElemValueDef> annoValueList = null;
private FieldDef param = new FieldDef();
private java.util.Set<String> modifiers = new java.util.LinkedHashSet<String>();
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

public Parser( JavaLexer lexer, Builder builder ) 
{
    lexer.addCommentHandler( this );
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
    ElemValueDef annoval;
}


private void makeField(TypeDef field, String body, boolean enumConstant) {
    fd = new FieldDef( field.getName() );
    fd.setName(field.getName());
    fd.setLineNumber(line);
    fd.getModifiers().addAll(modifiers); 
    fd.setType( fieldType );
    fd.setDimensions(field.getDimensions());
    fd.setEnumConstant(enumConstant);
    fd.setBody(body);
}

public void onComment( String comment, int line, int column ) {
  DefaultJavaCommentLexer commentLexer  = new DefaultJavaCommentLexer( new java.io.StringReader( comment ) );
  commentLexer.setLineOffset( line );
  commentLexer.setColumnOffset( column );
  DefaultJavaCommentParser commentParser = new DefaultJavaCommentParser( commentLexer, builder);
  commentParser.setDebugLexer( this.debugLexer );
  commentParser.setDebugParser( this.yydebug );
  commentParser.parse();
}