package com.thoughtworks.qdox.parser;

import junit.framework.TestCase;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;

public class ParserTest extends TestCase {

    private MockLexer lexer;
    private MockBuilder builder;

    public ParserTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        lexer = new MockLexer();
        builder = new MockBuilder();
    }

    public void testPackageWithOneWord() throws Exception {

        // setup values
        setupLex(Parser.PACKAGE);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.SEMI);
        setupLex(0);

        // expectations
        builder.addExpectedAddPackageValues("mypackage");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testPackageWithMultipleWords() throws Exception {

        // setup values
        setupLex(Parser.PACKAGE);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "thingy");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.SEMI);
        setupLex(0);

        // expectations
        builder.addExpectedAddPackageValues("com.blah.thingy.x");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testImportWithOneWord() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.SEMI);
        setupLex(0);

        // expectations
        builder.addExpectedAddImportValues("mypackage");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testImportWithMultipleWords() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "thingy");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.SEMI);
        setupLex(0);

        // expectations
        builder.addExpectedAddImportValues("com.blah.thingy.x");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testImportWithOneWordAndStar() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.DOT);
        setupLex(Parser.STAR);
        setupLex(Parser.SEMI);
        setupLex(0);

        // expectations
        builder.addExpectedAddImportValues("mypackage.*");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testImportWithMultipleWordsAndStar() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "thingy");
        setupLex(Parser.DOT);
        setupLex(Parser.STAR);
        setupLex(Parser.SEMI);
        setupLex(0);

        // expectations
        builder.addExpectedAddImportValues("com.blah.thingy.*");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testOneLineJavaDoc() throws Exception {

        // setup values
        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "This");
        setupLex(Parser.JAVADOCTOKEN, "is");
        setupLex(Parser.JAVADOCTOKEN, "great!");
        setupLex(Parser.JAVADOCEND);
        setupLex(0);

        // expectations
        builder.addExpectedAddJavaDocValues("This is great!");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testOneJavaDocTag() throws Exception {

        // setup values
        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTAG, "@This");
        setupLex(Parser.JAVADOCTOKEN, "is");
        setupLex(Parser.JAVADOCTOKEN, "great!");
        setupLex(Parser.JAVADOCEND);
        setupLex(0);

        // expectations
        builder.addExpectedAddJavaDocTagValues("This", "is great!");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testOneJavaDocTagWithNoValue() throws Exception {

        // setup values
        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTAG, "@eatme");
        setupLex(Parser.JAVADOCEND);
        setupLex(0);

        // expectations
        builder.addExpectedAddJavaDocTagValues("eatme", "");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testOneMultiLineJavaDocTag() throws Exception {

        // setup values
        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTAG, "@This");
        setupLex(Parser.JAVADOCTOKEN, "is");
        setupLex(Parser.JAVADOCTOKEN, "great!");
        setupLex(Parser.JAVADOCTOKEN, "Mmmkay.");
        setupLex(Parser.JAVADOCEND);
        setupLex(0);

        // expectations
        builder.addExpectedAddJavaDocValues("");
        builder.addExpectedAddJavaDocTagValues("This", "is great! Mmmkay.");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testMultipleJavaDocTags() throws Exception {

        // setup values
        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTAG, "@This");
        setupLex(Parser.JAVADOCTOKEN, "is");
        setupLex(Parser.JAVADOCTOKEN, "great!");
        setupLex(Parser.JAVADOCTAG, "@mock");
        setupLex(Parser.JAVADOCTOKEN, "generate");
        setupLex(Parser.JAVADOCEND);
        setupLex(0);

        // expectations
        builder.addExpectedAddJavaDocValues("");
        builder.addExpectedAddJavaDocTagValues("This", "is great!");
        builder.addExpectedAddJavaDocTagValues("mock", "generate");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testJavaDocTextAndMultipleJavaDocTags() throws Exception {

        // setup values
        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "Welcome!");
        setupLex(Parser.JAVADOCTOKEN, "Here");
        setupLex(Parser.JAVADOCTOKEN, "is");
        setupLex(Parser.JAVADOCTOKEN, "my");
        setupLex(Parser.JAVADOCTOKEN, "class.");
        setupLex(Parser.JAVADOCTAG, "@This");
        setupLex(Parser.JAVADOCTOKEN, "is");
        setupLex(Parser.JAVADOCTOKEN, "great!");
        setupLex(Parser.JAVADOCTAG, "@mock");
        setupLex(Parser.JAVADOCTOKEN, "generate");
        setupLex(Parser.JAVADOCEND);
        setupLex(0);

        // expectations
        builder.addExpectedAddJavaDocValues("Welcome! Here is my class.");
        builder.addExpectedAddJavaDocTagValues("This", "is great!");
        builder.addExpectedAddJavaDocTagValues("mock", "generate");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testJavaDocEmpty() throws Exception {

        // setup values
        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCEND);
        setupLex(0);

        // expectations
        builder.addExpectedAddJavaDocValues("");
        builder.setExpectedAddJavaDocTagCalls(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testJavaDocOnlyContainsNewLines() throws Exception {

        // setup values
        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCEND);
        setupLex(0);

        // expectations
        builder.addExpectedAddJavaDocValues("");
        builder.setExpectedAddJavaDocTagCalls(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyVanillaClass() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyClass";
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyVanillaInterface() throws Exception {

        // setup values
        setupLex(Parser.INTERFACE);
        setupLex(Parser.IDENTIFIER, "MyInterface");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyInterface";
        cls.isInterface = true;
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyClassExtendsAnotherClass() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MySubClass");
        setupLex(Parser.EXTENDS);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "MyBaseClass");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MySubClass";
        cls.extendz.add("com.blah.MyBaseClass");
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyInterfaceExtendsMultipleInterfaces() throws Exception {

        // setup values
        setupLex(Parser.INTERFACE);
        setupLex(Parser.IDENTIFIER, "MyInterface");
        setupLex(Parser.EXTENDS);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "AnotherInterface");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "Serializable");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyInterface";
        cls.isInterface = true;
        cls.extendz.add("com.blah.AnotherInterface");
        cls.extendz.add("Serializable");
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyClassImplementsOneInterface() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.IMPLEMENTS);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "AnInterface");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyClass";
        cls.implementz.add("com.blah.AnInterface");
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyClassImplementsMultipleInterfaces() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.IMPLEMENTS);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "AnInterface");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "java");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "io");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "Serializable");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "Eatable");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyClass";
        cls.implementz.add("com.blah.AnInterface");
        cls.implementz.add("java.io.Serializable");
        cls.implementz.add("Eatable");
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyClassExtendsOneClassAndImplementsOneInterface() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.EXTENDS);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "BaseClass");
        setupLex(Parser.IMPLEMENTS);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "AnInterface");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyClass";
        cls.extendz.add("mypackage.BaseClass");
        cls.implementz.add("com.blah.AnInterface");
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyClassExtendsOneClassAndImplementsMultipleInterface() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.EXTENDS);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "BaseClass");
        setupLex(Parser.IMPLEMENTS);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "AnInterface");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "java");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "io");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "Serializable");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "Eatable");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyClass";
        cls.extendz.add("mypackage.BaseClass");
        cls.implementz.add("com.blah.AnInterface");
        cls.implementz.add("java.io.Serializable");
        cls.implementz.add("Eatable");
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyClassWithPublicFinalModifiers() throws Exception {

        // setup values
        setupLex(Parser.PUBLIC);
        setupLex(Parser.FINAL);
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyClass";
        cls.modifiers.add("public");
        cls.modifiers.add("final");
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testEmptyClassWithAllModifiers() throws Exception {

        // setup values
        setupLex(Parser.PUBLIC);
        setupLex(Parser.PROTECTED);
        setupLex(Parser.PRIVATE);
        setupLex(Parser.FINAL);
        setupLex(Parser.ABSTRACT);
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyClass";
        cls.modifiers.add("public");
        cls.modifiers.add("protected");
        cls.modifiers.add("private");
        cls.modifiers.add("final");
        cls.modifiers.add("abstract");
        builder.addExpectedBeginClassValues(cls);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testMultipleClassesInSingleFile() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "Class1");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);

        setupLex(Parser.PUBLIC);
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "Class2");
        setupLex(Parser.EXTENDS);
        setupLex(Parser.IDENTIFIER, "SubClass");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);

        setupLex(Parser.INTERFACE);
        setupLex(Parser.IDENTIFIER, "Intf1");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        ClassDef cls1 = new ClassDef();
        cls1.name = "Class1";
        cls1.isInterface = false;
        builder.addExpectedBeginClassValues(cls1);

        ClassDef cls2 = new ClassDef();
        cls2.name = "Class2";
        cls2.isInterface = false;
        cls2.modifiers.add("public");
        cls2.extendz.add("SubClass");
        builder.addExpectedBeginClassValues(cls2);

        ClassDef cls3 = new ClassDef();
        cls3.name = "Intf1";
        cls3.isInterface = true;
        builder.addExpectedBeginClassValues(cls3);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testJavaDocAppearingAllOverThePlace() throws Exception {

        // setup values
        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc1");
        setupLex(Parser.JAVADOCEND);

        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc2");
        setupLex(Parser.JAVADOCEND);

        setupLex(Parser.PACKAGE);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.SEMI);

        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc3");
        setupLex(Parser.JAVADOCEND);

        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc4");
        setupLex(Parser.JAVADOCEND);

        setupLex(Parser.IMPORT);
        setupLex(Parser.IDENTIFIER, "anotherpackage");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "Something");
        setupLex(Parser.SEMI);

        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc5");
        setupLex(Parser.JAVADOCEND);

        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc6");
        setupLex(Parser.JAVADOCEND);

        setupLex(Parser.IMPORT);
        setupLex(Parser.IDENTIFIER, "elsewhere");
        setupLex(Parser.DOT);
        setupLex(Parser.STAR);
        setupLex(Parser.SEMI);

        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc7");
        setupLex(Parser.JAVADOCEND);

        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc8");
        setupLex(Parser.JAVADOCEND);

        setupLex(Parser.PUBLIC);
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);

        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc9");
        setupLex(Parser.JAVADOCEND);

        setupLex(Parser.JAVADOCSTART);
        setupLex(Parser.JAVADOCTOKEN, "javadoc10");
        setupLex(Parser.JAVADOCEND);

        setupLex(0);

        // expectations
        ClassDef cls = new ClassDef();
        cls.name = "MyClass";
        cls.modifiers.add("public");
        builder.addExpectedBeginClassValues(cls);
        builder.addExpectedAddJavaDocValues("javadoc1");
        builder.addExpectedAddJavaDocValues("javadoc2");
        builder.addExpectedAddJavaDocValues("javadoc3");
        builder.addExpectedAddJavaDocValues("javadoc4");
        builder.addExpectedAddJavaDocValues("javadoc5");
        builder.addExpectedAddJavaDocValues("javadoc6");
        builder.addExpectedAddJavaDocValues("javadoc7");
        builder.addExpectedAddJavaDocValues("javadoc8");
        builder.addExpectedAddJavaDocValues("javadoc9");
        builder.addExpectedAddJavaDocValues("javadoc10");

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();

    }

    public void testSimpleVoidMethod() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testSimpleVoidMethodWithNoCode() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testSimpleMethodReturningSomething() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "Something");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "Something";
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testSimpleMethodReturningSomethingFullyQualified() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "Something");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "com.blah.Something";
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testSimpleMethodWithAllModifiers() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.PUBLIC);
        setupLex(Parser.PROTECTED);
        setupLex(Parser.PRIVATE);
        setupLex(Parser.ABSTRACT);
        setupLex(Parser.STATIC);
        setupLex(Parser.FINAL);
        setupLex(Parser.NATIVE);
        setupLex(Parser.SYNCHRONIZED);
        setupLex(Parser.VOLATILE);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "Something");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "com.blah.Something";
        mth.modifiers.add("public");
        mth.modifiers.add("protected");
        mth.modifiers.add("private");
        mth.modifiers.add("abstract");
        mth.modifiers.add("static");
        mth.modifiers.add("final");
        mth.modifiers.add("native");
        mth.modifiers.add("synchronized");
        mth.modifiers.add("volatile");
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodWithOneArg() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "numberOfTimes");
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = "int";
        mth.params.add(p1);
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodWithOneFullyQualifiedArg() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.IDENTIFIER, "java");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "lang");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "String");
        setupLex(Parser.IDENTIFIER, "numberOfTimes");
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = "java.lang.String";
        mth.params.add(p1);
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodWithTwoArgs() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "numberOfTimes");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "String");
        setupLex(Parser.IDENTIFIER, "name");
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = "int";
        mth.params.add(p1);
        FieldDef p2 = new FieldDef();
        p2.name = "name";
        p2.type = "String";
        mth.params.add(p2);
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodWithThreeArgs() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "numberOfTimes");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "String");
        setupLex(Parser.IDENTIFIER, "name");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "boolean");
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = "int";
        mth.params.add(p1);
        FieldDef p2 = new FieldDef();
        p2.name = "name";
        p2.type = "String";
        mth.params.add(p2);
        FieldDef p3 = new FieldDef();
        p3.name = "x";
        p3.type = "boolean";
        mth.params.add(p3);
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodWithOneArgThatHasModifier() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.FINAL);
        setupLex(Parser.VOLATILE);
        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "numberOfTimes");
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = "int";
        p1.modifiers.add("final");
        p1.modifiers.add("volatile");
        mth.params.add(p1);
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodThrowingOneException() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.THROWS);
        setupLex(Parser.IDENTIFIER, "IOException");
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        mth.exceptions.add("IOException");
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodThrowingTwoExceptions() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.THROWS);
        setupLex(Parser.IDENTIFIER, "IOException");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "MyException");
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        mth.exceptions.add("IOException");
        mth.exceptions.add("MyException");
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodThrowingThreeExceptions() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.THROWS);
        setupLex(Parser.IDENTIFIER, "IOException");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "MyException");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "AnotherException");
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        mth.exceptions.add("IOException");
        mth.exceptions.add("MyException");
        mth.exceptions.add("AnotherException");
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodThrowingOneFullyQualifiedException() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.THROWS);
        setupLex(Parser.IDENTIFIER, "java");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "io");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "IOException");
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        mth.exceptions.add("java.io.IOException");
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodThrowingTwoFullyQualifiedException() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.THROWS);
        setupLex(Parser.IDENTIFIER, "java");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "io");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "IOException");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "java");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "lang");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "RuntimeException");
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returns = "void";
        mth.exceptions.add("java.io.IOException");
        mth.exceptions.add("java.lang.RuntimeException");
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testDefaultConstructor() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;
        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testPublicConstructorWithParam() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.PUBLIC);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;
        mth.modifiers.add("public");
        FieldDef p1 = new FieldDef();
        p1.name = "count";
        p1.type = "int";
        mth.params.add(p1);

        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testConstructorWithMultipleParams() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.PUBLIC);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "java");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "lang");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "String");
        setupLex(Parser.IDENTIFIER, "thingy");
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;
        mth.modifiers.add("public");
        FieldDef p1 = new FieldDef();
        p1.name = "count";
        p1.type = "int";
        mth.params.add(p1);
        FieldDef p2 = new FieldDef();
        p2.name = "thingy";
        p2.type = "java.lang.String";
        mth.params.add(p2);

        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testConstructorWithException() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.THROWS);
        setupLex(Parser.IDENTIFIER, "SomeException");
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;
        mth.exceptions.add("SomeException");

        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testConstructorWithMultipleException() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.THROWS);
        setupLex(Parser.IDENTIFIER, "java");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "io");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "IOException");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "SomeException");
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;
        mth.exceptions.add("SomeException");
        mth.exceptions.add("java.io.IOException");

        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testField() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = "int";

        builder.addExpectedAddFieldValues(fld);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testFieldFullyQualified() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "java");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "lang");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "String");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = "java.lang.String";

        builder.addExpectedAddFieldValues(fld);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testFieldWithModifiers() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.PUBLIC);
        setupLex(Parser.PROTECTED);
        setupLex(Parser.PRIVATE);
        setupLex(Parser.STATIC);
        setupLex(Parser.FINAL);
        setupLex(Parser.TRANSIENT);
        setupLex(Parser.STRICTFP);
        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = "int";
        fld.modifiers.add("public");
        fld.modifiers.add("protected");
        fld.modifiers.add("private");
        fld.modifiers.add("static");
        fld.modifiers.add("final");
        fld.modifiers.add("transient");
        fld.modifiers.add("strictfp");

        builder.addExpectedAddFieldValues(fld);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testFieldWithAssignment() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "String");
        setupLex(Parser.IDENTIFIER, "thing");
        setupLex(Parser.ASSIGNMENT);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testFieldWithMultipleDefinitionsOnOneLine() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "String");
        setupLex(Parser.IDENTIFIER, "thing");
        setupLex(Parser.COMMA);
        setupLex(Parser.IDENTIFIER, "another");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld1 = new FieldDef();
        fld1.name = "thing";
        fld1.type = "String";
        builder.addExpectedAddFieldValues(fld1);

        // TODO: expect both fields.

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testStaticBlock() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.STATIC);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testInnerClass() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "InnerCls");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);

        setupLex(Parser.BRACECLOSE);

        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "AnotherClass");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);

        setupLex(0);

        // expectations
        builder.setExpectedBeginClassCalls(3);
        ClassDef cls = new ClassDef();
        cls.name = "MyClass";
        builder.addExpectedBeginClassValues(cls);
        ClassDef cls2 = new ClassDef();
        cls2.name = "InnerCls";
        builder.addExpectedBeginClassValues(cls2);
        ClassDef cls3 = new ClassDef();
        cls3.name = "AnotherClass";
        builder.addExpectedBeginClassValues(cls3);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testRogueSemiColon() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testFieldNotArray() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = "int";
        fld.dimensions = 0;

        builder.addExpectedAddFieldValues(fld);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testFieldArrayOnType() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = "int";
        fld.dimensions = 1;

        builder.addExpectedAddFieldValues(fld);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testField2dArrayOnType() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = "int";
        fld.dimensions = 2;

        builder.addExpectedAddFieldValues(fld);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testFieldArrayOnName() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = "int";
        fld.dimensions = 1;

        builder.addExpectedAddFieldValues(fld);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testField3dArrayOnTypeAndName() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = "int";
        fld.dimensions = 3;

        builder.addExpectedAddFieldValues(fld);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testFieldArrayThenAnotherNonArray() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count2");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = "int";
        fld.dimensions = 1;

        FieldDef fld2 = new FieldDef();
        fld2.name = "count2";
        fld2.type = "int";
        fld2.dimensions = 0;

        builder.addExpectedAddFieldValues(fld);
        builder.addExpectedAddFieldValues(fld2);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodNoArray() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returns = "int";
        mth.dimensions = 0;

        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodArray() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returns = "int";
        mth.dimensions = 1;

        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodReturningArrayWithParamNoArray() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.PARENOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "p1");

        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returns = "int";
        mth.dimensions = 1;
        FieldDef p1 = new FieldDef();
        p1.name = "p1";
        p1.type = "int";
        p1.dimensions = 0;
        mth.params.add(p1);

        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodReturningNoArrayWithParamArray() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.PARENOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.IDENTIFIER, "p1");

        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returns = "int";
        mth.dimensions = 0;
        FieldDef p1 = new FieldDef();
        p1.name = "p1";
        p1.type = "int";
        p1.dimensions = 1;
        mth.params.add(p1);

        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }

    public void testMethodReturningArrayWithParam2dArray() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.PARENOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.IDENTIFIER, "p1");

        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // expectations
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returns = "int";
        mth.dimensions = 1;
        FieldDef p1 = new FieldDef();
        p1.name = "p1";
        p1.type = "int";
        p1.dimensions = 2;
        mth.params.add(p1);

        builder.addExpectedAddMethodValues(mth);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        builder.verify();
    }


    private void setupLex(int token, String value) {
        lexer.setupLex(token);
        lexer.setupText(value);
    }

    private void setupLex(int token) {
        setupLex(token, null);
    }

}
