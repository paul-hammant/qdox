package com.thoughtworks.qdox.parser;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.parser.expression.ExpressionDef;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collection;
import java.util.LinkedList;

import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.returnsElementsOf;

public class ParserTest {
    
	private Collection<Integer> lexValues = new LinkedList<Integer>();
    private Collection<String> textValues = new LinkedList<String>();
    private Collection<String> codeBodyValues = new LinkedList<String>();
    
    private JavaLexer lexer;
    private Builder builder;

    @BeforeEach
    public void setUp() throws Exception {
        builder = mock(Builder.class);
        lexer = mock(JavaLexer.class);
        lexValues.clear();
        textValues.clear();
    }

    @AfterEach
    public void tearDown()
        throws Exception
    {
        verifyNoMoreInteractions( builder );
    }

    @Test
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
        ArgumentCaptor<PackageDef> argument = ArgumentCaptor.forClass(PackageDef.class);
        verify(builder).addPackage( argument.capture() );
        Assertions.assertEquals("mypackage", argument.getValue().getName());
    }

    @Test
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
        ArgumentCaptor<PackageDef> argument = ArgumentCaptor.forClass(PackageDef.class);
        verify(builder).addPackage( argument.capture() );
        Assertions.assertEquals("com.blah.thingy.x", argument.getValue().getName());
    }

    @Test
    public void testPackageWithAnnotation() throws Exception {

        // setup values
        setupLex(Parser.AT);
        setupLex(Parser.IDENTIFIER, "Bar");
        setupLex(Parser.PACKAGE);
        setupLex(Parser.IDENTIFIER, "mypackage");
        setupLex(Parser.SEMI);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();

        // verify
        ArgumentCaptor<AnnoDef> annoCaptor = ArgumentCaptor.forClass( AnnoDef.class );
        verify( builder ).addAnnotation( annoCaptor.capture() );
        Assertions.assertEquals("Bar", annoCaptor.getValue().getTypeDef().getName());
        ArgumentCaptor<PackageDef> argument = ArgumentCaptor.forClass( PackageDef.class );
        verify( builder ).addPackage( argument.capture() );
        Assertions.assertEquals("mypackage", argument.getValue().getName());
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();

        Assertions.assertEquals("MyClass", classCaptor.getValue().getName());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyInterface", cls.getName());
        Assertions.assertEquals(ClassDef.INTERFACE, cls.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyEnum", cls.getName());
        Assertions.assertEquals(ClassDef.ENUM, cls.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MySubClass", cls.getName());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "com.blah.MyBaseClass" ) }, cls.getExtends().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyInterface", cls.getName());
        Assertions.assertEquals(ClassDef.INTERFACE, cls.getType());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "com.blah.AnotherInterface" ),
            new TypeDef( "Serializable" ) }, cls.getExtends().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        Assertions.assertArrayEquals(new TypeDef[] {new TypeDef("com.blah.AnInterface")}, cls.getImplements().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "com.blah.AnInterface" ),
            new TypeDef( "java.io.Serializable" ), new TypeDef( "Eatable" ) }, cls.getImplements().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "mypackage.BaseClass" ) }, cls.getExtends().toArray( new TypeDef[0] ));
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "com.blah.AnInterface" ) }, cls.getImplements().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "mypackage.BaseClass" ) }, cls.getExtends().toArray( new TypeDef[0] ));
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "com.blah.AnInterface" ),
            new TypeDef( "java.io.Serializable" ), new TypeDef( "Eatable" ) }, cls.getImplements().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        Assertions.assertArrayEquals(new String[] { "public", "final" }, cls.getModifiers().toArray( new String[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        Assertions.assertArrayEquals(new String[] {"public", "protected","private", "final", "abstract" }, cls.getModifiers().toArray( new String[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        
        // verify
        verify(builder, times(3)).beginClass( classCaptor.capture() );
        verify(builder, times(3)).endClass();

        ClassDef cls1 = classCaptor.getAllValues().get( 0 );
        Assertions.assertEquals("Class1", cls1.getName());
        Assertions.assertEquals(ClassDef.CLASS, cls1.getType());

        ClassDef cls2 = classCaptor.getAllValues().get( 1 );
        Assertions.assertEquals("Class2", cls2.getName());
        Assertions.assertEquals(ClassDef.CLASS, cls2.getType());
        Assertions.assertArrayEquals(new String[]{"public"}, cls2.getModifiers().toArray( new String[0] ));
        Assertions.assertArrayEquals(new TypeDef[]{new TypeDef("SubClass")}, cls2.getExtends().toArray( new TypeDef[0] ));

        ClassDef cls3 = classCaptor.getAllValues().get( 2 );
        Assertions.assertEquals("Intf1", cls3.getName());
        Assertions.assertEquals(ClassDef.INTERFACE, cls3.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        
        // verify
        verify(builder, times(2)).beginClass( classCaptor.capture() );
        verify(builder, times(2)).endClass();

        ClassDef cls1 = classCaptor.getAllValues().get( 0 );
        Assertions.assertEquals("Class1", cls1.getName());
        Assertions.assertEquals(ClassDef.CLASS, cls1.getType());

        ClassDef cls2 = classCaptor.getAllValues().get( 1 );
        Assertions.assertEquals("Class2", cls2.getName());
        Assertions.assertEquals(ClassDef.CLASS, cls2.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);
        
        // verify
        verify(builder).beginClass(classCaptor.capture());
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());

        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("void"), mth.getReturnType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef( "void" ), mth.getReturnType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("Something"), mth.getReturnType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("com.blah.Something"), mth.getReturnType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> captor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( captor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = captor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("com.blah.Something"), mth.getReturnType());
        Assertions.assertArrayEquals(new String[]{"public", "protected", "private", "abstract", "static", "final", "native", "synchronized", "volatile"}, mth.getModifiers().toArray(new String[0]));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );
        ArgumentCaptor<FieldDef> p1 = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).addParameter( p1.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("void"), mth.getReturnType());
        FieldDef prm = p1.getValue();
        Assertions.assertEquals("numberOfTimes", prm.getName());
        Assertions.assertEquals(new TypeDef("int"), prm.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );
        ArgumentCaptor<FieldDef> p1 = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).addParameter( p1.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("void"), mth.getReturnType());
        FieldDef prm = p1.getValue();
        Assertions.assertEquals("numberOfTimes", prm.getName());
        Assertions.assertEquals(new TypeDef("java.lang.String"), prm.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );
        ArgumentCaptor<FieldDef> p = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder, times(2) ).addParameter( p.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("void"), mth.getReturnType());
        
        FieldDef p1 = p.getAllValues().get( 0 );
        Assertions.assertEquals("numberOfTimes", p1.getName());
        Assertions.assertEquals(new TypeDef( "int" ), p1.getType());
        FieldDef p2 = p.getAllValues().get( 1 );
        Assertions.assertEquals("name", p2.getName());
        Assertions.assertEquals(new TypeDef( "String" ), p2.getType());
        
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);
        ArgumentCaptor<FieldDef> parameterCaptor = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder, times(3) ).addParameter( parameterCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());

        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("void"), mth.getReturnType());

        FieldDef p1 = parameterCaptor.getAllValues().get( 0 );
        Assertions.assertEquals("numberOfTimes", p1.getName());
        Assertions.assertEquals(new TypeDef( "int" ), p1.getType());
        FieldDef p2 = parameterCaptor.getAllValues().get( 1 );
        Assertions.assertEquals("name", p2.getName());
        Assertions.assertEquals(new TypeDef( "String" ), p2.getType());
        FieldDef p3 = parameterCaptor.getAllValues().get( 2 );
        Assertions.assertEquals("x", p3.getName());
        Assertions.assertEquals(new TypeDef( "boolean" ), p3.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);
        ArgumentCaptor<FieldDef> parameterCaptor = ArgumentCaptor.forClass(FieldDef.class);
        
        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).addParameter( parameterCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());

        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("void"), mth.getReturnType());

        Assertions.assertEquals("numberOfTimes", parameterCaptor.getValue().getName());
        Assertions.assertArrayEquals(new String[] { "final", "volatile" }, parameterCaptor.getValue().getModifiers().toArray( new String[0] ));
        Assertions.assertEquals(new TypeDef("int"), parameterCaptor.getValue().getType());
    }

    @Test
    public void testMethodWithAnnotatedGenericReturnValue() throws Exception {
    	
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);
        
        setupLex(Parser.PUBLIC);
        setupLex(Parser.LESSTHAN);
        setupLex(Parser.IDENTIFIER, "T");
        setupLex(Parser.GREATERTHAN);
        setupLex(Parser.AT);
        setupLex(Parser.IDENTIFIER, "Nullable");
        setupLex(Parser.IDENTIFIER, "T");
        setupLex(Parser.IDENTIFIER, "doSomething");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);
        
    	Parser parser = new Parser(lexer, builder);
    	parser.parse();
        
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass(ClassDef.class);
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);
        ArgumentCaptor<AnnoDef> annotationCaptor = ArgumentCaptor.forClass(AnnoDef.class);

        verify(builder).beginClass(classCaptor.capture());
        verify(builder).beginMethod();
        verify(builder).endMethod(methodCaptor.capture());
        verify(builder).addAnnotation(annotationCaptor.capture());
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());

        MethodDef mth = methodCaptor.getValue();
        Assertions.assertTrue(mth.getModifiers().contains("public"));
        Assertions.assertEquals(new TypeDef("T"), mth.getReturnType());
        Assertions.assertEquals("doSomething", mth.getName());
        
        AnnoDef annotation = annotationCaptor.getValue();
        Assertions.assertEquals("Nullable", annotation.getTypeDef().getName());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef( "void" ), mth.getReturnType());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef("IOException")}, mth.getExceptions().toArray(new TypeDef[0]));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef( "void" ), mth.getReturnType());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "IOException" ), new TypeDef( "MyException" ) }, mth.getExceptions().toArray( new TypeDef[0] ));

    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef( "void" ), mth.getReturnType());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "IOException" ), new TypeDef( "MyException" ),
            new TypeDef( "AnotherException" ) }, mth.getExceptions().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef("void"), mth.getReturnType());
        Assertions.assertArrayEquals(new TypeDef[] {new TypeDef("java.io.IOException")}, mth.getExceptions().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doSomething", mth.getName());
        Assertions.assertEquals(new TypeDef( "void" ), mth.getReturnType());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "java.io.IOException" ),
            new TypeDef( "java.lang.RuntimeException" ) }, mth.getExceptions().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginConstructor();
        verify(builder).endConstructor( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("MyClass", mth.getName());
        Assertions.assertEquals(true, mth.isConstructor());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );
        ArgumentCaptor<FieldDef> parameterCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginConstructor();
        verify( builder ).addParameter( parameterCaptor.capture() );
        verify( builder ).endConstructor( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("MyClass", mth.getName());
        Assertions.assertEquals(true, mth.isConstructor());
        Assertions.assertArrayEquals(new String[] { "public" }, mth.getModifiers().toArray( new String[0] ));

        Assertions.assertEquals("count", parameterCaptor.getValue().getName());
        Assertions.assertEquals(new TypeDef( "int" ), parameterCaptor.getValue().getType());
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );
        ArgumentCaptor<FieldDef> parameterCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginConstructor();
        verify( builder, times( 2 ) ).addParameter( parameterCaptor.capture() );
        verify( builder ).endConstructor( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("MyClass", mth.getName());
        Assertions.assertEquals(true, mth.isConstructor());
        Assertions.assertArrayEquals(new String[] { "public" }, mth.getModifiers().toArray( new String[0] ));
        FieldDef p1 = parameterCaptor.getAllValues().get( 0 );
        Assertions.assertEquals("count", p1.getName());
        Assertions.assertEquals(new TypeDef( "int" ), p1.getType());
        FieldDef p2 = parameterCaptor.getAllValues().get( 1 );
        Assertions.assertEquals("thingy", p2.getName());
        Assertions.assertEquals(new TypeDef( "java.lang.String" ), p2.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginConstructor();
        verify( builder ).endConstructor( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("MyClass", mth.getName());
        Assertions.assertEquals(true, mth.isConstructor());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef( "SomeException" ) }, mth.getExceptions().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginConstructor();
        verify(builder).endConstructor( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("MyClass", mth.getName());
        Assertions.assertEquals(true, mth.isConstructor());
        Assertions.assertArrayEquals(new TypeDef[] { new TypeDef("java.io.IOException"), new TypeDef("SomeException") }, mth.getExceptions().toArray( new TypeDef[0] ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginField( fieldCaptor.capture() );
        verify(builder).endField();
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("count", fld.getName());
        Assertions.assertEquals(new TypeDef("int"), fld.getType());
        
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("count", fld.getName());
        Assertions.assertEquals(new TypeDef( "java.lang.String" ), fld.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);
        
        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("count", fld.getName());
        Assertions.assertEquals(new TypeDef("int"), fld.getType());
        Assertions.assertArrayEquals(new String[] {"public", "protected", "private", "static", "final", "transient", "strictfp"}, fld.getModifiers().toArray(new String[0]));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder, times( 2 ) ).beginField( fieldCaptor.capture() );
        verify( builder, times( 2 ) ).endField();
        verify( builder ).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        FieldDef fld1 = fieldCaptor.getAllValues().get( 0 );
        Assertions.assertEquals("thing", fld1.getName());
        Assertions.assertEquals(new TypeDef("String"), fld1.getType());
        FieldDef fld2 = fieldCaptor.getAllValues().get( 1 );
        Assertions.assertEquals("another", fld2.getName());
        Assertions.assertEquals(new TypeDef("String"), fld2.getType());
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("l", fld.getName());
        Assertions.assertEquals("List", fld.getType().getName());
        Assertions.assertEquals(1, fld.getType().getActualArgumentTypes().size());
        Assertions.assertEquals(new TypeDef( "String" ), fld.getType().getActualArgumentTypes().get( 0 ));
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("l", fld.getName());
        Assertions.assertEquals("List", fld.getType().getName());
        Assertions.assertEquals(1, fld.getType().getActualArgumentTypes().size());
        Assertions.assertEquals(new WildcardTypeDef( new TypeDef( "A" ), "extends" ), fld.getType().getActualArgumentTypes().get( 0 ));
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<InitDef> initCaptor = ArgumentCaptor.forClass( InitDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).addInitializer( initCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());

        MethodDef method = methodCaptor.getValue();
        Assertions.assertEquals("doStuff", method.getName());
        Assertions.assertEquals(new TypeDef("void"), method.getReturnType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder, times(3) ).beginClass( classCaptor.capture() );
        verify(builder, times(3)).endClass();

        Assertions.assertEquals("MyClass", classCaptor.getAllValues().get( 0 ).getName());
        Assertions.assertEquals("InnerCls", classCaptor.getAllValues().get( 1 ).getName());
        Assertions.assertEquals("AnotherClass", classCaptor.getAllValues().get( 2 ).getName());
    }

    @Test
    public void testRogueSemiColon() throws Exception {

        // setup values
        setupLex(Parser.CLASS);
        setupLex(Parser.IDENTIFIER, "MyClass");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("MyClass", cls.getName());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);
        
        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("count", fld.getName());
        Assertions.assertEquals(new TypeDef("int"), fld.getType());
        Assertions.assertEquals(0, fld.getDimensions());
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("count", fld.getName());
        Assertions.assertEquals(0, fld.getDimensions());
        Assertions.assertEquals("int", fld.getType().getName());
        Assertions.assertEquals(1, fld.getType().getDimensions());
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("count", fld.getName());
        Assertions.assertEquals(0, fld.getDimensions());
        Assertions.assertEquals("int", fld.getType().getName());
        Assertions.assertEquals(2, fld.getType().getDimensions());
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("count", fld.getName());
        Assertions.assertEquals(1, fld.getDimensions());
        Assertions.assertEquals(new TypeDef( "int", 0 ), fld.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        FieldDef fld = fieldCaptor.getValue();
        Assertions.assertEquals("count", fld.getName());
        Assertions.assertEquals(1, fld.getDimensions());
        Assertions.assertEquals("int", fld.getType().getName());
        Assertions.assertEquals(2, fld.getType().getDimensions());
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder, times( 2 ) ).beginField( fieldCaptor.capture() );
        verify( builder, times( 2 ) ).endField();
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        FieldDef fld = fieldCaptor.getAllValues().get( 0 );
        Assertions.assertEquals("count", fld.getName());
        Assertions.assertEquals(1, fld.getDimensions());
        Assertions.assertEquals(new TypeDef( "int" ), fld.getType());
        FieldDef fld2 = fieldCaptor.getAllValues().get( 1 );
        Assertions.assertEquals("count2", fld2.getName());
        Assertions.assertEquals(0, fld2.getDimensions());
        Assertions.assertEquals(new TypeDef( "int" ), fld2.getType());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("count", mth.getName());
        Assertions.assertEquals(new TypeDef("int"), mth.getReturnType());
        Assertions.assertEquals(0, mth.getDimensions());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("count", mth.getName());
        Assertions.assertEquals("int", mth.getReturnType().getName());
        Assertions.assertEquals(1, mth.getReturnType().getDimensions());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("count", mth.getName());
        Assertions.assertEquals(1, mth.getDimensions());
        Assertions.assertEquals(new TypeDef("int"), mth.getReturnType());
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );
        ArgumentCaptor<FieldDef> parameterCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).addParameter( parameterCaptor.capture() );
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("count", mth.getName());
        Assertions.assertEquals(new TypeDef( "int", 1 ), mth.getReturnType());
        FieldDef prm = parameterCaptor.getValue();
        Assertions.assertEquals("p1", prm.getName());
        Assertions.assertEquals(new TypeDef( "int" ), prm.getType());
        Assertions.assertEquals(0, prm.getDimensions());
    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );
        ArgumentCaptor<FieldDef> parameterCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).addParameter( parameterCaptor.capture() );
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("count", mth.getName());
        Assertions.assertEquals(new TypeDef( "int" ), mth.getReturnType());
        Assertions.assertEquals(0, mth.getDimensions());
        FieldDef prm = parameterCaptor.getValue();
        Assertions.assertEquals("p1", prm.getName());
        Assertions.assertEquals(0, prm.getDimensions());
        Assertions.assertEquals("int", prm.getType().getName());
        Assertions.assertEquals(1, prm.getType().getDimensions());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);
        ArgumentCaptor<FieldDef> parameterCaptor = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).addParameter( parameterCaptor.capture() );
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("count", mth.getName());
        Assertions.assertEquals(new TypeDef("int", 1), mth.getReturnType());
        FieldDef prm = parameterCaptor.getValue();
        Assertions.assertEquals("p1", prm.getName());
        Assertions.assertEquals("int", prm.getType().getName());
        Assertions.assertEquals(2, prm.getType().getDimensions());

    }

    @Test
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );
        ArgumentCaptor<FieldDef> parameterCaptor = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).addParameter( parameterCaptor.capture() );
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        Assertions.assertEquals("doStuff", mth.getName());
        Assertions.assertEquals(new TypeDef( "void" ), mth.getReturnType());
        FieldDef prm = parameterCaptor.getValue();
        Assertions.assertEquals("stuff", prm.getName());
        Assertions.assertEquals(new TypeDef( "int" ), prm.getType());
        Assertions.assertEquals(0, prm.getDimensions());
        Assertions.assertEquals(true, prm.isVarArgs());
    }

    @Test
    public void testEnumWithConstructors() throws Exception {
        setupLex(Parser.ENUM);
        setupLex(Parser.IDENTIFIER, "x");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "a");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.SEMI);

        setupLex(Parser.IDENTIFIER, "int");
        setupLex(Parser.IDENTIFIER, "someField");
        setupLex(Parser.SEMI);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        // MethodDef mth = new MethodDef();
        // mth.name = "a";

        ArgumentCaptor<FieldDef> f = ArgumentCaptor.forClass( FieldDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        // verify(mockBuilder).beginConstructor();
        // verify(mockBuilder).endConstructor(mth);
        verify( builder, times( 2 ) ).beginField( f.capture() );
        verify( builder, times( 2 ) ).endField();
        verify( builder ).endClass();
        verifyNoMoreInteractions( builder );

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        Assertions.assertEquals(ClassDef.ENUM, cls.getType());
        FieldDef fld0 = f.getAllValues().get( 0 );
        Assertions.assertEquals("a", fld0.getName());
        Assertions.assertEquals(new TypeDef( "x" ), fld0.getType()); // bug @todo fixme
        Assertions.assertEquals("", fld0.getBody());
        FieldDef fld1 = f.getAllValues().get( 1 );
        Assertions.assertEquals("someField", fld1.getName());
        Assertions.assertEquals(new TypeDef( "int" ), fld1.getType());
        Assertions.assertEquals(null, fld1.getBody());
    }

    @Test
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
//        MethodDef mth = new MethodDef();
//        mth.name = "a";
        ArgumentCaptor<FieldDef> f = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        // verify(mockBuilder).beginConstructor();
        // verify(mockBuilder).endConstructor(mth);
        verify( builder ).beginField( f.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        Assertions.assertEquals(ClassDef.ENUM, cls.getType());
        FieldDef fld = f.getValue();
        Assertions.assertEquals("a", fld.getName());
        Assertions.assertEquals(new TypeDef( "x" ), fld.getType()); //bug @todo fixme
        Assertions.assertEquals("", fld.getBody());
    }
    
    // QDOX-266
    @Test
    public void testEnumConstantWithClassBody() throws Exception
    {
        setupLex(Parser.PUBLIC);
        setupLex(Parser.ENUM);
        setupLex(Parser.IDENTIFIER, "MethodLocationOfEnumMethod");
        setupLex(Parser.BRACEOPEN);

        setupLex(Parser.IDENTIFIER, "A");
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        
        setupLex(Parser.BRACEOPEN);
        
        setupLex(Parser.AT);
        setupLex(Parser.IDENTIFIER, "Override");
        setupLex(Parser.PUBLIC);
        setupLex(Parser.IDENTIFIER, "void" );
        setupLex(Parser.IDENTIFIER, "method" );
        setupLex(Parser.PARENOPEN);
        setupLex(Parser.PARENCLOSE);
        setupLex(Parser.CODEBLOCK);
        setupLex(Parser.SEMI);
        
        setupLex(Parser.BRACECLOSE);

        setupLex(Parser.BRACECLOSE);
        setupLex(0);

        // execute
        Parser parser = new Parser(lexer, builder);
        parser.parse();
        
        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> f = ArgumentCaptor.forClass(FieldDef.class);
        ArgumentCaptor<ClassDef> enumConstantClassCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<AnnoDef> annoCaptor = ArgumentCaptor.forClass( AnnoDef.class );
        ArgumentCaptor<MethodDef> methodClassCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( f.capture() );
        verify( builder ).beginClass( enumConstantClassCaptor.capture() );
        verify( builder ).addAnnotation( annoCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).endMethod( methodClassCaptor.capture() );
        verify( builder ).endClass();
        verify( builder ).endField();
        verify( builder ).endClass();
        
        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals(true, cls.getModifiers().contains( "public" ));
        Assertions.assertEquals(ClassDef.ENUM, cls.getType());
        Assertions.assertEquals("MethodLocationOfEnumMethod", cls.getName());
        FieldDef fld = f.getValue();
        Assertions.assertEquals("A", fld.getName());
        Assertions.assertEquals(new TypeDef( "MethodLocationOfEnumMethod" ), fld.getType());
        //ClassDef ecCls = enumConstantClassCaptor.getValue();
        AnnoDef ann = annoCaptor.getValue();
        Assertions.assertEquals("Override", ann.getTypeDef().getName());
        MethodDef mth = methodClassCaptor.getValue();
        Assertions.assertEquals("method", mth.getName());
        
//        Class methodLocationOfEnumMethod = MethodLocationOfEnumMethod.class;
//        Field a = methodLocationOfEnumMethod.getField( "A" );
//        assertNotNull( a );
//        assertSame( methodLocationOfEnumMethod, a.getDeclaringClass() );
//        assertSame( methodLocationOfEnumMethod, a.getType() );
//        assertEquals( 2, methodLocationOfEnumMethod.getDeclaredMethods().length);
    }

    @Test
    public void testStaticInitializer()
        throws Exception
    {
        // setup values
        setupLex( Parser.CLASS );
        setupLex( Parser.IDENTIFIER, "x" );
        setupLex( Parser.BRACEOPEN );

        setupLex( Parser.STATIC );
        setupLex( Parser.CODEBLOCK, null, "//test" );

        setupLex( Parser.BRACECLOSE );
        setupLex( 0 );

        // execute
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<InitDef> initCaptor = ArgumentCaptor.forClass( InitDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).addInitializer( initCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        InitDef init = initCaptor.getValue();
        Assertions.assertTrue(init.isStatic());
        Assertions.assertEquals("//test", init.getBlockContent());
    }

    @Test
    public void testInstanceInitializer() throws Exception
    {
        // setup values
        setupLex( Parser.CLASS );
        setupLex( Parser.IDENTIFIER, "x" );
        setupLex( Parser.BRACEOPEN );

        setupLex( Parser.CODEBLOCK, null, "//test" );

        setupLex( Parser.BRACECLOSE );
        setupLex( 0 );

        // execute
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<InitDef> initCaptor = ArgumentCaptor.forClass( InitDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).addInitializer( initCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        Assertions.assertEquals("x", cls.getName());
        InitDef init = initCaptor.getValue();
        Assertions.assertFalse(init.isStatic());
        Assertions.assertEquals("//test", init.getBlockContent());
    }

    @Test
    public void testModule() throws Exception
    {
        setupLex( Parser.AT );
        setupLex( Parser.IDENTIFIER, "Foo" );
        setupLex( Parser.PARENOPEN );
        setupLex( Parser.INTEGER_LITERAL, "1" );
        setupLex( Parser.PARENCLOSE );
        setupLex( Parser.AT );
        setupLex( Parser.IDENTIFIER, "Foo" );
        setupLex( Parser.PARENOPEN );
        setupLex( Parser.INTEGER_LITERAL, "2" );
        setupLex( Parser.PARENCLOSE );
        setupLex( Parser.AT );
        setupLex( Parser.IDENTIFIER, "Bar" );
        
        setupLex( Parser.OPEN );
        setupLex( Parser.MODULE );
        setupLex( Parser.IDENTIFIER, "M" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "N" );
        setupLex( Parser.BRACEOPEN );
        
        setupLex( Parser.REQUIRES );
        setupLex( Parser.IDENTIFIER, "A" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "B" );
        setupLex( Parser.SEMI );
        
        setupLex( Parser.REQUIRES );
        setupLex( Parser.TRANSITIVE );
        setupLex( Parser.IDENTIFIER, "C" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "D" );
        setupLex( Parser.SEMI );
        
        setupLex( Parser.REQUIRES );
        setupLex( Parser.STATIC );
        setupLex( Parser.IDENTIFIER, "E" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "F" );
        setupLex( Parser.SEMI );
        
        setupLex( Parser.REQUIRES );
        setupLex( Parser.TRANSITIVE );
        setupLex( Parser.STATIC );
        setupLex( Parser.IDENTIFIER, "G" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "H" );
        setupLex( Parser.SEMI );

        setupLex( Parser.EXPORTS );
        setupLex( Parser.IDENTIFIER, "P" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "Q" );
        setupLex( Parser.SEMI );

        setupLex( Parser.EXPORTS );
        setupLex( Parser.IDENTIFIER, "R" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "S" );
        setupLex( Parser.TO );
        setupLex( Parser.IDENTIFIER, "T1" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "U1" );
        setupLex( Parser.COMMA );
        setupLex( Parser.IDENTIFIER, "T2" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "U2" );
        setupLex( Parser.SEMI );

        setupLex( Parser.OPENS );
        setupLex( Parser.IDENTIFIER, "P" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "Q" );
        setupLex( Parser.SEMI );

        setupLex( Parser.OPENS );
        setupLex( Parser.IDENTIFIER, "R" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "S" );
        setupLex( Parser.TO );
        setupLex( Parser.IDENTIFIER, "T1" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "U1" );
        setupLex( Parser.COMMA );
        setupLex( Parser.IDENTIFIER, "T2" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "U2" );
        setupLex( Parser.SEMI );

        setupLex( Parser.USES );
        setupLex( Parser.IDENTIFIER, "V" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "W" );
        setupLex( Parser.SEMI );

        setupLex( Parser.PROVIDES );
        setupLex( Parser.IDENTIFIER, "X" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "Y" );
        setupLex( Parser.WITH );
        setupLex( Parser.IDENTIFIER, "Z1" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "Z2" );
        setupLex( Parser.COMMA );
        setupLex( Parser.IDENTIFIER, "Z3" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "Z4" );
        setupLex( Parser.SEMI );

        setupLex( Parser.BRACECLOSE );
        setupLex( 0 );
        
       // execute
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        ArgumentCaptor<AnnoDef> annoCaptor = ArgumentCaptor.forClass( AnnoDef.class );
        verify( builder, times(3) ).addAnnotation( annoCaptor.capture() );
        Assertions.assertEquals("Foo", annoCaptor.getAllValues().get( 0 ).getTypeDef().getName());
        Assertions.assertEquals("1", annoCaptor.getAllValues().get( 0 ).getArgs().get( "value" ).toString());
        Assertions.assertEquals("Foo", annoCaptor.getAllValues().get( 1 ).getTypeDef().getName());
        Assertions.assertEquals("2", annoCaptor.getAllValues().get( 1 ).getArgs().get( "value" ).toString());
        Assertions.assertEquals("Bar", annoCaptor.getAllValues().get( 2 ).getTypeDef().getName());
        Assertions.assertEquals(null, annoCaptor.getAllValues().get( 2 ).getArgs().get( "value" ));
        
        ArgumentCaptor<ModuleDef> moduleCaptor = ArgumentCaptor.forClass( ModuleDef.class );
        verify( builder ).setModule( moduleCaptor.capture() );
        Assertions.assertEquals("M.N", moduleCaptor.getValue().getName());
        Assertions.assertEquals(true, moduleCaptor.getValue().isOpen());
        
        ArgumentCaptor<ModuleDef.RequiresDef> requiresCaptor = ArgumentCaptor.forClass( ModuleDef.RequiresDef.class );
        verify( builder, times(4) ).addRequires( requiresCaptor.capture() );
        Assertions.assertEquals("A.B", requiresCaptor.getAllValues().get(0).getName());
        Assertions.assertEquals(false, requiresCaptor.getAllValues().get(0).getModifiers().contains( "transitive" ));
        Assertions.assertEquals(false, requiresCaptor.getAllValues().get(0).getModifiers().contains( "static" ));
        
        Assertions.assertEquals("C.D", requiresCaptor.getAllValues().get(1).getName());
        Assertions.assertEquals(true, requiresCaptor.getAllValues().get(1).getModifiers().contains( "transitive" ));
        Assertions.assertEquals(false, requiresCaptor.getAllValues().get(1).getModifiers().contains( "static" ));
        
        Assertions.assertEquals("E.F", requiresCaptor.getAllValues().get(2).getName());
        Assertions.assertEquals(false, requiresCaptor.getAllValues().get(2).getModifiers().contains( "transitive" ));
        Assertions.assertEquals(true, requiresCaptor.getAllValues().get(2).getModifiers().contains( "static" ));
        
        Assertions.assertEquals("G.H", requiresCaptor.getAllValues().get(3).getName());
        Assertions.assertEquals(true, requiresCaptor.getAllValues().get(3).getModifiers().contains( "transitive" ));
        Assertions.assertEquals(true, requiresCaptor.getAllValues().get(3).getModifiers().contains( "static" ));
        
        ArgumentCaptor<ModuleDef.ExportsDef> exportsCaptor = ArgumentCaptor.forClass( ModuleDef.ExportsDef.class );
        verify( builder, times(2) ).addExports( exportsCaptor.capture() );
        Assertions.assertEquals("P.Q", exportsCaptor.getAllValues().get( 0 ).getSource());
        Assertions.assertEquals(0, exportsCaptor.getAllValues().get( 0 ).getTargets().size());

        Assertions.assertEquals("R.S", exportsCaptor.getAllValues().get( 1 ).getSource());
        Assertions.assertEquals(2, exportsCaptor.getAllValues().get( 1 ).getTargets().size());
        Assertions.assertEquals(true, exportsCaptor.getAllValues().get( 1 ).getTargets().contains( "T1.U1" ));
        Assertions.assertEquals(true, exportsCaptor.getAllValues().get( 1 ).getTargets().contains( "T2.U2" ));
        
        ArgumentCaptor<ModuleDef.OpensDef> opensCaptor = ArgumentCaptor.forClass( ModuleDef.OpensDef.class );
        verify( builder, times(2) ).addOpens( opensCaptor.capture() );
        Assertions.assertEquals("P.Q", opensCaptor.getAllValues().get( 0 ).getSource());
        Assertions.assertEquals(0, opensCaptor.getAllValues().get( 0 ).getTargets().size());

        Assertions.assertEquals("R.S", opensCaptor.getAllValues().get( 1 ).getSource());
        Assertions.assertEquals(2, opensCaptor.getAllValues().get( 1 ).getTargets().size());
        Assertions.assertEquals(true, opensCaptor.getAllValues().get( 1 ).getTargets().contains( "T1.U1" ));
        Assertions.assertEquals(true, opensCaptor.getAllValues().get( 1 ).getTargets().contains( "T2.U2" ));
        
        ArgumentCaptor<ModuleDef.UsesDef> usesCaptor = ArgumentCaptor.forClass( ModuleDef.UsesDef.class );
        verify( builder ).addUses( usesCaptor.capture() );
        Assertions.assertEquals("V.W", usesCaptor.getValue().getService().getName());
        
        ArgumentCaptor<ModuleDef.ProvidesDef> providesCaptor = ArgumentCaptor.forClass( ModuleDef.ProvidesDef.class );
        verify( builder, times(1) ).addProvides( providesCaptor.capture() );
        Assertions.assertEquals("X.Y", providesCaptor.getAllValues().get(0).getService().getName());
        Assertions.assertEquals("Z1.Z2", providesCaptor.getAllValues().get(0).getImplementations().get(0).getName());
        Assertions.assertEquals("Z3.Z4", providesCaptor.getAllValues().get(0).getImplementations().get(1).getName());
    }

    @Test
    public void testCEnums() throws Exception {
        setupLex( Parser.PUBLIC );
        setupLex( Parser.ENUM );
        setupLex( Parser.IDENTIFIER, "EnumWithFields" );
        setupLex( Parser.BRACEOPEN );
        
        setupLex( Parser.IDENTIFIER, "VALUEA" );
        setupLex( Parser.PARENOPEN );
        setupLex( Parser.IDENTIFIER, "By" );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "linkText" );
        setupLex( Parser.PARENOPEN );
        setupLex( Parser.STRING_LITERAL, "\"G\"");
        setupLex( Parser.PARENCLOSE );
        setupLex( Parser.COMMA );
        setupLex( Parser.STRING_LITERAL, "\"H\"");
        setupLex( Parser.PARENCLOSE );
        
        setupLex( Parser.BRACECLOSE );
        setupLex( 0 );        
        
        // execute
        Parser parser = new Parser( lexer, builder );
        parser.parse();
        
        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass( FieldDef.class );
        ArgumentCaptor<ExpressionDef> argumentCaptor = ArgumentCaptor.forClass( ExpressionDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() ); 
        verify( builder, times(3) ).addArgument( argumentCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();
    }

    @Test
    public void testStringBasedEnum()
        throws Exception
    {
        setupLex( Parser.PUBLIC );
        setupLex( Parser.ENUM );
        setupLex( Parser.IDENTIFIER, "StringBasedEnum" );

        setupLex( Parser.BRACEOPEN );

        setupLex( Parser.IDENTIFIER, "LIST" );
        setupLex( Parser.PARENOPEN );
        setupLex( Parser.IDENTIFIER, "List" );
        setupLex( Parser.DOT );
        setupLex( Parser.CLASS );
        setupLex( Parser.DOT );
        setupLex( Parser.IDENTIFIER, "getName" );
        setupLex( Parser.PARENOPEN );
        setupLex( Parser.PARENCLOSE );
        setupLex( Parser.PARENCLOSE );
        setupLex( Parser.SEMI );

        setupLex( Parser.IDENTIFIER, "StringBasedEnum" );
        setupLex( Parser.PARENOPEN );
        setupLex( Parser.IDENTIFIER, "String" );
        setupLex( Parser.IDENTIFIER, "className" );
        setupLex( Parser.PARENCLOSE );
        setupLex( Parser.CODEBLOCK, "}" );

        setupLex( Parser.BRACECLOSE );
        setupLex( 0 );

        Parser parser = new Parser( lexer, builder );
        parser.parse();

        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass( FieldDef.class );
        ArgumentCaptor<ExpressionDef> argumentCaptor = ArgumentCaptor.forClass( ExpressionDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).addArgument( argumentCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).beginConstructor();
        verify( builder ).addParameter( fieldCaptor.capture() );
        verify( builder ).endConstructor( methodCaptor.capture() );
        verify( builder ).endClass();
    }

    private void setupLex(int token, String value) {
        lexValues.add( token );
        textValues.add( value );
    }

    private void setupLex(int token, String value, String codeBody ) {
        setupLex( token, value );
        codeBodyValues.add( codeBody );
    }

    private void setupLex( int token ) throws Exception
    {
        setupLex( token, null );
        if ( token == 0 )
        {
            when( lexer.lex() ).thenAnswer( returnsElementsOf( lexValues ) );
            when( lexer.text() ).thenAnswer( returnsElementsOf( textValues ) );
            when( lexer.getCodeBody() ).thenAnswer( returnsElementsOf( codeBodyValues ) );
            when( lexer.getLine() ).thenReturn( -1 );
        }
    }
}