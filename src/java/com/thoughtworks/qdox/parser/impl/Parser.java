//### This file created by BYACC 1.8(/Java extension  1.1)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package com.thoughtworks.qdox.parser.impl;



//#line 2 "src\grammar\parser.y"
import com.thoughtworks.qdox.parser.*;
import com.thoughtworks.qdox.parser.structs.*;
import java.io.IOException;
//#line 19 "Parser.java"




/**
 * Encapsulates yacc() parser functionality in a Java
 *        class for quick code development
 */
public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[],stateptr;           //state stack
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
void state_push(int state)
{
  if (stateptr>=YYSTACKSIZE)         //overflowed?
    return;
  statestk[++stateptr]=state;
  if (stateptr>statemax)
    {
    statemax=state;
    stateptrmax=stateptr;
    }
}
int state_pop()
{
  if (stateptr<0)                    //underflowed?
    return -1;
  return statestk[stateptr--];
}
void state_drop(int cnt)
{
int ptr;
  ptr=stateptr-cnt;
  if (ptr<0)
    return;
  stateptr = ptr;
}
int state_peek(int relative)
{
int ptr;
  ptr=stateptr-relative;
  if (ptr<0)
    return -1;
  return statestk[ptr];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
boolean init_stacks()
{
  statestk = new int[YYSTACKSIZE];
  stateptr = -1;
  statemax = -1;
  stateptrmax = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:Value
String   yytext;//user variable to return contextual strings
Value yyval; //used to return semantic vals from action routines
Value yylval;//the 'lval' (result) I got from yylex()
Value valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new Value[YYSTACKSIZE];
  yyval=new Value();
  yylval=new Value();
  valptr=-1;
}
void val_push(Value val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
Value val_pop()
{
  if (valptr<0)
    return null;
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
Value val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return null;
  return valstk[ptr];
}
//#### end semantic value section ####
public final static short SEMI=257;
public final static short DOT=258;
public final static short COMMA=259;
public final static short STAR=260;
public final static short EQUALS=261;
public final static short PACKAGE=262;
public final static short IMPORT=263;
public final static short PUBLIC=264;
public final static short PROTECTED=265;
public final static short PRIVATE=266;
public final static short STATIC=267;
public final static short FINAL=268;
public final static short ABSTRACT=269;
public final static short NATIVE=270;
public final static short STRICTFP=271;
public final static short SYNCHRONIZED=272;
public final static short TRANSIENT=273;
public final static short VOLATILE=274;
public final static short CLASS=275;
public final static short INTERFACE=276;
public final static short THROWS=277;
public final static short EXTENDS=278;
public final static short IMPLEMENTS=279;
public final static short PARENOPEN=280;
public final static short PARENCLOSE=281;
public final static short SQUAREOPEN=282;
public final static short SQUARECLOSE=283;
public final static short BRACKETOPEN=284;
public final static short BRACKETCLOSE=285;
public final static short JAVADOCSTART=286;
public final static short JAVADOCEND=287;
public final static short JAVADOCNEWLINE=288;
public final static short JAVADOCTAGMARK=289;
public final static short CODEBLOCK=290;
public final static short STRING=291;
public final static short ASSIGNMENT=292;
public final static short IDENTIFIER=293;
public final static short JAVADOCTOKEN=294;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    7,    7,    7,    7,    1,    1,    1,    2,
    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,
    8,    9,   10,   12,   14,   14,   15,   15,   13,   13,
   16,   11,   17,   20,   20,   21,   21,   23,   23,   22,
   22,   24,   24,   18,   18,   25,   25,   25,   25,   25,
   25,   25,   19,   19,   27,   27,   27,   26,   26,    3,
    4,   29,   29,   28,   31,   31,   32,   32,   30,   30,
   34,   34,   33,   35,   35,    5,    6,   36,   36,
};
final static short yylen[] = {                            2,
    0,    2,    1,    1,    1,    1,    1,    3,    3,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    3,    3,    4,    1,    0,    2,    1,    1,    0,    2,
    3,    4,    5,    1,    1,    0,    2,    1,    3,    0,
    2,    1,    3,    0,    2,    1,    5,    5,    4,    2,
    6,    1,    0,    2,    1,    1,    1,    0,    3,    2,
    2,    0,    3,    4,    0,    2,    1,    3,    0,    2,
    0,    3,    3,    0,    2,    2,    2,    0,    3,
};
final static short yydefred[] = {                         1,
    0,    0,    0,   25,    2,    3,    4,    5,    6,    0,
    0,    7,    0,    0,   29,    0,   44,   10,   11,   12,
   13,   14,   15,   16,   20,   17,   19,   18,   34,   35,
   54,    0,   21,    0,   22,    0,   27,   28,   26,    0,
    0,    9,    8,   23,    0,   30,   52,   32,   46,    0,
   45,    0,    0,   25,   50,    0,    0,    0,    0,    0,
    0,    0,    0,   33,    0,    0,    0,    0,    0,   62,
    0,    0,    0,    0,    0,    0,    0,   71,    0,   55,
   56,   57,   49,    0,    0,    0,    0,    0,   63,    0,
    0,    0,   75,    0,    0,   47,   48,    0,    0,    0,
   64,   74,    0,   78,   73,    0,   51,    0,    0,   72,
    0,    0,    0,   79,    0,
};
final static short yydgoto[] = {                          1,
   13,   31,   58,   59,  105,   94,    5,    6,    7,    8,
    9,   15,   36,   16,   39,   46,   10,   40,   11,   32,
   53,   64,   62,   75,   51,   84,   83,   69,   66,   77,
  101,  109,   78,   91,   79,  103,
};
final static short yysindex[] = {                         0,
 -195, -228, -228,    0,    0,    0,    0,    0,    0, -184,
  -69,    0, -234, -171,    0, -262,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, -220,    0, -238,    0, -215,    0,    0,    0, -191,
 -189,    0,    0,    0, -192,    0,    0,    0,    0, -153,
    0, -228, -176,    0,    0,    0, -154, -179, -187, -186,
 -154, -151, -228,    0, -262, -173, -173,    0, -240,    0,
 -179, -189, -228, -154, -121, -144, -143,    0,  -80,    0,
    0,    0,    0, -255, -240, -176, -154, -228,    0, -136,
 -116, -154,    0, -149, -228,    0,    0, -145, -154, -228,
    0,    0, -135,    0,    0, -154,    0, -154, -113,    0,
 -134, -135, -228,    0, -154,
};
final static short yyrindex[] = {                         0,
  -50,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, -208,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, -140,
 -181,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0, -132,    0,    0, -253, -268,    0,    0,    0,
 -221, -260,    0,    0, -207, -256, -142, -110,    0,    0,
 -251, -263,    0, -197, -219,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, -138, -202,    0,    0, -239,
 -120, -218,    0,    0,    0,    0,    0,    0, -196,    0,
    0,    0, -127,    0,    0, -248,    0, -247, -236,    0,
    0, -252,    0,    0, -244,
};
final static short yygindex[] = {                         0,
   -3,   88,  109,    0,    0,    0,    0,    0,    0,  129,
    0,    0,    0,  116,    0,    0,    0,    0,  131,  122,
  101,   90,    0,    0,    0,    0,   16,  103,  120,    0,
    0,    0,   76,    0,    0,   75,
};
final static int YYTABLESIZE=226;
final static short yytable[] = {                         14,
   60,   80,   60,   95,    7,   58,   76,   58,   59,   67,
   59,   67,   68,   62,   68,   36,   80,   65,   37,   37,
   66,   42,   33,   34,   62,   37,   36,   60,    7,   37,
   62,   38,   76,   60,   81,   60,   82,   38,   58,    7,
   58,   59,   67,   59,   67,   68,   57,   68,   61,   81,
   65,   82,   65,   66,   43,   66,   39,   38,   38,   74,
   41,   42,   43,   78,   12,   47,    2,    3,   38,   87,
   41,   44,   41,   45,   78,   92,   39,   39,   24,   31,
   24,   31,   42,   43,   99,   35,   34,   39,   52,   48,
    4,  106,   42,   43,    4,   17,  108,   36,   36,   96,
   97,   54,   63,   34,   68,   70,   72,   73,   76,  115,
   18,   19,   20,   21,   22,   23,   24,   25,   26,   27,
   28,   29,   30,   53,   53,   53,   53,   53,   53,   53,
   53,   53,   53,   53,   53,   53,   55,   88,   89,   56,
  100,   90,  102,  104,  107,  113,  111,   40,  114,   53,
   61,   40,   53,   74,   74,   74,   74,   74,   74,   74,
   74,   74,   74,   74,   70,   77,   93,   71,   49,   65,
   50,   60,   86,   85,   69,   98,   67,  110,  112,    0,
    0,    0,   74,   18,   19,   20,   21,   22,   23,   24,
   25,   26,   27,   28,   18,   19,   20,   21,   22,   23,
   24,   25,   26,   27,   28,   29,   30,    0,    0,    0,
    0,    0,   12,   53,   53,   53,   53,   53,   53,   53,
   53,   53,   53,   53,   53,   53,
};
final static short yycheck[] = {                          3,
  257,  257,  259,  259,  258,  257,  259,  259,  257,  257,
  259,  259,  257,  282,  259,  279,  257,  257,  279,  280,
  257,  260,  257,  258,  293,  288,  290,  284,  282,  290,
  284,  294,  285,  290,  290,  292,  292,  259,  290,  293,
  292,  290,  290,  292,  292,  290,   50,  292,   52,  290,
  290,  292,  292,  290,  293,  292,  259,  279,  280,   63,
  280,  259,  259,  282,  293,  257,  262,  263,  290,   73,
  290,  287,  293,  289,  293,   79,  279,  280,  287,  287,
  289,  289,  280,  280,   88,  257,  258,  290,  278,  281,
  286,   95,  290,  290,  286,  280,  100,  279,  280,   84,
   85,  294,  279,  258,  284,  293,  293,  259,  282,  113,
  264,  265,  266,  267,  268,  269,  270,  271,  272,  273,
  274,  275,  276,  264,  265,  266,  267,  268,  269,  270,
  271,  272,  273,  274,  275,  276,  290,  259,  283,  293,
  277,  285,  259,  293,  290,  259,  282,  280,  283,  290,
  293,  290,  293,  264,  265,  266,  267,  268,  269,  270,
  271,  272,  273,  274,  285,  293,   79,   59,   40,   54,
   40,   50,   72,   71,  285,   86,   57,  102,  104,   -1,
   -1,   -1,  293,  264,  265,  266,  267,  268,  269,  270,
  271,  272,  273,  274,  264,  265,  266,  267,  268,  269,
  270,  271,  272,  273,  274,  275,  276,   -1,   -1,   -1,
   -1,   -1,  293,  264,  265,  266,  267,  268,  269,  270,
  271,  272,  273,  274,  275,  276,
};
final static short YYFINAL=1;
final static short YYMAXTOKEN=294;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"SEMI","DOT","COMMA","STAR","EQUALS","PACKAGE","IMPORT","PUBLIC",
"PROTECTED","PRIVATE","STATIC","FINAL","ABSTRACT","NATIVE","STRICTFP",
"SYNCHRONIZED","TRANSIENT","VOLATILE","CLASS","INTERFACE","THROWS","EXTENDS",
"IMPLEMENTS","PARENOPEN","PARENCLOSE","SQUAREOPEN","SQUARECLOSE","BRACKETOPEN",
"BRACKETCLOSE","JAVADOCSTART","JAVADOCEND","JAVADOCNEWLINE","JAVADOCTAGMARK",
"CODEBLOCK","STRING","ASSIGNMENT","IDENTIFIER","JAVADOCTOKEN",
};
final static String yyrule[] = {
"$accept : file",
"file :",
"file : file filepart",
"filepart : package",
"filepart : import",
"filepart : javadoc",
"filepart : class",
"fullidentifier : IDENTIFIER",
"fullidentifier : fullidentifier DOT IDENTIFIER",
"fullidentifier : fullidentifier DOT STAR",
"modifier : PUBLIC",
"modifier : PROTECTED",
"modifier : PRIVATE",
"modifier : STATIC",
"modifier : FINAL",
"modifier : ABSTRACT",
"modifier : NATIVE",
"modifier : SYNCHRONIZED",
"modifier : VOLATILE",
"modifier : TRANSIENT",
"modifier : STRICTFP",
"package : PACKAGE fullidentifier SEMI",
"import : IMPORT fullidentifier SEMI",
"javadoc : JAVADOCSTART javadocdescription javadoctags JAVADOCEND",
"javadocdescription : javadoctokens",
"javadoctokens :",
"javadoctokens : javadoctokens javadoctoken",
"javadoctoken : JAVADOCNEWLINE",
"javadoctoken : JAVADOCTOKEN",
"javadoctags :",
"javadoctags : javadoctags javadoctag",
"javadoctag : JAVADOCTAGMARK JAVADOCTOKEN javadoctokens",
"class : classdefinition PARENOPEN members PARENCLOSE",
"classdefinition : modifiers classorinterface IDENTIFIER extends implements",
"classorinterface : CLASS",
"classorinterface : INTERFACE",
"extends :",
"extends : EXTENDS extendslist",
"extendslist : fullidentifier",
"extendslist : extendslist COMMA fullidentifier",
"implements :",
"implements : IMPLEMENTS implementslist",
"implementslist : fullidentifier",
"implementslist : implementslist COMMA fullidentifier",
"members :",
"members : members member",
"member : javadoc",
"member : modifiers arrayfullidentifier arrayidentifier extraidentifiers memberend",
"member : modifiers arrayfullidentifier arrayidentifier method memberend",
"member : modifiers arrayidentifier method memberend",
"member : modifiers CODEBLOCK",
"member : modifiers classorinterface IDENTIFIER extends implements CODEBLOCK",
"member : SEMI",
"modifiers :",
"modifiers : modifiers modifier",
"memberend : SEMI",
"memberend : CODEBLOCK",
"memberend : ASSIGNMENT",
"extraidentifiers :",
"extraidentifiers : extraidentifiers COMMA fullidentifier",
"arrayidentifier : IDENTIFIER arrayparts",
"arrayfullidentifier : fullidentifier arrayparts",
"arrayparts :",
"arrayparts : arrayparts SQUAREOPEN SQUARECLOSE",
"method : BRACKETOPEN params BRACKETCLOSE exceptions",
"exceptions :",
"exceptions : THROWS exceptionlist",
"exceptionlist : fullidentifier",
"exceptionlist : exceptionlist COMMA fullidentifier",
"params :",
"params : param paramlist",
"paramlist :",
"paramlist : paramlist COMMA param",
"param : parammodifiers paramarrayfullidentifier paramarrayidentifier",
"parammodifiers :",
"parammodifiers : parammodifiers modifier",
"paramarrayidentifier : IDENTIFIER paramarrayparts",
"paramarrayfullidentifier : fullidentifier paramarrayparts",
"paramarrayparts :",
"paramarrayparts : paramarrayparts SQUAREOPEN SQUARECLOSE",
};

