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
%token CLASS INTERFACE ENUM ANNOINTERFACE THROWS EXTENDS IMPLEMENTS SUPER DEFAULT NEW
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
%token <ival> PLUSPLUS MINUSMINUS
%token <sval> EQUALS STAREQUALS SLASHEQUALS PERCENTEQUALS PLUSEQUALS MINUSEQUALS LESSTHAN2EQUALS GREATERTHAN2EQUALS GREATERTHAN3EQUALS AMPERSANDEQUALS CIRCUMFLEXEQUALS VERTLINEEQUALS
%type <type> BasicType
%type <annoval> Expression Literal Annotation ElementValue ElementValueArrayInitializer
%type <annoval> ConditionalExpression ConditionalOrExpression ConditionalAndExpression InclusiveOrExpression ExclusiveOrExpression AndExpression
%type <annoval> EqualityExpression RelationalExpression ShiftExpression AdditiveExpression MultiplicativeExpression
%type <annoval> UnaryExpression UnaryExpressionNotPlusMinus Primary MethodInvocation Creator
%type <annoval> PostfixExpression CastExpression Assignment LeftHandSide AssignmentExpression
%type <ival> Dims Dims_opt
%type <sval> QualifiedIdentifier TypeDeclSpecifier MethodBody AssignmentOperator CreatedName
%type <type> Type ReferenceType VariableDeclaratorId ClassOrInterfaceType TypeArgument

%%
// Source: Java Language Specification - Third Edition
//         The Java(TM) Language Specification - Java SE 8 Edition ( Chapter 19. Syntax )

// ------------------------------
// Productions from §7 (Packages)
// ------------------------------

// CompilationUnit:
//     [PackageDeclaration] {ImportDeclaration} {TypeDeclaration}  
CompilationUnit: PackageDeclaration_opt ImportDeclarations_opt TypeDeclarations_opt
               ;

// PackageDeclaration:
//     {PackageModifier} package Identifier {. Identifier} ;
// PackageModifier:
//      Annotation   
PackageDeclaration: package
                  | Annotation
                  ;
PackageDeclaration_opt:
                      | PackageDeclaration_opt PackageDeclaration
                      ;

package: PACKAGE 
         { 
           line = lexer.getLine(); 
         } 
         QualifiedIdentifier /* =PackageName */SEMI 
         { 
           builder.addPackage(new PackageDef($3, line)); 
         }
         ;

// ImportDeclaration:
//     SingleTypeImportDeclaration 
//     TypeImportOnDemandDeclaration 
//     SingleStaticImportDeclaration 
//     StaticImportOnDemandDeclaration 
ImportDeclaration: SingleTypeImportDeclaration
                 | TypeImportOnDemandDeclaration
                 | SingleStaticImportDeclaration
                 | StaticImportOnDemandDeclaration
                 ;
ImportDeclarations_opt: 
                      | ImportDeclarations_opt ImportDeclaration
                      ;

// SingleTypeImportDeclaration:
//     import TypeName ; 
SingleTypeImportDeclaration: IMPORT QualifiedIdentifier SEMI 
                             { 
                               builder.addImport( $2 ); 
                             }
                           ;

// TypeImportOnDemandDeclaration:
//     import PackageOrTypeName . * ; 
TypeImportOnDemandDeclaration: IMPORT QualifiedIdentifier DOT STAR SEMI 
                               { 
                                 builder.addImport( $2 + ".*" ); 
                               }
                             ;

// SingleStaticImportDeclaration:
//     import static TypeName . Identifier ; 
SingleStaticImportDeclaration: IMPORT STATIC QualifiedIdentifier SEMI 
                               { 
                                 builder.addImport( "static " + $3);
                               }
                             ;

// StaticImportOnDemandDeclaration:
//     import static TypeName . * ;
StaticImportOnDemandDeclaration: IMPORT STATIC QualifiedIdentifier DOT STAR SEMI 
                                 { 
                                   builder.addImport( "static " + $3 + ".*" ); 
                                 }
                               ;

// TypeDeclaration:
//     ClassDeclaration 
//     InterfaceDeclaration 
//     ; 
TypeDeclaration: ClassDeclaration
               | InterfaceDeclaration
               | SEMI
               ;
TypeDeclarations_opt: 
                    | TypeDeclarations_opt 
                      { 
                        line = lexer.getLine(); 
                      } 
                      TypeDeclaration
                    ;

// -----------------------------
// Productions from §8 (Classes)
// -----------------------------

// ClassDeclaration: 
//     NormalClassDeclaration
//     EnumDeclaration
ClassDeclaration: NormalClassDeclaration 
                | EnumDeclaration
                ;

