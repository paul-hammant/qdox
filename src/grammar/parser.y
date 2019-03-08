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
%token PACKAGE IMPORT PUBLIC PROTECTED PRIVATE STATIC FINAL ABSTRACT NATIVE STRICTFP SYNCHRONIZED TRANSIENT VOLATILE DEFAULT
%token OPEN MODULE REQUIRES TRANSITIVE EXPORTS OPENS TO USES PROVIDES WITH
%token CLASS INTERFACE ENUM ANNOINTERFACE THROWS EXTENDS IMPLEMENTS SUPER DEFAULT NEW
%token BRACEOPEN BRACECLOSE SQUAREOPEN SQUARECLOSE PARENOPEN PARENCLOSE
%token LESSTHAN GREATERTHAN LESSEQUALS GREATEREQUALS
%token LESSTHAN2 GREATERTHAN2 GREATERTHAN3
%token EXCLAMATION AMPERSAND2 VERTLINE2 EQUALS2 NOTEQUALS
%token TILDE AMPERSAND VERTLINE CIRCUMFLEX
%token VOID
%token QUERY COLON COLONCOLON AT
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
%token <sval> SUPER
%token <sval> EQUALS STAREQUALS SLASHEQUALS PERCENTEQUALS PLUSEQUALS MINUSEQUALS LESSTHAN2EQUALS GREATERTHAN2EQUALS GREATERTHAN3EQUALS AMPERSANDEQUALS CIRCUMFLEXEQUALS VERTLINEEQUALS
%type <type> PrimitiveType ReferenceType ArrayType ClassOrInterfaceType TypeVariable
%type <annoval> Expression Literal Annotation ElementValue ElementValueArrayInitializer
%type <annoval> ConditionalExpression ConditionalOrExpression ConditionalAndExpression InclusiveOrExpression ExclusiveOrExpression AndExpression
%type <annoval> EqualityExpression RelationalExpression ShiftExpression AdditiveExpression MultiplicativeExpression
%type <annoval> UnaryExpression UnaryExpressionNotPlusMinus PreIncrementExpression PreDecrementExpression Primary PrimaryNoNewArray ArrayCreationExpression MethodInvocation MethodReference ClassInstanceCreationExpression
%type <annoval> PostfixExpression PostIncrementExpression PostDecrementExpression CastExpression Assignment LeftHandSide AssignmentExpression
%type <ival> Dims Dims_opt
%type <sval> QualifiedIdentifier TypeDeclSpecifier MethodBody AssignmentOperator ModuleName
%type <type> Type ReferenceType Wildcard WildcardBounds VariableDeclaratorId ClassOrInterfaceType TypeArgument

%%
// Source: Java Language Specification - Third Edition
//         The Java(TM) Language Specification - Java SE 8 Edition ( Chapter 19. Syntax )

// ------------------------------
// Productions from �7 (Packages)
// ------------------------------

// CompilationUnit:
//     [PackageDeclaration] {ImportDeclaration} {TypeDeclaration}
//     {ImportDeclaration} ModuleDeclaration  
CompilationUnit: CompilationDeclaration
               | CompilationUnit CompilationDeclaration
               ;

CompilationDeclaration: Annotation
                      | ImportDeclaration
                      | ModuleDeclaration
                      | PackageDeclaration
                      | TypeDeclaration
                      ;

// ModuleDeclaration:
//    {Annotation} [open] module ModuleName { {ModuleStatement} }
ModuleDeclaration: OPEN MODULE ModuleName
                   {
                     ModuleDef module = new ModuleDef($3);
                     module.setOpen(true);
                     builder.setModule(module);
                   }
                   BRACEOPEN ModuleStatements_opt BRACECLOSE
                 | MODULE ModuleName
                   {
                     builder.setModule(new ModuleDef($2));
                   }
                   BRACEOPEN ModuleStatements_opt BRACECLOSE
                 ;

//  ModuleName:
//    Identifier
//    ModuleName . Identifier
ModuleName: QualifiedIdentifier 
          ;
ModuleNameList: ModuleName {
                  moduleTargets.add($1);
                }
              | ModuleNameList COMMA ModuleName {
                  moduleTargets.add($3);
                }
              ;

