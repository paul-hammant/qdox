package com.thoughtworks.qdox.parser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.answers.ReturnsElementsOf;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
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
        verify(builder).addField( fieldCaptor.capture() );
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
        verify( builder ).addField( fieldCaptor.capture() );
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
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).addField( fieldCaptor.capture() );
        verify(builder).endClass();
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
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder, times(2)).addField( fieldCaptor.capture() );
        verify(builder).endClass();
        
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
        verify( builder ).addField( fieldCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "l", fld.getName() );
        assertEquals( "List", fld.getType().name );
        assertEquals( 1, fld.getType().actualArgumentTypes.size() );
        assertEquals( new TypeDef( "String" ), fld.getType().actualArgumentTypes.get( 0 ) );
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
        verify( builder ).addField( fieldCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "MyClass", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "l", fld.getName() );
        assertEquals( "List", fld.getType().name );
        assertEquals( 1, fld.getType().actualArgumentTypes.size() );
        assertEquals( new WildcardTypeDef( new TypeDef( "A" ), "extends" ), fld.getType().actualArgumentTypes.get( 0 ) );
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
        ArgumentCaptor<MethodDef> methodCaptor = ArgumentCaptor.forClass(MethodDef.class);

        // verify
        verify(builder).beginClass( classCaptor.capture() );
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
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).addField( fieldCaptor.capture() );
        verify(builder).endClass();
        
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
        verify( builder ).addField( fieldCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "x", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( 0, fld.getDimensions() );
        assertEquals( "int", fld.getType().name );
        assertEquals( 1, fld.getType().dimensions );
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
        verify( builder ).addField( fieldCaptor.capture() );
        verify( builder ).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "x", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( 0, fld.getDimensions() );
        assertEquals( "int", fld.getType().name );
        assertEquals( 2, fld.getType().dimensions );
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
        verify( builder ).addField( fieldCaptor.capture() );
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
        verify(builder).beginClass( classCaptor.capture() );
        verify(builder).addField( fieldCaptor.capture() );
        verify(builder).endClass();

        ClassDef cls = classCaptor.getValue();
        assertEquals( "x", cls.getName() );
        FieldDef fld = fieldCaptor.getValue();
        assertEquals( "count", fld.getName() );
        assertEquals( 1, fld.getDimensions() );
        assertEquals( "int", fld.getType().name );
        assertEquals( 2, fld.getType().dimensions );
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
        verify( builder, times( 2 ) ).addField( fieldCaptor.capture() );
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
        assertEquals( "int", mth.getReturnType().name );
        assertEquals( 1, mth.getReturnType().dimensions);
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
        assertEquals( "int", prm.getType().name );
        assertEquals( 1, prm.getType().dimensions );
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
        assertEquals( "int", prm.getType().name );
        assertEquals( 2, prm.getType().dimensions );

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
        setupLex(Parser.PARENBLOCK);
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
        verify( builder, times( 2 ) ).addField( f.capture() );
        verify( builder ).endClass();

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
        verify(builder).beginClass( classCaptor.capture() );
//      verify(mockBuilder).beginConstructor();
//      verify(mockBuilder).endConstructor(mth);
        verify(builder).addField( f.capture() );
        verify(builder).endClass();
        
        ClassDef cls = classCaptor.getValue();
        assertEquals( "x", cls.getName() );
        assertEquals( ClassDef.ENUM, cls.getType() );
        FieldDef fld = f.getValue();
        assertEquals( "a", fld.getName() );
        assertEquals( new TypeDef( "x" ), fld.getType() ); //bug @todo fixme
        assertEquals( "" , fld.getBody() );
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