// NormalClassDeclaration: 
//     {ClassModifier} class Identifier [TypeParameters] [Superclass] [Superinterfaces] ClassBody
NormalClassDeclaration: Modifiers_opt CLASS IDENTIFIER TypeParameters_opt Superclass_opt Superinterfaces_opt  
                        {
                          cls.setType(ClassDef.CLASS);
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
                        }
                      ;  

// TypeParameters:
//     < TypeParameterList >
TypeParameters: LESSTHAN 
                { 
                  typeParams = new LinkedList<TypeVariableDef>(); 
                } 
                TypeParameterList GREATERTHAN
              ;
TypeParameters_opt: 
                  | TypeParameters
                  ;

// TypeParameterList:
//     TypeParameter {, TypeParameter} 
TypeParameterList: TypeParameter 
                 | TypeParameterList COMMA TypeParameter
                 ;
                
// Superclass:
//     extends ClassType 
Superclass_opt:
              | EXTENDS ReferenceType
                {
                  cls.getExtends().add($2);
                }
              ;

// Superinterfaces:
//     implements InterfaceTypeList                
Superinterfaces_opt:
                   | IMPLEMENTS TypeList
                     {
                       cls.getImplements().addAll( typeList );
                     }
                   ;

// InterfaceTypeList:
//     InterfaceType {, InterfaceType} 
//// -> InterfaceTypeList is for QDox the same as TypeList

// ClassBody: 
//     { { ClassBodyDeclaration } }
ClassBody: BRACEOPEN ClassBodyDeclarations_opt BRACECLOSE
         ; 
ClassBody_opt:
             | ClassBody
             ;

// ClassBodyDeclaration:
//     ClassMemberDeclaration 
//     InstanceInitializer 
//     StaticInitializer 
//     ConstructorDeclaration
// ## for now StaticInitializer includes InstanceInitializer due to parsing errors  
ClassBodyDeclaration: StaticInitializer
                    | ClassMemberDeclaration 
                    | ConstructorDeclaration
                    ;
ClassBodyDeclarations_opt:
                         | ClassBodyDeclarations_opt
                           { 
                             line = lexer.getLine(); 
                           }
                           ClassBodyDeclaration
                         ;

// ClassMemberDeclaration:
//     FieldDeclaration 
//     MethodDeclaration 
//     ClassDeclaration 
//     InterfaceDeclaration 
//     ; 
ClassMemberDeclaration: FieldDeclaration
                      | MethodDeclaration
                      | ClassDeclaration
                      | InterfaceDeclaration
                      | SEMI
                      ;

// FieldDeclaration:
//     {FieldModifier} UnannType VariableDeclaratorList ;
FieldDeclaration: Modifiers_opt Type VariableDeclaratorId
                  {
                    fieldType = $2;
                    // we're doing some lexer magic: lexer.getCodeBody() contains [= VariableInitializer]
                    makeField($3, lexer.getCodeBody(), false);
                    builder.beginField(fd);
                    builder.endField();
                  }
                  extrafields SEMI
                  {
                    modifiers.clear();
                  }
                ;

extrafields: 
           | extrafields COMMA 
             { 
               line = lexer.getLine();
             } 
             VariableDeclaratorId
             {
               // we're doing some lexer magic: lexer.getCodeBody() contains [= VariableInitializer]
               makeField($4, lexer.getCodeBody(), false);
               builder.beginField(fd);
               builder.endField();
             }
           ; 

// VariableDeclaratorId:
//     Identifier [Dims]
VariableDeclaratorId: IDENTIFIER Dims_opt 
                      {
                        $$ = new TypeDef($1,$2);
                      }
                    ;

// MethodDeclaration:
//     {MethodModifier} MethodHeader MethodBody
MethodDeclaration: Modifiers_opt MethodHeader MethodBody
                   {
                     mth.setBody($3);
                     builder.endMethod(mth);
                     mth = new MethodDef();
                   }
                 ;

// MethodHeader:
//     Result MethodDeclarator [Throws] 
//     TypeParameters {Annotation} Result MethodDeclarator [Throws]
// MethodDeclarator:
//     Identifier ( [FormalParameterList] ) [Dims]
//## MethodDeclarator must be part of MethodHeader so Parser recognizes this as a Method 
MethodHeader: TypeParameters Type /* =Result */ IDENTIFIER
              {
                builder.beginMethod();
                mth.setLineNumber(lexer.getLine());
                mth.getModifiers().addAll(modifiers); modifiers.clear();
                mth.setTypeParams(typeParams);
                mth.setReturnType($2);
                mth.setName($3);
              } 
              PARENOPEN FormalParameterList_opt PARENCLOSE Dims_opt Throws_opt
              {
                mth.setDimensions($8);
              } 
            | Type /* =Result */ IDENTIFIER  
              {
                builder.beginMethod();
                mth.setLineNumber(lexer.getLine());
                mth.getModifiers().addAll(modifiers); modifiers.clear();
                mth.setReturnType($1);
                mth.setName($2);
              } 
              PARENOPEN FormalParameterList_opt PARENCLOSE Dims_opt Throws_opt 
              {
                mth.setDimensions($7);
              };

