// class headers
package com.thoughtworks.qdox.parser.impl;
import com.thoughtworks.qdox.parser.*;
%%

// class and lexer definitions
%class JFlexLexer
%public
%implements Lexer
%byaccj
%unicode
%line
%column

%{

    private int classDepth = 0;
    private int nestingDepth = 0;
    private int annotationDepth = 0;
    private int assignmentDepth = 0;
    private int stateDepth = 0;
    private int[] stateStack = new int[10];
    private int braceMode = CODEBLOCK;
    private String className;
    private boolean javaDocNewLine;
    private boolean javaDocStartedContent;
    private StringBuffer codeBody = new StringBuffer(8192);
	private boolean at;
	private boolean annotation;
    private boolean newMode;
    private boolean bracketMode;
    private boolean anonymousMode;
    private boolean enumMode;
    private boolean appendingToCodeBody;
    private boolean shouldCaptureCodeBody;
    private boolean isConstructor;

    public void setCaptureCodeBody(boolean shouldCaptureCodeBody) {
        this.shouldCaptureCodeBody = shouldCaptureCodeBody;
    }

    public String text() {
        return yytext();
    }

    public int lex() throws java.io.IOException {
        return yylex();
    }
    
    public int getLine() {
        return yyline + 1;
    }

    public int getColumn() {
        return yycolumn + 1;
    }

    private void pushState(int newState) {
        stateStack[stateDepth++] = zzLexicalState;
        yybegin(newState);
    }

    private void popState() {
        yybegin(stateStack[--stateDepth]);
    }
    
    public String getCodeBody(){
        String s = codeBody.toString();
        codeBody = new StringBuffer(8192);
        return s;
    }

%}

