package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public class JavaClassTest extends TestCase {

    private JavaClass cls;
    private JavaSource src;

    public JavaClassTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        src = new JavaSource();
        cls = new JavaClass();
        src.addClass(cls);
    }

    public void testToStringSimpleClass() throws Exception {
        cls.setName("MyClass");
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringSimpleInterface() throws Exception {
        cls.setName("MyClass");
        cls.setInterface(true);
        String expected = ""
                + "interface MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassExtends() throws Exception {
        cls.setName("MyClass");
        cls.setSuperClass(new Type("SuperClass"));
        String expected = ""
                + "class MyClass extends SuperClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringInterfaceExtends() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface"}));
        cls.setInterface(true);
        String expected = ""
                + "interface MyClass extends SomeInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringInterfaceExtendsTwo() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface", "AnotherInterface"}));
        cls.setInterface(true);
        String expected = ""
                + "interface MyClass extends SomeInterface, AnotherInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringInterfaceExtendsThree() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface", "AnotherInterface", "Thingy"}));
        cls.setInterface(true);
        String expected = ""
                + "interface MyClass extends SomeInterface, AnotherInterface, Thingy {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassImplements() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface"}));
        String expected = ""
                + "class MyClass implements SomeInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassImplementsTwo() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface", "AnotherInterface", "Xx"}));
        String expected = ""
                + "class MyClass implements SomeInterface, AnotherInterface, Xx {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassImplementsAndExtends() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface", "AnotherInterface", "Xx"}));
        cls.setSuperClass(new Type("SubMarine"));
        String expected = ""
                + "class MyClass extends SubMarine implements SomeInterface, AnotherInterface, Xx {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringModifers() throws Exception {
        cls.setName("MyClass");
        cls.setModifiers(new String[]{"public", "final"});
        String expected = ""
                + "public final class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringModifersProtectionAlwaysFirst() throws Exception {
        cls.setName("MyClass");
        cls.setModifiers(new String[]{"final", "public"});
        String expected = ""
                + "public final class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());

        cls.setModifiers(new String[]{"abstract", "protected"});
        expected = ""
                + "protected abstract class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassWithOneMethod() throws Exception {
        cls.setName("MyClass");
        JavaMethod mth = new JavaMethod();
        mth.setName("doStuff");
        mth.setReturns(new Type("void"));
        cls.addMethod(mth);
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "\tvoid doStuff();\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassWithThreeMethods() throws Exception {
        cls.setName("MyClass");

        {
            JavaMethod mth = new JavaMethod();
            mth.setName("doStuff");
            mth.setReturns(new Type("void"));
            cls.addMethod(mth);
        }

        {
            JavaMethod mth = new JavaMethod();
            mth.setName("somethingElse");
            mth.setReturns(new Type("Goose"));
            cls.addMethod(mth);
        }

        {
            JavaMethod mth = new JavaMethod();
            mth.setName("eat");
            mth.setReturns(new Type("void"));
            cls.addMethod(mth);
        }

        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "\tvoid doStuff();\n"
                + "\n"
                + "\tGoose somethingElse();\n"
                + "\n"
                + "\tvoid eat();\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassWithTwoFields() throws Exception {
        cls.setName("MyClass");

        {
            JavaField fld = new JavaField();
            fld.setName("count");
            fld.setType(new Type("int"));
            cls.addField(fld);
        }

        {
            JavaField fld = new JavaField();
            fld.setName("thing");
            fld.setType(new Type("String"));
            fld.setModifiers(new String[]{"public"});
            cls.addField(fld);
        }

        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "\tint count;\n"
                + "\n"
                + "\tpublic String thing;\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassWithInnerClass() throws Exception {
        cls.setName("Outer");
        JavaClass innerClass = new JavaClass();
        innerClass.setName("Inner");
        cls.addClass(innerClass);

        String expected = ""
                + "class Outer {\n"
                + "\n"
                + "\tclass Inner {\n"
                + "\n"
                + "\t}\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassWithComment() throws Exception {
        cls.setName("MyClass");
        cls.setComment("Hello World");

        String expected = ""
                + "/**\n"
                + " * Hello World\n"
                + " */\n"
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testToStringClassWithIndentedCommentsForFieldAndMethod() throws Exception {
        cls.setName("MyClass");
        cls.setComment("Hello World");

        JavaMethod mth = new JavaMethod();
        mth.setReturns(new Type("String"));
        mth.setName("thingy");
        mth.setComment("Hello Method");
        cls.addMethod(mth);

        JavaField fld = new JavaField();
        fld.setType(new Type("String"));
        fld.setName("thing");
        fld.setComment("Hello Field");
        cls.addField(fld);

        String expected = ""
                + "/**\n"
                + " * Hello World\n"
                + " */\n"
                + "class MyClass {\n"
                + "\n"
                + "\t/**\n"
                + "\t * Hello Field\n"
                + "\t */\n"
                + "\tString thing;\n"
                + "\n"
                + "\t/**\n"
                + "\t * Hello Method\n"
                + "\t */\n"
                + "\tString thingy();\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.toString());
    }

    public void testIsPublic() {
        cls.setName("MyClass");
        assertTrue(!cls.isPublic());

        cls.setModifiers(new String[]{"public"});
        assertTrue(cls.isPublic());
    }

    public void testQualifiedType() throws Exception {
        src.setPackage("com.thoughtworks.qdox");

        cls.setName("MyClass");

        assertEquals("MyClass", cls.getName());
        assertEquals("com.thoughtworks.qdox", cls.getPackage());
        assertEquals("com.thoughtworks.qdox.MyClass",
                cls.getFullyQualifiedName());
        assertTrue(cls.asType().isResolved());
        assertEquals("com.thoughtworks.qdox.MyClass", cls.asType().getValue());
    }

    public void testGetClassNamePrefix() {
        src.setPackage("foo.bar");
        cls.setName("Stanley");
        assertEquals("foo.bar.Stanley$", cls.getClassNamePrefix());
    }
    
    public void testInnerClass() throws Exception {
        src.setPackage("foo.bar");

        JavaClass outer = new JavaClass();
        outer.setName("Outer");
        src.addClass(outer);

        JavaClass inner = new JavaClass();
        inner.setName("Inner");
        outer.addClass(inner);

        assertEquals("Inner", inner.getName());
        assertEquals("foo.bar", inner.getPackage());
        assertEquals("foo.bar.Outer$Inner",
                inner.getFullyQualifiedName());
    }

    public void testDefaultClassSuperclass() throws Exception {
        cls.setName("MyClass");
        assertEquals("java.lang.Object", cls.getSuperClass().getValue());
        cls.setSuperClass(new Type("x.X"));
        assertEquals("x.X", cls.getSuperClass().getValue());
    }

    public void testDefaultInterfaceSuperclass() throws Exception {
        cls.setName("MyInterface");
        cls.setInterface(true);
        assertNull(cls.getSuperClass());
        cls.setSuperClass(new Type("x.X"));
        assertEquals("x.X", cls.getSuperClass().getValue());
    }

    public void testCanGetFieldByName() throws Exception {
        JavaField fredField = new JavaField();
        fredField.setName("fred");
        fredField.setType(new Type("int"));
        cls.addField(fredField);

        assertEquals(fredField, cls.getFieldByName("fred"));
        assertEquals(null, cls.getFieldByName("barney"));
    }

    public void testCanGetMethodBySignature() {
        JavaMethod method = new JavaMethod();
        method.setReturns(new Type("void"));
        method.setName("doStuff");
        JavaParameter[] parameters = {
            new JavaParameter(new Type("int"), "x"),
            new JavaParameter(new Type("double"), "y"),
        };
        method.setParameters(parameters);
        cls.addMethod(method);

        Type[] correctTypes = type(new String[]{"int", "double"});
        assertSame(
                method,
                cls.getMethodBySignature("doStuff", correctTypes)
        );
        assertEquals(
                null,
                cls.getMethodBySignature("doStuff", new Type[0])
        );
        assertEquals(
                null,
                cls.getMethodBySignature("sitIdlyBy", correctTypes)
        );
    }

    public void testCanGetInnerClassByName() throws Exception {
        JavaClass innerClass = new JavaClass();
        innerClass.setName("Inner");
        cls.addClass(innerClass);

        assertEquals(innerClass, cls.getInnerClassByName("Inner"));
        assertEquals(null, cls.getInnerClassByName("Bogus"));
    }

    public void testResolveTypeDefaultsToParentScope() throws Exception {
        cls.setName("X");
        assertEquals("int", cls.resolveType("int"));
    }
    
    public void testResolveTypeInnerClass() throws Exception {
        src.setPackage("p");
        cls.setName("X");
        JavaClass innerClass = new JavaClass();
        innerClass.setName("DogFood");
        cls.addClass(innerClass);
        assertEquals("p.X$DogFood", cls.resolveType("DogFood"));
        assertEquals(null, cls.resolveType("Food"));
    }

    public void testGetBeanPropertiesReturnsEmptyForEmptyClass() throws Exception {
        assertEquals(0, cls.getBeanProperties().length);
    }

    public void testGetBeanPropertiesFindsSimpleProperties() throws Exception {
        
        JavaMethod setFooMethod = new JavaMethod();
        setFooMethod.setName("setFoo");
        setFooMethod.setParameters(
            new JavaParameter[] {
                new JavaParameter(new Type("int"), "foo")
            }
        );
        cls.addMethod(setFooMethod);

        JavaMethod getFooMethod = new JavaMethod();
        getFooMethod.setName("getFoo");
        getFooMethod.setReturns(new Type("int"));
        cls.addMethod(getFooMethod);
        
        assertEquals(1, cls.getBeanProperties().length);
        BeanProperty fooProp = cls.getBeanProperties()[0];
        assertEquals("foo", fooProp.getName());
        assertEquals(new Type("int"), fooProp.getType());
        assertEquals(getFooMethod, fooProp.getAccessor());
        assertEquals(setFooMethod, fooProp.getMutator());
    }
    
    // TODO - more tests for bean properties. Ref QDOX-59
    
    private Type[] type(String[] typeNames) {
        Type[] result = new Type[typeNames.length];
        for (int i = 0; i < typeNames.length; i++) {
            result[i] = new Type(typeNames[i]);
        }
        return result;
    }
}