// FormalParameterList:
//     FormalParameters , LastFormalParameter 
//     LastFormalParameter
FormalParameterList: FormalParameters COMMA LastFormalParameter
                   | LastFormalParameter
                   ;
FormalParameterList_opt: 
                       | FormalParameterList
                       ;
 
// FormalParameters:
//     FormalParameter {, FormalParameter} 
//     ReceiverParameter {, FormalParameter}
FormalParameters: FormalParameter 
                | FormalParameters COMMA FormalParameter
//                | ReceiverParameter {, FormalParameter}
//                | ReceiverParameter COMMA FormalParameter
                ; 
 
// FormalParameter:
//     {VariableModifier} UnannType VariableDeclaratorId
FormalParameter: Modifiers_opt Type VariableDeclaratorId
                 {
                    param.getModifiers().addAll(modifiers); modifiers.clear();
                    param.setType($2);
                    param.setName($3.getName());
                    param.setDimensions($3.getDimensions());
                    param.setVarArgs(false);
                    builder.addParameter(param);
                    param = new FieldDef();
                 }
               ;

// LastFormalParameter:
//     {VariableModifier} UnannType {Annotation} ... VariableDeclaratorId 
//     FormalParameter
LastFormalParameter: Modifiers_opt Type DOTDOTDOT VariableDeclaratorId
                     {
                       param.getModifiers().addAll(modifiers); modifiers.clear();
                       param.setType($2);
	                   param.setName($4.getName());
                       param.setDimensions($4.getDimensions());
                       param.setVarArgs(true);
                       builder.addParameter(param);
                       param = new FieldDef();
                     }
                   | FormalParameter
                   ;
 
// ReceiverParameter:
//     {Annotation} UnannType [Identifier .] this
// ## todo

// Throws:
//     throws ExceptionTypeList
Throws_opt:
          | THROWS ExceptionTypeList
          ;

// ExceptionTypeList:
//     ExceptionType {, ExceptionType}
ExceptionTypeList: ClassOrInterfaceType /* =ExceptionType */
                   { 
                     mth.getExceptions().add($1); 
                   }
                 | ExceptionTypeList COMMA ClassOrInterfaceType /* =ExceptionType */
                   {
                     mth.getExceptions().add($3);
                   }
                 ;

// MethodBody:
//     Block 
//     ;
MethodBody: CODEBLOCK 
            {
              $$ = lexer.getCodeBody();
            } 
          | SEMI 
           {
             $$ = "";
           }
         ;

// InstanceInitializer:
//     CODEBLOCK 
//                      { 
//                        InitDef def = new InitDef();
//                        def.setBlockContent(lexer.getCodeBody());
//                        builder.addInitializer(def);
//                      };

// StaticInitializer:
//     static Block
StaticInitializer: Modifiers_opt CODEBLOCK 
                   { 
                     InitDef def = new InitDef();
                     def.setStatic(modifiers.contains("static"));modifiers.clear();
                     def.setBlockContent(lexer.getCodeBody());
                     builder.addInitializer(def);
                   }
                 ;

// ConstructorDeclaration:
//     {ConstructorModifier} ConstructorDeclarator [Throws] ConstructorBody
ConstructorDeclaration: Modifiers_opt IDENTIFIER 
                        {
                          builder.beginConstructor();
                          mth.setLineNumber(lexer.getLine());
                          mth.getModifiers().addAll(modifiers); modifiers.clear();
                          mth.setConstructor(true); 
                          mth.setName($2);
                        }
                        PARENOPEN FormalParameterList_opt PARENCLOSE Throws_opt MethodBody /* =ConstructorBody */ 
                        {
                          mth.setBody($8);
                          builder.endConstructor(mth);
                          mth = new MethodDef(); 
                        }
                     |  Modifiers_opt TypeParameters IDENTIFIER 
                        {
                          builder.beginConstructor();
                          mth.setLineNumber(lexer.getLine());
                          mth.setTypeParams(typeParams);
                          mth.getModifiers().addAll(modifiers); modifiers.clear();
                          mth.setConstructor(true); 
                          mth.setName($3);
                        } 
                        PARENOPEN FormalParameterList_opt PARENCLOSE Throws_opt CODEBLOCK 
                        {
                          mth.setBody(lexer.getCodeBody());
                          builder.endConstructor(mth);
                          mth = new MethodDef(); 
                        }
                     ;