// ModuleStatement:
//    requires {RequiresModifier} ModuleName ;
//    exports PackageName [to ModuleName {, ModuleName}] ;
//    opens PackageName [to ModuleName {, ModuleName}] ;
//    uses TypeName ;
//    provides TypeName with TypeName {, TypeName} ;
ModuleStatement: REQUIRES RequiresModifiers_opt ModuleName SEMI 
                 {
                   ModuleDef.RequiresDef req = new ModuleDef.RequiresDef($3, modifiers);
                   modifiers = new java.util.LinkedHashSet<String>();
                   req.setLineNumber(line);
                   builder.addRequires(req);
                 }
               | EXPORTS QualifiedIdentifier /* =PackageName */ 
                 {
                   exp = new ModuleDef.ExportsDef($2);
                   exp.setLineNumber(line);
                 } 
                 ToDeclaration_opt SEMI
                 {
                   exp.getTargets().addAll(moduleTargets);
                   moduleTargets = new LinkedList<String>();
                   builder.addExports(exp);
                 }
               | OPENS QualifiedIdentifier /* =PackageName */
                 {
                   opn = new ModuleDef.OpensDef($2);
                   opn.setLineNumber(line);
                 }
                 ToDeclaration_opt SEMI
                 {
                   opn.getTargets().addAll(moduleTargets);
                   moduleTargets = new LinkedList<String>();
                   builder.addOpens(opn);
                 }
               | USES Type /* =TypeName */ SEMI {
                   ModuleDef.UsesDef uss = new ModuleDef.UsesDef($2);
                   uss.setLineNumber(line);
                   builder.addUses(uss);
                 }
               | PROVIDES Type /* =TypeName */ 
                 {
                   prv = new ModuleDef.ProvidesDef($2);
                   prv.setLineNumber(line);
                 }
                 WITH TypeList SEMI 
                 {
                   prv.getImplementations().addAll(typeList);
                   builder.addProvides(prv);
                 }
               ;
ModuleStatements_opt: 
                    | ModuleStatements_opt ModuleStatement
                    ;

RequiresModifier: TRANSITIVE { modifiers.add("transitive"); }
                | STATIC     { modifiers.add("static"); }
                ;
RequiresModifiers_opt:
                     | RequiresModifiers_opt RequiresModifier
                     ;
                     
ToDeclaration_opt:
                 | TO ModuleNameList
                 ;

// PackageDeclaration:
//     {PackageModifier} package Identifier {. Identifier} ;
// PackageModifier:
//      Annotation   
PackageDeclaration: PACKAGE 
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

// -----------------------------
// Productions from �8 (Classes)
// -----------------------------

// ClassDeclaration: 
//     NormalClassDeclaration
//     EnumDeclaration
ClassDeclaration: NormalClassDeclaration 
                | EnumDeclaration
                ;

