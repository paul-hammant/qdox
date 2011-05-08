package com.thoughtworks.qdox.builder;

import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.thoughtworks.qdox.library.ClassNameLibrary;
import com.thoughtworks.qdox.model.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.DefaultJavaParameter;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

public class ModelBuilderTest extends TestCase {

    private ModelBuilder builder;

    public ModelBuilderTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        builder = new ModelBuilder(new ClassNameLibrary(), new DefaultDocletTagFactory());
    }

    public void testNumberOfClassesGrows() throws Exception {
        assertEquals(0, builder.getSource().getClasses().size());
        builder.beginClass(new ClassDef());
        builder.endClass();
        assertEquals(1, builder.getSource().getClasses().size());
        builder.beginClass(new ClassDef());
        builder.endClass();
        assertEquals(2, builder.getSource().getClasses().size());
    }

    public void testSimpleClass() throws Exception {
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

    public void testInterface() throws Exception {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.type = ClassDef.INTERFACE;
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(false, source.getClasses().get(0).isInterface());
        assertEquals(true, source.getClasses().get(1).isInterface());
    }

    public void testEnum() throws Exception {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.type = ClassDef.ENUM;
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(false, source.getClasses().get(0).isEnum());
        assertEquals(true, source.getClasses().get(1).isEnum());
    }

    public void testAnnotationType() throws Exception {
        ClassDef cls = new ClassDef();
        cls.type = ClassDef.ANNOTATION_TYPE;
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();
        
        assertEquals(1, source.getClasses().size());
    }

    public void testClassExtends() throws Exception {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.extendz.add(new TypeDef("Another"));
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

    public void testInterfaceExtends() throws Exception {
        ClassDef cls = new ClassDef();
        cls.type = ClassDef.INTERFACE;
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.type = ClassDef.INTERFACE;
        cls2.extendz.add(new TypeDef("Another"));
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(0, source.getClasses().get(0).getImplements().size());
        assertEquals(1, source.getClasses().get(1).getImplements().size());
        assertEquals("Another", source.getClasses().get(1).getImplements().get(0).getValue());

        assertNull(source.getClasses().get(0).getSuperClass());
        assertNull(source.getClasses().get(1).getSuperClass());
    }

    public void testInterfaceExtendsMultiple() throws Exception {
        ClassDef cls = new ClassDef();
        cls.type = ClassDef.INTERFACE;
        cls.extendz.add(new TypeDef("Another"));
        cls.extendz.add(new TypeDef("java.io.Serializable"));
        cls.extendz.add(new TypeDef("BottleOpener"));
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();

        // sorted
        Collections.sort(source.getClasses().get(0).getImplements());
        assertEquals(3, source.getClasses().get(0).getImplements().size());
        assertEquals("Another", source.getClasses().get(0).getImplements().get(0).getValue());
        assertEquals("BottleOpener", source.getClasses().get(0).getImplements().get(1).getValue());
        assertEquals("java.io.Serializable", source.getClasses().get(0).getImplements().get(2).getValue());

        assertNull(source.getClasses().get(0).getSuperClass());
    }

    public void testClassImplements() throws Exception {
        ClassDef cls = new ClassDef();
        builder.beginClass(cls);
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.implementz.add(new TypeDef("SomeInterface"));
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(0, source.getClasses().get(0).getImplements().size());
        assertEquals(1, source.getClasses().get(1).getImplements().size());

        assertEquals("SomeInterface", source.getClasses().get(1).getImplements().get(0).getValue());

        assertEquals("java.lang.Object", source.getClasses().get(0).getSuperClass().getValue());
        assertEquals("java.lang.Object", source.getClasses().get(1).getSuperClass().getValue());
    }

    public void testClassImplementsMultiple() throws Exception {
        ClassDef cls = new ClassDef();
        cls.implementz.add(new TypeDef("SomeInterface"));
        cls.implementz.add(new TypeDef("XX"));
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(2, source.getClasses().get(0).getImplements().size());

        Collections.sort(source.getClasses().get(0).getImplements());
        assertEquals("SomeInterface", source.getClasses().get(0).getImplements().get(0).getValue());
        assertEquals("XX", source.getClasses().get(0).getImplements().get(1).getValue());
    }

    public void testClassExtendsAndImplements() throws Exception {
        ClassDef cls = new ClassDef();
        cls.extendz.add(new TypeDef("SubClass"));
        cls.implementz.add(new TypeDef("SomeInterface"));
        cls.implementz.add(new TypeDef("XX"));
        builder.beginClass(cls);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(2, source.getClasses().get(0).getImplements().size());

        Collections.sort(source.getClasses().get(0).getImplements());
        assertEquals("SomeInterface", source.getClasses().get(0).getImplements().get(0).getValue());
        assertEquals("XX", source.getClasses().get(0).getImplements().get(1).getValue());

        assertEquals("SubClass", source.getClasses().get(0).getSuperClass().getValue());
    }

    public void testClassModifiers() throws Exception {
        builder.beginClass(new ClassDef());
        builder.endClass();

        ClassDef cls2 = new ClassDef();
        cls2.modifiers.add("public");
        cls2.modifiers.add("final");
        builder.beginClass(cls2);
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals(0, source.getClasses().get(0).getModifiers().size());
        assertEquals(2, source.getClasses().get(1).getModifiers().size());

        // sorted
        List<String> modifiers = source.getClasses().get(1).getModifiers();
        Collections.sort(modifiers);
        assertEquals("final", modifiers.get(0));
        assertEquals("public", modifiers.get(1));
    }

    public void testAddMethodsToCorrectClass() throws Exception {
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

    public void testInnerClass() throws Exception {
        builder.addPackage(new PackageDef("xyz"));

        ClassDef outerDef = new ClassDef("Outer");
        builder.beginClass(outerDef);

        ClassDef innerDef = new ClassDef("Inner");
        builder.beginClass(innerDef);

        MethodDef fooDef = new MethodDef();
        fooDef.name = "foo";
        builder.beginMethod();
        builder.endMethod(fooDef);
        builder.endClass();

        MethodDef barDef = new MethodDef();
        barDef.name = "bar";
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
        assertEquals("xyz.Outer$Inner", innerClass.getFullyQualifiedName());
        assertEquals(1, innerClass.getMethods().size());
        assertEquals("foo", innerClass.getMethods().get(0).getName());
    }

    public void testSimpleMethod() throws Exception {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod doSomething = source.getClasses().get(0).getMethods().get(0);
        assertEquals("doSomething", doSomething.getName());
        assertEquals("void", doSomething.getReturns().getValue());
        JavaSource parentSource = doSomething.getReturns().getJavaClassParent().getParentSource();
        assertSame(source, parentSource);
        assertEquals(0, doSomething.getModifiers().size());
        assertEquals(0, doSomething.getParameters().size());
        assertEquals(0, doSomething.getExceptions().size());
    }

    public void testMethodNoArray() throws Exception {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        mth.dimensions = 0;
        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        assertEquals(0, result.getReturns().getDimensions());
    }

    public void testMethod1dArray() throws Exception {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        mth.dimensions = 1;
        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        assertEquals(1, result.getReturns().getDimensions());
    }

    public void testMethod2dArray() throws Exception {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();
        mth.name = "doSomething";
        mth.returnType = new TypeDef("void");
        mth.dimensions = 2;
        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        assertEquals(2, result.getReturns().getDimensions());
    }

    public void testMethodParameters() throws Exception {
        builder.beginClass(new ClassDef());
        builder.beginMethod();

        MethodDef mth = new MethodDef();

        FieldDef f1 = new FieldDef();
        f1.name = "count";
        f1.type = new TypeDef("int");
        f1.modifiers.add("final");
        builder.addParameter( f1 );

        FieldDef f2 = new FieldDef();
        f2.name = "name";
        f2.type = new TypeDef("String");
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

    public void testMethodParametersWithArrays() throws Exception {
        builder.beginClass(new ClassDef());
        builder.beginMethod();
        MethodDef mth = new MethodDef();

        FieldDef f1 = new FieldDef();
        f1.name = "count";
        f1.type = new TypeDef("int");
        f1.modifiers.add("final");
        f1.dimensions = 1;
        builder.addParameter( f1 );


        FieldDef f2 = new FieldDef();
        f2.name = "name";
        f2.type = new TypeDef("String");
        f2.dimensions = 2;
        builder.addParameter( f2 );

        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        assertEquals(1, result.getParameters().get(0).getType().getDimensions());
        assertEquals(2, result.getParameters().get(1).getType().getDimensions());
    }

    public void testMethodExceptions() throws Exception {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();

        mth.exceptions.add(new TypeDef("RuntimeException"));
        mth.exceptions.add(new TypeDef("java.io.IOException"));

        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        assertEquals(2, result.getExceptions().size());
        // sorted
        Collections.sort( result.getExceptions() );
        assertEquals("RuntimeException", result.getExceptions().get(0).getValue());
        assertEquals("java.io.IOException", result.getExceptions().get(1).getValue());
    }

    public void testMethodModifiers() throws Exception {
        builder.beginClass(new ClassDef());
        MethodDef mth = new MethodDef();

        mth.modifiers.add("public");
        mth.modifiers.add("final");
        mth.modifiers.add("synchronized");

        builder.beginMethod();
        builder.endMethod(mth);
        builder.endClass();

        JavaSource source = builder.getSource();
        JavaMethod result = source.getClasses().get(0).getMethods().get(0);
        assertEquals(3, result.getModifiers().size());
        // sorted
        List<String> modifiers = result.getModifiers();
        Collections.sort(modifiers);
        assertEquals("final", modifiers.get(0));
        assertEquals("public", modifiers.get(1));
        assertEquals("synchronized", modifiers.get(2));
    }

    public void testSimpleField() throws Exception {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        builder.addField(fld);
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        assertNotNull(result);
        assertEquals("count", result.getName());
        assertEquals("int", result.getType().getValue());

    }

    public void testFieldWithModifiers() throws Exception {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.modifiers.add("blah2");
        fld.modifiers.add("blah");
        builder.addField(fld);
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        assertNotNull(result);
        assertNotNull(result.getModifiers());
        Collections.sort(result.getModifiers());
        assertEquals("blah", result.getModifiers().get(0));
        assertEquals("blah2", result.getModifiers().get(1));
    }

    public void testFieldNoArray() throws Exception {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.dimensions = 0;
        builder.addField(fld);
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        assertEquals(0, result.getType().getDimensions());

    }

    public void testField1dArray() throws Exception {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.dimensions = 1;
        builder.addField(fld);
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        assertEquals(1, result.getType().getDimensions());

    }

    public void testField2dArray() throws Exception {
        builder.beginClass(new ClassDef());

        FieldDef fld = new FieldDef();
        fld.name = "count";
        fld.type = new TypeDef("int");
        fld.dimensions = 2;
        builder.addField(fld);
        builder.endClass();

        JavaSource source = builder.getSource();

        JavaField result = source.getClasses().get(0).getFields().get(0);
        assertEquals(2, result.getType().getDimensions());
    }

    public void testSimpleConstructor() throws Exception {
        builder.beginClass(new ClassDef());

        MethodDef c1 = new MethodDef();
        c1.name = "MyClass";
        builder.beginConstructor();
        builder.endConstructor(c1);

        MethodDef m1 = new MethodDef();
        m1.name = "method";
        m1.returnType = new TypeDef("void");
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

    public void testJavaDocOnClass() throws Exception {
        builder.addJavaDoc("Hello");
        builder.beginClass(new ClassDef());
        builder.endClass();

        JavaSource source = builder.getSource();
        assertEquals("Hello", source.getClasses().get(0).getComment());
    }

    public void testJavaDocSpiradiclyOnManyClasses() throws Exception {

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

    public void testJavaDocOnMethod() throws Exception {
        builder.beginClass(new ClassDef());

        builder.addJavaDoc("Hello");
        builder.beginMethod();
        builder.endMethod(new MethodDef());
        builder.endClass();

        JavaSource source = builder.getSource();

        assertNull(source.getClasses().get(0).getComment());
        assertEquals("Hello", source.getClasses().get(0).getMethods().get(0).getComment());
    }

    public void testJavaDocOnField() throws Exception {
        builder.beginClass(new ClassDef());

        builder.addJavaDoc("Hello");
        builder.addField(new FieldDef());
        builder.endClass();

        JavaSource source = builder.getSource();

        assertNull(source.getClasses().get(0).getComment());
        assertEquals("Hello", source.getClasses().get(0).getFields().get(0).getComment());
    }

    public void testJavaDocOnMethodsAndFields() throws Exception {
        builder.addJavaDoc("Thing");
        builder.beginClass(new ClassDef());

        builder.addField(new FieldDef());// f0

        builder.addJavaDoc("Hello");
        builder.beginMethod();
        builder.endMethod(new MethodDef());//m0

        builder.addJavaDoc("Hello field");
        builder.addField(new FieldDef());//f1

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

    public void testDocletTag() throws Exception {
        builder.addJavaDoc("Hello");
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.beginClass(new ClassDef());

        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals("Hello", source.getClasses().get(0).getComment());
        assertEquals(1, source.getClasses().get(0).getTags().size());
        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
    }

    public void testDocletTagWithNoComment() throws Exception {
        builder.addJavaDoc(""); // parser will always call this method to signify start of javadoc
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.beginClass(new ClassDef());

        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals("", source.getClasses().get(0).getComment());
        assertEquals(1, source.getClasses().get(0).getTags().size());
        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
    }

    public void testMultipleDocletTags() throws Exception {
        builder.addJavaDoc("Hello");
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.addJavaDocTag(new TagDef("food", "is great"));
        builder.addJavaDocTag(new TagDef("chairs", "are boring"));
        builder.beginClass(new ClassDef());

        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals("Hello", source.getClasses().get(0).getComment());
        assertEquals(3, source.getClasses().get(0).getTags().size());
        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
        assertEquals("food", source.getClasses().get(0).getTags().get(1).getName());
        assertEquals("is great", source.getClasses().get(0).getTags().get(1).getValue());
        assertEquals("chairs", source.getClasses().get(0).getTags().get(2).getName());
        assertEquals("are boring", source.getClasses().get(0).getTags().get(2).getValue());
    }

    public void testDocletTagsOnMethodsAndFields() throws Exception {
        builder.addJavaDoc("");
        builder.addJavaDocTag(new TagDef("cheese", "is good"));
        builder.beginClass(new ClassDef());

        builder.addJavaDoc("");
        builder.addJavaDocTag(new TagDef("food", "is great"));
        builder.beginMethod();
        builder.endMethod(new MethodDef());

        builder.addJavaDoc("");
        builder.addJavaDocTag(new TagDef("chairs", "are boring"));
        builder.addField(new FieldDef());
        builder.endClass();

        JavaSource source = builder.getSource();

        assertEquals("cheese", source.getClasses().get(0).getTags().get(0).getName());
        assertEquals("is good", source.getClasses().get(0).getTags().get(0).getValue());
        assertEquals("food", source.getClasses().get(0).getMethods().get(0).getTags().get(0).getName());
        assertEquals("is great", source.getClasses().get(0).getMethods().get(0).getTags().get(0).getValue());
        assertEquals("chairs", source.getClasses().get(0).getFields().get(0).getTags().get(0).getName());
        assertEquals("are boring", source.getClasses().get(0).getFields().get(0).getTags().get(0).getValue());
    }

    public void testRetrieveJavaSource() throws Exception {
        builder.beginClass(new ClassDef());
        builder.endClass();

        JavaSource source = builder.getSource();
        assertNotNull(source);
    }

    public void testJavaSourceClassCount() throws Exception {
        builder.beginClass(new ClassDef());
        builder.endClass();
        builder.beginClass(new ClassDef());
        builder.endClass();
        builder.beginClass(new ClassDef());
        builder.endClass();
        JavaSource result = builder.getSource();
        assertEquals(3, result.getClasses().size());
    }

    public void testJavaSourceNoPackage() throws Exception {
        JavaSource result = builder.getSource();
        assertNull(result.getPackage());
    }

    public void testJavaSourceWithPackage() throws Exception {
        builder.addPackage(new PackageDef("com.blah.thing"));
        JavaSource result = builder.getSource();
        assertEquals("com.blah.thing", result.getPackage().getName());
    }

    public void testJavaSourceNoImports() throws Exception {
        JavaSource result = builder.getSource();
        assertEquals(0, result.getImports().size());
    }

    public void testJavaSourceOneImport() throws Exception {
        builder.addImport("com.blah.Thing");
        JavaSource result = builder.getSource();
        assertEquals(1, result.getImports().size());
        assertEquals("com.blah.Thing", result.getImports().get(0));
    }

    public void testJavaSourceMultipleImports() throws Exception {
        builder.addImport("com.blah.Thing");
        builder.addImport("java.util.List");
        builder.addImport("org.apache.*");
        JavaSource result = builder.getSource();
        assertEquals(3, result.getImports().size());
        assertEquals("com.blah.Thing", result.getImports().get(0));
        assertEquals("java.util.List", result.getImports().get(1));
        assertEquals("org.apache.*", result.getImports().get(2));
    }

    public void testModelHashCodes() {

        ClassDef classDef = new ClassDef("hello");
        assertTrue(classDef.hashCode() > 0);

        MethodDef methodDef = new MethodDef();
        methodDef.name = "hello";
        assertTrue(methodDef.hashCode() > 0);

        FieldDef fieldDef = new FieldDef();
        fieldDef.name = "hello";
        assertTrue(fieldDef.hashCode() > 0);

        JavaParameter javaParameter = new DefaultJavaParameter(new Type("q"), "w");
        assertTrue(javaParameter.hashCode() > 0);

    }


    public void testType() {

        Type type1 = new Type("fred", 1);
        Type type2 = new Type("fred", 1);
        Type type3 = new Type("wilma", 2);
        assertTrue(type1.compareTo(type2) == 0);
        assertFalse(type1.compareTo(type3) == 0);
        assertTrue(type1.compareTo("barney") == 0);
    }


}
