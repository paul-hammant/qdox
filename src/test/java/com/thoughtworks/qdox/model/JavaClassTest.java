package com.thoughtworks.qdox.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public abstract class JavaClassTest<C extends JavaClass> extends TestCase {

    private C cls;
    private JavaSource src;

    public JavaClassTest(String s) {
        super(s);
    }
    
    //contructors
    public abstract C newJavaClass();
    public abstract C newJavaClass(String name);
    
    //setters
    public abstract void setComment(C clazz, String comment);
    public abstract void setEnum(C clazz, boolean isEnum);
    public abstract void setFields(C clazz, List<JavaField> fields);
    public abstract void setImplementz(C clazz, List<Type> implementz);
    public abstract void setInterface(C clazz, boolean isInterface);
    public abstract void setMethods(C clazz, List<JavaMethod> method);
    public abstract void setModifiers(C clazz, List<String> modifiers);
    public abstract void setName(C clazz, String name);
    public abstract void setPackage(C clazz, JavaPackage pckg);
    public abstract void setSuperClass(C clazz, Type type);
    public abstract void setSource( C clazz, JavaSource source );
    
    public JavaPackage newJavaPackage(String name) {
        JavaPackage result = mock(JavaPackage.class);
        when(result.getName()).thenReturn( name );
        return result;
    }
    public abstract JavaParameter newJavaParameter(Type type, String name);
    public abstract JavaParameter newJavaParameter(Type type, String name, boolean varArgs);
    public abstract JavaSource newJavaSource();

    public Type newType( String fullname )
    {
        Type result = mock( Type.class );
        when( result.getFullyQualifiedName() ).thenReturn( fullname );
        when( result.getValue() ).thenReturn( fullname );
        //@todo fix
        when( result.toString() ).thenReturn( fullname );
        return result;
    }
    
    public abstract void setPackage(JavaSource source, JavaPackage pckg);
    
    public abstract void addClass(JavaClass clazz, JavaClass innerClazz);
    public abstract void addClass(JavaSource source, JavaClass clazz);

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
        JavaMethod mth = mock(JavaMethod.class);
        when(mth.getName()).thenReturn( "doStuff" );
        Type voidType = newType("void");
        when(mth.getReturns()).thenReturn( voidType );
        
        setMethods(cls, Collections.singletonList( mth ));
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
        List<JavaMethod> methods = new ArrayList<JavaMethod>();
        {
            JavaMethod mth = mock(JavaMethod.class);
            when(mth.getName()).thenReturn( "doStuff" );
            Type voidType = newType( "void" );
            when(mth.getReturns()).thenReturn( voidType );
            methods.add(mth);
        }

        {
            JavaMethod mth = mock(JavaMethod.class);
            when(mth.getName()).thenReturn( "somethingElse" );
            Type gooseType = newType("Goose");
            when(mth.getReturns()).thenReturn( gooseType );
            methods.add(mth);
        }

        {
            JavaMethod mth = mock(JavaMethod.class);
            when(mth.getName()).thenReturn( "eat" );
            Type voidType = newType("void");
            when(mth.getReturns()).thenReturn( voidType );
            methods.add(mth);
        }
        setMethods( cls, methods );

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
        List<JavaField> fields = new ArrayList<JavaField>();
        {
            JavaField fld = mock( JavaField.class );
            when(fld.getName()).thenReturn( "count" );
            Type intType = newType("int");
            when(fld.getType()).thenReturn( intType );
            when(fld.getDeclaringClass()).thenReturn( cls );
            fields.add( fld );
        }

        {
            JavaField fld = mock( JavaField.class );
            when(fld.getName()).thenReturn( "thing" );
            Type stringType = newType( "String" );
            when(fld.getType()).thenReturn( stringType );
            when(fld.getModifiers()).thenReturn( Collections.singletonList( "public" ) );
            when(fld.getDeclaringClass()).thenReturn( cls );
            fields.add( fld );
        }
        setFields( cls, fields );

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
        C innerClass = newJavaClass();
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
        C innerEnum = newJavaClass();
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
        C innerClass = newJavaClass();
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

        JavaMethod mth = mock(JavaMethod.class);
        when(mth.getName()).thenReturn( "thingy" );
        Type stringType = newType( "String" );
        when(mth.getReturns()).thenReturn( stringType );
        when(mth.getComment()).thenReturn( "Hello Method" );
        setMethods( cls, Collections.singletonList( mth ) );
        
        JavaField fld = mock(JavaField.class);
        when(fld.getType()).thenReturn( stringType );
        when(fld.getName()).thenReturn( "thing" );
        when(fld.getComment()).thenReturn( "Hello Field" );
        when(fld.getDeclaringClass()).thenReturn( cls );
        setFields( cls, Collections.singletonList( fld ) );

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
    }

    public void testGetClassNamePrefix() {
        setPackage(src, newJavaPackage("foo.bar"));
        setName(cls, "Stanley");
        assertEquals("foo.bar.Stanley$", cls.getClassNamePrefix());
    }
    
    public void testInnerClass() throws Exception {
        setPackage(src, newJavaPackage("foo.bar"));

        C outer = newJavaClass();
        setName(outer, "Outer");
        addClass(src, outer);

        C inner = newJavaClass();
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
    
    public void testGetEnumConstants() {
        setName( cls, "MyEnum" );
        assertNull( cls.getEnumConstants() );
        
        setEnum( cls, true );
        assertNotNull( cls.getEnumConstants() );
        assertEquals( 0, cls.getEnumConstants().size() );
        
        List<JavaField> fields = new ArrayList<JavaField>();
        JavaField nonEnumConstantField = mock(JavaField.class);
        fields.add( nonEnumConstantField );
        setFields( cls, fields );
        assertEquals( 0, cls.getEnumConstants().size() );
        
        JavaField enumConstantField = mock(JavaField.class);
        when ( enumConstantField.isEnumConstant() ).thenReturn( true );
        fields.add( enumConstantField );
        setFields( cls, fields );
        assertEquals( 1, cls.getEnumConstants().size() );
    }
    
    public void testGetEnumConstantByName() {
        setName( cls, "MyEnum" );
        
        List<JavaField> fields = new ArrayList<JavaField>();
        JavaField nonEnumConstantField = mock(JavaField.class);
        when ( nonEnumConstantField.getName() ).thenReturn( "nonEnumField" );
        fields.add( nonEnumConstantField );
        setFields( cls, fields );
        assertEquals( null, cls.getEnumConstantByName( "nonEnumField" ) );
        
        JavaField enumConstantField = mock(JavaField.class);
        when ( enumConstantField.isEnumConstant() ).thenReturn( true );
        when ( enumConstantField.getName() ).thenReturn( "enumField" );
        fields.add( enumConstantField );
        setFields( cls, fields );
        assertEquals( enumConstantField, cls.getEnumConstantByName( "enumField" ) );
    }
    

    public void testCanGetFieldByName() throws Exception {
        JavaField fredField = mock(JavaField.class);
        when(fredField.getName()).thenReturn( "fred" );
        Type intType = newType("int");
        when(fredField.getType()).thenReturn( intType );
        when(fredField.getDeclaringClass()).thenReturn( cls );
        setFields( cls, Collections.singletonList( fredField ) );

        assertEquals(fredField, cls.getFieldByName("fred"));
        assertEquals(null, cls.getFieldByName("barney"));
    }

    public void testCanGetMethodBySignature() {
        final String methodName = "doStuff";
        final List<Type> parameterTypes = type(new String[]{"int", "double"});
        JavaMethod method = mock(JavaMethod.class);
        when(method.getName()).thenReturn(methodName);
        //both signatureMatches-methods are allowed
        when(method.signatureMatches( "doStuff", parameterTypes )).thenReturn( true );
        when(method.signatureMatches( "doStuff", parameterTypes, false )).thenReturn( true );
        setMethods(cls, Collections.singletonList( method ));

        assertSame(
                method,
                cls.getMethodBySignature("doStuff", parameterTypes)
        );
        assertEquals(
                null,
                cls.getMethodBySignature("doStuff", new ArrayList<Type>())
        );
        assertEquals(
                null,
                cls.getMethodBySignature("sitIdlyBy", parameterTypes)
        );
    }

    public void testCanGetInnerClassByName() throws Exception {
        C innerClass = newJavaClass();
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
        C innerClass = newJavaClass();
        setName(innerClass, "DogFood");
        addClass(cls, innerClass);
        assertEquals("p.X$DogFood", cls.resolveType("DogFood"));
        assertEquals(null, cls.resolveType("Food"));
    }

    public void testGetBeanPropertiesReturnsEmptyForEmptyClass() throws Exception {
        assertEquals(0, cls.getBeanProperties().size());
    }

    public void testGetBeanPropertiesFindsSimpleProperties() throws Exception {
        List<JavaMethod> methods = new ArrayList<JavaMethod>();
        JavaMethod setFooMethod = mock(JavaMethod.class);
        when(setFooMethod.getName()).thenReturn( "setFoo" );
        Type intType = newType("int");
        when(setFooMethod.getParameters()).thenReturn( Collections.singletonList( newJavaParameter( intType, "foo" ) ) );
        when(setFooMethod.isPropertyMutator()).thenReturn( true );
        when(setFooMethod.getPropertyName()).thenReturn( "foo" );
        when(setFooMethod.getPropertyType()).thenReturn( intType );
        methods.add(setFooMethod);

        JavaMethod getFooMethod = mock(JavaMethod.class);
        when(getFooMethod.getName()).thenReturn( "getFoo" );
        when(getFooMethod.getReturns()).thenReturn( intType );
        when(getFooMethod.isPropertyAccessor()).thenReturn( true );
        when(getFooMethod.getPropertyName()).thenReturn( "foo" );
        when(getFooMethod.getPropertyType()).thenReturn( intType );
        methods.add( getFooMethod );
        
        setMethods( cls, methods );
        
        assertEquals(1, cls.getBeanProperties().size());
        BeanProperty fooProp = cls.getBeanProperties().get(0);
        assertEquals("foo", fooProp.getName());
        assertEquals(intType, fooProp.getType());
        assertEquals(getFooMethod, fooProp.getAccessor());
        assertEquals(setFooMethod, fooProp.getMutator());
    }
    
    public void testToStringClass() {
    	setName(cls, "com.MyClass");
    	assertEquals("class com.MyClass", cls.toString());
    }
    
    public void testInnerClassToString() throws Exception {
    	JavaPackage jPackage = newJavaPackage("com.thoughtworks.qdox.model");
    	C jOuterClass = newJavaClass("OuterClass");
    	setPackage(jOuterClass, jPackage);
    	C jInnerClass = newJavaClass("InnerClass");
    	addClass(jOuterClass, jInnerClass);
    	assertEquals("class com.thoughtworks.qdox.model.OuterClass$InnerClass", jInnerClass.toString());
    }
    
    public void testInnerClassType() {
        JavaPackage jPackage = newJavaPackage("com.thoughtworks.qdox.model");
        C jOuterClass = newJavaClass("OuterClass");
        setPackage( jOuterClass, jPackage );
        JavaClass jInnerClass = newJavaClass("InnerClass");
        addClass(jOuterClass, jInnerClass);
        assertEquals( "com.thoughtworks.qdox.model.OuterClass$InnerClass", jInnerClass.getFullyQualifiedName() );
    }
    
    public void testInnerInterfaceToString() {
    	JavaPackage jPackage = newJavaPackage("com.thoughtworks.qdox.model");
    	C jOuterClass = newJavaClass("OuterClass");
    	setPackage( jOuterClass, jPackage );
    	C jInnerInterface = newJavaClass("InnerInterface");
    	setInterface(jInnerInterface, true);
    	addClass(jOuterClass, jInnerInterface);
    	assertEquals("interface com.thoughtworks.qdox.model.OuterClass$InnerInterface", jInnerInterface.toString());
    }
    
    public void testToStringInterface() {
    	setName(cls, "com.MyClass");
    	setInterface(cls, true);
    	assertEquals("interface com.MyClass", cls.toString());
    }
    
    

    /**
     * @codehaus.jira QDOX-59
     */
    public void testBeanPropertiesAreReturnedInOrderDeclared() {
        List<JavaMethod> methods = new ArrayList<JavaMethod>();
        JavaMethod getFooMethod = mock(JavaMethod.class);
        when(getFooMethod.getName()).thenReturn( "getFoo" );
        Type intType = newType("int");
        when(getFooMethod.getReturns()).thenReturn( intType );
        when(getFooMethod.getPropertyName()).thenReturn( "foo" );
        when(getFooMethod.isPropertyAccessor()).thenReturn( true );
        methods.add( getFooMethod );

        JavaMethod getBarMethod = mock(JavaMethod.class);
        when(getBarMethod.getName()).thenReturn( "getBar" );
        when(getBarMethod.getReturns()).thenReturn( intType );
        when(getBarMethod.getPropertyName()).thenReturn( "bar" );
        when(getBarMethod.isPropertyAccessor()).thenReturn( true );
        methods.add( getBarMethod );
        
        JavaMethod getMcFNordMethod = mock(JavaMethod.class);
        when(getMcFNordMethod.getName()).thenReturn( "getMcFnord" );
        Type stringType = newType("String");
        when(getMcFNordMethod.getReturnType()).thenReturn( stringType );
        when(getMcFNordMethod.getPropertyName()).thenReturn( "mcFnord" );
        when(getMcFNordMethod.isPropertyAccessor()).thenReturn( true );
        methods.add( getMcFNordMethod );
        setMethods( cls, methods );

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
        List<JavaMethod> methods = new ArrayList<JavaMethod>();
        JavaMethod simpleMethod = mock(JavaMethod.class);
        
        Type stringType = newType( "String" );
        //both signatureMatches-methods are allowed
        when(simpleMethod.signatureMatches( "doSomething", Collections.singletonList( stringType ) )).thenReturn( true );
        when(simpleMethod.signatureMatches( "doSomething", Collections.singletonList( stringType ), false )).thenReturn( true );
        methods.add( simpleMethod );
        
        JavaMethod varArgMethod = mock(JavaMethod.class);
        when(varArgMethod.signatureMatches( "doSomething", Collections.singletonList( stringType ), true )).thenReturn( true );
        methods.add( varArgMethod );
        
        setMethods( cls, methods );
        
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ) ) );
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), false ) );
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), true ) );
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), false, false ) );
        assertEquals( varArgMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), false, true ) );
        assertEquals( simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), true, false ) );
        assertEquals( varArgMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), true, true ) );
    }
 
    public void QDOX2_tofix_testJavaLangObjectAsDefaultSuperClass() throws Exception {
        //up untill now this succeeds, because other tests have already set the static value of OBJECT
        //running this test alone make it fail, so it's not a proper test.
        //should be fixed if we can get rid of the Type-visibility
        JavaClass clazz = newJavaClass( "a.b.Sample" );
        assertEquals( "java.lang.Object", clazz.getSuperClass().getJavaClass().getFullyQualifiedName() );
        assertEquals( "java.lang.Object", clazz.getSuperClass().getFullyQualifiedName() );
    }
    
}
