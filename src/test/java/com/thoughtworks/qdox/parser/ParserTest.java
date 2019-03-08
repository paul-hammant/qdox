package com.thoughtworks.qdox.parser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.answers.ReturnsElementsOf;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.parser.expression.ExpressionDef;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.InitDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.WildcardTypeDef;

import junit.framework.TestCase;

public class ParserTest extends TestCase {
    
	private Collection<Integer> lexValues = new LinkedList<Integer>();
    private Collection<String> textValues = new LinkedList<String>();
    private Collection<String> codeBodyValues = new LinkedList<String>();
    
    private JavaLexer lexer;
    private Builder builder;

    public ParserTest(String s) {
        super(s);
    }

    @Override
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
        ArgumentCaptor<PackageDef> argument = ArgumentCaptor.forClass(PackageDef.class);
        verify(builder).addPackage( argument.capture() );
        assertEquals( "mypackage", argument.getValue().getName() );
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
        ArgumentCaptor<PackageDef> argument = ArgumentCaptor.forClass(PackageDef.class);
        verify(builder).addPackage( argument.capture() );
        assertEquals( "com.blah.thingy.x", argument.getValue().getName() );
    }
    
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
        assertEquals( "Bar", annoCaptor.getValue().getTypeDef().getName() );
        ArgumentCaptor<PackageDef> argument = ArgumentCaptor.forClass( PackageDef.class );
        verify( builder ).addPackage( argument.capture() );
        assertEquals( "mypackage", argument.getValue().getName() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();

        assertEquals( "MyClass", classCaptor.getValue().getName() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyInterface", cls.getName() );
        assertEquals( ClassDef.INTERFACE, cls.getType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyEnum", cls.getName() );
        assertEquals( ClassDef.ENUM, cls.getType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MySubClass", cls.getName() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "com.blah.MyBaseClass" ) },
                                  cls.getExtends().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyInterface", cls.getName() );
        assertEquals( ClassDef.INTERFACE, cls.getType() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "com.blah.AnotherInterface" ),
            new TypeDef( "Serializable" ) }, cls.getExtends().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        Assert.assertArrayEquals( new TypeDef[] {new TypeDef("com.blah.AnInterface")}, cls.getImplements().toArray( new TypeDef[0] ));
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "com.blah.AnInterface" ),
            new TypeDef( "java.io.Serializable" ), new TypeDef( "Eatable" ) }, cls.getImplements().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "mypackage.BaseClass" ) },
                                  cls.getExtends().toArray( new TypeDef[0] ) );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "com.blah.AnInterface" ) },
                                  cls.getImplements().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "mypackage.BaseClass" ) },
                                  cls.getExtends().toArray( new TypeDef[0] ) );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "com.blah.AnInterface" ),
            new TypeDef( "java.io.Serializable" ), new TypeDef( "Eatable" ) }, cls.getImplements().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        Assert.assertArrayEquals( new String[] { "public", "final" }, cls.getModifiers().toArray( new String[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        Assert.assertArrayEquals( new String[] {"public", "protected","private", "final", "abstract" }, cls.getModifiers().toArray( new String[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        
        // verify
        verify(builder, times(3)).beginClass( classCaptor.capture() );
        verify(builder, times(3)).endClass();

        ClassDef cls1 = classCaptor.getAllValues().get( 0 );
        assertEquals( "Class1", cls1.getName());
        assertEquals( ClassDef.CLASS, cls1.getType() );

        ClassDef cls2 = classCaptor.getAllValues().get( 1 );
        assertEquals( "Class2", cls2.getName() );
        assertEquals( ClassDef.CLASS, cls2.getType()  );
        Assert.assertArrayEquals( new String[]{"public"}, cls2.getModifiers().toArray( new String[0] ));
        Assert.assertArrayEquals( new TypeDef[]{new TypeDef("SubClass")}, cls2.getExtends().toArray( new TypeDef[0] ));

        ClassDef cls3 = classCaptor.getAllValues().get( 2 );
        assertEquals( "Intf1", cls3.getName() );
        assertEquals( ClassDef.INTERFACE, cls3.getType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        
        // verify
        verify(builder, times(2)).beginClass( classCaptor.capture() );
        verify(builder, times(2)).endClass();

        ClassDef cls1 = classCaptor.getAllValues().get( 0 );
        assertEquals( "Class1", cls1.getName() );
        assertEquals( ClassDef.CLASS, cls1.getType() );

        ClassDef cls2 = classCaptor.getAllValues().get( 1 );
        assertEquals( "Class2", cls2.getName() );
        assertEquals( ClassDef.CLASS, cls2.getType() );
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
        assertEquals( "MyClass", cls.getName() );

        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("void"), mth.getReturnType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef( "void" ), mth.getReturnType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("Something"), mth.getReturnType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("com.blah.Something"), mth.getReturnType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> captor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( captor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = captor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("com.blah.Something"), mth.getReturnType() );
        Assert.assertArrayEquals(new String[]{"public", "protected", "private", "abstract", "static", "final", "native", "synchronized", "volatile"}, 
                mth.getModifiers().toArray(new String[0]));
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
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("void"), mth.getReturnType() );
        FieldDef prm = p1.getValue();
        assertEquals( "numberOfTimes", prm.getName() );
        assertEquals( new TypeDef("int"), prm.getType() );
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
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("void"), mth.getReturnType() );
        FieldDef prm = p1.getValue();
        assertEquals( "numberOfTimes", prm.getName() );
        assertEquals( new TypeDef("java.lang.String"), prm.getType() );
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
        assertEquals( "MyClass", cls.getName() );
        
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("void"), mth.getReturnType() );
        
        FieldDef p1 = p.getAllValues().get( 0 );
        assertEquals( "numberOfTimes", p1.getName() );
        assertEquals( new TypeDef( "int" ), p1.getType() );
        FieldDef p2 = p.getAllValues().get( 1 );
        assertEquals( "name", p2.getName() );
        assertEquals( new TypeDef( "String" ), p2.getType() );
        
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
        assertEquals( "MyClass", cls.getName() );

        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("void"), mth.getReturnType() );

        FieldDef p1 = parameterCaptor.getAllValues().get( 0 );
        assertEquals( "numberOfTimes", p1.getName() );
        assertEquals( new TypeDef( "int" ), p1.getType() );
        FieldDef p2 = parameterCaptor.getAllValues().get( 1 );
        assertEquals( "name", p2.getName() );
        assertEquals( new TypeDef( "String" ), p2.getType() );
        FieldDef p3 = parameterCaptor.getAllValues().get( 2 );
        assertEquals( "x", p3.getName() );
        assertEquals( new TypeDef( "boolean" ), p3.getType() );
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
        assertEquals( "MyClass", cls.getName() );

        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("void"), mth.getReturnType() );

        assertEquals( "numberOfTimes", parameterCaptor.getValue().getName() );
        Assert.assertArrayEquals( new String[] { "final", "volatile" }, parameterCaptor.getValue().getModifiers().toArray( new String[0] ) );
        assertEquals( new TypeDef("int"), parameterCaptor.getValue().getType() );
    }
    
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
        assertEquals("MyClass", cls.getName());

        MethodDef mth = methodCaptor.getValue();
        assertTrue(mth.getModifiers().contains("public"));
        assertEquals(new TypeDef("T"), mth.getReturnType());
        assertEquals("doSomething", mth.getName());
        
        AnnoDef annotation = annotationCaptor.getValue();
        assertEquals("Nullable", annotation.getTypeDef().getName());
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef( "void" ), mth.getReturnType() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef("IOException")}, mth.getExceptions().toArray(new TypeDef[0]));
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef( "void" ), mth.getReturnType() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "IOException" ), new TypeDef( "MyException" ) },
                                  mth.getExceptions().toArray( new TypeDef[0] ) );

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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef( "void" ), mth.getReturnType() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "IOException" ), new TypeDef( "MyException" ),
            new TypeDef( "AnotherException" ) }, mth.getExceptions().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef("void"), mth.getReturnType() );
        Assert.assertArrayEquals( new TypeDef[] {new TypeDef("java.io.IOException")}, mth.getExceptions().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginMethod();
        verify( builder ).endMethod( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doSomething", mth.getName() );
        assertEquals( new TypeDef( "void" ), mth.getReturnType() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "java.io.IOException" ),
            new TypeDef( "java.lang.RuntimeException" ) }, mth.getExceptions().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginConstructor();
        verify(builder).endConstructor( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "MyClass", mth.getName() );
        assertEquals(true, mth.isConstructor() );
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
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "MyClass", mth.getName() );
        assertEquals( true, mth.isConstructor() );
        Assert.assertArrayEquals( new String[] { "public" }, mth.getModifiers().toArray( new String[0] ) );

        assertEquals( "count", parameterCaptor.getValue().getName() );
        assertEquals( new TypeDef( "int" ), parameterCaptor.getValue().getType() );
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
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "MyClass", mth.getName() );
        assertEquals( true, mth.isConstructor() );
        Assert.assertArrayEquals( new String[] { "public" }, mth.getModifiers().toArray( new String[0] ) );
        FieldDef p1 = parameterCaptor.getAllValues().get( 0 );
        assertEquals( "count", p1.getName() );
        assertEquals( new TypeDef( "int" ), p1.getType() );
        FieldDef p2 = parameterCaptor.getAllValues().get( 1 );
        assertEquals( "thingy", p2.getName() );
        assertEquals( new TypeDef( "java.lang.String" ), p2.getType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginConstructor();
        verify( builder ).endConstructor( methodCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "MyClass", mth.getName() );
        assertEquals( true, mth.isConstructor() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef( "SomeException" ) },
                                  mth.getExceptions().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginConstructor();
        verify(builder).endConstructor( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName());
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "MyClass", mth.getName() );
        assertEquals( true, mth.isConstructor() );
        Assert.assertArrayEquals( new TypeDef[] { new TypeDef("java.io.IOException"), new TypeDef("SomeException") }, 
                                  mth.getExceptions().toArray( new TypeDef[0] ) );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginField( fieldCaptor.capture() );
        verify(builder).endField();
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals(new TypeDef("int"), fld.getType() );
        
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
        assertEquals( "MyClass", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( new TypeDef( "java.lang.String" ), fld.getType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);
        
        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( new TypeDef("int"), fld.getType() );
        Assert.assertArrayEquals( new String[] {"public", "protected", "private", "static", "final", "transient", "strictfp"}, 
                    fld.getModifiers().toArray(new String[0]));
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder, times( 2 ) ).beginField( fieldCaptor.capture() );
        verify( builder, times( 2 ) ).endField();
        verify( builder ).endClass();
        
        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        FieldDef fld1 = fieldCaptor.getAllValues().get( 0 );
        assertEquals( "thing", fld1.getName() );
        assertEquals( new TypeDef("String"), fld1.getType() );
        FieldDef fld2 = fieldCaptor.getAllValues().get( 1 );
        assertEquals( "another", fld2.getName() );
        assertEquals( new TypeDef("String"), fld2.getType() );
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
        assertEquals( "MyClass", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "l", fld.getName() );
        assertEquals( "List", fld.getType().getName() );
        assertEquals( 1, fld.getType().getActualArgumentTypes().size() );
        assertEquals( new TypeDef( "String" ), fld.getType().getActualArgumentTypes().get( 0 ) );
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
        assertEquals( "MyClass", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "l", fld.getName() );
        assertEquals( "List", fld.getType().getName() );
        assertEquals( 1, fld.getType().getActualArgumentTypes().size() );
        assertEquals( new WildcardTypeDef( new TypeDef( "A" ), "extends" ), fld.getType().getActualArgumentTypes().get( 0 ) );
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
        assertEquals( "MyClass", cls.getName() );

        MethodDef method = methodCaptor.getValue();
        assertEquals( "doStuff", method.getName() );
        assertEquals( new TypeDef("void"), method.getReturnType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify(builder, times(3) ).beginClass( classCaptor.capture() );
        verify(builder, times(3)).endClass();

        assertEquals( "MyClass", classCaptor.getAllValues().get( 0 ).getName() );
        assertEquals( "InnerCls", classCaptor.getAllValues().get( 1 ).getName() );
        assertEquals( "AnotherClass", classCaptor.getAllValues().get( 2 ).getName() );
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
        Parser parser = new Parser( lexer, builder );
        parser.parse();

        // expectations
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);
        
        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();
        
        ClassDef cls = classCaptor.getValue();
        assertEquals( "x", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( new TypeDef("int"), fld.getType() );
        assertEquals( 0, fld.getDimensions() );
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
        assertEquals( "x", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( 0, fld.getDimensions() );
        assertEquals( "int", fld.getType().getName() );
        assertEquals( 1, fld.getType().getDimensions() );
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
        assertEquals( "x", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( 0, fld.getDimensions() );
        assertEquals( "int", fld.getType().getName() );
        assertEquals( 2, fld.getType().getDimensions() );
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
        assertEquals( "x", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( 1, fld.getDimensions() );
        assertEquals( new TypeDef( "int", 0 ), fld.getType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<FieldDef> fieldCaptor = ArgumentCaptor.forClass(FieldDef.class);

        // verify
        verify( builder ).beginClass( classCaptor.capture() );
        verify( builder ).beginField( fieldCaptor.capture() );
        verify( builder ).endField();
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "x", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( 1, fld.getDimensions() );
        assertEquals( "int", fld.getType().getName() );
        assertEquals( 2, fld.getType().getDimensions() );
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
        assertEquals( "x", cls.getName() );
        FieldDef fld = fieldCaptor.getAllValues().get( 0 );
        assertEquals( "count", fld.getName() );
        assertEquals( 1, fld.getDimensions() );
        assertEquals( new TypeDef( "int" ), fld.getType() );
        FieldDef fld2 = fieldCaptor.getAllValues().get( 1 );
        assertEquals( "count2", fld2.getName() );
        assertEquals( 0, fld2.getDimensions() );
        assertEquals( new TypeDef( "int" ), fld2.getType() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        assertEquals( "x", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "count", mth.getName() );
        assertEquals( new TypeDef("int"), mth.getReturnType() );
        assertEquals( 0, mth.getDimensions() );
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "x", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "count", mth.getName() );
        assertEquals( "int", mth.getReturnType().getName() );
        assertEquals( 1, mth.getReturnType().getDimensions());
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
        ArgumentCaptor<ClassDef> classCaptor = ArgumentCaptor.forClass( ClassDef.class );
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass( MethodDef.class );

        // verify
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).beginMethod();
        verify(builder).endMethod( methodCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "x", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "count", mth.getName() );
        assertEquals( 1, mth.getDimensions() );
        assertEquals( new TypeDef("int"), mth.getReturnType() );
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
        assertEquals( "x", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "count", mth.getName() );
        assertEquals( new TypeDef( "int", 1 ), mth.getReturnType() );
        FieldDef prm = parameterCaptor.getValue();
        assertEquals( "p1", prm.getName() );
        assertEquals( new TypeDef( "int" ), prm.getType() );
        assertEquals( 0, prm.getDimensions() );
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
        assertEquals( "x", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "count", mth.getName() );
        assertEquals( new TypeDef( "int" ), mth.getReturnType() );
        assertEquals( 0, mth.getDimensions() );
        FieldDef prm = parameterCaptor.getValue();
        assertEquals( "p1", prm.getName() );
        assertEquals( 0, prm.getDimensions() );
        assertEquals( "int", prm.getType().getName() );
        assertEquals( 1, prm.getType().getDimensions() );
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
        assertEquals( "x", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "count", mth.getName() );
        assertEquals( new TypeDef("int", 1), mth.getReturnType() );
        FieldDef prm = parameterCaptor.getValue();
        assertEquals( "p1", prm.getName() );
        assertEquals( "int", prm.getType().getName() );
        assertEquals( 2, prm.getType().getDimensions() );

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
        assertEquals( "x", cls.getName() );
        MethodDef mth = methodCaptor.getValue();
        assertEquals( "doStuff", mth.getName() );
        assertEquals( new TypeDef( "void" ), mth.getReturnType() );
        FieldDef prm = parameterCaptor.getValue();
        assertEquals( "stuff", prm.getName() );
        assertEquals( new TypeDef( "int" ), prm.getType() );
        assertEquals( 0, prm.getDimensions() );
        assertEquals( true, prm.isVarArgs() );
    }

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
        assertEquals( "x", cls.getName() );
        assertEquals( ClassDef.ENUM, cls.getType() );
        FieldDef fld0 = f.getAllValues().get( 0 );
        assertEquals( "a", fld0.getName() );
        assertEquals( new TypeDef( "x" ), fld0.getType() ); // bug @todo fixme
        assertEquals( "", fld0.getBody() );
        FieldDef fld1 = f.getAllValues().get( 1 );
        assertEquals( "someField", fld1.getName() );
        assertEquals( new TypeDef( "int" ), fld1.getType() );
        assertEquals( null, fld1.getBody() );
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
        assertEquals( "x", cls.getName() );
        assertEquals( ClassDef.ENUM, cls.getType() );
        FieldDef fld = f.getValue();
        assertEquals( "a", fld.getName() );
        assertEquals( new TypeDef( "x" ), fld.getType() ); //bug @todo fixme
        assertEquals( "" , fld.getBody() );
    }
    
    // QDOX-266
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
        assertEquals( true, cls.getModifiers().contains( "public" ) );
        assertEquals( ClassDef.ENUM, cls.getType() );
        assertEquals( "MethodLocationOfEnumMethod", cls.getName() );
        FieldDef fld = f.getValue();
        assertEquals( "A", fld.getName() );
        assertEquals( new TypeDef( "MethodLocationOfEnumMethod" ), fld.getType() );
        //ClassDef ecCls = enumConstantClassCaptor.getValue();
        AnnoDef ann = annoCaptor.getValue();
        assertEquals( "Override", ann.getTypeDef().getName() );
        MethodDef mth = methodClassCaptor.getValue();
        assertEquals( "method", mth.getName() );
        
//        Class methodLocationOfEnumMethod = MethodLocationOfEnumMethod.class;
//        Field a = methodLocationOfEnumMethod.getField( "A" );
//        assertNotNull( a );
//        assertSame( methodLocationOfEnumMethod, a.getDeclaringClass() );
//        assertSame( methodLocationOfEnumMethod, a.getType() );
//        assertEquals( 2, methodLocationOfEnumMethod.getDeclaredMethods().length);
    }
    
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
        assertEquals( "x", cls.getName() );
        InitDef init = initCaptor.getValue();
        assertTrue( init.isStatic() );
        assertEquals( "//test", init.getBlockContent() );
    }
    
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
        assertEquals( "x", cls.getName() );
        InitDef init = initCaptor.getValue();
        assertFalse( init.isStatic() );
        assertEquals( "//test", init.getBlockContent() );
    }
    
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
        assertEquals( "Foo", annoCaptor.getAllValues().get( 0 ).getTypeDef().getName() );
        assertEquals( "1", annoCaptor.getAllValues().get( 0 ).getArgs().get( "value" ).toString());
        assertEquals( "Foo", annoCaptor.getAllValues().get( 1 ).getTypeDef().getName() );
        assertEquals( "2", annoCaptor.getAllValues().get( 1 ).getArgs().get( "value" ).toString());
        assertEquals( "Bar", annoCaptor.getAllValues().get( 2 ).getTypeDef().getName() );
        assertEquals( null, annoCaptor.getAllValues().get( 2 ).getArgs().get( "value" ));
        
        ArgumentCaptor<ModuleDef> moduleCaptor = ArgumentCaptor.forClass( ModuleDef.class );
        verify( builder ).setModule( moduleCaptor.capture() );
        assertEquals( "M.N", moduleCaptor.getValue().getName() );
        assertEquals( true, moduleCaptor.getValue().isOpen() );
        
        ArgumentCaptor<ModuleDef.RequiresDef> requiresCaptor = ArgumentCaptor.forClass( ModuleDef.RequiresDef.class );
        verify( builder, times(4) ).addRequires( requiresCaptor.capture() );
        assertEquals( "A.B", requiresCaptor.getAllValues().get(0).getName() );
        assertEquals( false, requiresCaptor.getAllValues().get(0).getModifiers().contains( "transitive" ) );
        assertEquals( false, requiresCaptor.getAllValues().get(0).getModifiers().contains( "static" ) );
        
        assertEquals( "C.D", requiresCaptor.getAllValues().get(1).getName() );
        assertEquals( true, requiresCaptor.getAllValues().get(1).getModifiers().contains( "transitive" ) );
        assertEquals( false, requiresCaptor.getAllValues().get(1).getModifiers().contains( "static" ) );
        
        assertEquals( "E.F", requiresCaptor.getAllValues().get(2).getName() );
        assertEquals( false, requiresCaptor.getAllValues().get(2).getModifiers().contains( "transitive" ) );
        assertEquals( true, requiresCaptor.getAllValues().get(2).getModifiers().contains( "static" ) );
        
        assertEquals( "G.H", requiresCaptor.getAllValues().get(3).getName() );
        assertEquals( true, requiresCaptor.getAllValues().get(3).getModifiers().contains( "transitive" ) );
        assertEquals( true, requiresCaptor.getAllValues().get(3).getModifiers().contains( "static" ) );
        
        ArgumentCaptor<ModuleDef.ExportsDef> exportsCaptor = ArgumentCaptor.forClass( ModuleDef.ExportsDef.class );
        verify( builder, times(2) ).addExports( exportsCaptor.capture() );
        assertEquals( "P.Q", exportsCaptor.getAllValues().get( 0 ).getSource() );
        assertEquals( 0, exportsCaptor.getAllValues().get( 0 ).getTargets().size() );

        assertEquals( "R.S", exportsCaptor.getAllValues().get( 1 ).getSource() );
        assertEquals( 2, exportsCaptor.getAllValues().get( 1 ).getTargets().size() );
        assertEquals( true, exportsCaptor.getAllValues().get( 1 ).getTargets().contains( "T1.U1" ));
        assertEquals( true, exportsCaptor.getAllValues().get( 1 ).getTargets().contains( "T2.U2" ));
        
        ArgumentCaptor<ModuleDef.OpensDef> opensCaptor = ArgumentCaptor.forClass( ModuleDef.OpensDef.class );
        verify( builder, times(2) ).addOpens( opensCaptor.capture() );
        assertEquals( "P.Q", opensCaptor.getAllValues().get( 0 ).getSource() );
        assertEquals( 0, opensCaptor.getAllValues().get( 0 ).getTargets().size() );

        assertEquals( "R.S", opensCaptor.getAllValues().get( 1 ).getSource() );
        assertEquals( 2, opensCaptor.getAllValues().get( 1 ).getTargets().size() );
        assertEquals( true, opensCaptor.getAllValues().get( 1 ).getTargets().contains( "T1.U1" ));
        assertEquals( true, opensCaptor.getAllValues().get( 1 ).getTargets().contains( "T2.U2" ));
        
        ArgumentCaptor<ModuleDef.UsesDef> usesCaptor = ArgumentCaptor.forClass( ModuleDef.UsesDef.class );
        verify( builder ).addUses( usesCaptor.capture() );
        assertEquals( "V.W", usesCaptor.getValue().getService().getName() );
        
        ArgumentCaptor<ModuleDef.ProvidesDef> providesCaptor = ArgumentCaptor.forClass( ModuleDef.ProvidesDef.class );
        verify( builder, times(1) ).addProvides( providesCaptor.capture() );
        assertEquals( "X.Y", providesCaptor.getAllValues().get(0).getService().getName() );
        assertEquals( "Z1.Z2", providesCaptor.getAllValues().get(0).getImplementations().get(0).getName() );
        assertEquals( "Z3.Z4", providesCaptor.getAllValues().get(0).getImplementations().get(1).getName() );
    }
    
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
            when( lexer.lex() ).thenAnswer( new ReturnsElementsOf( lexValues ) );
            when( lexer.text() ).thenAnswer( new ReturnsElementsOf( textValues ) );
            when( lexer.getCodeBody() ).thenAnswer( new ReturnsElementsOf( codeBodyValues ) );
            when( lexer.getLine() ).thenReturn( -1 );
        }
    }
}