//#line 115 "src\grammar\parser.y"

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
//#line 442 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 7:
//#line 32 "src\grammar\parser.y"
{ yyval.sval = val_peek(0).sval; }
break;
case 8:
//#line 33 "src\grammar\parser.y"
{ yyval.sval = val_peek(2).sval + '.' + val_peek(0).sval; }
break;
case 9:
//#line 34 "src\grammar\parser.y"
{ yyval.sval = val_peek(2).sval + ".*"; }
break;
case 10:
//#line 38 "src\grammar\parser.y"
{ yyval.sval = "public"; }
break;
case 11:
//#line 39 "src\grammar\parser.y"
{ yyval.sval = "protected"; }
break;
case 12:
//#line 40 "src\grammar\parser.y"
{ yyval.sval = "private"; }
break;
case 13:
//#line 41 "src\grammar\parser.y"
{ yyval.sval = "static"; }
break;
case 14:
//#line 42 "src\grammar\parser.y"
{ yyval.sval = "final"; }
break;
case 15:
//#line 43 "src\grammar\parser.y"
{ yyval.sval = "abstract"; }
break;
case 16:
//#line 44 "src\grammar\parser.y"
{ yyval.sval = "native"; }
break;
case 17:
//#line 45 "src\grammar\parser.y"
{ yyval.sval = "synchronized"; }
break;
case 18:
//#line 46 "src\grammar\parser.y"
{ yyval.sval = "volatile"; }
break;
case 19:
//#line 47 "src\grammar\parser.y"
{ yyval.sval = "transient"; }
break;
case 20:
//#line 48 "src\grammar\parser.y"
{ yyval.sval = "strictfp"; }
break;
case 21:
//#line 55 "src\grammar\parser.y"
{ builder.addPackage(val_peek(1).sval); }
break;
case 22:
//#line 58 "src\grammar\parser.y"
{ builder.addImport(val_peek(1).sval); }
break;
case 24:
//#line 62 "src\grammar\parser.y"
{ builder.addJavaDoc(buffer()); }
break;
case 28:
//#line 64 "src\grammar\parser.y"
{ textBuffer.append(val_peek(0).sval); textBuffer.append(' '); }
break;
case 31:
//#line 66 "src\grammar\parser.y"
{ builder.addJavaDocTag(val_peek(1).sval, buffer()); }
break;
case 33:
//#line 71 "src\grammar\parser.y"
{ cls.modifiers.addAll(modifiers); modifiers.clear(); cls.name = val_peek(2).sval; builder.addClass(cls); cls = new ClassDef(); }
break;
case 35:
//#line 72 "src\grammar\parser.y"
{ cls.isInterface = true; }
break;
case 38:
//#line 74 "src\grammar\parser.y"
{ cls.extendz.add(val_peek(0).sval); }
break;
case 39:
//#line 75 "src\grammar\parser.y"
{ cls.extendz.add(val_peek(0).sval); }
break;
case 42:
//#line 77 "src\grammar\parser.y"
{ cls.implementz.add(val_peek(0).sval); }
break;
case 43:
//#line 78 "src\grammar\parser.y"
{ cls.implementz.add(val_peek(0).sval); }
break;
case 47:
//#line 83 "src\grammar\parser.y"
{ fld.modifiers.addAll(modifiers); modifiers.clear(); fld.type = val_peek(3).sval; fld.name = val_peek(2).sval; fld.dimensions = getDimensions(); builder.addField(fld); fld = new FieldDef(); }
break;
case 48:
//#line 84 "src\grammar\parser.y"
{ mth.modifiers.addAll(modifiers); modifiers.clear(); mth.returns = val_peek(3).sval; mth.name = val_peek(2).sval; mth.dimensions = getDimensions(); builder.addMethod(mth); mth = new MethodDef(); }
break;
case 49:
//#line 85 "src\grammar\parser.y"
{ mth.modifiers.addAll(modifiers); modifiers.clear(); mth.constructor = true; mth.name = val_peek(2).sval; builder.addMethod(mth); mth = new MethodDef(); }
break;
case 51:
//#line 87 "src\grammar\parser.y"
{ cls = new ClassDef(); modifiers.clear(); }
break;
case 54:
//#line 90 "src\grammar\parser.y"
{ modifiers.add(val_peek(0).sval); }
break;
case 60:
//#line 95 "src\grammar\parser.y"
{ yyval.sval = val_peek(1).sval; }
break;
case 61:
//#line 96 "src\grammar\parser.y"
{ yyval.sval = val_peek(1).sval; }
break;
case 63:
//#line 97 "src\grammar\parser.y"
{ dimensions++; }
break;
case 67:
//#line 101 "src\grammar\parser.y"
{ mth.exceptions.add(val_peek(0).sval); }
break;
case 68:
//#line 101 "src\grammar\parser.y"
{ mth.exceptions.add(val_peek(0).sval); }
break;
case 73:
//#line 106 "src\grammar\parser.y"
{ fld.name = val_peek(0).sval; fld.type = val_peek(1).sval; fld.dimensions = getParamDimensions(); mth.params.add(fld); fld = new FieldDef(); }
break;
case 75:
//#line 107 "src\grammar\parser.y"
{ fld.modifiers.add(val_peek(0).sval); }
break;
case 76:
//#line 109 "src\grammar\parser.y"
{ yyval.sval = val_peek(1).sval; }
break;
case 77:
//#line 110 "src\grammar\parser.y"
{ yyval.sval = val_peek(1).sval; }
break;
case 79:
//#line 111 "src\grammar\parser.y"
{ paramDimensions++; }
break;
//#line 749 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
