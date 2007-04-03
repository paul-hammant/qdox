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
    private int assignmentDepth = 0;
    private int stateDepth = 0;
    private int[] stateStack = new int[10];
    private boolean javaDocNewLine;
    private boolean javaDocStartedContent;
    private StringBuffer codeBody = new StringBuffer(8192);
	private boolean annoExpected;
    private boolean newMode;
    private boolean enumMode;
    private boolean appendingToCodeBody;
    private boolean shouldCaptureCodeBody;

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
        stateStack[stateDepth++] = yy_lexical_state;
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
IntegerLiteral			= (( [1-9] ([0-9])* ) | ( "0" [xX] ([0-9]|[a-f]|[A-F])+ ) | ( "0" ([0-7])* )) ([lL])?
Exponent				= [eE] [+-]? ([0-9])+
FloatLiteral			= ( [0-9]+ ("." [0-9]+)? ({Exponent})? ([fFdD])? ) |
						  ( "." [0-9]+ ({Exponent})? ([fFdD])? ) |
						  ( ([0-9])+ {Exponent} ([fFdD])? ) |
						  ( ([0-9])+ ({Exponent})? [fFdD] )
Id						= [:jletter:] [:jletterdigit:]*

%state JAVADOC CODEBLOCK PARENBLOCK ASSIGNMENT STRING CHAR SINGLELINECOMMENT MULTILINECOMMENT ANNOTATION ANNOSTRING ANNOCHAR

%%

<YYINITIAL> {
    ";"                 { enumMode = false; return Parser.SEMI; }
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
    "("                 {
        nestingDepth++;
		if( annoExpected ) { pushState(ANNOTATION); }
        if (enumMode) {
          pushState(PARENBLOCK);
        } else {
          return Parser.PARENOPEN;
        }
    }

    ")"                 { nestingDepth--; return Parser.PARENCLOSE; }
    "<"                 { return Parser.LESSTHAN; }
    ">"                 { return Parser.GREATERTHAN; }
    "&"                 { return Parser.AMPERSAND; }
    "?"                 { return Parser.QUERY; }

    "@"                 {
		return Parser.AT;
	}

    "class"             {
        classDepth++;
        return Parser.CLASS; 
    }
    "interface"         { 
        classDepth++;
        return Parser.INTERFACE;
    }
    "enum"              {
        classDepth++;
        enumMode = true;
        return Parser.ENUM;
    }
	"@" {WhiteSpace}* "interface"		{
        classDepth++;
        return Parser.ANNOINTERFACE;
	}

    "{"                 {
        nestingDepth++;
        if (nestingDepth == classDepth + 1) {
            appendingToCodeBody = true;
            pushState(CODEBLOCK);
        }
        else {
            return Parser.BRACEOPEN;
        }
    }
    "}"                 { 
        nestingDepth--;
        if (nestingDepth == classDepth - 1) {
            classDepth--;
        }
        return Parser.BRACECLOSE; 
    }

    "/*" "*"+           { 
        pushState(JAVADOC); 
        javaDocNewLine = true; 
        return Parser.JAVADOCSTART;
    }

    "="                 { 
        assignmentDepth = nestingDepth;
        appendingToCodeBody = true;
        pushState(ASSIGNMENT);
    }

    "default"           { 
        assignmentDepth = nestingDepth;
        appendingToCodeBody = true;
        pushState(ASSIGNMENT);
    }

    [:jletter:] [:jletterdigit:]* {
		annoExpected = false;
        return Parser.IDENTIFIER;
    }

	"@" {WhiteSpace}* {Id} ( {WhiteSpace}* "." {WhiteSpace}* {Id} )* {
		annoExpected = true;
		return Parser.ANNOTATION;
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
	"("                 { nestingDepth++; return Parser.PARENOPEN; }
    ")"                 { if( --nestingDepth == classDepth) { popState(); } return Parser.PARENCLOSE; }

	","                 { return Parser.COMMA; }
    "="                 { return Parser.EQUALS; }

	"{"                 { nestingDepth++; return Parser.BRACEOPEN; }
    "}"                 { nestingDepth--; return Parser.BRACECLOSE; }

	"\""                { appendingToCodeBody=true; codeBody.append("\""); pushState(ANNOSTRING); }
    \'                  { appendingToCodeBody=true; codeBody.append("\'"); pushState(ANNOCHAR); }

	"."                 { return Parser.DOT; }

    "<"                 { return Parser.LESSTHAN; }
    ">"                 { return Parser.GREATERTHAN; }
    "*"                 { return Parser.STAR; }
    "/"                 { return Parser.SLASH; }
    "+"                 { return Parser.PLUS; }
    "-"                 { return Parser.MINUS; }

	{IntegerLiteral}	{ return Parser.INTEGER_LITERAL; }
	{FloatLiteral}		{ return Parser.FLOAT_LITERAL; }
	"true" | "false"	{ return Parser.BOOLEAN_LITERAL; }

	[:jletter:] [:jletterdigit:]* {
        return Parser.IDENTIFIER;
    }

	"@" {WhiteSpace}* [:jletter:] [:jletterdigit:]* {
		return Parser.ANNOTATION;
	}

	<ANNOSTRING> {
		"\""            { codeBody.append("\""); popState(); appendingToCodeBody=false; return Parser.ANNOSTRING; }
		"\\\""          { codeBody.append("\\\""); }
		"\\\\"          { codeBody.append("\\\\"); }
	}

	<ANNOCHAR> {
		\'              { codeBody.append("\'"); popState(); appendingToCodeBody=false; return Parser.ANNOCHAR; }
		"\\'"           { codeBody.append("\\'"); }
		"\\\\"          { codeBody.append("\\\\"); }
	}
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
    "{"                 { codeBody.append('{'); nestingDepth++; }
    "}"                 { codeBody.append('}'); nestingDepth--; }
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
    "["                 { codeBody.append('['); nestingDepth++; }
    "]"                 { codeBody.append(']'); nestingDepth--; }
    "new"               {
        codeBody.append("new");
        if (nestingDepth==assignmentDepth) {
            newMode=true;
        } 
    }
    "<"                 {
        codeBody.append('<');
        if (newMode) { 
            nestingDepth++; 
        } 
    }
    ">"                 {
        codeBody.append('>');
        if (newMode) {
            nestingDepth--;
        	if (nestingDepth==assignmentDepth) { 
                newMode=false;
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
