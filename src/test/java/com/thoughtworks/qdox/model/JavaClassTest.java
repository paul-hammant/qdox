package com.thoughtworks.qdox.model;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public abstract class JavaClassTest<C extends JavaClass> {

    private C cls;
    private JavaSource src;

    //contructors
    public abstract C newJavaClass();
    public abstract C newJavaClass(String name);
    
    //setters
    public abstract void setClasses(C clazz, List<JavaClass> innerClazz);
    public abstract void setComment(C clazz, String comment);
    public abstract void setDeclaringClass( C clazz, JavaClass declaringClazz );
    public abstract void setEnum(C clazz, boolean isEnum);
    public abstract void setFields(C clazz, List<JavaField> fields);
    public abstract void setImplementz(C clazz, List<JavaClass> implementz);
    public abstract void setInterface(C clazz, boolean isInterface);
    public abstract void setMethods(C clazz, List<JavaMethod> method);
    public abstract void setModifiers(C clazz, List<String> modifiers);
    public abstract void setName(C clazz, String name);
    public abstract void setPackage(C clazz, JavaPackage pckg);
    public abstract void setSuperClass(C clazz, JavaType type);
    public abstract void setSource( C clazz, JavaSource source );
    
    public JavaPackage newJavaPackage(String name) {
        JavaPackage result = mock(JavaPackage.class);
        when(result.getName()).thenReturn( name );
        return result;
    }
    
    public JavaParameter newJavaParameter(JavaType type, String name) {
        return newJavaParameter( type, name, false );
    }
    
    public JavaParameter newJavaParameter(JavaType type, String name, boolean varArgs)
    {
        JavaParameter result = mock(JavaParameter.class);
        when( result.getType()).thenReturn( type );
        when( result.getName()).thenReturn( name );
        when( result.isVarArgs()).thenReturn( varArgs );
        return result;
    }
    
    public abstract JavaSource newJavaSource();

    public JavaType newType( String fullname )
    {
        JavaType result = mock( JavaType.class );
        when( result.getFullyQualifiedName() ).thenReturn( fullname );
        when( result.getValue() ).thenReturn( fullname.replace( '$', '.' ) );
        when( result.getCanonicalName() ).thenReturn( fullname.replace( '$', '.' ) );
        when( result.getGenericCanonicalName() ).thenReturn( fullname.replace( '$', '.' ) );
        return result;
    }
    
    public abstract void setPackage(JavaSource source, JavaPackage pckg);
    
    public abstract void addClass(JavaSource source, JavaClass clazz);

    @Before
    public void setUp() {
        src = newJavaSource();
        cls = newJavaClass();
        addClass(src, cls);
    }

    @Test
    public void testGetCodeBlockSimpleClass() {
        setName(cls, "MyClass");
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockSimpleInterface() {
        setName(cls, "MyClass");
        setInterface(cls, true);
        String expected = ""
                + "interface MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockSimpleEnum() {
        setName(cls, "MyEnum");
        setEnum(cls, true);
        String expected = ""
                + "enum MyEnum {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockClassExtends() {
        setName(cls, "MyClass");
        setSuperClass(cls, newType("SuperClass"));
        String expected = ""
                + "class MyClass extends SuperClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockInterfaceExtends() {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface"}));
        setInterface(cls, true);
        String expected = ""
                + "interface MyClass extends SomeInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockInterfaceExtendsTwo() {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface", "AnotherInterface"}));
        setInterface(cls, true);
        String expected = ""
                + "interface MyClass extends SomeInterface, AnotherInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockInterfaceExtendsThree() {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface", "AnotherInterface", "Thingy"}));
        setInterface(cls, true);
        String expected = ""
                + "interface MyClass extends SomeInterface, AnotherInterface, Thingy {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockClassImplements() {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface"}));
        String expected = ""
                + "class MyClass implements SomeInterface {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockClassImplementsTwo() {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface", "AnotherInterface", "Xx"}));
        String expected = ""
                + "class MyClass implements SomeInterface, AnotherInterface, Xx {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockClassImplementsAndExtends() {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface", "AnotherInterface", "Xx"}));
        setSuperClass(cls, newType("SubMarine"));
        String expected = ""
                + "class MyClass extends SubMarine implements SomeInterface, AnotherInterface, Xx {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockModifers() {
        setName(cls, "MyClass");
        setModifiers(cls, Arrays.asList(new String[]{"public", "final"}));
        String expected = ""
                + "public final class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockModifersProtectionAlwaysFirst() {
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

    @Test
    public void testGetCodeBlockClassWithOneMethod() {
        setName(cls, "MyClass");
        JavaMethod mth = mock(JavaMethod.class);
        when(mth.getName()).thenReturn( "doStuff" );
        JavaType voidType = newType("void");
        when(mth.getReturnType()).thenReturn( voidType );
        
        setMethods(cls, Collections.singletonList( mth ));
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "\tvoid doStuff();\n"
                + "\n"
                + "}\n";
        assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockClassWithThreeMethods() {
        setName(cls, "MyClass");
        List<JavaMethod> methods = new ArrayList<JavaMethod>();
        {
            JavaMethod mth = mock(JavaMethod.class);
            when(mth.getName()).thenReturn( "doStuff" );
            JavaType voidType = newType( "void" );
            when(mth.getReturnType()).thenReturn( voidType );
            methods.add(mth);
        }

        {
            JavaMethod mth = mock(JavaMethod.class);
            when(mth.getName()).thenReturn( "somethingElse" );
            JavaType gooseType = newType("Goose");
            when(mth.getReturnType()).thenReturn( gooseType );
            methods.add(mth);
        }

        {
            JavaMethod mth = mock(JavaMethod.class);
            when(mth.getName()).thenReturn( "eat" );
            JavaType voidType = newType("void");
            when(mth.getReturnType()).thenReturn( voidType );
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

    @Test
    public void testGetCodeBlockClassWithTwoFields() {
        setName(cls, "MyClass");
        List<JavaField> fields = new ArrayList<JavaField>();
        {
            JavaField fld = mock( JavaField.class );
            when(fld.getName()).thenReturn( "count" );
            JavaClass intType = newJavaClass("int");
            when(fld.getType()).thenReturn( intType );
            when(fld.getDeclaringClass()).thenReturn( cls );
            fields.add( fld );
        }

        {
            JavaField fld = mock( JavaField.class );
            when(fld.getName()).thenReturn( "thing" );
            JavaClass stringType = newJavaClass( "String" );
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

    @Test
    public void testGetCodeBlockClassWithInnerClass() {
        setName(cls, "Outer");
        JavaClass innerClass = mock( JavaClass.class );
        when( innerClass.getName() ).thenReturn( "Inner" );
        setClasses(cls, Collections.singletonList( innerClass ) );

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

    @Test
    public void testGetCodeBlockClassWithInnerEnum() {
        setName(cls, "Outer");
        JavaClass innerEnum = mock( JavaClass.class );
        when( innerEnum.getName() ).thenReturn( "Inner" );
        when( innerEnum.isEnum() ).thenReturn( true );
        setClasses(cls, Collections.singletonList( innerEnum ) );

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

    @Test
    public void testGetCodeBlockEnumWithInnerClass() {
        setName(cls, "Outer");
        setEnum(cls, true);
        JavaClass innerClass = mock( JavaClass.class );
        when( innerClass.getName() ).thenReturn( "Inner" );
        setClasses(cls, Collections.singletonList( innerClass ) );

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

    @Test
    public void testGetCodeBlockClassWithComment() {
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

    @Test
    public void testGetCodeBlockClassWithIndentedCommentsForFieldAndMethod() {
        setName(cls, "MyClass");
        setComment(cls, "Hello World");

        JavaMethod mth = mock(JavaMethod.class);
        when(mth.getName()).thenReturn( "thingy" );
        JavaClass stringType = newJavaClass( "String" );
        when(mth.getReturnType()).thenReturn( stringType );
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

    @Test
    public void testIsPublic()
    {
        assertTrue( !cls.isPublic() );

        setModifiers( cls, Arrays.asList( new String[] { "public" } ) );
        assertTrue( cls.isPublic() );
    }

    @Test
    public void testIsProtected()
    {
        assertTrue( !cls.isProtected() );

        setModifiers( cls, Arrays.asList( new String[] { "protected" } ) );
        assertTrue( cls.isProtected() );
    }
    
    @Test
    public void testIsPrivate()
    {
        assertTrue( !cls.isPrivate() );

        setModifiers( cls, Arrays.asList( new String[] { "private" } ) );
        assertTrue( cls.isPrivate() );
    }

    @Test
    public void testIsAbstract()
    {
        assertTrue( !cls.isAbstract() );

        setModifiers( cls, Arrays.asList( new String[] { "abstract" } ) );
        assertTrue( cls.isAbstract() );
    }

    @Test
    public void testIsFinal()
    {
        assertTrue( !cls.isFinal() );

        setModifiers( cls, Arrays.asList( new String[] { "final" } ) );
        assertTrue( cls.isFinal() );
    }

    @Test
    public void testIsStatic()
    {
        assertTrue( !cls.isStatic() );

        setModifiers( cls, Arrays.asList( new String[] { "static" } ) );
        assertTrue( cls.isStatic() );
    }
    
    @Test
    public void testIsVoid()
    {
        setName( cls, "void" );
        assertTrue( cls.isVoid() );
        
        setName( cls, "Void" );
        assertFalse( cls.isVoid() );
    }

    @Test
    public void testQualifiedType() {
        setPackage(src, newJavaPackage("com.thoughtworks.qdox"));

        setName(cls, "MyClass");

        assertEquals("MyClass", cls.getName());
        assertEquals("MyClass", cls.getSimpleName());
        assertEquals("com.thoughtworks.qdox", cls.getPackage().getName());
        assertEquals("com.thoughtworks.qdox", cls.getPackageName());
        assertEquals("com.thoughtworks.qdox.MyClass", cls.getBinaryName());
        assertEquals("com.thoughtworks.qdox.MyClass", cls.getCanonicalName());
        assertEquals("com.thoughtworks.qdox.MyClass", cls.getFullyQualifiedName());
    }

    @Test
    public void testInnerClass()
    {
        JavaPackage pkg = mock(JavaPackage.class);
        when(pkg.getName()).thenReturn( "foo.bar" );

        JavaClass outer = mock( JavaClass.class );
        when( outer.getName() ).thenReturn( "Outer" );
        when( outer.getPackageName() ).thenReturn( "foo.bar" );
        when( outer.getFullyQualifiedName() ).thenReturn( "foo.bar.Outer" );
        when( outer.getBinaryName() ).thenReturn( "foo.bar.Outer" );

        C inner = newJavaClass();
        setName(inner, "Inner");
        setPackage( inner, pkg );
        setDeclaringClass( inner, outer );
        
        assertEquals("Inner", inner.getName());
        assertEquals("Inner", inner.getSimpleName());
        assertEquals("foo.bar", inner.getPackageName());
        assertEquals("foo.bar.Outer$Inner", inner.getBinaryName());
        assertEquals("foo.bar.Outer.Inner", inner.getCanonicalName());
        assertEquals("foo.bar.Outer.Inner", inner.getFullyQualifiedName());
    }
    
    @Test
    public void testDefaultPackageClass() {
    	setPackage(src, null);
    	setName(cls, "DefaultPackageClass");
    	
    	assertEquals("", src.getClasses().get(0).getPackageName());
    	assertEquals("DefaultPackageClass", src.getClasses().get(0).getFullyQualifiedName());
    }

    @Test
    public void testDefaultClassSuperclass() {
        setName(cls, "MyClass");
        assertEquals("java.lang.Object", cls.getSuperClass().getValue());
        setSuperClass(cls, newType("x.X"));
        assertEquals("x.X", cls.getSuperClass().getValue());
    }

    @Test
    public void testDefaultInterfaceSuperclass() {
        setName(cls, "MyInterface");
        setInterface(cls, true);
        assertNull(cls.getSuperClass());
        setSuperClass(cls, newType("x.X"));
        assertEquals("x.X", cls.getSuperClass().getValue());
    }

    @Test
    public void testEnumSuperclass() {
        setName(cls, "MyEnum");
        setEnum(cls, true);
        assertEquals("java.lang.Enum", cls.getSuperClass().getValue());
    }

    @Test
    public void testEnumCannotExtendAnythingElse() {
        setName(cls, "MyEnum");
        setEnum(cls, true);
        try {
            setSuperClass(cls, newType("x.X"));
            fail("expected an exception");
        } catch (IllegalArgumentException e) {
            assertEquals("enums cannot extend other classes", e.getMessage());
        }
    }
    
    @Test
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
    
    @Test
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
    
    @Test
    public void testCanGetFieldByName() {
        JavaField fredField = mock(JavaField.class);
        when(fredField.getName()).thenReturn( "fred" );
        JavaClass intType = newJavaClass("int");
        when(fredField.getType()).thenReturn( intType );
        when(fredField.getDeclaringClass()).thenReturn( cls );
        setFields( cls, Collections.singletonList( fredField ) );

        assertEquals(fredField, cls.getFieldByName("fred"));
        assertEquals(null, cls.getFieldByName("barney"));
    }

    @Test
    public void testCanGetMethodBySignature() {
        final String methodName = "doStuff";
        final List<JavaType> parameterTypes = javaType(new String[]{"int", "double"});
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
                cls.getMethodBySignature("doStuff", Collections.<JavaType>emptyList())
        );
        assertEquals(
                null,
                cls.getMethodBySignature("sitIdlyBy", parameterTypes)
        );
    }

    @Test
    public void testCanGetInnerClassByName() 
    {
        JavaClass innerClass = mock( JavaClass.class );
        when( innerClass.getName() ).thenReturn( "Inner" );
        setClasses(cls, Collections.singletonList( innerClass ) );

        assertEquals(innerClass, cls.getNestedClassByName("Inner"));
        assertEquals(null, cls.getNestedClassByName("Bogus"));
    }

    @Test
    public void testGetBeanPropertiesReturnsEmptyForEmptyClass() {
        assertEquals(0, cls.getBeanProperties().size());
    }

    @Test
    public void testGetBeanPropertiesFindsSimpleProperties() {
        List<JavaMethod> methods = new ArrayList<JavaMethod>();
        JavaMethod setFooMethod = mock(JavaMethod.class);
        when(setFooMethod.getName()).thenReturn( "setFoo" );
        JavaClass intType = newJavaClass("int");
        List<JavaParameter> parameters = Collections.singletonList( newJavaParameter( intType, "foo" ) );
        when(setFooMethod.getParameters()).thenReturn( parameters );
        when(setFooMethod.isPropertyMutator()).thenReturn( true );
        when(setFooMethod.getPropertyName()).thenReturn( "foo" );
        when(setFooMethod.getPropertyType()).thenReturn( intType );
        methods.add(setFooMethod);

        JavaMethod getFooMethod = mock(JavaMethod.class);
        when(getFooMethod.getName()).thenReturn( "getFoo" );
        when(getFooMethod.getReturnType()).thenReturn( intType );
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
    
    @Test
    public void testToStringClass() {
    	setName(cls, "com.MyClass");
    	assertEquals("class com.MyClass", cls.toString());
    }
    
    @Test
    public void testInnerClassToString() {
    	JavaClass jOuterClass = mock(JavaClass.class);
    	when(jOuterClass.getFullyQualifiedName()).thenReturn( "com.thoughtworks.qdox.model.OuterClass" );
    	
        C jInnerClass = newJavaClass( "InnerClass" );
    	setDeclaringClass( jInnerClass, jOuterClass );
    	
    	assertEquals("class com.thoughtworks.qdox.model.OuterClass.InnerClass", jInnerClass.toString());
    }
    
    @Test
    public void testInnerClassType() {
        JavaClass jOuterClass = mock(JavaClass.class);
        when(jOuterClass.getFullyQualifiedName()).thenReturn("com.thoughtworks.qdox.model.OuterClass");

        C jInnerClass = newJavaClass("InnerClass");
        setDeclaringClass( jInnerClass, jOuterClass );
        
        assertEquals( "com.thoughtworks.qdox.model.OuterClass.InnerClass", jInnerClass.getFullyQualifiedName() );
    }
    
    @Test
    public void testInnerInterfaceToString() {
    	JavaClass jOuterClass = mock(JavaClass.class);
    	when(jOuterClass.getFullyQualifiedName()).thenReturn( "com.thoughtworks.qdox.model.OuterClass");

    	C jInnerInterface = newJavaClass( "InnerInterface" );
    	setInterface( jInnerInterface, true );
        setDeclaringClass( jInnerInterface, jOuterClass );
        
    	assertEquals("interface com.thoughtworks.qdox.model.OuterClass.InnerInterface", jInnerInterface.toString());
    }
    
    @Test
    public void testToStringInterface() {
    	setName(cls, "com.MyClass");
    	setInterface(cls, true);
    	assertEquals("interface com.MyClass", cls.toString());
    }
    
    @Test
    public void testEquals()
    {
        C c1 = newJavaClass( "java.util.String" );
        C c2 = newJavaClass( "java.util.String" );
        C c3 = newJavaClass( "org.mycompany.String" );

        assertEquals( c1, c1 );
        assertThat( c1, not( new Object() ) );
        assertEquals( c1, c2 );
        assertThat( c1, not( c3 ) );
    }    

    /**
     * @codehaus.jira QDOX-59
     */
    @Test
    public void testBeanPropertiesAreReturnedInOrderDeclared() {
        List<JavaMethod> methods = new ArrayList<JavaMethod>();
        JavaMethod getFooMethod = mock(JavaMethod.class);
        when(getFooMethod.getName()).thenReturn( "getFoo" );
        JavaClass intType = newJavaClass("int");
        when(getFooMethod.getReturnType()).thenReturn( intType );
        when(getFooMethod.getPropertyName()).thenReturn( "foo" );
        when(getFooMethod.isPropertyAccessor()).thenReturn( true );
        methods.add( getFooMethod );

        JavaMethod getBarMethod = mock(JavaMethod.class);
        when(getBarMethod.getName()).thenReturn( "getBar" );
        when(getBarMethod.getReturnType()).thenReturn( intType );
        when(getBarMethod.getPropertyName()).thenReturn( "bar" );
        when(getBarMethod.isPropertyAccessor()).thenReturn( true );
        methods.add( getBarMethod );
        
        JavaMethod getMcFNordMethod = mock(JavaMethod.class);
        when(getMcFNordMethod.getName()).thenReturn( "getMcFnord" );
        JavaClass stringType = newJavaClass("String");
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
    
    private List<JavaClass> type(String[] typeNames) {
        List<JavaClass> result = new LinkedList<JavaClass>();
        for (int i = 0; i < typeNames.length; i++) {
            result.add(newJavaClass(typeNames[i]));
        }
        return result;
    }
    
    private List<JavaType> javaType(String[] typeNames)
    {
        return new LinkedList<JavaType>(type(typeNames));
    }
    
    // QDOX-201
    @Test
    public void testGetVarArgMethodSignature() {
        List<JavaMethod> methods = new ArrayList<JavaMethod>();
        JavaMethod simpleMethod = mock(JavaMethod.class);
        
        JavaType stringType = newType( "String" );
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
 
    @Ignore
    public void testJavaLangObjectAsDefaultSuperClass() {
        //up untill now this succeeds, because other tests have already set the static value of OBJECT
        //running this test alone make it fail, so it's not a proper test.
        //should be fixed if we can get rid of the Type-visibility
        JavaClass clazz = newJavaClass( "a.b.Sample" );
        assertEquals( "java.lang.Object", clazz.getSuperJavaClass().getFullyQualifiedName() );
        assertEquals( "java.lang.Object", clazz.getSuperClass().getFullyQualifiedName() );
    }
    
}
