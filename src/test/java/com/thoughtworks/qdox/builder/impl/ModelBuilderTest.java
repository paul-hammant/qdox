package com.thoughtworks.qdox.builder.impl;

import com.thoughtworks.qdox.library.ClassNameLibrary;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.parser.structs.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class ModelBuilderTest {

    private ModelBuilder builder;
    private DocletTagFactory docletTagFactory;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        docletTagFactory = mock( DocletTagFactory.class );
        builder = new ModelBuilder( new ClassNameLibrary(), docletTagFactory );
    }

    @Test
    public void testNumberOfClassesGrows() {
        Assertions.assertEquals(0, builder.getSource().getClasses().size());
        builder.beginClass(new ClassDef());
        builder.endClass();
        Assertions.assertEquals(1, builder.getSource().getClasses().size());
        builder.beginClass(new ClassDef());
        builder.endClass();
        Assertions.assertEquals(2, builder.getSource().getClasses().size());
    }

    @Test
    public void testSimpleClass() {
        ClassDef cls = new ClassDef("Thingy");
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef("ThingyThing");
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals("Thingy", source.getClasses().get(0).getName());
        Assertions.assertEquals("ThingyThing", source.getClasses().get(1).getName());
        Assertions.assertEquals(source, source.getClasses().get(0).getParentSource());
    }

    @Test
    public void testInterface() {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.setType( ClassDef.INTERFACE );
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals(false, source.getClasses().get(0).isInterface());
        Assertions.assertEquals(true, source.getClasses().get(1).isInterface());
    }

    @Test
    public void testEnum() {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.setType( ClassDef.ENUM );
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals(false, source.getClasses().get(0).isEnum());
        Assertions.assertEquals(true, source.getClasses().get(1).isEnum());
    }

    @Test
    public void testAnnotationType() {
        ClassDef cls = new ClassDef();
        cls.setType( ClassDef.ANNOTATION_TYPE );
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();
        
        Assertions.assertEquals(1, source.getClasses().size());
    }

    @Test
    public void testClassExtends() {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.getExtends().add(new TypeDef("Another"));
        builder.beginClass(cls2);
        builder.endClass();
        builder.addImport("com.thoughtworks.*");

        JavaSource source = builder.getSource();

        Assertions.assertEquals("java.lang.Object", source.getClasses().get(0).getSuperClass().getValue());
        Assertions.assertEquals("Another", source.getClasses().get(1).getSuperClass().getValue());

        Assertions.assertEquals(0, source.getClasses().get(0).getImplements().size());
        Assertions.assertEquals(0, source.getClasses().get(1).getImplements().size());

// With qdox-2.0 it's not possible to inspect the source during parsing, so this has become an invalid test
/* 
        //Add another class and see if Another gets resolved
        builder.addPackage(new PackageDef("com.thoughtworks"));
        ClassDef anotherCls = new ClassDef();
        anotherCls.name = "Another";
        builder.beginClass(anotherCls);
        builder.endClass();

        assertEquals("com.thoughtworks.Another", source.getClasses()[1].getSuperClass().getValue());
*/
    }

    @Test
    public void testInterfaceExtends() {
        ClassDef cls = new ClassDef();
        cls.setType( ClassDef.INTERFACE );
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.setType( ClassDef.INTERFACE );
        cls2.getExtends().add(new TypeDef("Another"));
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals(0, source.getClasses().get(0).getImplements().size());
        Assertions.assertEquals(1, source.getClasses().get(1).getImplements().size());
        Assertions.assertEquals("Another", source.getClasses().get(1).getImplements().get(0).getValue());

        Assertions.assertNull(source.getClasses().get(0).getSuperClass());
        Assertions.assertNull(source.getClasses().get(1).getSuperClass());
    }

    @Test
    public void testInterfaceExtendsMultiple() {
        ClassDef cls = new ClassDef();
        cls.setType( ClassDef.INTERFACE );
        cls.getExtends().add(new TypeDef("Another"));
        cls.getExtends().add(new TypeDef("java.io.Serializable"));
        cls.getExtends().add(new TypeDef("BottleOpener"));
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals(3, source.getClasses().get(0).getImplements().size());
        Assertions.assertEquals("Another", source.getClasses().get(0).getImplements().get(0).getValue());
        Assertions.assertEquals("java.io.Serializable", source.getClasses().get(0).getImplements().get(1).getValue());
        Assertions.assertEquals("BottleOpener", source.getClasses().get(0).getImplements().get(2).getValue());

        Assertions.assertNull(source.getClasses().get(0).getSuperClass());
    }

    @Test
    public void testClassImplements() {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.getImplements().add(new TypeDef("SomeInterface"));
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals(0, source.getClasses().get(0).getImplements().size());
        Assertions.assertEquals(1, source.getClasses().get(1).getImplements().size());

        Assertions.assertEquals("SomeInterface", source.getClasses().get(1).getImplements().get(0).getValue());

        Assertions.assertEquals("java.lang.Object", source.getClasses().get(0).getSuperClass().getValue());
        Assertions.assertEquals("java.lang.Object", source.getClasses().get(1).getSuperClass().getValue());
    }

    @Test
    public void testClassImplementsMultiple() {
        ClassDef cls = new ClassDef();
        cls.getImplements().add(new TypeDef("SomeInterface"));
        cls.getImplements().add(new TypeDef("XX"));
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals(2, source.getClasses().get(0).getImplements().size());

        Assertions.assertEquals("SomeInterface", source.getClasses().get(0).getImplements().get(0).getValue());
        Assertions.assertEquals("XX", source.getClasses().get(0).getImplements().get(1).getValue());
    }

    @Test
    public void testClassExtendsAndImplements() {
        ClassDef cls = new ClassDef();
        cls.getExtends().add(new TypeDef("SubClass"));
        cls.getImplements().add(new TypeDef("SomeInterface"));
        cls.getImplements().add(new TypeDef("XX"));
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals(2, source.getClasses().get(0).getImplements().size());

        Assertions.assertEquals("SomeInterface", source.getClasses().get(0).getImplements().get(0).getValue());
        Assertions.assertEquals("XX", source.getClasses().get(0).getImplements().get(1).getValue());

        Assertions.assertEquals("SubClass", source.getClasses().get(0).getSuperClass().getValue());
    }

    @Test
    public void testClassModifiers() {
        builder.beginClass(new ClassDef());
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.getModifiers().add("public");
        cls2.getModifiers().add("final");
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals(0, source.getClasses().get(0).getModifiers().size());
        Assertions.assertEquals(2, source.getClasses().get(1).getModifiers().size());

        List<String> modifiers = source.getClasses().get(1).getModifiers();
        Assertions.assertEquals("public", modifiers.get(0));
        Assertions.assertEquals("final", modifiers.get(1));
    }

    @Test
    public void testAddMethodsToCorrectClass() {
        builder.beginClass(new ClassDef());
        builder.beginMethod();
        builder.endMethod(new MethodDef());
        builder.endClass();

        builder.beginClass(new ClassDef());
        builder.beginMethod();
        builder.endMethod(new MethodDef());
        builder.beginMethod();
        builder.endMethod(new MethodDef());
        builder.beginMethod();
        builder.endMethod(new MethodDef());
        builder.endClass();

        builder.beginClass(new ClassDef());
        builder.beginMethod();
        builder.endMethod(new MethodDef());
        builder.beginMethod();
        builder.endMethod(new MethodDef());
        builder.endClass();

        JavaSource source = builder.getSource();
        Assertions.assertEquals(1, source.getClasses().get(0).getMethods().size());
        Assertions.assertEquals(3, source.getClasses().get(1).getMethods().size());
        Assertions.assertEquals(2, source.getClasses().get(2).getMethods().size());
    }

    @Test
    public void testInnerClass() {
        builder.addPackage(new PackageDef("xyz"));

        ClassDef outerDef = new ClassDef("Outer");
        builder.beginClass(outerDef);

        ClassDef innerDef = new ClassDef("Inner");
        builder.beginClass(innerDef);

        MethodDef fooDef = new MethodDef();
        fooDef.setName("foo");
        builder.beginMethod();
        builder.endMethod(fooDef);
        builder.endClass();

        MethodDef barDef = new MethodDef();
        barDef.setName("bar");
        builder.beginMethod();
        builder.endMethod(barDef);
        builder.endClass();

        JavaSource source = builder.getSource();
        Assertions.assertEquals(1, source.getClasses().size());
        JavaClass outerClass = source.getClasses().get(0);
        Assertions.assertEquals("xyz.Outer", outerClass.getFullyQualifiedName());
        Assertions.assertEquals(1, outerClass.getMethods().size());
        Assertions.assertEquals("bar", outerClass.getMethods().get(0).getName());
        Assertions.assertEquals(1, outerClass.getNestedClasses().size());
        JavaClass innerClass = outerClass.getNestedClasses().get(0);
        Assertions.assertEquals("xyz.Outer$Inner", innerClass.getBinaryName());
        Assertions.assertEquals("xyz.Outer.Inner", innerClass.getFullyQualifiedName());
        Assertions.assertEquals(1, innerClass.getMethods().size());
        Assertions.assertEquals("foo", innerClass.getMethods().get(0).getName());
    }

    @Test
    public void testSimpleMethod() {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();
        mth.setName("doSomething");
        mth.setReturnType(new TypeDef("void"));
        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod doSomething = source.getClasses().get(0).getMethods().get(0);
        Assertions.assertEquals("doSomething", doSomething.getName());
        Assertions.assertEquals("void", doSomething.getReturns().getValue());
        Assertions.assertEquals(0, doSomething.getModifiers().size());
        Assertions.assertEquals(0, doSomething.getParameters().size());
        Assertions.assertEquals(0, doSomething.getExceptions().size());
    }

    @Test
    public void testMethodNoArray() {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();
        mth.setName("doSomething");
        mth.setReturnType(new TypeDef("void"));
        mth.setDimensions(0);
        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        Assertions.assertEquals(0, result.getReturns().getDimensions());
    }

    @Test
    public void testMethod1dArray() {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();
        mth.setName("doSomething");
        mth.setReturnType(new TypeDef("void"));
        mth.setDimensions(1);
        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        Assertions.assertEquals(1, result.getReturns().getDimensions());
    }

    @Test
    public void testMethod2dArray() {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();
        mth.setName("doSomething");
        mth.setReturnType(new TypeDef("void"));
        mth.setDimensions(2);
        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        Assertions.assertEquals(2, result.getReturns().getDimensions());
    }

    @Test
    public void testMethodParameters() {
        builder.beginClass(new ClassDef());
        builder.beginMethod();

        MethodDef mth = new MethodDef();

        FieldDef f1 = new FieldDef();
        f1.setName( "count" );
        f1.setType( new TypeDef("int") );
        f1.getModifiers().add("final");
        builder.addParameter( f1 );

        FieldDef f2 = new FieldDef();
        f2.setName( "name" );
        f2.setType( new TypeDef("String") );
        builder.addParameter( f2 );

        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        Assertions.assertEquals(2, result.getParameters().size());
        Assertions.assertEquals("count", result.getParameters().get(0).getName());
        Assertions.assertEquals("int", result.getParameters().get(0).getType().getValue());
        Assertions.assertEquals("name", result.getParameters().get(1).getName());
        Assertions.assertEquals("String", result.getParameters().get(1).getType().getValue());
    }

    @Test
    public void testMethodParametersWithArrays() {
        builder.beginClass(new ClassDef());
        builder.beginMethod();
        MethodDef mth = new MethodDef();

        FieldDef f1 = new FieldDef();
        f1.setName( "count" );
        f1.setType( new TypeDef("int") );
        f1.getModifiers().add("final");
        f1.setDimensions( 1 );
        builder.addParameter( f1 );


        FieldDef f2 = new FieldDef();
        f2.setName( "name" );
        f2.setType( new TypeDef("String") );
        f2.setDimensions( 2 );
        builder.addParameter( f2 );

        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        Assertions.assertEquals(1, result.getParameters().get(0).getJavaClass().getDimensions());
        Assertions.assertEquals(2, result.getParameters().get(1).getJavaClass().getDimensions());
    }

    @Test
    public void testMethodExceptions() {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();

        mth.getExceptions().add(new TypeDef("RuntimeException"));
        mth.getExceptions().add(new TypeDef("java.io.IOException"));

        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        Assertions.assertEquals(2, result.getExceptions().size());
        Assertions.assertEquals("RuntimeException", result.getExceptions().get(0).getValue());
        Assertions.assertEquals("java.io.IOException", result.getExceptions().get(1).getValue());
    }

    @Test
    public void testMethodModifiers() {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();

        mth.getModifiers().add("public");
        mth.getModifiers().add("final");
        mth.getModifiers().add("synchronized");

        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        Assertions.assertEquals(3, result.getModifiers().size());

        List<String> modifiers = result.getModifiers();
        Assertions.assertEquals("public", modifiers.get(0));
        Assertions.assertEquals("final", modifiers.get(1));
        Assertions.assertEquals("synchronized", modifiers.get(2));
    }

    @Test
    public void testSimpleField() {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.setName( "count" );
        fld.setType( new TypeDef("int") );
        builder.beginField(fld);
        builder.endField();
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("count", result.getName());
        Assertions.assertEquals("int", result.getType().getValue());

    }

    @Test
    public void testFieldWithModifiers() {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.getModifiers().add("blah2");
        fld.getModifiers().add("blah");
        builder.beginField(fld);
        builder.endField();
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getModifiers());
        Assertions.assertEquals("blah2", result.getModifiers().get(0));
        Assertions.assertEquals("blah", result.getModifiers().get(1));
    }

    @Test
    public void testFieldNoArray() {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.setName( "count" );
        fld.setType( new TypeDef("int") );
        fld.setDimensions( 0 );
        builder.beginField(fld);
        builder.endField();
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        Assertions.assertEquals(0, result.getType().getDimensions());

    }

    @Test
    public void testField1dArray() {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.setName( "count" );
        fld.setType( new TypeDef("int") );
        fld.setDimensions( 1 );
        
        builder.beginField(fld);
        builder.endField();
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        Assertions.assertEquals(1, result.getType().getDimensions());

    }

    @Test
    public void testField2dArray() {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.setName( "count" );
        fld.setType( new TypeDef("int") );
        fld.setDimensions( 2 );
        builder.beginField(fld);
        builder.endField();
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        Assertions.assertEquals(2, result.getType().getDimensions());
    }

    @Test
    public void testSimpleConstructor() {
        builder.beginClass(new ClassDef());

        MethodDef c1 = new MethodDef();
        c1.setName("MyClass");
        builder.beginConstructor();
        builder.endConstructor(c1);

        MethodDef m1 = new MethodDef();
        m1.setName("method");
        m1.setReturnType(new TypeDef("void"));
        builder.beginMethod();
        builder.endMethod(m1);
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaConstructor result1 = source.getClasses().get(0).getConstructors().get(0);
        JavaMethod result2 = source.getClasses().get(0).getMethods().get(0);

        Assertions.assertNotNull(result1);
        Assertions.assertNotNull(result2);
        Assertions.assertNotNull(result2.getReturns());
    }

    @Test
    public void testJavaDocOnClass() {
        builder.addJavaDoc("Hello");
        builder.beginClass(new ClassDef());
        builder.endClass();

        JavaSource source = builder.getSource();
        Assertions.assertEquals("Hello", source.getClasses().get(0).getComment());
    }

    @Test
    public void testJavaDocSpiradiclyOnManyClasses() {

        builder.addJavaDoc("Hello");
        builder.beginClass(new ClassDef());
        builder.endClass();

        builder.beginClass(new ClassDef());
        builder.endClass();

        builder.addJavaDoc("World");
        builder.beginClass(new ClassDef());
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals("Hello", source.getClasses().get(0).getComment());
        Assertions.assertNull(source.getClasses().get(1).getComment());
        Assertions.assertEquals("World", source.getClasses().get(2).getComment());
    }

    @Test
    public void testJavaDocOnMethod() {
        builder.beginClass(new ClassDef());

        builder.addJavaDoc("Hello");
        builder.beginMethod();
        builder.endMethod(new MethodDef());
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertNull(source.getClasses().get(0).getComment());
        Assertions.assertEquals("Hello", source.getClasses().get(0).getMethods().get(0).getComment());
    }

    @Test
    public void testJavaDocOnField() {
        builder.beginClass(new ClassDef());

        builder.addJavaDoc("Hello");
        builder.beginField(new FieldDef());
        builder.endField();
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertNull(source.getClasses().get(0).getComment());
        Assertions.assertEquals("Hello", source.getClasses().get(0).getFields().get(0).getComment());
    }

    @Test
    public void testJavaDocOnMethodsAndFields() {
        builder.addJavaDoc("Thing");
        builder.beginClass(new ClassDef());

        builder.beginField(new FieldDef());// f0
        builder.endField();

        builder.addJavaDoc("Hello");
        builder.beginMethod();
        builder.endMethod(new MethodDef());//m0

        builder.addJavaDoc("Hello field");
        builder.beginField(new FieldDef());//f1
        builder.endField();

        builder.beginMethod();
        builder.endMethod(new MethodDef());//m1

        builder.addJavaDoc("World");
        builder.beginMethod();
        builder.endMethod(new MethodDef());//m2

        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals("Thing", source.getClasses().get(0).getComment());
        Assertions.assertNull(source.getClasses().get(0).getFields().get(0).getComment());
        Assertions.assertEquals("Hello field", source.getClasses().get(0).getFields().get(1).getComment());
        Assertions.assertEquals("Hello", source.getClasses().get(0).getMethods().get(0).getComment());
        Assertions.assertNull(source.getClasses().get(0).getMethods().get(1).getComment());
        Assertions.assertEquals("World", source.getClasses().get(0).getMethods().get(2).getComment());
    }

    @Test
    public void testDocletTag() {
        builder.addJavaDoc("Hello");
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.beginClass(new ClassDef());
        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals("Hello", source.getClasses().get(0).getComment());
        Assertions.assertEquals(1, source.getClasses().get(0).getTags().size());
        
        verify( docletTagFactory ).createDocletTag( eq("cheese"), eq("is good"), isA( JavaClass.class ), eq(-1) ); 
        verifyNoMoreInteractions( docletTagFactory );
        
//        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
//        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
    }

    @Test
    public void testDocletTagWithNoComment() {
        builder.addJavaDoc(""); // parser will always call this method to signify start of javadoc
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.beginClass(new ClassDef());

        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals("", source.getClasses().get(0).getComment());
        Assertions.assertEquals(1, source.getClasses().get(0).getTags().size());
        
        verify( docletTagFactory ).createDocletTag( eq("cheese"), eq("is good"), isA( JavaClass.class ), eq(-1) );
        verifyNoMoreInteractions( docletTagFactory );

//        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
//        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
    }

    @Test
    public void testMultipleDocletTags() {
        builder.addJavaDoc("Hello");
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.addJavaDocTag(new TagDef("food", "is great"));
        builder.addJavaDocTag(new TagDef("chairs", "are boring"));
        builder.beginClass(new ClassDef());

        builder.endClass();

        JavaSource source = builder.getSource();

        Assertions.assertEquals("Hello", source.getClasses().get(0).getComment());
        Assertions.assertEquals(3, source.getClasses().get(0).getTags().size());
        
        verify( docletTagFactory ).createDocletTag( eq("cheese"), eq("is good"), isA( JavaClass.class ), eq(-1) );
        verify( docletTagFactory ).createDocletTag( eq("food"), eq("is great"), isA( JavaClass.class ), eq(-1) );
        verify( docletTagFactory ).createDocletTag( eq("chairs"), eq("are boring"), isA( JavaClass.class ), eq(-1) );
        verifyNoMoreInteractions( docletTagFactory );
        
//        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
//        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
//        assertEquals("food", source.getClasses().get(0).getTags().get(1).getName());
//        assertEquals("is great", source.getClasses().get(0).getTags().get(1).getValue());
//        assertEquals("chairs", source.getClasses().get(0).getTags().get(2).getName());
//        assertEquals("are boring", source.getClasses().get(0).getTags().get(2).getValue());
    }

    @Test
    public void testDocletTagsOnMethodsAndFields() {
        builder.addJavaDoc("");
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.beginClass(new ClassDef());

        builder.addJavaDoc("");
        builder.addJavaDocTag(new TagDef("food", "is great"));
        builder.beginMethod();
        builder.endMethod(new MethodDef());

        builder.addJavaDoc("");
        builder.addJavaDocTag(new TagDef("chairs", "are boring"));
        builder.beginField(new FieldDef());
        builder.endField();
        builder.endClass();

        verify( docletTagFactory ).createDocletTag( eq("cheese"), eq("is good"), isA( JavaClass.class ), eq(-1) );
        verify( docletTagFactory ).createDocletTag( eq("food"), eq("is great"), isA( JavaMethod.class ), eq(-1) );
        verify( docletTagFactory ).createDocletTag( eq("chairs"), eq("are boring"), isA( JavaField.class ), eq(-1) );
        verifyNoMoreInteractions( docletTagFactory );

//        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
//        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
//        assertEquals("food", source.getClasses().get(0).getMethods().get(0).getTags().get(0).getName());
//        assertEquals("is great", source.getClasses().get(0).getMethods().get(0).getTags().get(0).getValue());
//        assertEquals("chairs", source.getClasses().get(0).getFields().get(0).getTags().get(0).getName());
//        assertEquals("are boring", source.getClasses().get(0).getFields().get(0).getTags().get(0).getValue());
    }

    @Test
    public void testRetrieveJavaSource() {
        builder.beginClass(new ClassDef());
        builder.endClass();

        JavaSource source = builder.getSource();
        Assertions.assertNotNull(source);
    }

    @Test
    public void testJavaSourceClassCount() {
        builder.beginClass(new ClassDef());
        builder.endClass();
        builder.beginClass(new ClassDef());
        builder.endClass();
        builder.beginClass(new ClassDef());
        builder.endClass();
        JavaSource result = builder.getSource();
        Assertions.assertEquals(3, result.getClasses().size());
    }

    @Test
    public void testJavaSourceNoPackage() {
        JavaSource result = builder.getSource();
        Assertions.assertNull(result.getPackage());
    }

    @Test
    public void testJavaSourceWithPackage() {
        builder.addPackage(new PackageDef("com.blah.thing"));
        JavaSource result = builder.getSource();
        Assertions.assertEquals("com.blah.thing", result.getPackage().getName());
    }

    @Test
    public void testJavaSourceNoImports() {
        JavaSource result = builder.getSource();
        Assertions.assertEquals(0, result.getImports().size());
    }

    @Test
    public void testJavaSourceOneImport() {
        builder.addImport("com.blah.Thing");
        JavaSource result = builder.getSource();
        Assertions.assertEquals(1, result.getImports().size());
        Assertions.assertEquals("com.blah.Thing", result.getImports().get(0));
    }

    @Test
    public void testJavaSourceMultipleImports() {
        builder.addImport("com.blah.Thing");
        builder.addImport("java.util.List");
        builder.addImport("org.apache.*");
        JavaSource result = builder.getSource();
        Assertions.assertEquals(3, result.getImports().size());
        Assertions.assertEquals("com.blah.Thing", result.getImports().get(0));
        Assertions.assertEquals("java.util.List", result.getImports().get(1));
        Assertions.assertEquals("org.apache.*", result.getImports().get(2));
    }

    @Test
    public void testStaticInitializers()
    {
        builder.beginClass( new ClassDef( "Foo" ) );
        builder.addInitializer( new InitDef( "//test", true ) );
        builder.addInitializer( new InitDef( null, true ) );
        builder.endClass();
        JavaClass cls = builder.getSource().getClassByName( "Foo" );
        Assertions.assertEquals(2, cls.getInitializers().size());
        Assertions.assertEquals("//test", cls.getInitializers().get( 0 ).getBlockContent());
        Assertions.assertTrue(cls.getInitializers().get( 0 ).isStatic());
        Assertions.assertEquals(null, cls.getInitializers().get( 1 ).getBlockContent());
        Assertions.assertTrue(cls.getInitializers().get( 0 ).isStatic());
    }

    @Test
    public void testInstanceInitializers()
    {
        builder.beginClass( new ClassDef( "Foo" ) );
        builder.addInitializer( new InitDef( "//test", false ) );
        builder.addInitializer( new InitDef( null, false ) );
        builder.endClass();
        JavaClass cls = builder.getSource().getClassByName( "Foo" );
        Assertions.assertEquals(2, cls.getInitializers().size());
        Assertions.assertEquals("//test", cls.getInitializers().get( 0 ).getBlockContent());
        Assertions.assertFalse(cls.getInitializers().get( 0 ).isStatic());
        Assertions.assertEquals(null, cls.getInitializers().get( 1 ).getBlockContent());
        Assertions.assertFalse(cls.getInitializers().get( 0 ).isStatic());
    }

}