// ConstructorDeclarator:
//     [TypeParameters] SimpleTypeName ( [FormalParameterList] )
// ** ConstructorBody, ExplicitConstructorInvocation not used by QDox, out of scope
// ConstructorBody:
//     { [ExplicitConstructorInvocation] [BlockStatements] }
// ExplicitConstructorInvocation:
//     [TypeArguments] this ( [ArgumentList] ) ; 
//     [TypeArguments] super ( [ArgumentList] ) ; 
//     ExpressionName . [TypeArguments] super ( [ArgumentList] ) ; 
//     Primary . [TypeArguments] super ( [ArgumentList] ) ;

// EnumDeclaration:
//     {ClassModifier} enum Identifier [Superinterfaces] EnumBody
EnumDeclaration: Modifiers_opt ENUM IDENTIFIER Superinterfaces_opt 
                 { 
                   cls.setLineNumber(line);
                   cls.getModifiers().addAll(modifiers);
                   cls.setName( $3 );
                   cls.setType(ClassDef.ENUM);
                   builder.beginClass(cls);
                   cls = new ClassDef();
                   fieldType = new TypeDef($3, 0);
                 } 
                 EnumBody
               ;

// EnumBody:
//     { [EnumConstantList] [,] [EnumBodyDeclarations] }
/* The optional COMMA causes trouble for the parser
   For that reason the adjusted options of EnumConstants_opt, which will accept all cases 
*/
EnumBody: BRACEOPEN EnumConstants_opt EnumBodyDeclarations_opt BRACECLOSE 
          { 
            builder.endClass();
            fieldType = null;
            modifiers.clear();
          }
        ;

// EnumConstantList:
//     EnumConstant {, EnumConstant}

// EnumConstants:
//     EnumConstant
//     EnumConstants , EnumConstant
EnumConstants_opt:
                 | EnumConstants_opt COMMA
                 | EnumConstants_opt EnumConstant
                 ;
                 
// EnumConstant:
//     {EnumConstantModifier} Identifier [( [ArgumentList] )] [ClassBody]             
EnumConstant: Annotations_opt IDENTIFIER 
              {
                TypeDef td = new TypeDef($2, 0);
                typeStack.push(td); 
                makeField( td, "", true );
                builder.beginField( fd );
              }
              Arguments_opt ClassBody_opt
              {
                builder.endField();
                typeStack.pop();
              }
            ;

// EnumBodyDeclarations:
//     ; {ClassBodyDeclaration}
EnumBodyDeclarations_opt:
                        | SEMI ClassBodyDeclarations_opt
                        ;      

// -----------------------------
// Productions from §9 (Interfaces)
// -----------------------------

// InterfaceDeclaration: 
//     NormalInterfaceDeclaration
//     AnnotationTypeDeclaration
InterfaceDeclaration: NormalInterfaceDeclaration
                    | AnnotationTypeDeclaration
                    ;
               
// NormalInterfaceDeclaration: 
//     {InterfaceModifier} interface Identifier [TypeParameters] [ExtendsInterfaces] InterfaceBody
NormalInterfaceDeclaration: Modifiers_opt INTERFACE IDENTIFIER TypeParameters_opt ExtendsInterfaces_opt  
                            {
                              cls.setType(ClassDef.INTERFACE);
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
                            }
                          ;

// ExtendsInterfaces:
//     extends InterfaceTypeList
ExtendsInterfaces: EXTENDS TypeList
                   {
                     cls.getExtends().addAll( typeList );
                     typeList.clear();
                   }
                 ;
ExtendsInterfaces_opt: 
                     | ExtendsInterfaces
                     ;

// InterfaceBody:
//     { {InterfaceMemberDeclaration} }
// InterfaceMemberDeclaration:
//     ConstantDeclaration 
//     InterfaceMethodDeclaration 
//     ClassDeclaration 
//     InterfaceDeclaration 
//     ;
// ConstantDeclaration:
//     {ConstantModifier} UnannType VariableDeclaratorList ; 
// ConstantModifier:
//     Annotation public 
//     static final 
// InterfaceMethodDeclaration:
//     {InterfaceMethodModifier} MethodHeader MethodBody 
// InterfaceMethodModifier:
//     Annotation public 
//     abstract default static strictfp