Eol                     = \r|\n|\r\n
WhiteSpace              = {Eol} | [ \t\f]
CommentChar             = ( [^ \t\r\n*] | "*"+ [^ \t\r\n/*] )
IntegerLiteral			= (( [1-9] ([0-9])* ) | ( "0" [xX] ([0-9]|[a-f]|[A-F])+ ) | ( "0" ([0-7])* ))
LongLiteral				= (( [1-9] ([0-9])* ) | ( "0" [xX] ([0-9]|[a-f]|[A-F])+ ) | ( "0" ([0-7])* )) [lL]
Exponent				= [eE] [+-]? ([0-9])+
FloatLiteral			= ( [0-9]+ ("." [0-9]+)? ({Exponent})? ([fF])? ) |
						  ( "." [0-9]+ ({Exponent})? ([fF])? ) |
						  ( ([0-9])+ {Exponent} ([fF])? ) |
						  ( ([0-9])+ ({Exponent})? [fF] )
DoubleLiteral			= ( [0-9]+ ("." [0-9]+)? ({Exponent})? [dD] ) |
						  ( "." [0-9]+ ({Exponent})? [dD] ) |
						  ( ([0-9])+ {Exponent} [dD] ) |
						  ( ([0-9])+ ({Exponent})? [dD] )
Id						= [:jletter:] [:jletterdigit:]*

%state JAVADOC CODEBLOCK PARENBLOCK ASSIGNMENT STRING CHAR SINGLELINECOMMENT MULTILINECOMMENT ANNOTATION ANNOSTRING ANNOCHAR ENUM

%%

<YYINITIAL, ENUM> {
    "."                 { return Parser.DOT; }
    "..."               { return Parser.DOTDOTDOT; }
    ","                 { return Parser.COMMA; }
    "*"                 { return Parser.STAR; }

    "package"           { return Parser.PACKAGE; }
    "import"            { return Parser.IMPORT; }
    "public"            { return Parser.PUBLIC; }
    "protected"         { return Parser.PROTECTED; }
    "private"           { return Parser.PRIVATE; }
    "static"            { return Parser.STATIC; }
    "final"             { return Parser.FINAL; }
    "abstract"          { return Parser.ABSTRACT; }
    "native"            { return Parser.NATIVE; }
    "strictfp"          { return Parser.STRICTFP; }
    "synchronized"      { return Parser.SYNCHRONIZED; }
    "transient"         { return Parser.TRANSIENT; }
    "volatile"          { return Parser.VOLATILE; }
    "throws"            { return Parser.THROWS; }
    "extends"           { return Parser.EXTENDS; }
    "implements"        { return Parser.IMPLEMENTS; }
    "super"             { return Parser.SUPER; }

    "["                 { nestingDepth++; return Parser.SQUAREOPEN; }
    "]"                 { nestingDepth--; return Parser.SQUARECLOSE; }
/*    "("                 {
        nestingDepth++;
		
        if( annotation ) {
        	annotationDepth = nestingDepth;
            pushState(ANNOTATION);
        }

        annotation = false;

        if (enumMode) {
          pushState(PARENBLOCK);
        } else {
          return Parser.PARENOPEN;
        }
    }
*/    
    ")"                 { nestingDepth--; return Parser.PARENCLOSE; }
    "<"                 { return Parser.LESSTHAN; }
    ">"                 { return Parser.GREATERTHAN; }
    "&"                 { return Parser.AMPERSAND; }
    "?"                 { return Parser.QUERY; }

    "@" {WhiteSpace}* "interface" {
      	classDepth++;
        braceMode = CODEBLOCK;
        return Parser.ANNOINTERFACE;
	}

    "class"             {
        classDepth++;
        braceMode = CODEBLOCK;
        return Parser.CLASS; 
    }
    
    "interface"         { 
        classDepth++;
        braceMode = CODEBLOCK;
        return Parser.INTERFACE;
    }
    
    "enum"              {
        classDepth++;
        braceMode = ENUM;
        return Parser.ENUM;
    }

    "@"                 {
        at = true;                
        return Parser.AT;
    }

    "{"                 {
        if(braceMode == ENUM) { /* when fulle supported braceMode >= 0 */
          if(braceMode == ENUM) {
            isConstructor = true;
          }
          else if(braceMode == CODEBLOCK) { } /* todo */        
          pushState(braceMode);
          braceMode = -1;
          yypushback(1);
        }
        else {
          nestingDepth++;
          if (nestingDepth == classDepth + 1) {
            getCodeBody(); /* reset codebody */
              appendingToCodeBody = true;
              pushState(CODEBLOCK);
          }
          else {
              return Parser.BRACEOPEN;
          }
        }
    }
/*    
    "}"                 { 
        nestingDepth--;
        if (nestingDepth == classDepth - 1) {
            classDepth--;
        }
        return Parser.BRACECLOSE; 
    }
*/
    "/*" "*"+           { 
        pushState(JAVADOC); 
        javaDocNewLine = true; 
        return Parser.JAVADOCSTART;
    }

    "=" {WhiteSpace}* { 
        assignmentDepth = nestingDepth;
        appendingToCodeBody = true;
        pushState(ASSIGNMENT);
    }
    "default"           { 
        assignmentDepth = nestingDepth;
        appendingToCodeBody = true;
        pushState(ASSIGNMENT);
    }
    {Id} {
        annotation = at;
        at = false;
        
        return Parser.IDENTIFIER;
    }
}
<YYINITIAL> {
    ";"  { return Parser.SEMI; }
    "}"  { 
        nestingDepth--;
        if (nestingDepth == classDepth - 1) {
            classDepth--;
        }
        return Parser.BRACECLOSE; 
    }
        "("                 {
        nestingDepth++;
    
        if( annotation ) {
          annotationDepth = nestingDepth;
            pushState(ANNOTATION);
        }

        annotation = false;

        return Parser.PARENOPEN;
    }
}
<ENUM> {
    ";"  { isConstructor = false; return Parser.SEMI; }
    "}"  { 
        nestingDepth--;
        classDepth--;
        popState();
        return Parser.BRACECLOSE; 
    }
    "("  {
        nestingDepth++;
        if(isConstructor) {
          pushState(PARENBLOCK);
        }
        else {
          return Parser.PARENOPEN;
        }
    }
}
<JAVADOC> {
    "*"+ "/"            { popState(); return Parser.JAVADOCEND; }
    ^ [ \t]* "*"+ / [^/*] { /* ignore */ }
    {Eol}               { javaDocNewLine = true; return Parser.JAVADOCEOL; }
    {CommentChar}* "*"+ / [ \t\r\n] {
        return Parser.JAVADOCTOKEN;
    }
    {CommentChar}+ { 
        int token = Parser.JAVADOCTOKEN;
        if (javaDocNewLine && yycharat(0) == '@') {
                token = Parser.JAVADOCTAG;
        }
        javaDocNewLine = false;
        return token;
    }
}

<CODEBLOCK> {
     "{"                 { codeBody.append('{'); nestingDepth++; }
     "}"                 {
        nestingDepth--;
        if (nestingDepth == classDepth) {
            popState();
            appendingToCodeBody = false;
            return Parser.CODEBLOCK;
        } else {
            codeBody.append('}');
        }
    }
}

<ANNOTATION> {
	"("                 { ++ nestingDepth; return Parser.PARENOPEN; }
    ")"                 { if( nestingDepth-- == annotationDepth) { popState(); } return Parser.PARENCLOSE; }

	","                 { return Parser.COMMA; }
    "="                 { return Parser.EQUALS; }

	"{"                 { nestingDepth++; return Parser.BRACEOPEN; }
    "}"                 { nestingDepth--; return Parser.BRACECLOSE; }

	"\""                { appendingToCodeBody=true; codeBody.append("\""); pushState(ANNOSTRING); }
    "\'"                { appendingToCodeBody=true; codeBody.append("\'"); pushState(ANNOCHAR); }

	"."                 { return Parser.DOT; }

    "?"                 { return Parser.QUERY; }
    ":"                 { return Parser.COLON; }
    "<<"                { return Parser.LESSTHAN2; }
    ">>>"               { return Parser.GREATERTHAN3; }
    ">>"                { return Parser.GREATERTHAN2; }
    "=="                { return Parser.EQUALS2; }
    "!="                { return Parser.NOTEQUALS; }
    "<"                 { return Parser.LESSTHAN; }
    ">"                 { return Parser.GREATERTHAN; }
    "<="                { return Parser.LESSEQUALS; }
    ">="                { return Parser.GREATEREQUALS; }
    "*"                 { return Parser.STAR; }
    "/"                 { return Parser.SLASH; }
    "%"                 { return Parser.PERCENT; }
    "+"                 { return Parser.PLUS; }
    "-"                 { return Parser.MINUS; }
    
    "byte"              { return Parser.BYTE; }
    "char"              { return Parser.CHAR; }
    "short"             { return Parser.SHORT; }
    "int"               { return Parser.INT; }
    "long"              { return Parser.LONG; }
    "float"             { return Parser.FLOAT; }
    "double"            { return Parser.DOUBLE; }

    "&&"                { return Parser.AMPERSAND2; }
    "||"                { return Parser.VERTLINE2; }
    "!"                	{ return Parser.EXCLAMATION; }

    "&"                 { return Parser.AMPERSAND; }
    "|"                 { return Parser.VERTLINE; }
    "^"                	{ return Parser.CIRCUMFLEX; }
    "~"                 { return Parser.TILDE; }

	{IntegerLiteral}	{ return Parser.INTEGER_LITERAL; }
	{LongLiteral}		{ return Parser.LONG_LITERAL; }
	{FloatLiteral}		{ return Parser.FLOAT_LITERAL; }
	{DoubleLiteral}		{ return Parser.DOUBLE_LITERAL; }
	"true" | "false"	{ return Parser.BOOLEAN_LITERAL; }
	
	"class"				{ return Parser.CLASS; }

	{Id} {
        return Parser.IDENTIFIER;
    }

	"@" 				{ return Parser.AT; }
}

<ANNOSTRING> {
	"\""            { codeBody.append("\""); popState(); appendingToCodeBody=false; return Parser.STRING_LITERAL; }
	"\\\""          { codeBody.append("\\\""); }
	"\\\\"          { codeBody.append("\\\\"); }
}

<ANNOCHAR> {
	\'              { codeBody.append("\'"); popState(); appendingToCodeBody=false; return Parser.CHAR_LITERAL; }
	"\\'"           { codeBody.append("\\'"); }
	"\\\\"          { codeBody.append("\\\\"); }
}

<PARENBLOCK> {
    "("                 { nestingDepth++; }
    ")"                 {
		nestingDepth--;
        if (nestingDepth == classDepth) {
            popState();
			return Parser.PARENBLOCK;
        }
    }
}

<ASSIGNMENT> {
    ";"                 { 
        if (nestingDepth == assignmentDepth) {
            appendingToCodeBody = true;
            newMode = false;
            popState(); 
            return Parser.SEMI; 
        } else {
            codeBody.append(';');
        }
    }
    ","                 {
        if (nestingDepth == assignmentDepth) {
            appendingToCodeBody = true;
            popState(); 
            return Parser.COMMA; 
        } else {
            codeBody.append(',');
        }
    }
    "{"                 {
        codeBody.append('{');
		anonymousMode = true;
		nestingDepth++;
    }
    "}"                 {
		codeBody.append('}');
        if (anonymousMode) {
            nestingDepth--;
        	if (nestingDepth==assignmentDepth) { 
                anonymousMode=false;
            }
        }
    }

    "("                 { codeBody.append('('); nestingDepth++; }
    ")"                 {
        codeBody.append(')');
        nestingDepth--; 
        if (nestingDepth < assignmentDepth) {
            appendingToCodeBody = true; 
            popState(); 
            return Parser.PARENCLOSE; 
        }
    }
    "["                 { codeBody.append('['); bracketMode = true; nestingDepth++; }
    "]"                 { codeBody.append(']'); bracketMode = false; nestingDepth--; }
    "new"               {
        codeBody.append("new");
        if (nestingDepth==assignmentDepth) {
            newMode=true;
        } 
    }
    "<"                 {
        codeBody.append('<');
        if (!bracketMode && newMode && !anonymousMode) {
            nestingDepth++; 
        }
    }
    ">"                 {
        codeBody.append('>');
        if (!anonymousMode) {
	        if (!bracketMode && newMode) {
    	        nestingDepth--;
    	    	if (nestingDepth==assignmentDepth) { 
    	            newMode=false;
    	        }
        	}
        }
    }
}

<ASSIGNMENT, YYINITIAL, CODEBLOCK, PARENBLOCK> {
    "\""                { if (appendingToCodeBody) { codeBody.append('"');  } pushState(STRING); }
    \'                  { if (appendingToCodeBody) { codeBody.append('\''); } pushState(CHAR); }
    "//"                { if (appendingToCodeBody) { codeBody.append("//"); } pushState(SINGLELINECOMMENT); }
    "/*"                { if (appendingToCodeBody) { codeBody.append("/*"); } pushState(MULTILINECOMMENT); }
    "/**/"              { if (appendingToCodeBody) { codeBody.append("/**/"); } }
}

<CODEBLOCK, ASSIGNMENT> { 
    .|{WhiteSpace}	    { codeBody.append(yytext()); }
}

<STRING> {
    "\""                { if (appendingToCodeBody) { codeBody.append('"');    } popState(); }
    "\\\""              { if (appendingToCodeBody) { codeBody.append("\\\""); } }
    "\\\\"              { if (appendingToCodeBody) { codeBody.append("\\\\"); } }
}

<CHAR> {
    \'                  { if (appendingToCodeBody) { codeBody.append('"');    } popState(); }
    "\\'"               { if (appendingToCodeBody) { codeBody.append("\\'");  } }
    "\\\\"              { if (appendingToCodeBody) { codeBody.append("\\\\"); } }
}

<SINGLELINECOMMENT> {
    {Eol}               { if (appendingToCodeBody) { codeBody.append(yytext()); } popState(); }
}

<MULTILINECOMMENT> {
    "*/"                { if (appendingToCodeBody) { codeBody.append("*/"); } popState(); }
}

.|\r|\n|\r\n            { if (appendingToCodeBody) { codeBody.append(yytext()); } }
