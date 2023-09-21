package com.thoughtworks.qdox.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @BeforeEach
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockSimpleInterface() {
        setName(cls, "MyClass");
        setInterface(cls, true);
        String expected = ""
                + "interface MyClass {\n"
                + "\n"
                + "}\n";
        Assertions.assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockSimpleEnum() {
        setName(cls, "MyEnum");
        setEnum(cls, true);
        String expected = ""
                + "enum MyEnum {\n"
                + "\n"
                + "}\n";
        Assertions.assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockClassExtends() {
        setName(cls, "MyClass");
        setSuperClass(cls, newType("SuperClass"));
        String expected = ""
                + "class MyClass extends SuperClass {\n"
                + "\n"
                + "}\n";
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockClassImplements() {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface"}));
        String expected = ""
                + "class MyClass implements SomeInterface {\n"
                + "\n"
                + "}\n";
        Assertions.assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockClassImplementsTwo() {
        setName(cls, "MyClass");
        setImplementz(cls, type(new String[]{"SomeInterface", "AnotherInterface", "Xx"}));
        String expected = ""
                + "class MyClass implements SomeInterface, AnotherInterface, Xx {\n"
                + "\n"
                + "}\n";
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockModifers() {
        setName(cls, "MyClass");
        setModifiers(cls, Arrays.asList(new String[]{"public", "final"}));
        String expected = ""
                + "public final class MyClass {\n"
                + "\n"
                + "}\n";
        Assertions.assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockModifersProtectionAlwaysFirst() {
        setName(cls, "MyClass");
        setModifiers(cls, Arrays.asList(new String[]{"final", "public"}));
        String expected = ""
                + "public final class MyClass {\n"
                + "\n"
                + "}\n";
        Assertions.assertEquals(expected, cls.getCodeBlock());

        setModifiers(cls, Arrays.asList(new String[]{"abstract", "protected"}));
        expected = ""
                + "protected abstract class MyClass {\n"
                + "\n"
                + "}\n";
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
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
        Assertions.assertEquals(expected, cls.getCodeBlock());
    }

    @Test
    public void testIsPublic()
    {
        Assertions.assertTrue(!cls.isPublic());

        setModifiers( cls, Arrays.asList( new String[] { "public" } ) );
        Assertions.assertTrue(cls.isPublic());
    }

    @Test
    public void testIsProtected()
    {
        Assertions.assertTrue(!cls.isProtected());

        setModifiers( cls, Arrays.asList( new String[] { "protected" } ) );
        Assertions.assertTrue(cls.isProtected());
    }
    
    @Test
    public void testIsPrivate()
    {
        Assertions.assertTrue(!cls.isPrivate());

        setModifiers( cls, Arrays.asList( new String[] { "private" } ) );
        Assertions.assertTrue(cls.isPrivate());
    }

    @Test
    public void testIsAbstract()
    {
        Assertions.assertTrue(!cls.isAbstract());

        setModifiers( cls, Arrays.asList( new String[] { "abstract" } ) );
        Assertions.assertTrue(cls.isAbstract());
    }

    @Test
    public void testIsFinal()
    {
        Assertions.assertTrue(!cls.isFinal());

        setModifiers( cls, Arrays.asList( new String[] { "final" } ) );
        Assertions.assertTrue(cls.isFinal());
    }

    @Test
    public void testIsStatic()
    {
        Assertions.assertTrue(!cls.isStatic());

        setModifiers( cls, Arrays.asList( new String[] { "static" } ) );
        Assertions.assertTrue(cls.isStatic());
    }
    
    @Test
    public void testIsVoid()
    {
        setName( cls, "void" );
        Assertions.assertTrue(cls.isVoid());
        
        setName( cls, "Void" );
        Assertions.assertFalse(cls.isVoid());
    }

    @Test
    public void testQualifiedType() {
        setPackage(src, newJavaPackage("com.thoughtworks.qdox"));

        setName(cls, "MyClass");

        Assertions.assertEquals("MyClass", cls.getName());
        Assertions.assertEquals("MyClass", cls.getSimpleName());
        Assertions.assertEquals("com.thoughtworks.qdox", cls.getPackage().getName());
        Assertions.assertEquals("com.thoughtworks.qdox", cls.getPackageName());
        Assertions.assertEquals("com.thoughtworks.qdox.MyClass", cls.getBinaryName());
        Assertions.assertEquals("com.thoughtworks.qdox.MyClass", cls.getCanonicalName());
        Assertions.assertEquals("com.thoughtworks.qdox.MyClass", cls.getFullyQualifiedName());
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
        
        Assertions.assertEquals("Inner", inner.getName());
        Assertions.assertEquals("Inner", inner.getSimpleName());
        Assertions.assertEquals("foo.bar", inner.getPackageName());
        Assertions.assertEquals("foo.bar.Outer$Inner", inner.getBinaryName());
        Assertions.assertEquals("foo.bar.Outer.Inner", inner.getCanonicalName());
        Assertions.assertEquals("foo.bar.Outer.Inner", inner.getFullyQualifiedName());
    }
    
    @Test
    public void testDefaultPackageClass() {
    	setPackage(src, null);
    	setName(cls, "DefaultPackageClass");
    	
    	Assertions.assertEquals("", src.getClasses().get(0).getPackageName());
    	Assertions.assertEquals("DefaultPackageClass", src.getClasses().get(0).getFullyQualifiedName());
    }

    @Test
    public void testDefaultClassSuperclass() {
        setName(cls, "MyClass");
        Assertions.assertEquals("java.lang.Object", cls.getSuperClass().getValue());
        setSuperClass(cls, newType("x.X"));
        Assertions.assertEquals("x.X", cls.getSuperClass().getValue());
    }

    @Test
    public void testDefaultInterfaceSuperclass() {
        setName(cls, "MyInterface");
        setInterface(cls, true);
        Assertions.assertNull(cls.getSuperClass());
        setSuperClass(cls, newType("x.X"));
        Assertions.assertEquals("x.X", cls.getSuperClass().getValue());
    }

    @Test
    public void testEnumSuperclass() {
        setName(cls, "MyEnum");
        setEnum(cls, true);
        Assertions.assertEquals("java.lang.Enum", cls.getSuperClass().getValue());
    }

    @Test
    public void testEnumCannotExtendAnythingElse() {
        setName(cls, "MyEnum");
        setEnum(cls, true);
        try {
            setSuperClass(cls, newType("x.X"));
            Assertions.fail("expected an exception");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("enums cannot extend other classes", e.getMessage());
        }
    }
    
    @Test
    public void testGetEnumConstants() {
        setName( cls, "MyEnum" );
        Assertions.assertNull(cls.getEnumConstants());
        
        setEnum( cls, true );
        Assertions.assertNotNull(cls.getEnumConstants());
        Assertions.assertEquals(0, cls.getEnumConstants().size());
        
        List<JavaField> fields = new ArrayList<JavaField>();
        JavaField nonEnumConstantField = mock(JavaField.class);
        fields.add( nonEnumConstantField );
        setFields( cls, fields );
        Assertions.assertEquals(0, cls.getEnumConstants().size());
        
        JavaField enumConstantField = mock(JavaField.class);
        when ( enumConstantField.isEnumConstant() ).thenReturn( true );
        fields.add( enumConstantField );
        setFields( cls, fields );
        Assertions.assertEquals(1, cls.getEnumConstants().size());
    }
    
    @Test
    public void testGetEnumConstantByName() {
        setName( cls, "MyEnum" );
        
        List<JavaField> fields = new ArrayList<JavaField>();
        JavaField nonEnumConstantField = mock(JavaField.class);
        when ( nonEnumConstantField.getName() ).thenReturn( "nonEnumField" );
        fields.add( nonEnumConstantField );
        setFields( cls, fields );
        Assertions.assertEquals(null, cls.getEnumConstantByName( "nonEnumField" ));
        
        JavaField enumConstantField = mock(JavaField.class);
        when ( enumConstantField.isEnumConstant() ).thenReturn( true );
        when ( enumConstantField.getName() ).thenReturn( "enumField" );
        fields.add( enumConstantField );
        setFields( cls, fields );
        Assertions.assertEquals(enumConstantField, cls.getEnumConstantByName( "enumField" ));
    }
    
    @Test
    public void testCanGetFieldByName() {
        JavaField fredField = mock(JavaField.class);
        when(fredField.getName()).thenReturn( "fred" );
        JavaClass intType = newJavaClass("int");
        when(fredField.getType()).thenReturn( intType );
        when(fredField.getDeclaringClass()).thenReturn( cls );
        setFields( cls, Collections.singletonList( fredField ) );

        Assertions.assertEquals(fredField, cls.getFieldByName("fred"));
        Assertions.assertEquals(null, cls.getFieldByName("barney"));
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

        Assertions.assertSame(method, cls.getMethodBySignature("doStuff", parameterTypes));
        Assertions.assertEquals(null, cls.getMethodBySignature("doStuff", Collections.<JavaType>emptyList()));
        Assertions.assertEquals(null, cls.getMethodBySignature("sitIdlyBy", parameterTypes));
    }

    @Test
    public void testCanGetInnerClassByName() 
    {
        JavaClass innerClass = mock( JavaClass.class );
        when( innerClass.getName() ).thenReturn( "Inner" );
        setClasses(cls, Collections.singletonList( innerClass ) );

        Assertions.assertEquals(innerClass, cls.getNestedClassByName("Inner"));
        Assertions.assertEquals(null, cls.getNestedClassByName("Bogus"));
    }

    @Test
    public void testGetBeanPropertiesReturnsEmptyForEmptyClass() {
        Assertions.assertEquals(0, cls.getBeanProperties().size());
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
        
        Assertions.assertEquals(1, cls.getBeanProperties().size());
        BeanProperty fooProp = cls.getBeanProperties().get(0);
        Assertions.assertEquals("foo", fooProp.getName());
        Assertions.assertEquals(intType, fooProp.getType());
        Assertions.assertEquals(getFooMethod, fooProp.getAccessor());
        Assertions.assertEquals(setFooMethod, fooProp.getMutator());
    }
    
    @Test
    public void testToStringClass() {
    	setName(cls, "com.MyClass");
    	Assertions.assertEquals("class com.MyClass", cls.toString());
    }
    
    @Test
    public void testInnerClassToString() {
    	JavaClass jOuterClass = mock(JavaClass.class);
    	when(jOuterClass.getFullyQualifiedName()).thenReturn( "com.thoughtworks.qdox.model.OuterClass" );
    	
        C jInnerClass = newJavaClass( "InnerClass" );
    	setDeclaringClass( jInnerClass, jOuterClass );
    	
    	Assertions.assertEquals("class com.thoughtworks.qdox.model.OuterClass.InnerClass", jInnerClass.toString());
    }
    
    @Test
    public void testInnerClassType() {
        JavaClass jOuterClass = mock(JavaClass.class);
        when(jOuterClass.getFullyQualifiedName()).thenReturn("com.thoughtworks.qdox.model.OuterClass");

        C jInnerClass = newJavaClass("InnerClass");
        setDeclaringClass( jInnerClass, jOuterClass );
        
        Assertions.assertEquals("com.thoughtworks.qdox.model.OuterClass.InnerClass", jInnerClass.getFullyQualifiedName());
    }
    
    @Test
    public void testInnerInterfaceToString() {
    	JavaClass jOuterClass = mock(JavaClass.class);
    	when(jOuterClass.getFullyQualifiedName()).thenReturn( "com.thoughtworks.qdox.model.OuterClass");

    	C jInnerInterface = newJavaClass( "InnerInterface" );
    	setInterface( jInnerInterface, true );
        setDeclaringClass( jInnerInterface, jOuterClass );
        
    	Assertions.assertEquals("interface com.thoughtworks.qdox.model.OuterClass.InnerInterface", jInnerInterface.toString());
    }
    
    @Test
    public void testToStringInterface() {
    	setName(cls, "com.MyClass");
    	setInterface(cls, true);
    	Assertions.assertEquals("interface com.MyClass", cls.toString());
    }
    
    @Test
    public void testEquals()
    {
        C c1 = newJavaClass( "java.util.String" );
        C c2 = newJavaClass( "java.util.String" );
        C c3 = newJavaClass( "org.mycompany.String" );

        assertThat(c1).isEqualTo(c1);
        assertThat(c1).isNotEqualTo(new Object() );
        assertThat(c1).isEqualTo(c2);
        assertThat(c1).isNotEqualTo(c3);
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
        Assertions.assertEquals(3, properties.size());
        Assertions.assertEquals("foo", properties.get(0).getName());
        Assertions.assertEquals("bar", properties.get(1).getName());
        Assertions.assertEquals("mcFnord", properties.get(2).getName());
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
        
        Assertions.assertEquals(simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ) ));
        Assertions.assertEquals(simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), false ));
        Assertions.assertEquals(simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), true ));
        Assertions.assertEquals(simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), false, false ));
        Assertions.assertEquals(varArgMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), false, true ));
        Assertions.assertEquals(simpleMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), true, false ));
        Assertions.assertEquals(varArgMethod, cls.getMethodBySignature( "doSomething", Collections.singletonList( stringType ), true, true ));
    }
 
    @Disabled
    public void testJavaLangObjectAsDefaultSuperClass() {
        //up untill now this succeeds, because other tests have already set the static value of OBJECT
        //running this test alone make it fail, so it's not a proper test.
        //should be fixed if we can get rid of the Type-visibility
        JavaClass clazz = newJavaClass( "a.b.Sample" );
        Assertions.assertEquals("java.lang.Object", clazz.getSuperJavaClass().getFullyQualifiedName());
        Assertions.assertEquals("java.lang.Object", clazz.getSuperClass().getFullyQualifiedName());
    }
    
}