// AnnotationTypeDeclaration:
//     {InterfaceModifier} @ interface Identifier AnnotationTypeBody
AnnotationTypeDeclaration: Modifiers_opt ANNOINTERFACE IDENTIFIER 
                           {
                             cls.setType(ClassDef.ANNOTATION_TYPE);
                             cls.setLineNumber(line);
                             cls.getModifiers().addAll(modifiers); modifiers.clear(); 
                             cls.setName( $3 );
                             builder.beginClass(cls); 
                             cls = new ClassDef();
                           }
                           ClassBody
                           {
                             builder.endClass(); 
                           }
                         ;

// AnnotationTypeBody:
//     { {AnnotationTypeMemberDeclaration} }
// AnnotationTypeMemberDeclaration:
//     AnnotationTypeElementDeclaration 
//     ConstantDeclaration 
//     ClassDeclaration 
//     InterfaceDeclaration 
//     ;
// AnnotationTypeElementDeclaration:
//     {AnnotationTypeElementModifier} UnannType Identifier ( ) [Dims] [DefaultValue] ;
// AnnotationTypeElementModifier:
//     Annotation public 
//     abstract
// DefaultValue:
//     default ElementValue

// Annotation:
//     NormalAnnotation 
//     MarkerAnnotation 
//     SingleElementAnnotation
// NormalAnnotation:
//     @ TypeName ( [ElementValuePairList] )
// MarkerAnnotation:
//     @ TypeName 
// SingleElementAnnotation:
//     @ TypeName ( ElementValue )
Annotation: AT QualifiedIdentifier 
            {
              AnnoDef annotation = new AnnoDef( new TypeDef($2) );
              annotation.setLineNumber(lexer.getLine());
              annotationStack.addFirst(annotation);
            }
            _AnnotationParens_opt
            {
              AnnoDef annotation = annotationStack.removeFirst();
              if(annotationStack.isEmpty()) 
              {
                builder.addAnnotation(annotation);
              }
              $$ = annotation;
            }
          ;

// ElementValuePairList:
//     ElementValuePair { , ElementValuePair }
ElementValuePairList: ElementValuePair 
                    | ElementValuePairList COMMA ElementValuePair
                    ;

// ElementValuePair:
//     Identifier = ElementValue
ElementValuePair: IDENTIFIER EQUALS ElementValue 
                  {
                    annotationStack.getFirst().getArgs().put($1, $3);
                  }
                ;

// ElementValue:
//     ConditionalExpression 
//     ElementValueArrayInitializer 
//     Annotation
ElementValue: ConditionalExpression 
            | Annotation 
            | ElementValueArrayInitializer
            ;

// ElementValueArrayInitializer:
//     { [ElementValueList] [,] }
/* Specs say: { ElementValueList_opt COMMA_opt }
   The optional COMMA causes trouble for the parser
   For that reason the adjusted options of ElementValueList_opt, which will accept all cases
*/    
ElementValueArrayInitializer: {
                                annoValueListStack.add(annoValueList);
                                annoValueList = new LinkedList<ElemValueDef>();
                              }
                              BRACEOPEN ElementValueList_opt BRACECLOSE
                              { 
                                $$ = new ElemValueListDef(annoValueList);
                                annoValueList = annoValueListStack.remove(annoValueListStack.size() - 1);
                              }
                            ;


// AnnotationElement:
//     ElementValuePairList
//     ElementValue
AnnotationElement_opt: 
                     | ElementValuePairList
                     | ElementValue
                       { 
                         annotationStack.getFirst().getArgs().put("value", $1);
                       }
                     ;

    
ElementValueList_opt:
                 | ElementValueList_opt ElementValue
                   { 
                     annoValueList.add($2); 
                   } 
                 | ElementValueList_opt COMMA;    

//--------------------------------------------------------
 _AnnotationParens_opt:
                   | PARENOPEN AnnotationElement_opt PARENCLOSE 
                   ;          
          
          
Annotations_opt: 
               | Annotations_opt Annotation;


//========================================================
// QualifiedIdentifier:
//     Identifier { . Identifier }
QualifiedIdentifier: IDENTIFIER
                   | QualifiedIdentifier DOT IDENTIFIER
                     {
                       $$ = $1 + '.' + $3;
                     }
                   ;
                   
// TypeName:
//     Identifier
//     PackageOrTypeName . Identifier

// PackageOrTypeName:
//     Identifier
//     PackageOrTypeName . Identifier

//========================================================
// Type:
//     BasicType {[]}
//     ReferenceType  {[]}
Type: BasicType Dims_opt
      {
        TypeDef td = $1;
        td.setDimensions($2);
        $$ = td;
      }
    | ReferenceType Dims_opt
      {
        TypeDef td = $1;
        td.setDimensions($2);
        $$ = td;
      }
    ;

