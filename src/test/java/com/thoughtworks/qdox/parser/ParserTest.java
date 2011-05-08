package com.thoughtworks.qdox.parser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.mockito.stubbing.answers.ReturnsElementsOf;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.WildcardTypeDef;

public class ParserTest extends TestCase {
    
    private Collection<Integer> lexValues = new LinkedList<Integer>();
    private Collection<String> textValues = new LinkedList<String>();
    
    private JavaLexer lexer;
    private Builder builder;

    public ParserTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        builder = mock(Builder.class);
        lexer = mock(JavaLexer.class);
        lexValues.clear();
        textValues.clear();
    }
    
    @Override
    protected void tearDown()
        throws Exception
    {
        verifyNoMoreInteractions( builder );
    }

    public void testPackageWithOneWord() throws Exception {

        // setup values
        setupLex(Parser.PACKAGE);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.SEMI);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addPackage( new PackageDef("mypackage") );
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addPackage( new PackageDef("com.blah.thingy.x") );
    }

    public void testImportWithOneWord() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.SEMI);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addImport( "mypackage" );
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addImport( "com.blah.thingy.x" );
    }

    public void testImportWithOneWordAndStar() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.DOT);
        setupLex(Parser.STAR);
        setupLex(Parser.SEMI);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addImport( "mypackage.*" );
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addImport( "com.blah.thingy.*" );
    }
    
    public void testImportStaticWithOneWord() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.STATIC);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.SEMI);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addImport( "static mypackage" );
    }

    public void testImportStaticWithMultipleWords() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.STATIC);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "Thingy");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.SEMI);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addImport( "static com.blah.Thingy.x" );
    }

    public void testImportStaticWithOneWordAndStar() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.STATIC);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.DOT);
        setupLex(Parser.STAR);
        setupLex(Parser.SEMI);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addImport( "static MyClass.*" );
    }

    public void testImportStaticWithMultipleWordsAndStar() throws Exception {

        // setup values
        setupLex(Parser.IMPORT);
        setupLex(Parser.STATIC);
        setupLex(Parser.IDENTIFIER, "com");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "blah");
        setupLex(Parser.DOT);
        setupLex(Parser.IDENTIFIER, "Thingy");
        setupLex(Parser.DOT);
        setupLex(Parser.STAR);
        setupLex(Parser.SEMI);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        verify( builder ).addImport( "static com.blah.Thingy.*" );
    }

    public void testEmptyVanillaClass() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
    }

    public void testEmptyVanillaInterface() throws Exception {

        // setup values
        setupLex(Parser.INTERFACE);
        setupLex(Parser.IDENTIFIER, "MyInterface");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyInterface" );
        cls.type = ClassDef.INTERFACE;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
    }

    public void testEmptyVanillaEnum() throws Exception {

        // setup values
        setupLex(Parser.ENUM);
        setupLex(Parser.IDENTIFIER, "MyEnum");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyEnum" );
        cls.type = ClassDef.ENUM;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MySubClass" );
        cls.extendz.add(new TypeDef("com.blah.MyBaseClass"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyInterface" );
        cls.type = ClassDef.INTERFACE;
        cls.extendz.add(new TypeDef("com.blah.AnotherInterface"));
        cls.extendz.add(new TypeDef("Serializable"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        cls.implementz.add(new TypeDef("com.blah.AnInterface"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        cls.implementz.add(new TypeDef("com.blah.AnInterface"));
        cls.implementz.add(new TypeDef("java.io.Serializable"));
        cls.implementz.add(new TypeDef("Eatable"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        cls.extendz.add(new TypeDef("mypackage.BaseClass"));
        cls.implementz.add(new TypeDef("com.blah.AnInterface"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        cls.extendz.add(new TypeDef("mypackage.BaseClass"));
        cls.implementz.add(new TypeDef("com.blah.AnInterface"));
        cls.implementz.add(new TypeDef("java.io.Serializable"));
        cls.implementz.add(new TypeDef("Eatable"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        cls.modifiers.add("public");
        cls.modifiers.add("final");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        cls.modifiers.add("public");
        cls.modifiers.add("protected");
        cls.modifiers.add("private");
        cls.modifiers.add("final");
        cls.modifiers.add("abstract");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls1 = new ClassDef();
        cls1.setName( "Class1" );
        cls1.type = ClassDef.CLASS;

        ClassDef cls2 = new ClassDef();
        cls2.setName( "Class2" );
        cls2.type = ClassDef.CLASS;
        cls2.modifiers.add("public");
        cls2.extendz.add(new TypeDef("SubClass"));

        ClassDef cls3 = new ClassDef();
        cls3.setName( "Intf1" );
        cls3.type = ClassDef.INTERFACE;

        // verify
        verify(builder).beginClass( cls1 );
        verify(builder).beginClass( cls2 );
        verify(builder).beginClass( cls3 );
        verify(builder, times(3)).endClass();
    }

    public void testSemiColonBetweenClass() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "Class1");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(Parser.SEMI);  // ;

        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "Class2");
        setupLex(Parser.BRACEOPEN);
        setupLex(Parser.BRACECLOSE);
        setupLex(Parser.SEMI); // ;
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls1 = new ClassDef();
        cls1.setName( "Class1" );
        cls1.type = ClassDef.CLASS;

        ClassDef cls2 = new ClassDef();
        cls2.setName( "Class2" );
        cls2.type = ClassDef.CLASS;

        // verify
        verify(builder).beginClass( cls1 );
        verify(builder).beginClass( cls2 );
        verify(builder, times(2)).endClass();
    }

/*can't be tested like this anymore*/    
//    public void testJavaDocAppearingAllOverThePlace() throws Exception {
//
//        // setup values
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc1");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc2");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(Parser.PACKAGE);
//        setupLex(Parser.IDENTIFIER, "mypackage");
//        setupLex(Parser.SEMI);
//
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc3");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc4");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(Parser.IMPORT);
//        setupLex(Parser.IDENTIFIER, "anotherpackage");
//        setupLex(Parser.DOT);
//        setupLex(Parser.IDENTIFIER, "Something");
//        setupLex(Parser.SEMI);
//
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc5");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc6");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(Parser.IMPORT);
//        setupLex(Parser.IDENTIFIER, "elsewhere");
//        setupLex(Parser.DOT);
//        setupLex(Parser.STAR);
//        setupLex(Parser.SEMI);
//
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc7");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc8");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(Parser.PUBLIC);
//        setupLex(Parser.CLASS);
//        setupLex(Parser.IDENTIFIER, "MyClass");
//        setupLex(Parser.BRACEOPEN);
//        setupLex(Parser.BRACECLOSE);
//
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc9");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(Parser.JAVADOCSTART);
//        setupLex(Parser.JAVADOCLINE, "javadoc10");
//        setupLex(Parser.JAVADOCEND);
//
//        setupLex(0);
//
//        // execute
//        Parser parser = new Parser(lexer, builder);
//        parser.parse();
//
//        // expectations
//        ClassDef cls = new ClassDef();
//        cls.name = "MyClass";
//        cls.modifiers.add("public");
//        
//        // verify
//        verify(builder).addJavaDoc("javadoc1");
//        verify(builder).addJavaDoc("javadoc2");
//        verify(builder).addPackage( new PackageDef( "mypackage" ) );
//        verify(builder).addJavaDoc("javadoc3");
//        verify(builder).addJavaDoc("javadoc4");
//        verify(builder).addImport( "anotherpackage.Something" );
//        verify(builder).addJavaDoc("javadoc5");
//        verify(builder).addJavaDoc("javadoc6");
//        verify(builder).addImport("elsewhere.*");
//        verify(builder).addJavaDoc("javadoc7");
//        verify(builder).beginClass( cls );
//        verify(builder).endClass();
//        verify(builder).addJavaDoc("javadoc8");
//        verify(builder).addJavaDoc("javadoc9");
//        verify(builder).addJavaDoc("javadoc10");
//
//    }

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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");

        // verify
        verify(builder).beginClass(cls);
        verify(builder).beginMethod();
        verify(builder).endMethod(mth);
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        
        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("Something");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("com.blah.Something");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("com.blah.Something");
        mth.modifiers.add("public");
        mth.modifiers.add("protected");
        mth.modifiers.add("private");
        mth.modifiers.add("abstract");
        mth.modifiers.add("static");
        mth.modifiers.add("final");
        mth.modifiers.add("native");
        mth.modifiers.add("synchronized");
        mth.modifiers.add("volatile");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = new TypeDef("int");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).addParameter( p1 );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = new TypeDef("java.lang.String");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).addParameter( p1 );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = new TypeDef("int");
        FieldDef p2 = new FieldDef();
        p2.name = "name";
        p2.type = new TypeDef("String");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).addParameter( p1 );
        verify(builder).addParameter( p2 );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = new TypeDef("int");
        FieldDef p2 = new FieldDef();
        p2.name = "name";
        p2.type = new TypeDef("String");
        FieldDef p3 = new FieldDef();
        p3.name = "x";
        p3.type = new TypeDef("boolean");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).addParameter( p1 );
        verify(builder).addParameter( p2 );
        verify(builder).addParameter( p3 );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        FieldDef p1 = new FieldDef();
        p1.name = "numberOfTimes";
        p1.type = new TypeDef("int");
        p1.modifiers.add("final");
        p1.modifiers.add("volatile");
        
        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).addParameter( p1 );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        mth.exceptions.add(new TypeDef("IOException"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        mth.exceptions.add(new TypeDef("IOException"));
        mth.exceptions.add(new TypeDef("MyException"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        mth.exceptions.add(new TypeDef("IOException"));
        mth.exceptions.add(new TypeDef("MyException"));
        mth.exceptions.add(new TypeDef("AnotherException"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        mth.exceptions.add(new TypeDef("java.io.IOException"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        mth.exceptions.add(new TypeDef("java.io.IOException"));
        mth.exceptions.add(new TypeDef("java.lang.RuntimeException"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginConstructor();
        verify(builder).endConstructor( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;
        mth.modifiers.add("public");
        FieldDef p1 = new FieldDef();
        p1.name = "count";
        p1.type = new TypeDef("int");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginConstructor();
        verify(builder).addParameter( p1 );
        verify(builder).endConstructor( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;
        mth.modifiers.add("public");
        FieldDef p1 = new FieldDef();
        p1.name = "count";
        p1.type = new TypeDef("int");
        FieldDef p2 = new FieldDef();
        p2.name = "thingy";
        p2.type = new TypeDef("java.lang.String");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginConstructor();
        verify(builder).addParameter( p1 );
        verify(builder).addParameter( p2 );
        verify(builder).endConstructor( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;
        mth.exceptions.add(new TypeDef("SomeException"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginConstructor();
        verify(builder).endConstructor( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef mth = new MethodDef();
        mth.name = "MyClass";
        mth.constructor = true;
        mth.exceptions.add(new TypeDef("SomeException"));
        mth.exceptions.add(new TypeDef("java.io.IOException"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginConstructor();
        verify(builder).endConstructor( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("java.lang.String");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.modifiers.add("public");
        fld.modifiers.add("protected");
        fld.modifiers.add("private");
        fld.modifiers.add("static");
        fld.modifiers.add("final");
        fld.modifiers.add("transient");
        fld.modifiers.add("strictfp");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        FieldDef fld1 = new FieldDef();
        fld1.name = "thing";
        fld1.type = new TypeDef("String");
        FieldDef fld2 = new FieldDef();
        fld2.name = "another";
        fld2.type = new TypeDef("String");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld1 );
        verify(builder).addField( fld2 );
        verify(builder).endClass();
    }

    public void testFieldWithSimpleGenericType() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "List");
        setupLex(Parser.LESSTHAN);
        setupLex(Parser.IDENTIFIER, "String");
        setupLex(Parser.GREATERTHAN);
        
        setupLex(Parser.IDENTIFIER, "l");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        FieldDef fld = new FieldDef();
        fld.name = "l";
        fld.type = new TypeDef("List");
        fld.type.actualArgumentTypes = new ArrayList<TypeDef>();
        fld.type.actualArgumentTypes.add(new TypeDef("String"));

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
    }

    public void testFieldWithWildcardGenericType() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "List");
        setupLex(Parser.LESSTHAN);
        setupLex(Parser.QUERY);
        setupLex(Parser.EXTENDS);
        setupLex(Parser.IDENTIFIER, "A");
        setupLex(Parser.GREATERTHAN);
        
        setupLex(Parser.IDENTIFIER, "l");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        FieldDef fld = new FieldDef();
        fld.name = "l";
        fld.type = new TypeDef("List");
        fld.type.actualArgumentTypes = new ArrayList<TypeDef>();
        fld.type.actualArgumentTypes.add(new WildcardTypeDef(new TypeDef("A"), "extends"));
        
        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
    }

    public void testStaticBlock() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.STATIC);
        setupLex(Parser.CODEBLOCK);

        // a random method afterwards
        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doStuff");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expect no the method, and it shouldn't be static.
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        MethodDef method = new MethodDef();
        method.name = "doStuff";
        method.returnType = new TypeDef("void");

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod(method);
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );
        ClassDef cls2 = new ClassDef();
        cls2.setName( "InnerCls" );
        ClassDef cls3 = new ClassDef();
        cls3.setName( "AnotherClass" );

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginClass( cls2 );
        verify(builder).beginClass( cls3 );
        verify(builder, times(3)).endClass();

    }

    public void testRogueSemiColon() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "MyClass" );

        // verify
        verify(builder).beginClass( cls );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.dimensions = 0;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.dimensions = 1;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.dimensions = 2;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.dimensions = 1;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.dimensions = 3;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.dimensions = 1;
        FieldDef fld2 = new FieldDef();
        fld2.name = "count2";
        fld2.type = new TypeDef("int");
        fld2.dimensions = 0;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).addField( fld );
        verify(builder).addField( fld2 );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returnType = new TypeDef("int");
        mth.dimensions = 0;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returnType = new TypeDef("int");
        mth.dimensions = 1;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
    }

    public void testMethodWithArrayDefinedAtEnd() throws Exception {

        // It is legal in Java to define a method like this:
        //   String doStuff()[] { 
        // ... which is equivalent to:
        //   String[] doStuff() { 
        // This is done in some places in the JDK.

        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "count");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SQUAREOPEN);
        setupLex(Parser.SQUARECLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returnType = new TypeDef("int");
        mth.dimensions = 1;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returnType = new TypeDef("int");
        mth.dimensions = 1;
        FieldDef p1 = new FieldDef();
        p1.name = "p1";
        p1.type = new TypeDef("int");
        p1.dimensions = 0;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).addParameter( p1 );
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returnType = new TypeDef("int");
        mth.dimensions = 0;
        FieldDef p1 = new FieldDef();
        p1.name = "p1";
        p1.type = new TypeDef("int");
        p1.dimensions = 1;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).addParameter( p1 );
        verify(builder).endMethod( mth );
        verify(builder).endClass();
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

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        MethodDef mth = new MethodDef();
        mth.name = "count";
        mth.returnType = new TypeDef("int");
        mth.dimensions = 1;
        FieldDef p1 = new FieldDef();
        p1.name = "p1";
        p1.type = new TypeDef("int");
        p1.dimensions = 2;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).addParameter( p1 );
        verify(builder).endMethod( mth );
        verify(builder).endClass();
    }

    public void testMethodWithVarArgsParameter() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "void");
        setupLex(Parser.IDENTIFIER, "doStuff");
        setupLex(Parser.PARENOPEN);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.DOTDOTDOT);
        setupLex(Parser.IDENTIFIER, "stuff");

        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // expectations
        ClassDef cls = new ClassDef();
        cls.setName( "x" );
        MethodDef mth = new MethodDef();
        mth.name = "doStuff";
        mth.returnType = new TypeDef("void");
        FieldDef p1 = new FieldDef();
        p1.name = "stuff";
        p1.type = new TypeDef("int");
        p1.dimensions = 0;
        p1.isVarArgs = true;

        // verify
        verify(builder).beginClass( cls );
        verify(builder).beginMethod();
        verify(builder).addParameter( p1 );
        verify(builder).endMethod( mth );
        verify(builder).endClass();
    }

    public void testEnumWithConstructors() throws Exception {
        setupLex(Parser.ENUM);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "a");
        setupLex(Parser.PARENBLOCK);
        setupLex(Parser.SEMI);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "someField");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();
        
        // expectations
        ClassDef cls = new ClassDef();
        cls.type = ClassDef.ENUM;
        cls.setName( "x" );
