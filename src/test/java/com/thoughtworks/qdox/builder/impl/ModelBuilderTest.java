package com.thoughtworks.qdox.builder.impl;

import static org.mockito.Mockito.*;

import java.util.List;

import junit.framework.TestCase;

import com.thoughtworks.qdox.library.ClassNameLibrary;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.InitDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

public class ModelBuilderTest extends TestCase {

    private ModelBuilder builder;
    private DocletTagFactory docletTagFactory;

    public ModelBuilderTest(String s) {
        super(s);
    }

    @Override
	protected void setUp()
        throws Exception
    {
        docletTagFactory = mock( DocletTagFactory.class );
        builder = new ModelBuilder( new ClassNameLibrary(), docletTagFactory );
    }

    public void testNumberOfClassesGrows() {
        assertEquals(0, builder.getSource().getClasses().size());
        builder.beginClass(new ClassDef());
        builder.endClass();
        assertEquals(1, builder.getSource().getClasses().size());
        builder.beginClass(new ClassDef());
        builder.endClass();
        assertEquals(2, builder.getSource().getClasses().size());
    }

    public void testSimpleClass() {
        ClassDef cls = new ClassDef("Thingy");
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef("ThingyThing");
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals("Thingy", source.getClasses().get(0).getName());
        assertEquals("ThingyThing", source.getClasses().get(1).getName());
        assertEquals(source, source.getClasses().get(0).getParentSource());
    }

    public void testInterface() {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.setType( ClassDef.INTERFACE );
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(false, source.getClasses().get(0).isInterface());
        assertEquals(true, source.getClasses().get(1).isInterface());
    }

    public void testEnum() {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.setType( ClassDef.ENUM );
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(false, source.getClasses().get(0).isEnum());
        assertEquals(true, source.getClasses().get(1).isEnum());
    }

    public void testAnnotationType() {
        ClassDef cls = new ClassDef();
        cls.setType( ClassDef.ANNOTATION_TYPE );
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();
        
        assertEquals(1, source.getClasses().size());
    }

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

        assertEquals("java.lang.Object", source.getClasses().get(0).getSuperClass().getValue());
        assertEquals("Another", source.getClasses().get(1).getSuperClass().getValue());

        assertEquals(0, source.getClasses().get(0).getImplements().size());
        assertEquals(0, source.getClasses().get(1).getImplements().size());

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

        assertEquals(0, source.getClasses().get(0).getImplements().size());
        assertEquals(1, source.getClasses().get(1).getImplements().size());
        assertEquals("Another", source.getClasses().get(1).getImplements().get(0).getValue());