// BasicType: 
//     byte
//     short
//     char
//     int
//     long
//     float
//     double
//     boolean
BasicType: BYTE 
           { 
             $$ = new TypeDef("byte"); 
           } 
         | SHORT 
           { 
             $$ = new TypeDef("short"); 
           } 
         | CHAR 
           { 
             $$ = new TypeDef("char");
           }
         | INT 
           { 
             $$ = new TypeDef("int"); 
           } 
         | LONG 
           { 
             $$ = new TypeDef("long"); 
           } 
         | FLOAT 
           {
             $$ = new TypeDef("float");
           }
         | DOUBLE 
           { 
             $$ = new TypeDef("double");
           }
         | BOOLEAN 
           { 
             $$ = new TypeDef("boolean"); 
           }  
         ;

// ReferenceType:
//     Identifier [TypeArguments] { . Identifier [TypeArguments] }
ReferenceType: ClassOrInterfaceType
             ; 

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
TypeDeclSpecifier: QualifiedIdentifier
                 | ClassOrInterfaceType DOT IDENTIFIER 
                   { 
                     $$ = $1.getName() + '.' + $3;
                   };


// TypeArguments: 
//     < TypeArgument { , TypeArgument } >
TypeArguments: LESSTHAN 
               {
                 typeStack.peek().setActualArgumentTypes(new LinkedList<TypeDef>());
               }
               _TypeArgumentList GREATERTHAN
             ;
TypeArguments_opt:
                 | TypeArguments
                 ;

// TypeArgument:  
//    ReferenceType {[]}
//    ? [ ( extends | super ) ( BasicType [] {[]} | ReferenceType {[]} ) ]
TypeArgument: ReferenceType Dims_opt
              { 
                TypeDef td = $1;
                td.setDimensions($2);
                $$ = td;
              } 
            | QUERY              
              { 
                $$ = new WildcardTypeDef(); 
              } 
            | QUERY EXTENDS Type // actually ( BasicType [] {[]} | ReferenceType {[]} )
              { 
                $$ = new WildcardTypeDef($3, "extends" ); 
              }
            | QUERY SUPER Type   // actually ( BasicType [] {[]} | ReferenceType {[]} )
              { 
                $$ = new WildcardTypeDef($3, "super" ); 
              }
            ;
//---------------------------------------------------------
_TypeArgumentList: TypeArgument 
                  { 
                    (typeStack.peek()).getActualArgumentTypes().add($1);
                  }
                | _TypeArgumentList COMMA TypeArgument 
                  { 
                    (typeStack.peek()).getActualArgumentTypes().add($3);
                  }
                ;
//========================================================

// NonWildcardTypeArguments:
//     < TypeList >
NonWildcardTypeArguments: LESSTHAN TypeList GREATERTHAN
                        ;

// TypeList:  
//     ReferenceType { , ReferenceType }
TypeList: ReferenceType
          {
            typeList.add( $1 );
          }
        | TypeList COMMA ReferenceType
          {
            typeList.add( $3 );
          }
        ;

// TypeArgumentsOrDiamond:
//     < > 
//     TypeArguments
TypeArgumentsOrDiamond_opt:
                          | LESSTHAN GREATERTHAN
                          | TypeArguments
                          ;

// NonWildcardTypeArgumentsOrDiamond:
//     < >
//     NonWildcardTypeArguments




// TypeParameter:
//     Identifier [extends Bound]
TypeParameter: IDENTIFIER 
               { 
                 typeVariable = new TypeVariableDef($1);
                 typeVariable.setBounds(new LinkedList<TypeDef>());
               }
               _ExtendsBound_opt
               {
                 typeParams.add(typeVariable);
                 typeVariable = null;
               };
             
// Bound:
//     ReferenceType { & ReferenceType }
Bound: ReferenceType
       {
         typeVariable.getBounds().add($1);
       }
     | Bound AMPERSAND ReferenceType
       {
         typeVariable.getBounds().add($3);
       }
     ;  
//--------------------------------------------------------
_ExtendsBound_opt:
                | EXTENDS
                  {
                    typeVariable.setBounds(new LinkedList<TypeDef>());
                  } 
                  Bound
                ;
//========================================================
Modifiers_opt:
             | Modifiers_opt Modifier;

