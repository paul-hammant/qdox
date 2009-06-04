package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import java.util.HashMap;

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

    public void testGetCodeBlockSimpleClass() throws Exception {
        cls.setName("MyClass");
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockSimpleInterface() throws Exception {
        cls.setName("MyClass");
        cls.setInterface(true);
        String expected = ""
                + "interface MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockSimpleEnum() throws Exception {
        cls.setName("MyEnum");
        cls.setEnum(true);
        String expected = ""
                + "enum MyEnum {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassExtends() throws Exception {
        cls.setName("MyClass");
        cls.setSuperClass(new Type("SuperClass"));
        String expected = ""
                + "class MyClass extends SuperClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockInterfaceExtends() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface"}));
        cls.setInterface(true);
        String expected = ""
                + "interface MyClass extends SomeInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockInterfaceExtendsTwo() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface", "AnotherInterface"}));
        cls.setInterface(true);
        String expected = ""
                + "interface MyClass extends SomeInterface, AnotherInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockInterfaceExtendsThree() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface", "AnotherInterface", "Thingy"}));
        cls.setInterface(true);
        String expected = ""
                + "interface MyClass extends SomeInterface, AnotherInterface, Thingy {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassImplements() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface"}));
        String expected = ""
                + "class MyClass implements SomeInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassImplementsTwo() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface", "AnotherInterface", "Xx"}));
        String expected = ""
                + "class MyClass implements SomeInterface, AnotherInterface, Xx {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassImplementsAndExtends() throws Exception {
        cls.setName("MyClass");
        cls.setImplementz(type(new String[]{"SomeInterface", "AnotherInterface", "Xx"}));
        cls.setSuperClass(new Type("SubMarine"));
        String expected = ""
                + "class MyClass extends SubMarine implements SomeInterface, AnotherInterface, Xx {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockModifers() throws Exception {
        cls.setName("MyClass");
        cls.setModifiers(new String[]{"public", "final"});
        String expected = ""
                + "public final class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockModifersProtectionAlwaysFirst() throws Exception {
        cls.setName("MyClass");
        cls.setModifiers(new String[]{"final", "public"});
        String expected = ""
                + "public final class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());

        cls.setModifiers(new String[]{"abstract", "protected"});
        expected = ""
                + "protected abstract class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassWithOneMethod() throws Exception {
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
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassWithThreeMethods() throws Exception {
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
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassWithTwoFields() throws Exception {
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
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassWithInnerClass() throws Exception {
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
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassWithInnerEnum() throws Exception {
        cls.setName("Outer");
        JavaClass innerEnum = new JavaClass();
        innerEnum.setEnum(true);
        innerEnum.setName("Inner");
        cls.addClass(innerEnum);

        String expected = ""
                + "class Outer {\n"
                + "\n"
                + "\tenum Inner {\n"
                + "\n"
                + "\t}\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockEnumWithInnerClass() throws Exception {
        cls.setName("Outer");
        cls.setEnum(true);
        JavaClass innerClass = new JavaClass();
        innerClass.setName("Inner");
        cls.addClass(innerClass);

        String expected = ""
                + "enum Outer {\n"
                + "\n"
                + "\tclass Inner {\n"
                + "\n"
                + "\t}\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }


    public void testGetCodeBlockClassWithComment() throws Exception {
        cls.setName("MyClass");
        cls.setComment("Hello World");

        String expected = ""
                + "/**\n"
                + " * Hello World\n"
                + " */\n"
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassWithIndentedCommentsForFieldAndMethod() throws Exception {
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
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testIsPublic() {
        cls.setName("MyClass");
        assertTrue(!cls.isPublic());

        cls.setModifiers(new String[]{"public"});
        assertTrue(cls.isPublic());
    }

    public void testQualifiedType() throws Exception {
        src.setPackage(new JavaPackage("com.thoughtworks.qdox", new HashMap()));

        cls.setName("MyClass");

        assertEquals("MyClass", cls.getName());
        assertEquals("com.thoughtworks.qdox", cls.getPackage().getName());
        assertEquals("com.thoughtworks.qdox.MyClass",
                cls.getFullyQualifiedName());
        assertTrue(cls.asType().isResolved());
        assertEquals("com.thoughtworks.qdox.MyClass", cls.asType().getValue());
    }

    public void testGetClassNamePrefix() {
        src.setPackage(new JavaPackage("foo.bar", new HashMap()));
        cls.setName("Stanley");
        assertEquals("foo.bar.Stanley$", cls.getClassNamePrefix());
    }
    
    public void testInnerClass() throws Exception {
        src.setPackage(new JavaPackage("foo.bar", new HashMap()));

        JavaClass outer = new JavaClass();
        outer.setName("Outer");
        src.addClass(outer);

        JavaClass inner = new JavaClass();
        inner.setName("Inner");
        outer.addClass(inner);

        assertEquals("Inner", inner.getName());
        assertEquals("foo.bar", inner.getPackage().getName());
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

    public void testEnumSuperclass() throws Exception {
        cls.setName("MyEnum");
        cls.setEnum(true);
        assertEquals("java.lang.Enum", cls.getSuperClass().getValue());
    }

    public void testEnumCannotExtendAnythingElse() throws Exception {
        cls.setName("MyEnum");
        cls.setEnum(true);
        try {
            cls.setSuperClass(new Type("x.X"));
            fail("expected an exception");
        } catch (IllegalArgumentException e) {
            assertEquals("enums cannot extend other classes", e.getMessage());
        }
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

        assertEquals(innerClass, cls.getNestedClassByName("Inner"));
        assertEquals(null, cls.getNestedClassByName("Bogus"));
    }

    public void testResolveTypeDefaultsToParentScope() throws Exception {
        cls.setName("X");
        assertEquals("int", cls.resolveType("int"));
    }
    
    public void testResolveTypeInnerClass() throws Exception {
        src.setPackage(new JavaPackage("p", new HashMap()));
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
        JavaMethod setFooMethod = new JavaMethod("setFoo");
        setFooMethod.setParameters(
            new JavaParameter[] {
                new JavaParameter(new Type("int"), "foo")
            }
        );
        cls.addMethod(setFooMethod);

        JavaMethod getFooMethod = new JavaMethod(new Type("int"), "getFoo");
        cls.addMethod(getFooMethod);
        
        assertEquals(1, cls.getBeanProperties().length);
        BeanProperty fooProp = cls.getBeanProperties()[0];
        assertEquals("foo", fooProp.getName());
        assertEquals(new Type("int"), fooProp.getType());
        assertEquals(getFooMethod, fooProp.getAccessor());
        assertEquals(setFooMethod, fooProp.getMutator());
    }
    
    public void testToStringClass() {
    	cls.setName("com.MyClass");
    	assertEquals("class com.MyClass", cls.toString());
    }
    
    public void testInnerClassToString() throws Exception {
    	JavaPackage jPackage = new JavaPackage("com.thoughtworks.qdox.model");
    	JavaClass jOuterClass = new JavaClass("OuterClass");
    	jPackage.addClass(jOuterClass);
    	JavaClass jInnerClass = new JavaClass("InnerClass");
    	jOuterClass.addClass(jInnerClass);
    	assertEquals("class com.thoughtworks.qdox.model.OuterClass$InnerClass", jInnerClass.toString());
    }
    
    public void testInnerClassType() {
        JavaPackage jPackage = new JavaPackage("com.thoughtworks.qdox.model");
        JavaClass jOuterClass = new JavaClass("OuterClass");
        jPackage.addClass(jOuterClass);
        JavaClass jInnerClass = new JavaClass("InnerClass");
        jOuterClass.addClass(jInnerClass);
        assertEquals("com.thoughtworks.qdox.model.OuterClass.InnerClass", jInnerClass.asType().getValue());
    }
    
    public void testInnerInterfaceToString() {
    	JavaPackage jPackage = new JavaPackage("com.thoughtworks.qdox.model");
    	JavaClass jOuterClass = new JavaClass("OuterClass");
    	jPackage.addClass(jOuterClass);
    	JavaClass jInnerInterface = new JavaClass("InnerInterface");
    	jInnerInterface.setInterface(true);
    	jOuterClass.addClass(jInnerInterface);
    	assertEquals("interface com.thoughtworks.qdox.model.OuterClass$InnerInterface", jInnerInterface.toString());
    }
    
    public void testToStringInterface() {
    	cls.setName("com.MyClass");
    	cls.setInterface(true);
    	assertEquals("interface com.MyClass", cls.toString());
    }
    
    public void testToStringVoid() {
    	cls.setName("com.MyClass");
    	cls.addMethod(new JavaMethod(Type.VOID, "doSomething"));
    	JavaMethod javaMethod = cls.getMethods()[0];
    	assertEquals("void", javaMethod.getReturns().toString());
    }

    public void testToStringBoolean() {
    	cls.setName("com.MyClass");
    	cls.addMethod(new JavaMethod(new Type("boolean"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods()[0];
    	assertEquals("boolean", javaMethod.getReturns().toString());
    }
    
    public void testToStringInt() {
    	cls.setName("com.MyClass");
    	cls.addMethod(new JavaMethod(new Type("int"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods()[0];
    	assertEquals("int", javaMethod.getReturns().toString());
    }

    public void testToStringLong() {
    	cls.setName("com.MyClass");
    	cls.addMethod(new JavaMethod(new Type("long"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods()[0];
    	assertEquals("long", javaMethod.getReturns().toString());
    }

    public void testToStringFloat() {
    	cls.setName("com.MyClass");
    	cls.addMethod(new JavaMethod(new Type("float"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods()[0];
    	assertEquals("float", javaMethod.getReturns().toString());
    }

    public void testToStringDouble() {
    	cls.setName("com.MyClass");
    	cls.addMethod(new JavaMethod(new Type("double"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods()[0];
    	assertEquals("double", javaMethod.getReturns().toString());
    }
    
    public void testToStringChar() {
    	cls.setName("com.MyClass");
    	cls.addMethod(new JavaMethod(new Type("char"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods()[0];
    	assertEquals("char", javaMethod.getReturns().toString());
    }

    public void testToStringByte() {
    	cls.setName("com.MyClass");
    	cls.addMethod(new JavaMethod(new Type("byte"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods()[0];
    	assertEquals("byte", javaMethod.getReturns().toString());
    }

    /**
     * @codehaus.jira QDOX-59
     */
    public void testBeanPropertiesAreReturnedInOrderDeclared() {
        cls.addMethod(new JavaMethod(new Type("int"), "getFoo"));
        cls.addMethod(new JavaMethod(new Type("int"), "getBar"));
        cls.addMethod(new JavaMethod(new Type("String"), "getMcFnord"));

        BeanProperty[] properties = cls.getBeanProperties();
        assertEquals(3, properties.length);
        assertEquals("foo", properties[0].getName());
        assertEquals("bar", properties[1].getName());
        assertEquals("mcFnord", properties[2].getName());        
    }
    
    private Type[] type(String[] typeNames) {
        Type[] result = new Type[typeNames.length];
        for (int i = 0; i < typeNames.length; i++) {
            result[i] = new Type(typeNames[i]);
        }
        return result;
    }
    
    
}