        assertNull(source.getClasses().get(0).getSuperClass());
        assertNull(source.getClasses().get(1).getSuperClass());
    }

    public void testInterfaceExtendsMultiple() {
        ClassDef cls = new ClassDef();
        cls.setType( ClassDef.INTERFACE );
        cls.getExtends().add(new TypeDef("Another"));
        cls.getExtends().add(new TypeDef("java.io.Serializable"));
        cls.getExtends().add(new TypeDef("BottleOpener"));
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(3, source.getClasses().get(0).getImplements().size());
        assertEquals("Another", source.getClasses().get(0).getImplements().get(0).getValue());
        assertEquals("java.io.Serializable", source.getClasses().get(0).getImplements().get(1).getValue());
        assertEquals("BottleOpener", source.getClasses().get(0).getImplements().get(2).getValue());

        assertNull(source.getClasses().get(0).getSuperClass());
    }

    public void testClassImplements() {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.getImplements().add(new TypeDef("SomeInterface"));
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(0, source.getClasses().get(0).getImplements().size());
        assertEquals(1, source.getClasses().get(1).getImplements().size());

        assertEquals("SomeInterface", source.getClasses().get(1).getImplements().get(0).getValue());

        assertEquals("java.lang.Object", source.getClasses().get(0).getSuperClass().getValue());
        assertEquals("java.lang.Object", source.getClasses().get(1).getSuperClass().getValue());
    }

    public void testClassImplementsMultiple() {
        ClassDef cls = new ClassDef();
        cls.getImplements().add(new TypeDef("SomeInterface"));
        cls.getImplements().add(new TypeDef("XX"));
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(2, source.getClasses().get(0).getImplements().size());

        assertEquals("SomeInterface", source.getClasses().get(0).getImplements().get(0).getValue());
        assertEquals("XX", source.getClasses().get(0).getImplements().get(1).getValue());
    }

    public void testClassExtendsAndImplements() {
        ClassDef cls = new ClassDef();
        cls.getExtends().add(new TypeDef("SubClass"));
        cls.getImplements().add(new TypeDef("SomeInterface"));
        cls.getImplements().add(new TypeDef("XX"));
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(2, source.getClasses().get(0).getImplements().size());

        assertEquals("SomeInterface", source.getClasses().get(0).getImplements().get(0).getValue());
        assertEquals("XX", source.getClasses().get(0).getImplements().get(1).getValue());

        assertEquals("SubClass", source.getClasses().get(0).getSuperClass().getValue());
    }

    public void testClassModifiers() {
        builder.beginClass(new ClassDef());
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.getModifiers().add("public");
        cls2.getModifiers().add("final");
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(0, source.getClasses().get(0).getModifiers().size());
        assertEquals(2, source.getClasses().get(1).getModifiers().size());

        List<String> modifiers = source.getClasses().get(1).getModifiers();
        assertEquals("public", modifiers.get(0));
        assertEquals("final", modifiers.get(1));
    }

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
        assertEquals(1, source.getClasses().get(0).getMethods().size());
        assertEquals(3, source.getClasses().get(1).getMethods().size());
        assertEquals(2, source.getClasses().get(2).getMethods().size());
    }

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
        assertEquals(1, source.getClasses().size());
        JavaClass outerClass = source.getClasses().get(0);
        assertEquals("xyz.Outer", outerClass.getFullyQualifiedName());
        assertEquals(1, outerClass.getMethods().size());
        assertEquals("bar", outerClass.getMethods().get(0).getName());
        assertEquals(1, outerClass.getNestedClasses().size());
        JavaClass innerClass = outerClass.getNestedClasses().get(0);
        assertEquals("xyz.Outer$Inner", innerClass.getBinaryName());
        assertEquals("xyz.Outer.Inner", innerClass.getFullyQualifiedName());
        assertEquals(1, innerClass.getMethods().size());
        assertEquals("foo", innerClass.getMethods().get(0).getName());
    }

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
        assertEquals("doSomething", doSomething.getName());
        assertEquals("void", doSomething.getReturns().getValue());
        assertEquals(0, doSomething.getModifiers().size());
        assertEquals(0, doSomething.getParameters().size());
        assertEquals(0, doSomething.getExceptions().size());
    }

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
        assertEquals(0, result.getReturns().getDimensions());
    }

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
        assertEquals(1, result.getReturns().getDimensions());
    }

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
        assertEquals(2, result.getReturns().getDimensions());
    }

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
        assertEquals(2, result.getParameters().size());
        assertEquals("count", result.getParameters().get(0).getName());
        assertEquals("int", result.getParameters().get(0).getType().getValue());
        assertEquals("name", result.getParameters().get(1).getName());
        assertEquals("String", result.getParameters().get(1).getType().getValue());
    }

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
        assertEquals(1, result.getParameters().get(0).getJavaClass().getDimensions());
        assertEquals(2, result.getParameters().get(1).getJavaClass().getDimensions());
    }

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
        assertEquals(2, result.getExceptions().size());
        assertEquals("RuntimeException", result.getExceptions().get(0).getValue());
        assertEquals("java.io.IOException", result.getExceptions().get(1).getValue());
    }

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
        assertEquals(3, result.getModifiers().size());

        List<String> modifiers = result.getModifiers();
        assertEquals("public", modifiers.get(0));
        assertEquals("final", modifiers.get(1));
        assertEquals("synchronized", modifiers.get(2));
    }

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
        assertNotNull(result);
        assertEquals("count", result.getName());
        assertEquals("int", result.getType().getValue());

    }

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
        assertNotNull(result);
        assertNotNull(result.getModifiers());
        assertEquals("blah2", result.getModifiers().get(0));
        assertEquals("blah", result.getModifiers().get(1));
    }

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
        assertEquals(0, result.getType().getDimensions());

    }

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
        assertEquals(1, result.getType().getDimensions());

    }

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
        assertEquals(2, result.getType().getDimensions());
    }

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

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result2.getReturns());
    }

    public void testJavaDocOnClass() {
        builder.addJavaDoc("Hello");
        builder.beginClass(new ClassDef());
        builder.endClass();

        JavaSource source = builder.getSource();
        assertEquals("Hello", source.getClasses().get(0).getComment());
    }

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

        assertEquals("Hello", source.getClasses().get(0).getComment());
        assertNull(source.getClasses().get(1).getComment());
        assertEquals("World", source.getClasses().get(2).getComment());
    }

    public void testJavaDocOnMethod() {
        builder.beginClass(new ClassDef());

        builder.addJavaDoc("Hello");
        builder.beginMethod();
        builder.endMethod(new MethodDef());
        builder.endClass();

        JavaSource source = builder.getSource();

        assertNull(source.getClasses().get(0).getComment());
        assertEquals("Hello", source.getClasses().get(0).getMethods().get(0).getComment());
    }

    public void testJavaDocOnField() {
        builder.beginClass(new ClassDef());

        builder.addJavaDoc("Hello");
        builder.beginField(new FieldDef());
        builder.endField();
        builder.endClass();

        JavaSource source = builder.getSource();

        assertNull(source.getClasses().get(0).getComment());
        assertEquals("Hello", source.getClasses().get(0).getFields().get(0).getComment());
    }

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

        assertEquals("Thing", source.getClasses().get(0).getComment());
        assertNull(source.getClasses().get(0).getFields().get(0).getComment());
        assertEquals("Hello field", source.getClasses().get(0).getFields().get(1).getComment());
        assertEquals("Hello", source.getClasses().get(0).getMethods().get(0).getComment());
        assertNull(source.getClasses().get(0).getMethods().get(1).getComment());
        assertEquals("World", source.getClasses().get(0).getMethods().get(2).getComment());
    }

    public void testDocletTag() {
        builder.addJavaDoc("Hello");
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.beginClass(new ClassDef());
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals("Hello", source.getClasses().get(0).getComment());
        assertEquals(1, source.getClasses().get(0).getTags().size());
        
        verify( docletTagFactory ).createDocletTag( eq("cheese"), eq("is good"), isA( JavaClass.class ), eq(-1) ); 
        verifyNoMoreInteractions( docletTagFactory );
        
//        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
//        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
    }

    public void testDocletTagWithNoComment() {
        builder.addJavaDoc(""); // parser will always call this method to signify start of javadoc
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.beginClass(new ClassDef());

        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals("", source.getClasses().get(0).getComment());
        assertEquals(1, source.getClasses().get(0).getTags().size());
        
        verify( docletTagFactory ).createDocletTag( eq("cheese"), eq("is good"), isA( JavaClass.class ), eq(-1) );
        verifyNoMoreInteractions( docletTagFactory );

//        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
//        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
    }

    public void testMultipleDocletTags() {
        builder.addJavaDoc("Hello");
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.addJavaDocTag(new TagDef("food", "is great"));
        builder.addJavaDocTag(new TagDef("chairs", "are boring"));
        builder.beginClass(new ClassDef());

        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals("Hello", source.getClasses().get(0).getComment());
        assertEquals(3, source.getClasses().get(0).getTags().size());
        
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

    public void testRetrieveJavaSource() {
        builder.beginClass(new ClassDef());
        builder.endClass();

        JavaSource source = builder.getSource();
        assertNotNull(source);
    }

    public void testJavaSourceClassCount() {
        builder.beginClass(new ClassDef());
        builder.endClass();
        builder.beginClass(new ClassDef());
        builder.endClass();
        builder.beginClass(new ClassDef());
        builder.endClass();
        JavaSource result = builder.getSource();
        assertEquals(3, result.getClasses().size());
    }

    public void testJavaSourceNoPackage() {
        JavaSource result = builder.getSource();
        assertNull(result.getPackage());
    }

    public void testJavaSourceWithPackage() {
        builder.addPackage(new PackageDef("com.blah.thing"));
        JavaSource result = builder.getSource();
        assertEquals("com.blah.thing", result.getPackage().getName());
    }

    public void testJavaSourceNoImports() {
        JavaSource result = builder.getSource();
        assertEquals(0, result.getImports().size());
    }

    public void testJavaSourceOneImport() {
        builder.addImport("com.blah.Thing");
        JavaSource result = builder.getSource();
        assertEquals(1, result.getImports().size());
        assertEquals("com.blah.Thing", result.getImports().get(0));
    }

    public void testJavaSourceMultipleImports() {
        builder.addImport("com.blah.Thing");
        builder.addImport("java.util.List");
        builder.addImport("org.apache.*");
        JavaSource result = builder.getSource();
        assertEquals(3, result.getImports().size());
        assertEquals("com.blah.Thing", result.getImports().get(0));
        assertEquals("java.util.List", result.getImports().get(1));
        assertEquals("org.apache.*", result.getImports().get(2));
    }
    
    public void testStaticInitializers()
    {
        builder.beginClass( new ClassDef( "Foo" ) );
        builder.addInitializer( new InitDef( "//test", true ) );
        builder.addInitializer( new InitDef( null, true ) );
        builder.endClass();
        JavaClass cls = builder.getSource().getClassByName( "Foo" );
        assertEquals( 2, cls.getInitializers().size() );
        assertEquals( "//test", cls.getInitializers().get( 0 ).getBlockContent() );
        assertTrue( cls.getInitializers().get( 0 ).isStatic() );
        assertEquals( null, cls.getInitializers().get( 1 ).getBlockContent() );
        assertTrue(cls.getInitializers().get( 0 ).isStatic() );
    }

    public void testInstanceInitializers()
    {
        builder.beginClass( new ClassDef( "Foo" ) );
        builder.addInitializer( new InitDef( "//test", false ) );
        builder.addInitializer( new InitDef( null, false ) );
        builder.endClass();
        JavaClass cls = builder.getSource().getClassByName( "Foo" );
        assertEquals( 2, cls.getInitializers().size() );
        assertEquals( "//test", cls.getInitializers().get( 0 ).getBlockContent() );
        assertFalse( cls.getInitializers().get( 0 ).isStatic() );
        assertEquals( null, cls.getInitializers().get( 1 ).getBlockContent() );
        assertFalse( cls.getInitializers().get( 0 ).isStatic() );
    }

}