// Modifier: 
//     Annotation
//     public
//     protected
//     private
//     static 
//     abstract
//     final
//     native
//     synchronized
//     transient
//     volatile
//     strictfp
Modifier: Annotation 
        | PUBLIC
          {
            modifiers.add("public");
          }
        | PROTECTED
          {
            modifiers.add("protected");
          } 
        | PRIVATE
          {
            modifiers.add("private");
          }
        | STATIC
          {
            modifiers.add("static");
          }
        | FINAL
          {
            modifiers.add("final");
          }
        | ABSTRACT
          {
            modifiers.add("abstract");
          }
        | NATIVE
          {
            modifiers.add("native");
          }
        | SYNCHRONIZED
          {
            modifiers.add("synchronized");
          }
        | VOLATILE
          {
            modifiers.add("volatile");
          }
        | TRANSIENT
          {
            modifiers.add("transient");
          }
        | STRICTFP
          {
            modifiers.add("strictfp");
          }
        ;
         
Arguments_opt:
             | PARENOPEN ArgumentList_opt PARENCLOSE
             ;

// VariableInitializer:
//     ArrayInitializer
//     Expression
VariableInitializer: ArrayInitializer
                   | Expression
                   ;
                   
// ArrayInitializer:
//     { [ VariableInitializer { , VariableInitializer } [,] ] }
ArrayInitializer: BRACEOPEN VariableInitializers_opt BRACECLOSE
                ;
VariableInitializers_opt:
                        | VariableInitializers_opt VariableInitializer
                        | VariableInitializers_opt COMMA
                        ;
                        
//========================================================

// Primary: 
//     Literal
//     ParExpression
//     this [Arguments]
//     super SuperSuffix
//     new Creator
//     NonWildcardTypeArguments ( ExplicitGenericInvocationSuffix | this Arguments )
//     Identifier { . Identifier } [IdentifierSuffix]
//     BasicType {[]} . class
//     void . class
Primary: Literal 
       | PARENOPEN Expression PARENCLOSE /* ParExpression*/
         { 
           $$ = new ParenExpressionDef($2); 
         }
       | BasicType Dims_opt DOT CLASS 
         { 
           $$ = new TypeRefDef(new TypeDef($1.getName(), $2));
         }
       | QualifiedIdentifier DOT CLASS 
         { 
            $$ = new TypeRefDef(new TypeDef($1, 0));
         }
       | QualifiedIdentifier Dims DOT CLASS
         {
           $$ = new TypeRefDef(new TypeDef($1, $2));
         } 
       | QualifiedIdentifier 
         { 
           $$ = new FieldRefDef($1); 
         }
       | MethodInvocation 
         {
           $$ = $1;
         }
       | NEW Creator
         {
           $$ = $2;
         }
       ;
          
// Literal:
//     IntegerLiteral
//     FloatingPointLiteral
//     CharacterLiteral  
//     StringLiteral  
//     BooleanLiteral
//     NullLiteral
Literal: INTEGER_LITERAL
         { 
           $$ = new ConstantDef($1, Integer.class); 
         } 
       | FLOAT_LITERAL 
         { 
           $$ = new ConstantDef($1, Float.class); 
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
         }
       | BOOLEAN_LITERAL 
         { 
           $$ = new ConstantDef($1, Boolean.class);
         }
       ; 

// Arguments:
//     ( [ Expression { , Expression } ] )
Arguments: PARENOPEN ExpressionList_opt PARENCLOSE
         ;
         
//========================================================

// Creator:  
//     NonWildcardTypeArguments CreatedName ClassCreatorRest
//     CreatedName ( ClassCreatorRest | ArrayCreatorRest )
Creator: NonWildcardTypeArguments CreatedName ClassCreatorRest 
         { 
           CreatorDef creator = new CreatorDef();
           creator.setCreatedName( $2 );
           $$ = creator; 
         }
       | CreatedName ClassCreatorRest
         {
           CreatorDef creator = new CreatorDef();
           creator.setCreatedName( $1 );
           $$ = creator; 
         }
       | CreatedName ArrayCreatorRest 
         {
           CreatorDef creator = new CreatorDef();
           creator.setCreatedName( $1 );
           $$ = creator; 
         }
       ;

// CreatedName:   
//     Identifier [TypeArgumentsOrDiamond] { . Identifier [TypeArgumentsOrDiamond] }
CreatedName: IDENTIFIER TypeArgumentsOrDiamond_opt
             {
               $$ = $1;
             }
           | CreatedName DOT IDENTIFIER TypeArgumentsOrDiamond_opt
             {
               $$ = $1 + "." + $3;
             }
           ; 

// ClassCreatorRest: 
//     Arguments [ClassBody]
ClassCreatorRest: Arguments ClassBody_opt
                ;

// ArrayCreatorRest:
//     [ ( ] {[]} ArrayInitializer | Expression ] {[ Expression ]} {[]} )
ArrayCreatorRest: Dims ArrayInitializer
                | DimExprs Dims_opt
                ;  

