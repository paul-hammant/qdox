package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import com.thoughtworks.qdox.DataProvider;

public class JavaClassTest extends TestCase {

    private JavaClass cls;

    public JavaClassTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        cls = new JavaClass();
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
        cls.setSuperClass(DataProvider.createType("SuperClass", 0));
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
        cls.setSuperClass(DataProvider.createType("SubMarine", 0));
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
        mth.setReturns(DataProvider.createType("void", 0));
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
            mth.setReturns(DataProvider.createType("void", 0));
            cls.addMethod(mth);
        }

        {
            JavaMethod mth = new JavaMethod();
            mth.setName("somethingElse");
            mth.setReturns(DataProvider.createType("Goose", 0));
            cls.addMethod(mth);
        }

        {
            JavaMethod mth = new JavaMethod();
            mth.setName("eat");
            mth.setReturns(DataProvider.createType("void", 0));
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
            fld.setType(DataProvider.createType("int", 0));
            cls.addField(fld);
        }

        {
            JavaField fld = new JavaField();
            fld.setName("thing");
            fld.setType(DataProvider.createType("String", 0));
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
        mth.setReturns(DataProvider.createType("String", 0));
        mth.setName("thingy");
        mth.setComment("Hello Method");
        cls.addMethod(mth);

        JavaField fld = new JavaField();
        fld.setType(DataProvider.createType("String", 0));
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

    public void testParentSource() throws Exception {
        assertNull(cls.getParentSource());

        JavaSource source = new JavaSource();
        source.addClass(cls);

        assertSame(source, cls.getParentSource());
    }

    public void testIsPublic() {
        cls.setName("MyClass");
        assertTrue(!cls.isPublic());

        cls.setModifiers(new String[]{"public"});
        assertTrue(cls.isPublic());
    }

    public void testQualifiedType() throws Exception {
        JavaSource source = new JavaSource();
        source.setPackage("com.thoughtworks.qdox");

        cls.setName("MyClass");

        source.addClass(cls);

        assertEquals("MyClass", cls.getName());
        assertEquals("com.thoughtworks.qdox", cls.getPackage());
        assertEquals("com.thoughtworks.qdox.MyClass",
                cls.getFullyQualifiedName());
        assertTrue(cls.asType().isResolved());
        assertEquals("com.thoughtworks.qdox.MyClass", cls.asType().getValue());
    }

    public void testInnerClass() throws Exception {
        JavaSource source = new JavaSource();
        source.setPackage("foo.bar");

        JavaClass outer = new JavaClass();
        outer.setName("Outer");
        source.addClass(outer);
        assertEquals("foo.bar.Outer", outer.asClassNamespace());

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
        cls.setSuperClass(new Type("x.X", 0));
        assertEquals("x.X", cls.getSuperClass().getValue());
    }

    public void testDefaultInterfaceSuperclass() throws Exception {
        cls.setName("MyInterface");
        cls.setInterface(true);
        assertNull(cls.getSuperClass());
        cls.setSuperClass(new Type("x.X", 0));
        assertEquals("x.X", cls.getSuperClass().getValue());
    }

    public void testCanGetFieldByName() throws Exception {
        JavaField fredField = new JavaField();
        fredField.setName("fred");
        fredField.setType(DataProvider.createType("int", 0));
        cls.addField(fredField);

        assertEquals(fredField, cls.getFieldByName("fred"));
        assertEquals(null, cls.getFieldByName("barney"));
    }

    public void testCanGetMethodBySignature() {
        JavaMethod method = new JavaMethod();
        method.setReturns(DataProvider.createType("void", 0));
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

    private Type[] type(String[] typeNames) {
        Type[] result = new Type[typeNames.length];
        for (int i = 0; i < typeNames.length; i++) {
            result[i] = new Type(typeNames[i]);
        }
        return result;
    }
}
