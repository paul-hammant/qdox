package com.thoughtworks.qdox.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public abstract class JavaClassTest extends TestCase {

    private JavaClass cls;
    private JavaSource src;

    public JavaClassTest(String s) {
        super(s);
    }
    
    public abstract JavaClass newJavaClass();
    public abstract JavaClass newJavaClass(String name);
    public abstract JavaField newJavaField(JavaClass jClass);
    public abstract JavaMethod newJavaMethod();
    public abstract JavaMethod newJavaMethod(String name);
    public abstract JavaMethod newJavaMethod(Type returns, String name);
    public abstract JavaPackage newJavaPackage(String name);
    public abstract JavaParameter newJavaParameter(Type type, String name);
    public abstract JavaParameter newJavaParameter(Type type, String name, boolean varArgs);
    public abstract JavaSource newJavaSource();
    public abstract Type newType(String fullname);
    
    public abstract void setComment(JavaClass clazz, String comment);
    public abstract void setComment(JavaField field, String comment);
    public abstract void setComment(JavaMethod method, String comment);
    public abstract void setEnum(JavaClass clazz, boolean isEnum);
    public abstract void setImplementz(JavaClass clazz, List<Type> implementz);
    public abstract void setInterface(JavaClass clazz, boolean isInterface);
    public abstract void setModifiers(JavaClass clazz, List<String> modifiers);
    public abstract void setModifiers(JavaField field, List<String> modifiers);
    public abstract void setName(JavaClass clazz, String name);
    public abstract void setName(JavaField field, String name);
    public abstract void setName(JavaMethod method, String name);
    public abstract void setPackage(JavaSource source, JavaPackage pckg);
    public abstract void setReturns(JavaMethod clazz, Type returns);
    public abstract void setSuperClass(JavaClass clazz, Type type);
    public abstract void setType(JavaField field, Type type);
    
    public abstract void addClass(JavaClass clazz, JavaClass innerClazz);
    public abstract void addClass(JavaPackage pckg, JavaClass clazz);
    public abstract void addClass(JavaSource source, JavaClass clazz);
    public abstract void addMethod(JavaClass clazz, JavaMethod method);
    public abstract void addField(JavaClass clazz, JavaField field);
    public abstract void addParameter(JavaMethod method, JavaParameter parameter);

    protected void setUp() throws Exception {
        super.setUp();
        src = newJavaSource();
        cls = newJavaClass();
        addClass(src, cls);
    }

    public void testGetCodeBlockSimpleClass() throws Exception {
        setName(cls, "MyClass");
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockSimpleInterface() throws Exception {
        setName(cls, "MyClass");
        setInterface(cls, true);
        String expected = ""
                + "interface MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockSimpleEnum() throws Exception {
        setName(cls, "MyEnum");
        setEnum(cls, true);
        String expected = ""
                + "enum MyEnum {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassExtends() throws Exception {
        setName(cls, "MyClass");
        setSuperClass(cls, newType("SuperClass"));
        String expected = ""
                + "class MyClass extends SuperClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockInterfaceExtends() throws Exception {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface"}));
        setInterface(cls, true);
        String expected = ""
                + "interface MyClass extends SomeInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockInterfaceExtendsTwo() throws Exception {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface", "AnotherInterface"}));
        setInterface(cls, true);
        String expected = ""
                + "interface MyClass extends SomeInterface, AnotherInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockInterfaceExtendsThree() throws Exception {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface", "AnotherInterface", "Thingy"}));
        setInterface(cls, true);
        String expected = ""
                + "interface MyClass extends SomeInterface, AnotherInterface, Thingy {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassImplements() throws Exception {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface"}));
        String expected = ""
                + "class MyClass implements SomeInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassImplementsTwo() throws Exception {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface", "AnotherInterface", "Xx"}));
        String expected = ""
                + "class MyClass implements SomeInterface, AnotherInterface, Xx {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassImplementsAndExtends() throws Exception {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface", "AnotherInterface", "Xx"}));
        setSuperClass(cls, newType("SubMarine"));
        String expected = ""
                + "class MyClass extends SubMarine implements SomeInterface, AnotherInterface, Xx {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockModifers() throws Exception {
        setName(cls, "MyClass");
        setModifiers(cls, Arrays.asList(new String[]{"public", "final"}));
        String expected = ""
                + "public final class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockModifersProtectionAlwaysFirst() throws Exception {
        setName(cls, "MyClass");
        setModifiers(cls, Arrays.asList(new String[]{"final", "public"}));
        String expected = ""
                + "public final class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());

        setModifiers(cls, Arrays.asList(new String[]{"abstract", "protected"}));
        expected = ""
                + "protected abstract class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassWithOneMethod() throws Exception {
        setName(cls, "MyClass");
        JavaMethod mth = newJavaMethod();
        setName(mth, "doStuff");
        setReturns(mth, newType("void"));
        addMethod(cls, mth);
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "\tvoid doStuff();\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    public void testGetCodeBlockClassWithThreeMethods() throws Exception {
        setName(cls, "MyClass");
        {
            JavaMethod mth = newJavaMethod();
            setName(mth, "doStuff");
            setReturns(mth, newType("void"));
            addMethod(cls, mth);
        }

        {
            JavaMethod mth = newJavaMethod();
            setName(mth, "somethingElse");
            setReturns(mth, newType("Goose"));
            addMethod(cls, mth);
        }

        {
            JavaMethod mth = newJavaMethod();
            setName(mth, "eat");
            setReturns(mth, newType("void"));
            addMethod(cls, mth);
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
        setName(cls, "MyClass");
        {
            JavaField fld = newJavaField(null);
            setName(fld, "count");
            setType(fld, newType("int"));
            addField(cls, fld);
        }

        {
            JavaField fld = newJavaField(null);
            setName(fld, "thing");
            setType(fld, newType("String"));
            setModifiers(fld, Arrays.asList(new String[]{"public"}));
            addField(cls, fld);
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
        setName(cls, "Outer");
        JavaClass innerClass = newJavaClass();
        setName(innerClass, "Inner");
        addClass(cls, innerClass);

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
        setName(cls, "Outer");
        JavaClass innerEnum = newJavaClass();
        setEnum(innerEnum, true);
        setName(innerEnum, "Inner");
        addClass(cls, innerEnum);

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
        setName(cls, "Outer");
        setEnum(cls, true);
        JavaClass innerClass = newJavaClass();
        setName(innerClass, "Inner");
        addClass(cls, innerClass);

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
        setName(cls, "MyClass");
        setComment(cls, "Hello World");

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
        setName(cls, "MyClass");
        setComment(cls, "Hello World");

        JavaMethod mth = newJavaMethod();
        setReturns(mth, newType("String"));
        setName(mth, "thingy");
        setComment(mth, "Hello Method");
        addMethod(cls, mth);

        JavaField fld = newJavaField(null);
        setType(fld, newType("String"));
        setName(fld, "thing");
        setComment(fld, "Hello Field");
        addField(cls, fld);

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
        setName(cls, "MyClass");
        assertTrue(!cls.isPublic());

        setModifiers(cls, Arrays.asList(new String[]{"public"}));
        assertTrue(cls.isPublic());
    }

    public void testQualifiedType() throws Exception {
        setPackage(src, newJavaPackage("com.thoughtworks.qdox"));

        setName(cls, "MyClass");

        assertEquals("MyClass", cls.getName());
        assertEquals("com.thoughtworks.qdox", cls.getPackage().getName());
        assertEquals("com.thoughtworks.qdox", cls.getPackageName());
        assertEquals("com.thoughtworks.qdox.MyClass",
                cls.getFullyQualifiedName());
        assertTrue(cls.asType().isResolved());
        assertEquals("com.thoughtworks.qdox.MyClass", cls.asType().getValue());
    }

    public void testGetClassNamePrefix() {
        setPackage(src, newJavaPackage("foo.bar"));
        setName(cls, "Stanley");
        assertEquals("foo.bar.Stanley$", cls.getClassNamePrefix());
    }
    
    public void testInnerClass() throws Exception {
        setPackage(src, newJavaPackage("foo.bar"));

        JavaClass outer = newJavaClass();
        setName(outer, "Outer");
        addClass(src, outer);

        JavaClass inner = newJavaClass();
        setName(inner, "Inner");
        addClass(outer, inner);

        assertEquals("Inner", inner.getName());
        assertEquals("foo.bar", inner.getPackage().getName());
        assertEquals("foo.bar", inner.getPackageName());
        assertEquals("foo.bar.Outer$Inner",
                inner.getFullyQualifiedName());
    }
    
    public void testDefaultPackageClass() {
    	setPackage(src, null);
    	setName(cls, "DefaultPackageClass");
    	
    	assertEquals("", src.getClasses().get(0).getPackageName());
    	assertEquals("DefaultPackageClass", src.getClasses().get(0).getFullyQualifiedName());
    }

    public void testDefaultClassSuperclass() throws Exception {
        setName(cls, "MyClass");
        assertEquals("java.lang.Object", cls.getSuperClass().getValue());
        setSuperClass(cls, newType("x.X"));
        assertEquals("x.X", cls.getSuperClass().getValue());
    }

    public void testDefaultInterfaceSuperclass() throws Exception {
        setName(cls, "MyInterface");
        setInterface(cls, true);
        assertNull(cls.getSuperClass());
        setSuperClass(cls, newType("x.X"));
        assertEquals("x.X", cls.getSuperClass().getValue());
    }

    public void testEnumSuperclass() throws Exception {
        setName(cls, "MyEnum");
        setEnum(cls, true);
        assertEquals("java.lang.Enum", cls.getSuperClass().getValue());
    }

    public void testEnumCannotExtendAnythingElse() throws Exception {
        setName(cls, "MyEnum");
        setEnum(cls, true);
        try {
            setSuperClass(cls, newType("x.X"));
            fail("expected an exception");
        } catch (IllegalArgumentException e) {
            assertEquals("enums cannot extend other classes", e.getMessage());
        }
    }

    public void testCanGetFieldByName() throws Exception {
        JavaField fredField = newJavaField(null);
        setName(fredField, "fred");
        setType(fredField, newType("int"));
        addField(cls, fredField);

        assertEquals(fredField, cls.getFieldByName("fred"));
        assertEquals(null, cls.getFieldByName("barney"));
    }

    public void testCanGetMethodBySignature() {
        JavaMethod method = newJavaMethod();
        setReturns(method, newType("void"));
        setName(method, "doStuff");
        addParameter(method, newJavaParameter(newType("int"), "x") );
        addParameter(method, newJavaParameter(newType("double"), "y") );
        addMethod(cls, method);

        List<Type> correctTypes = type(new String[]{"int", "double"});
        assertSame(
                method,
                cls.getMethodBySignature("doStuff", correctTypes)
        );
        assertEquals(
                null,
                cls.getMethodBySignature("doStuff", new ArrayList<Type>())
        );
        assertEquals(
                null,
                cls.getMethodBySignature("sitIdlyBy", correctTypes)
        );
    }

    public void testCanGetInnerClassByName() throws Exception {
        JavaClass innerClass = newJavaClass();
        setName(innerClass, "Inner");
        addClass(cls, innerClass);

        assertEquals(innerClass, cls.getNestedClassByName("Inner"));
        assertEquals(null, cls.getNestedClassByName("Bogus"));
    }

    public void testResolveTypeDefaultsToParentScope() throws Exception {
        setName(cls, "X");
        assertEquals("int", cls.resolveType("int"));
    }
    
    public void testResolveTypeInnerClass() throws Exception {
        setPackage(src, newJavaPackage("p"));
        setName(cls, "X");
        JavaClass innerClass = newJavaClass();
        setName(innerClass, "DogFood");
        addClass(cls, innerClass);
        assertEquals("p.X$DogFood", cls.resolveType("DogFood"));
        assertEquals(null, cls.resolveType("Food"));
    }

    public void testGetBeanPropertiesReturnsEmptyForEmptyClass() throws Exception {
        assertEquals(0, cls.getBeanProperties().size());
    }

    public void testGetBeanPropertiesFindsSimpleProperties() throws Exception {
        JavaMethod setFooMethod = newJavaMethod("setFoo");
        addParameter(setFooMethod, newJavaParameter(newType("int"), "foo"));
        addMethod(cls, setFooMethod);

        JavaMethod getFooMethod = newJavaMethod(newType("int"), "getFoo");
        addMethod(cls, getFooMethod);
        
        assertEquals(1, cls.getBeanProperties().size());
        BeanProperty fooProp = cls.getBeanProperties().get(0);
        assertEquals("foo", fooProp.getName());
        assertEquals(newType("int"), fooProp.getType());
        assertEquals(getFooMethod, fooProp.getAccessor());
        assertEquals(setFooMethod, fooProp.getMutator());
    }
    
    public void testToStringClass() {
    	setName(cls, "com.MyClass");
    	assertEquals("class com.MyClass", cls.toString());
    }
    
    public void testInnerClassToString() throws Exception {
    	JavaPackage jPackage = newJavaPackage("com.thoughtworks.qdox.model");
    	JavaClass jOuterClass = newJavaClass("OuterClass");
    	addClass(jPackage, jOuterClass);
    	JavaClass jInnerClass = newJavaClass("InnerClass");
    	addClass(jOuterClass, jInnerClass);
    	assertEquals("class com.thoughtworks.qdox.model.OuterClass$InnerClass", jInnerClass.toString());
    }
    
    public void testInnerClassType() {
        JavaPackage jPackage = newJavaPackage("com.thoughtworks.qdox.model");
        JavaClass jOuterClass = newJavaClass("OuterClass");
        addClass(jPackage, jOuterClass);
        JavaClass jInnerClass = newJavaClass("InnerClass");
        addClass(jOuterClass, jInnerClass);
        assertEquals("com.thoughtworks.qdox.model.OuterClass.InnerClass", jInnerClass.asType().getValue());
    }
    
    public void testInnerInterfaceToString() {
    	JavaPackage jPackage = newJavaPackage("com.thoughtworks.qdox.model");
    	JavaClass jOuterClass = newJavaClass("OuterClass");
    	addClass(jPackage, jOuterClass);
    	JavaClass jInnerInterface = newJavaClass("InnerInterface");
    	setInterface(jInnerInterface, true);
    	addClass(jOuterClass, jInnerInterface);
    	assertEquals("interface com.thoughtworks.qdox.model.OuterClass$InnerInterface", jInnerInterface.toString());
    }
    
    public void testToStringInterface() {
    	setName(cls, "com.MyClass");
    	setInterface(cls, true);
    	assertEquals("interface com.MyClass", cls.toString());
    }
    
    public void testToStringVoid() {
    	setName(cls, "com.MyClass");
    	addMethod(cls, newJavaMethod(Type.VOID, "doSomething"));
    	JavaMethod javaMethod = cls.getMethods().get(0);
    	assertEquals("void", javaMethod.getReturns().toString());
    }

    public void testToStringBoolean() {
    	setName(cls, "com.MyClass");
    	addMethod(cls, newJavaMethod(newType("boolean"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods().get(0);
    	assertEquals("boolean", javaMethod.getReturns().toString());
    }
    
    public void testToStringInt() {
    	setName(cls, "com.MyClass");
    	addMethod(cls,  newJavaMethod(newType("int"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods().get(0);
    	assertEquals("int", javaMethod.getReturns().toString());
    }

    public void testToStringLong() {
    	setName(cls, "com.MyClass");
    	addMethod(cls, newJavaMethod(newType("long"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods().get(0);
    	assertEquals("long", javaMethod.getReturns().toString());
    }

    public void testToStringFloat() {
    	setName(cls, "com.MyClass");
    	addMethod(cls, newJavaMethod(newType("float"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods().get(0);
    	assertEquals("float", javaMethod.getReturns().toString());
    }

    public void testToStringDouble() {
    	setName(cls, "com.MyClass");
    	addMethod(cls, newJavaMethod(newType("double"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods().get(0);
    	assertEquals("double", javaMethod.getReturns().toString());
    }
    
    public void testToStringChar() {
    	setName(cls, "com.MyClass");
    	addMethod(cls, newJavaMethod(newType("char"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods().get(0);
    	assertEquals("char", javaMethod.getReturns().toString());
    }

    public void testToStringByte() {
    	setName(cls, "com.MyClass");
    	addMethod(cls, newJavaMethod(newType("byte"), "doSomething"));
    	JavaMethod javaMethod = cls.getMethods().get(0);
    	assertEquals("byte", javaMethod.getReturns().toString());
    }

    /**
     * @codehaus.jira QDOX-59
     */
    public void testBeanPropertiesAreReturnedInOrderDeclared() {
        addMethod(cls, new JavaMethod(newType("int"), "getFoo"));
        addMethod(cls, new JavaMethod(newType("int"), "getBar"));
        addMethod(cls, new JavaMethod(newType("String"), "getMcFnord"));

        List<BeanProperty> properties = cls.getBeanProperties();
        assertEquals(3, properties.size());
        assertEquals("foo", properties.get(0).getName());
        assertEquals("bar", properties.get(1).getName());
        assertEquals("mcFnord", properties.get(2).getName());        
    }
    
    private List<Type> type(String[] typeNames) {
        List<Type> result = new LinkedList<Type>();
        for (int i = 0; i < typeNames.length; i++) {
            result.add(newType(typeNames[i]));
        }
        return result;
    }
    
    // QDOX-201
    public void testGetVarArgMethodSignature() {
        JavaMethod simpleMethod = newJavaMethod( "doSomething" );
        addParameter(simpleMethod,  newJavaParameter( newType("String"), "param", false ) );
        JavaMethod varArgMethod = newJavaMethod( "doSomething" );
        addParameter( varArgMethod, newJavaParameter( newType("String"), "param", true ) );
        
        addMethod(cls, simpleMethod );
        addMethod(cls, varArgMethod );
        
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList(newType("String")) ) );
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList(newType("String")), false ) );
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList(newType("String")), true ) );
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList(newType("String")), false, false ) );
        assertEquals( varArgMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList(newType("String")), false, true ) );
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList(newType("String")), true, false ) );
        assertEquals( varArgMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList(newType("String")), true, true ) );
    }
 
    public void testJavaLangObjectAsDefaultSuperClass() throws Exception {
        JavaClass clazz = newJavaClass( "a.b.Sample" );
        assertEquals( "java.lang.Object", clazz.getSuperClass().getJavaClass().getFullyQualifiedName() );
    }
    
    public void testGetSource() {
        JavaSource source = newJavaSource();
        JavaClass clazz = newJavaClass();
        source.addClass(clazz);
        ((DefaultJavaClass) clazz).setSource( source );
        JavaField field = newJavaField(clazz);
        ((DefaultJavaClass) clazz).addField(field);
        assertEquals(source, field.getSource());
    }
}