ArgumentList: Expression
              {
                builder.addArgument( (ExpressionDef) $1);
              }
            | ArgumentList COMMA Expression
              {
                builder.addArgument( (ExpressionDef) $3);
              }
            ;
ArgumentList_opt:
                | ArgumentList;

DimExprs: DimExpr
        | DimExprs DimExpr;

DimExpr: SQUAREOPEN Expression SQUARECLOSE

Dims: SQUAREOPEN SQUARECLOSE 
      { 
        $$ = 1;
      } 
    | Dims SQUAREOPEN SQUARECLOSE 
      { 
        $$ = $1 + 1;
      };
Dims_opt: { 
            $$ = 0; 
          }
  | Dims;
            
// 15.12 Method Invocation Expressions
MethodInvocation: IDENTIFIER PARENOPEN ArgumentList_opt PARENCLOSE
                  {
                    $$ = new MethodInvocationDef($1, null);
                  }
                | QualifiedIdentifier DOT TypeParameters_opt IDENTIFIER PARENOPEN ArgumentList_opt PARENCLOSE
                  {
                    $$ = new MethodInvocationDef($1, null);
                  };

// 15.14 Postfix Expressions
PostfixExpression: /* ExpressionName | */
                   Primary
     | PostfixExpression PLUSPLUS   { $$ = new PostIncrementDef($1); } 
     | PostfixExpression MINUSMINUS { $$ = new PostDecrementDef($1); };

// 15.15 Unary Operators
UnaryExpression: PLUSPLUS UnaryExpression   { $$ = new PreIncrementDef($2);  }
               | MINUSMINUS UnaryExpression { $$ = new PreDecrementDef($2);  }
               | PLUS UnaryExpression       { $$ = new PlusSignDef($2); } 
               | MINUS UnaryExpression      { $$ = new MinusSignDef($2); }
               | UnaryExpressionNotPlusMinus;

UnaryExpressionNotPlusMinus: PostfixExpression 
                           | TILDE UnaryExpression       { $$ = new NotDef($2); } 
                           | EXCLAMATION UnaryExpression { $$ = new LogicalNotDef($2); } 
                           | CastExpression;

// 15.16 Cast Expressions 
CastExpression: PARENOPEN BasicType Dims_opt PARENCLOSE UnaryExpression   { $$ = new CastDef(new TypeDef($2.getName(), $3), $5); } 
              | PARENOPEN QualifiedIdentifier PARENCLOSE UnaryExpressionNotPlusMinus      { $$ = new CastDef(new TypeDef($2, 0), $4); }
              | PARENOPEN QualifiedIdentifier Dims PARENCLOSE UnaryExpressionNotPlusMinus { $$ = new CastDef(new TypeDef($2, $3), $5); };

// 15.17 Multiplicative Operators
MultiplicativeExpression: UnaryExpression 
                        | MultiplicativeExpression STAR UnaryExpression    { $$ = new MultiplyDef($1, $3); } 
                        | MultiplicativeExpression SLASH UnaryExpression   { $$ = new DivideDef($1, $3); } 
                        | MultiplicativeExpression PERCENT UnaryExpression { $$ = new RemainderDef($1, $3); };

// 15.18 Additive Operators
AdditiveExpression: MultiplicativeExpression 
                  | AdditiveExpression PLUS MultiplicativeExpression  { $$ = new AddDef($1, $3); } 
                  | AdditiveExpression MINUS MultiplicativeExpression { $$ = new SubtractDef($1, $3); };

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
LeftHandSide: QualifiedIdentifier
              {
                $$ = new FieldRefDef($1);
              };
//            | ArrayAccess;

// AssignmentOperator: 
//      = 
//      +=
//      -= 
//      *=
//      /=
//      &=
//      |=
//      ^=
//      %=
//      <<=
//      >>=
//      >>>=            
AssignmentOperator: EQUALS
                  | PLUSEQUALS
                  | MINUSEQUALS
                  | STAREQUALS
                  | SLASHEQUALS
                  | AMPERSANDEQUALS
                  | VERTLINEEQUALS
                  | CIRCUMFLEXEQUALS
                  | PERCENTEQUALS
                  | LESSTHAN2EQUALS
                  | GREATERTHAN2EQUALS
                  | GREATERTHAN3EQUALS
                  ;

// 15.27 Expression
Expression: AssignmentExpression;

ExpressionList_opt: 
                  | ExpressionList;
                  
ExpressionList: Expression
              | ExpressionList Expression;

//========================================================

%%

private JavaLexer lexer;
private Builder builder;
private StringBuilder textBuffer = new StringBuilder();
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
private List<TypeDef> typeList = new LinkedList<TypeDef>();
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