// NormalClassDeclaration: 
//     {ClassModifier} class Identifier [TypeParameters] [Superclass] [Superinterfaces] ClassBody
NormalClassDeclaration: Modifiers_opt CLASS IDENTIFIER
                        {
                          cls.setType(ClassDef.CLASS);
                          cls.setLineNumber(lexer.getLine());
                          cls.getModifiers().addAll(modifiers); modifiers.clear(); 
                          cls.setName( $3 );
                        }
                        TypeParameters_opt Superclass_opt Superinterfaces_opt  
                        {
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
                       typeList.clear();
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

// VariableInitializer:
//     Expression
//     ArrayInitializer
// VariableInitializer: ArrayInitializer
//                    | Expression
//                    ;

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
            | TypeParameters Annotation Type /* =Result */ IDENTIFIER
              {
                builder.beginMethod();
                mth.setLineNumber(lexer.getLine());
                mth.getModifiers().addAll(modifiers); modifiers.clear();
                mth.setTypeParams(typeParams);
                mth.setReturnType($3);
                mth.setName($4);
              }
              PARENOPEN FormalParameterList_opt PARENCLOSE Dims_opt Throws_opt
              {
                mth.setDimensions($9);
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
EnumDeclaration: Modifiers_opt ENUM IDENTIFIER 
                 { 
                   cls.setLineNumber(lexer.getLine());
                   cls.getModifiers().addAll(modifiers);
                   cls.setName( $3 );
                   cls.setType(ClassDef.ENUM);
                   builder.beginClass(cls);
                   cls = new ClassDef();
                   fieldType = new TypeDef($3, 0);
                 }
                 Superinterfaces_opt EnumBody
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
// Productions from �9 (Interfaces)
// -----------------------------

// InterfaceDeclaration: 
//     NormalInterfaceDeclaration
//     AnnotationTypeDeclaration
InterfaceDeclaration: NormalInterfaceDeclaration
                    | AnnotationTypeDeclaration
                    ;
               
// NormalInterfaceDeclaration: 
//     {InterfaceModifier} interface Identifier [TypeParameters] [ExtendsInterfaces] InterfaceBody
NormalInterfaceDeclaration: Modifiers_opt INTERFACE  
                            {
                              cls.setType(ClassDef.INTERFACE);
                              cls.setLineNumber(lexer.getLine());
                              cls.getModifiers().addAll(modifiers); modifiers.clear(); 
                            }
                            IDENTIFIER TypeParameters_opt ExtendsInterfaces_opt
                            {
                              cls.setName( $4 );
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
                             cls.setLineNumber(lexer.getLine());
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
Annotation: AT 
            {
              line = lexer.getLine();
            }
			QualifiedIdentifier 
            {
              AnnoDef annotation = new AnnoDef( new TypeDef($3) );
              annotation.setLineNumber(line);
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

    
ElementValues_opt:
                 | ElementValues_opt ElementValue
                   { 
                     annoValueList.add($2); 
                   } 
                 | ElementValues_opt COMMA;    

//--------------------------------------------------------
 _AnnotationParens_opt:
                   | PARENOPEN AnnotationElement_opt PARENCLOSE 
                   ;          
          
          
Annotations_opt: 
               | Annotations_opt Annotation;

// -----------------------------
// Productions from �10 (Arrays)
// -----------------------------
//
// ArrayInitializer:
//     { [VariableInitializerList] [,] }
// ArrayInitializer: BRACEOPEN VariableInitializerList_opt BRACECLOSE
//                 ;
//  
// VariableInitializerList:
//     VariableInitializer {, VariableInitializer}
// VariableInitializerList: VariableInitializerList VariableInitializer
//                        | VariableInitializerList COMMA
//                        ;
// VariableInitializerList_opt:
//                            | VariableInitializerList
//                            ;

// ----------------------------------
// Productions from �15 (Expressions)
// ----------------------------------

// Primary:
//     PrimaryNoNewArray 
//     ArrayCreationExpression
Primary: PrimaryNoNewArray
       | ArrayCreationExpression
       ;

// PrimaryNoNewArray:
//     Literal 
//     TypeName {[ ]} . class 
//     void . class 
//     this 
//     TypeName . this 
//     ( Expression ) 
//     ClassInstanceCreationExpression 
//     FieldAccess 
//     ArrayAccess 
//     MethodInvocation 
//     MethodReference
PrimaryNoNewArray: Literal 
                 | MethodInvocation
                 | PrimitiveType Dims_opt DOT CLASS 
                   { 
                     $$ = new TypeRefDef(new TypeDef($1.getName(), $2));
                   }
                 | PARENOPEN Expression PARENCLOSE
                   { 
                     $$ = new ParenExpressionDef($2); 
                   }
                 | ClassInstanceCreationExpression
                 | QualifiedIdentifier DOT CLASS 
                   { 
                     $$ = new TypeRefDef(new TypeDef($1, 0));
                   }
                 | QualifiedIdentifier Dims DOT CLASS
                   {
                     $$ = new TypeRefDef(new TypeDef($1, $2));
                   } 
                 | MethodReference 
                 | QualifiedIdentifier 
                   { 
                     $$ = new FieldRefDef($1); 
                   }
                 ;

// ClassInstanceCreationExpression:
//     new [TypeArguments] {Annotation} Identifier [TypeArgumentsOrDiamond] ( [ArgumentList] ) [ClassBody] 
//     ExpressionName . new [TypeArguments] {Annotation} Identifier [TypeArgumentsOrDiamond] ( [ArgumentList] ) [ClassBody] 
//     Primary . new [TypeArguments] {Annotation} Identifier [TypeArgumentsOrDiamond] ( [ArgumentList] ) [ClassBody]
//// TypeArguments_opt confuses parser
ClassInstanceCreationExpression: NEW TypeArguments IDENTIFIER TypeArgumentsOrDiamond_opt PARENOPEN ArgumentList_opt PARENCLOSE CODEBLOCK_opt 
                                 { 
                                   CreatorDef creator = new CreatorDef();
                                   creator.setCreatedName( $3 );
                                   $$ = creator; 
                                 }
                               | NEW IDENTIFIER TypeArgumentsOrDiamond_opt PARENOPEN ArgumentList_opt PARENCLOSE CODEBLOCK_opt
                                 {
                                   CreatorDef creator = new CreatorDef();
                                   creator.setCreatedName( $2 );
                                   $$ = creator; 
                                 }
                               ;

CODEBLOCK_opt:
             | CODEBLOCK
             ;

// TypeArgumentsOrDiamond:
//     TypeArguments 
//     <>
TypeArgumentsOrDiamond: TypeArguments
                      | LESSTHAN GREATERTHAN
                      ;
TypeArgumentsOrDiamond_opt:
                          | TypeArgumentsOrDiamond
                          ;
                          
// FieldAccess:
//     Primary . Identifier 
//     super . Identifier 
//     TypeName . super . Identifier
// ArrayAccess:
//     ExpressionName [ Expression ]
//     PrimaryNoNewArray [ Expression ]

// MethodInvocation:
//     MethodName ( [ArgumentList] ) 
//     TypeName . [TypeArguments] Identifier ( [ArgumentList] ) 
//     ExpressionName . [TypeArguments] Identifier ( [ArgumentList] ) 
//     Primary . [TypeArguments] Identifier ( [ArgumentList] ) 
//     super . [TypeArguments] Identifier ( [ArgumentList] ) 
//     TypeName . super . [TypeArguments] Identifier ( [ArgumentList] )
MethodInvocation: Primary DOT TypeParameters_opt IDENTIFIER PARENOPEN ArgumentList_opt PARENCLOSE
                  {
                    $$ = new MethodInvocationDef( $1 + "." + $4, null);
                  }                
                | IDENTIFIER PARENOPEN ArgumentList_opt PARENCLOSE
                  {
                    $$ = new MethodInvocationDef($1, null);
                  }
                | QualifiedIdentifier PARENOPEN ArgumentList_opt PARENCLOSE
                  {
                    $$ = new MethodInvocationDef($1, null);
                  }
                | QualifiedIdentifier DOT TypeParameters IDENTIFIER PARENOPEN ArgumentList_opt PARENCLOSE
                  {
                    $$ = new MethodInvocationDef($1, null);
                  }
                | SUPER DOT TypeParameters_opt IDENTIFIER PARENOPEN ArgumentList_opt PARENCLOSE
                  {
                    $$ = new MethodInvocationDef("super." + $1, null);
                  }
                | QualifiedIdentifier DOT SUPER DOT TypeParameters_opt IDENTIFIER PARENOPEN ArgumentList_opt PARENCLOSE
                  {
                    $$ = new MethodInvocationDef($1 + ".super", null);
                  }
                ;

// ArgumentList:
//     Expression {, Expression}
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
                | ArgumentList
                ;

// MethodReference:
//     ExpressionName :: [TypeArguments] Identifier 
//     ReferenceType :: [TypeArguments] Identifier 
//     Primary :: [TypeArguments] Identifier 
//     super :: [TypeArguments] Identifier 
//     TypeName . super :: [TypeArguments] Identifier 
//     ClassType :: [TypeArguments] new 
//     ArrayType :: new
MethodReference: QualifiedIdentifier COLONCOLON TypeArguments_opt IDENTIFIER
                 {
                   $$ = new MethodReferenceDef();
                 }
               | SUPER COLONCOLON TypeArguments_opt IDENTIFIER
                 {
                   $$ = new MethodReferenceDef();
                 }
               | QualifiedIdentifier COLONCOLON NEW
                 {
                   $$ = new MethodReferenceDef();
                 }
               | ArrayType COLONCOLON TypeArguments_opt IDENTIFIER
                 {
                   $$ = new MethodReferenceDef();
                 }
               ;

// ArrayCreationExpression:
//     new PrimitiveType DimExprs [Dims] 
//     new ClassOrInterfaceType DimExprs [Dims] 
//     new PrimitiveType Dims ArrayInitializer 
//     new ClassOrInterfaceType Dims ArrayInitializer
ArrayCreationExpression: NEW PrimitiveType DimExprs Dims_opt 
                         {
                           CreatorDef creator = new CreatorDef();
                           creator.setCreatedName( $2.getName() );
                           $$ = creator; 
                         }
                       | NEW ClassOrInterfaceType DimExprs Dims_opt 
                         {
                           CreatorDef creator = new CreatorDef();
                           creator.setCreatedName( $2.getName() );
                           $$ = creator; 
                         }  
                       | NEW PrimitiveType Dims CODEBLOCK /* =ArrayInitializer */
                         {
                           CreatorDef creator = new CreatorDef();
                           creator.setCreatedName( $2.getName() );
                           $$ = creator; 
                         }
                       | NEW ClassOrInterfaceType Dims CODEBLOCK /* =ArrayInitializer */
                         {
                           CreatorDef creator = new CreatorDef();
                           creator.setCreatedName( $2.getName() );
                           $$ = creator; 
                         }
                       ;

// DimExprs:
//     DimExpr {DimExpr}
DimExprs: DimExpr
        | DimExprs DimExpr
        ;
 
// DimExpr:
//     {Annotation} [ Expression ]
DimExpr: SQUAREOPEN Expression SQUARECLOSE
       ;

// ConstantExpression:
//     Expression

// Expression:
//     LambdaExpression 
//     AssignmentExpression
Expression: AssignmentExpression
          ;

// LambdaExpression:
//     LambdaParameters -> LambdaBody 
// LambdaParameters:
//     Identifier 
//     ( [FormalParameterList] ) 
//     ( InferredFormalParameterList ) 
// InferredFormalParameterList:
//     Identifier {, Identifier} 
// LambdaBody:
//     Expression 
//     Block

// AssignmentExpression:
//     ConditionalExpression 
//     Assignment
AssignmentExpression: ConditionalExpression
                    | Assignment
                    ; 

// Assignment:
//     LeftHandSide AssignmentOperator Expression
Assignment: LeftHandSide AssignmentOperator Expression
            {
              $$ = new AssignmentDef($1, $2, $3);
            }
          ;

// LeftHandSide:
//     ExpressionName 
//     FieldAccess 
//     ArrayAccess
LeftHandSide: QualifiedIdentifier
              {
                $$ = new FieldRefDef($1);
              }
            ;

// AssignmentOperator:
//     =
//     *=
//     /=
//     %=
//     +=
//     -=
//     <<=
//     >>=
//     >>>=
//     &=
//     ^=
//     |=
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
                  | VERTLINEEQUALS
                  ;

// ConditionalExpression:
//     ConditionalOrExpression 
//     ConditionalOrExpression ? Expression : ConditionalExpression
ConditionalExpression: ConditionalOrExpression 
                     | ConditionalOrExpression QUERY Expression COLON ConditionalExpression 
                       { 
                         $$ = new QueryDef($1, $3, $5);
                       }
                     ;

// ConditionalOrExpression:
//     ConditionalAndExpression 
//     ConditionalOrExpression || ConditionalAndExpression
ConditionalOrExpression: ConditionalAndExpression 
                       | ConditionalOrExpression VERTLINE2 ConditionalAndExpression 
                         { 
                           $$ = new LogicalOrDef($1, $3);
                         }
                       ;

// ConditionalAndExpression:
//     InclusiveOrExpression 
//     ConditionalAndExpression && InclusiveOrExpression
ConditionalAndExpression: InclusiveOrExpression 
                        | ConditionalAndExpression AMPERSAND2 InclusiveOrExpression 
                          { 
                            $$ = new LogicalAndDef($1, $3); 
                          }
                        ;

// InclusiveOrExpression:
//     ExclusiveOrExpression 
//     InclusiveOrExpression | ExclusiveOrExpression
InclusiveOrExpression: ExclusiveOrExpression 
                     | InclusiveOrExpression VERTLINE ExclusiveOrExpression 
                       { 
                         $$ = new OrDef($1, $3); 
                       }
                     ;

// ExclusiveOrExpression:
//     AndExpression 
//     ExclusiveOrExpression ^ AndExpression
ExclusiveOrExpression: AndExpression 
                     | ExclusiveOrExpression CIRCUMFLEX AndExpression 
                       { 
                         $$ = new ExclusiveOrDef($1, $3);
                       }
                     ;

// AndExpression:
//     EqualityExpression 
//     AndExpression & EqualityExpression
AndExpression: EqualityExpression 
             | AndExpression AMPERSAND EqualityExpression 
               { 
                 $$ = new AndDef($1, $3); 
               }
             ;

// EqualityExpression:
//     RelationalExpression 
//     EqualityExpression == RelationalExpression 
//     EqualityExpression != RelationalExpression
EqualityExpression: RelationalExpression 
                  | EqualityExpression EQUALS2 RelationalExpression   
                    { 
                      $$ = new EqualsDef($1, $3);
                    } 
                  | EqualityExpression NOTEQUALS RelationalExpression 
                    { 
                      $$ = new NotEqualsDef($1, $3); 
                    }
                  ;

// RelationalExpression:
//     ShiftExpression 
//     RelationalExpression < ShiftExpression 
//     RelationalExpression > ShiftExpression 
//     RelationalExpression <= ShiftExpression 
//     RelationalExpression >= ShiftExpression 
//     RelationalExpression instanceof ReferenceType
RelationalExpression: ShiftExpression 
                    | RelationalExpression LESSTHAN ShiftExpression      
                      { 
                        $$ = new LessThanDef($1, $3); 
                      } 
                    | RelationalExpression GREATERTHAN ShiftExpression   
                      { 
                        $$ = new GreaterThanDef($1, $3); 
                      }
                    | RelationalExpression LESSEQUALS ShiftExpression    
                      { 
                        $$ = new LessEqualsDef($1, $3);
                      } 
                    | RelationalExpression GREATEREQUALS ShiftExpression 
                      { 
                        $$ = new GreaterEqualsDef($1, $3); 
                      } 
                    ;

// ShiftExpression:
//     AdditiveExpression 
//     ShiftExpression << AdditiveExpression 
//     ShiftExpression >> AdditiveExpression 
//     ShiftExpression >>> AdditiveExpression
ShiftExpression: AdditiveExpression 
               | ShiftExpression LESSTHAN2 AdditiveExpression
                 { 
                   $$ = new ShiftLeftDef($1, $3);
                 }
               | ShiftExpression GREATERTHAN2 AdditiveExpression
                 {
                   $$ = new ShiftRightDef($1, $3);
                 }
               | ShiftExpression GREATERTHAN3 AdditiveExpression
                 {
                   $$ = new UnsignedShiftRightDef($1, $3);
                 } 
               ;

// AdditiveExpression:
//     MultiplicativeExpression 
//     AdditiveExpression + MultiplicativeExpression 
//     AdditiveExpression - MultiplicativeExpression
AdditiveExpression: MultiplicativeExpression 
                  | AdditiveExpression PLUS MultiplicativeExpression
                    {
                      $$ = new AddDef($1, $3);
                    } 
                  | AdditiveExpression MINUS MultiplicativeExpression
                    {
                      $$ = new SubtractDef($1, $3);
                    }
                  ;

// MultiplicativeExpression:
//     UnaryExpression 
//     MultiplicativeExpression * UnaryExpression 
//     MultiplicativeExpression / UnaryExpression 
//     MultiplicativeExpression % UnaryExpression
MultiplicativeExpression: UnaryExpression 
                        | MultiplicativeExpression STAR UnaryExpression
                          {
                            $$ = new MultiplyDef($1, $3);
                          } 
                        | MultiplicativeExpression SLASH UnaryExpression
                          {
                            $$ = new DivideDef($1, $3);
                          } 
                        | MultiplicativeExpression PERCENT UnaryExpression
                          {
                            $$ = new RemainderDef($1, $3);
                          }
                        ;

// UnaryExpression:
//     PreIncrementExpression 
//     PreDecrementExpression 
//     + UnaryExpression 
//     - UnaryExpression 
//     UnaryExpressionNotPlusMinus
UnaryExpression: PreIncrementExpression
               | PreDecrementExpression
               | PLUS UnaryExpression
                 {
                   $$ = new PlusSignDef($2);
                 } 
               | MINUS UnaryExpression
                 {
                   $$ = new MinusSignDef($2);
                 }
               | UnaryExpressionNotPlusMinus
               ;

// PreIncrementExpression:
//     ++ UnaryExpression
PreIncrementExpression: PLUSPLUS UnaryExpression
                        { 
                          $$ = new PreIncrementDef($2);
                        }
                      ;

// PreDecrementExpression:
//     -- UnaryExpression
PreDecrementExpression: MINUSMINUS UnaryExpression
                        {
                          $$ = new PreDecrementDef($2);
                        }
                      ;

// UnaryExpressionNotPlusMinus:
//     PostfixExpression 
//     ~ UnaryExpression 
//     ! UnaryExpression 
//     CastExpression
UnaryExpressionNotPlusMinus: PostfixExpression 
                           | TILDE UnaryExpression
                             {
                               $$ = new NotDef($2);
                             } 
                           | EXCLAMATION UnaryExpression
                             {
                               $$ = new LogicalNotDef($2);
                             } 
                           | CastExpression
                           ;

// PostfixExpression:
//     Primary 
//     ExpressionName 
//     PostIncrementExpression 
//     PostDecrementExpression
PostfixExpression: Primary
                 | PostIncrementExpression
                 | PostDecrementExpression
                 ; 

// PostIncrementExpression:
//     PostfixExpression ++
PostIncrementExpression: PostfixExpression PLUSPLUS
                         {
                           $$ = new PostIncrementDef($1);
                         }
                       ; 

// PostDecrementExpression:
//     PostfixExpression -- 
PostDecrementExpression: PostfixExpression MINUSMINUS
                         {
                           $$ = new PostDecrementDef($1);
                         }
                       ;

// CastExpression:
//     ( PrimitiveType ) UnaryExpression 
//     ( ReferenceType {AdditionalBound} ) UnaryExpressionNotPlusMinus 
//     ( ReferenceType {AdditionalBound} ) LambdaExpression
CastExpression: PARENOPEN PrimitiveType Dims_opt PARENCLOSE UnaryExpression
                {
                  $$ = new CastDef(new TypeDef($2.getName(), $3), $5);
                } 
              | PARENOPEN QualifiedIdentifier PARENCLOSE UnaryExpressionNotPlusMinus
                {
                  $$ = new CastDef(new TypeDef($2, 0), $4);
                }
              | PARENOPEN QualifiedIdentifier Dims PARENCLOSE UnaryExpressionNotPlusMinus
                {
                  $$ = new CastDef(new TypeDef($2, $3), $5);
                }
              ;


// --------------------------------------------------
// Productions from §4 (Types, Values, and Variables)
// --------------------------------------------------

// Type:
//     PrimitiveType 
//     ReferenceType
Type: PrimitiveType
    | ReferenceType
    ;

// PrimitiveType:
//     {Annotation} NumericType 
//     {Annotation} boolean
// NumericType:
//     IntegralType 
//     FloatingPointType 
// IntegralType:
//     byte short int long char 
// FloatingPointType:
//     float double 

// ReferenceType:
//     ClassOrInterfaceType 
//     TypeVariable 
//     ArrayType
ReferenceType: TypeVariable
             | ArrayType
             | ClassOrInterfaceType
             ; 

// Actually
// ClassOrInterfaceType:
//     ClassType 
//     InterfaceType 
// ClassType:
//     {Annotation} TypeIdentifier [TypeArguments] 
//     PackageName . {Annotation} TypeIdentifier [TypeArguments] 
//     ClassOrInterfaceType . {Annotation} TypeIdentifier [TypeArguments] 
// InterfaceType:
//     ClassType
// Parser can't see the difference  
ClassOrInterfaceType: QualifiedIdentifier /* =PackageName */ DOT Annotations_opt IDENTIFIER 
                      {
                        TypeDef td = new TypeDef($1 + '.' + $4,0);
                        $$ = typeStack.push(td);
                      }
                      TypeArguments_opt
                      {
                        $$ = typeStack.pop();
                      };
                    |
                      TypeDeclSpecifier 
                      {
                        TypeDef td = new TypeDef($1,0);
                        $$ = typeStack.push(td);
                      }
                      TypeArguments_opt
                      {
                        $$ = typeStack.pop();
                      };


// TypeVariable:
//     {Annotation} Identifier
TypeVariable: Annotations_opt QualifiedIdentifier
              {
                $$ = new TypeDef($2,0);
              }
            ;

// ArrayType:
//     PrimitiveType Dims 
//     ClassOrInterfaceType Dims 
//     TypeVariable Dims
ArrayType: ClassOrInterfaceType Dims
           {
             TypeDef td = $1;
             td.setDimensions($2);
             $$ = td;
           }
         | PrimitiveType Dims
           {
             TypeDef td = $1;
             td.setDimensions($2);
             $$ = td;
           }
         ;

// Dims:
//     {Annotation} [ ] {{Annotation} [ ]}
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
        | Dims
        ;

// TypeParameter:
//     {TypeParameterModifier} Identifier [TypeBound]
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

// TypeBound:
//     extends TypeVariable 
//     extends ClassOrInterfaceType {AdditionalBound}
TypeBound: EXTENDS ClassOrInterfaceType
           {
             typeVariable.setBounds(new LinkedList<TypeDef>());
             typeVariable.getBounds().add($2);
           } 
           AdditionalBound_opts
         ;
TypeBound_opt:
             | TypeBound 
             ;

// AdditionalBound:
//     & InterfaceType
AdditionalBound: AMPERSAND ClassOrInterfaceType
                 {
                   typeVariable.getBounds().add($2);
                 }
               ;
AdditionalBound_opts:
                    | AdditionalBound AdditionalBound_opts
                    ;

// TypeArguments:
//     < TypeArgumentList >
TypeArguments: LESSTHAN 
               {
                 typeStack.peek().setActualArgumentTypes(new LinkedList<TypeDef>());
               }
               TypeArgumentList GREATERTHAN
             ;
TypeArguments_opt:
                 | TypeArguments
                 ;

// TypeArgumentList:
//     TypeArgument {, TypeArgument}
TypeArgumentList: TypeArgument 
                  { 
                    (typeStack.peek()).getActualArgumentTypes().add($1);
                  }
                | TypeArgumentList COMMA TypeArgument 
                  { 
                    (typeStack.peek()).getActualArgumentTypes().add($3);
                  }
                ;

// TypeArgument:
//     ReferenceType 
//     Wildcard
TypeArgument: ReferenceType
            | Wildcard
            ;

// Wildcard:
//     {Annotation} ? [WildcardBounds]
Wildcard: QUERY WildcardBounds
          {
            $$ = $2;
          }
        | QUERY
          {
            $$ = new WildcardTypeDef();
          }
        ;
 
// WildcardBounds:
//     extends ReferenceType 
//     super ReferenceType
WildcardBounds: EXTENDS ReferenceType
                {
                  $$ = new WildcardTypeDef($2, "extends" );
                }
              | SUPER ReferenceType
                {
                  $$ = new WildcardTypeDef($2, "super" ); 
                }
              ;

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


// PrimitiveType: 
//     byte
//     short
//     char
//     int
//     long
//     float
//     double
//     boolean
PrimitiveType: BYTE 
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

// Actually
// TypeDeclSpecifier: TypeName | ClassOrInterfaceType . Identifier
// TypeName:          Identifier | TypeName . Identifier
TypeDeclSpecifier: QualifiedIdentifier
                 | ClassOrInterfaceType DOT IDENTIFIER 
                   { 
                     $$ = $1.getName() + '.' + $3;
                   };





//========================================================

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
        | DEFAULT
          {
            modifiers.add("default");
          }
        ;
         
Arguments_opt:
             | PARENOPEN ArgumentList_opt PARENCLOSE
             ;


                        
//========================================================


          
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

//========================================================

%%

private JavaLexer lexer;
private Builder builder;
private StringBuilder textBuffer = new StringBuilder();
private ClassDef cls = new ClassDef();
private MethodDef mth = new MethodDef();
private FieldDef fd;
private ModuleDef.ExportsDef exp;
private ModuleDef.OpensDef opn;
private ModuleDef.ProvidesDef prv;
private List<String> moduleTargets = new LinkedList<String>();
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
    fd.setLineNumber(lexer.getLine());
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