//        MethodDef mth = new MethodDef();
//        mth.name = "a";
        FieldDef fld0 = new FieldDef();
        fld0.type = new TypeDef( "x" ); //bug @todo fixme
        fld0.name = "a";
        fld0.body = "";
        FieldDef fld = new FieldDef();
        fld.type = new TypeDef("int");
        fld.name = "someField";
        fld.body = "";
        
        // verify
        verify(builder).beginClass( cls );
//        verify(mockBuilder).beginConstructor();
//        verify(mockBuilder).endConstructor(mth);
        verify(builder).addField( fld0 );
        verify(builder).addField( fld );
        verify(builder).endClass();
    }
    
    public void testEnumEndingWithExtraComma() throws Exception {
        setupLex(Parser.ENUM);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "a");
        setupLex(Parser.COMMA);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();
        
     // expectations
        ClassDef cls = new ClassDef();
        cls.type = ClassDef.ENUM;
        cls.setName( "x" );
//        MethodDef mth = new MethodDef();
//        mth.name = "a";
        FieldDef fld = new FieldDef();
        fld.type = new TypeDef( "x" ); //bug @todo fixme
        fld.name = "a";
        fld.body = "";

        // verify
        verify(builder).beginClass( cls );
//      verify(mockBuilder).beginConstructor();
//      verify(mockBuilder).endConstructor(mth);
        verify(builder).addField( fld );
        verify(builder).endClass();
    }

    private void setupLex(int token, String value) {
        lexValues.add( token );
        textValues.add( value );
    }

    private void setupLex( int token ) throws Exception
    {
        setupLex( token, null );
        if ( token == 0 )
        {
            when( lexer.lex() ).thenAnswer( new ReturnsElementsOf( lexValues ) );
            when( lexer.text() ).thenAnswer( new ReturnsElementsOf( textValues ) );
            when( lexer.getLine() ).thenReturn( -1 );
        }
    }

}