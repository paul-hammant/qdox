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

%}

Eol                     = \r|\n|\r\n
WhiteSpace              = {Eol} | [ \t\f]
CommentChar             = ( [^ \t\r\n*] | "*"+ [^ \t\r\n/*] )

%state JAVADOC CODEBLOCK ASSIGNMENT STRING CHAR SINGLELINECOMMENT MULTILINECOMMENT

%%

<YYINITIAL> {
    ";"                 { return Parser.SEMI; }
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
    "("                 { nestingDepth++; return Parser.PARENOPEN; }
    ")"                 { nestingDepth--; return Parser.PARENCLOSE; }
    "<"                 { return Parser.LESSTHAN; }
    ">"                 { return Parser.GREATERTHAN; }
    "&"                 { return Parser.AMPERSAND; }
    "?"                 { return Parser.QUERY; }
    "@"                 { return Parser.AT; }

    "class"             {
        classDepth++;
        return Parser.CLASS; 
    }
    "interface"         { 
        classDepth++;
        return Parser.INTERFACE; 
    }

    "{"                 {
        nestingDepth++;
        if (nestingDepth == classDepth + 1) {
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
        pushState(ASSIGNMENT);
    }

    "default"           { 
        assignmentDepth = nestingDepth; 
        pushState(ASSIGNMENT);
    }

    [:jletter:] [:jletterdigit:]* { 
        return Parser.IDENTIFIER; 
    }

}

<JAVADOC> {
    "*"+ "/"            { popState(); return Parser.JAVADOCEND; }
    ^ [ \t]* "*"+ / [^/*] { /* ignore */ }
    {Eol}               { javaDocNewLine = true; }
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
    "{"                 { nestingDepth++; }
    "}"                 {
        nestingDepth--;
        if (nestingDepth == classDepth) {
            popState();
            return Parser.CODEBLOCK;
        }
    }
}

<ASSIGNMENT> {
    ";"                 { 
        if (nestingDepth == assignmentDepth) { 
            popState(); 
            return Parser.SEMI; 
        } 
    }
    ","                 {
        if (nestingDepth == assignmentDepth) { 
            popState(); 
            return Parser.COMMA; 
        } 
    }
    "{"                 { nestingDepth++; }
    "}"                 { nestingDepth--; }
    "("                 { nestingDepth++; }
    ")"                 { 
        nestingDepth--; 
        if (nestingDepth < assignmentDepth) { 
            popState(); 
            return Parser.PARENCLOSE; 
        }
    }
    "["                 { nestingDepth++; }
    "]"                 { nestingDepth--; }
}

<ASSIGNMENT, CODEBLOCK, YYINITIAL> {
    "\""                { pushState(STRING); }
    \'                  { pushState(CHAR); }
    "//"                { pushState(SINGLELINECOMMENT); }
    "/*"                { pushState(MULTILINECOMMENT); }
    "/**/"              { }
}

<STRING> {
    "\""                { popState(); }
    "\\\""              { }
    "\\\\"              { }
}

<CHAR> {
    \'                  { popState(); }
    "\\'"               { }
    "\\\\"              { }
}

<SINGLELINECOMMENT> {
    {Eol}               { popState(); }
}

<MULTILINECOMMENT> {
    "*/"                { popState(); }
}

.|\r|\n|\r\n            { }
