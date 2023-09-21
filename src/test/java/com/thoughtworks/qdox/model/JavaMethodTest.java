package com.thoughtworks.qdox.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class JavaMethodTest<M extends JavaMethod> {

    private M mth;

    //constructors
    public abstract M newJavaMethod();
    public abstract M newJavaMethod(JavaClass returns, String name);

    //setters
    public abstract void setExceptions(M method, List<JavaClass> exceptions);
    public abstract void setComment(M method, String comment);
    public abstract void setName(M method, String name);
    public abstract void setModifiers(M method, List<String> modifiers);
    public abstract void setParameters(M method, List<JavaParameter> parameters);
    public abstract void setDeclaringClass(M method, JavaClass clazz);
    public abstract void setReturns(M method, JavaClass type);
    public abstract void setSourceCode(M method, String code);
    
    public JavaParameter newJavaParameter(JavaClass type, String name)
    {
        return newJavaParameter( type, name, false );
    }
    
    public JavaParameter newJavaParameter(JavaClass type, String name, boolean varArgs) 
    {
        JavaParameter result = mock(JavaParameter.class);
        when( result.getType() ).thenReturn( type );
        String genericCanonicalName = type.getGenericCanonicalName();
        when( result.getGenericCanonicalName() ).thenReturn( genericCanonicalName );
        when( result.getName() ).thenReturn( name );
        when( result.isVarArgs() ).thenReturn( varArgs );
        return result;
    }
    
    public JavaClass newType( String fullname )
    {
        return newType( fullname, 0 );
    }

    public JavaClass newType(String fullname, int dimensions) 
    {
        JavaClass result = mock( JavaClass.class );
        when( result.getFullyQualifiedName() ).thenReturn( fullname );
        String canonicalName = fullname.replace( '$', '.' );
        when( result.getValue() ).thenReturn( canonicalName );
        when( result.getDimensions()).thenReturn( dimensions );
        for(int i = 0; i < dimensions; i++)
        {
            canonicalName += "[]";
        }
        when( result.getCanonicalName() ).thenReturn( canonicalName );
        when( result.getGenericCanonicalName() ).thenReturn( canonicalName );
        return result;
    }
    
    @BeforeEach
    public void setUp() {
        mth = newJavaMethod();
    }
    
    private void createSignatureTestMethod() {
        setName(mth, "blah");
        setModifiers(mth, Arrays.asList(new String[]{"protected", "final"}));
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new JavaClass[] {
            newType("FishException"),
            newType("FruitException"),
        } ));
        setParameters( mth, Arrays.asList( newJavaParameter(newType("int"), "count"), newJavaParameter(newType("MyThing"), "t") ) );
    }


    @Test
    public void testDeclarationSignatureWithModifiers() {
        createSignatureTestMethod();
        String signature = mth.getDeclarationSignature(true);
        Assertions.assertEquals("protected final void blah(int count, MyThing t) throws FishException, FruitException", signature);
    }

    @Test
    public void testDeclarationSignatureWithoutModifiers() {
        createSignatureTestMethod();
        String signature = mth.getDeclarationSignature(false);
        Assertions.assertEquals("void blah(int count, MyThing t) throws FishException, FruitException", signature);
    }

    @Test
    public void testCallSignature() {
        createSignatureTestMethod();
        String signature = mth.getCallSignature();
        Assertions.assertEquals("blah(count, t)", signature);
    }

//    public void testSignatureWithVarArgs() throws Exception {
//        mth.setName( "method" );
//        mth.addParameter( new JavaParameter(new Type("java.lang.String"), "param", true) );
//        assertEquals( mth, clazz.getMethodBySignature( "method", new Type[] { new Type("java.lang.String", true)} ) );
//    }
    
    @Test
    public void testGetCodeBlockSimple() {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String"));
        Assertions.assertEquals("java.lang.String doSomething();\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockOneParam() {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters( mth, Collections.singletonList( newJavaParameter(newType("String"), "thingy") ) );
        Assertions.assertEquals("void blah(String thingy);\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockTwoParams() {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int"), "count"), newJavaParameter(newType("MyThing"), "t") ) );
        Assertions.assertEquals("void blah(int count, MyThing t);\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockThreeParams() {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int"), "count"), newJavaParameter(newType("MyThing"), "t"), newJavaParameter(newType("java.lang.Meat"), "beef") ));
        Assertions.assertEquals("void blah(int count, MyThing t, java.lang.Meat beef);\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockModifiersWithAccessLevelFirst() {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setModifiers(mth, Arrays.asList(new String[]{"synchronized", "public", "final"}));
        Assertions.assertEquals("public synchronized final void blah();\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockOneException() {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions( mth, Arrays.asList( new JavaClass[] { newType( "RuntimeException" ) } ) );
        Assertions.assertEquals("void blah() throws RuntimeException;\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockTwoException() {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new JavaClass[]{newType("RuntimeException"), newType("java.lang.SheepException")}));
        Assertions.assertEquals("void blah() throws RuntimeException, java.lang.SheepException;\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockThreeException() {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new JavaClass[]{newType("RuntimeException"), newType("java.lang.SheepException"), newType("CowException")}));
        Assertions.assertEquals("void blah() throws RuntimeException, java.lang.SheepException, CowException;\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockWithComment() {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setComment(mth, "Hello");
        String expect = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "void blah();\n";
        Assertions.assertEquals(expect, mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlock1dArray() {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String", 1));
        Assertions.assertEquals("java.lang.String[] doSomething();\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlock2dArray() {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String", 2));
        Assertions.assertEquals("java.lang.String[][] doSomething();\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockParamArray() {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters( mth, Arrays.asList( newJavaParameter( newType("int", 2), "count"), newJavaParameter( newType("MyThing", 1), "t") ) );
        Assertions.assertEquals("void blah(int[][] count, MyThing[] t);\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockWithBody() {
        setName(mth, "doStuff");
        setReturns(mth, newType("java.lang.String"));
        setSourceCode(mth, "  int x = 2;\n  return STUFF;\n");

        Assertions.assertEquals("" +
                "java.lang.String doStuff() {\n" +
                "  int x = 2;\n" +
                "  return STUFF;\n" +
                "}\n", mth.getCodeBlock());
    }
    
    @Test
    public void testEquals() {
        JavaClass voidType = newType("void");

        setName(mth, "thing");
        setReturns(mth, voidType);

        M m2 = newJavaMethod();
        setName(m2, "thing");
        setReturns(m2, voidType);

        M m3 = newJavaMethod();
        setName(m3, "thingy");
        setReturns(m3, voidType);

        M m4 = newJavaMethod();
        setName(m4, "thing");
        setReturns(m4, newType("int"));

        M m5 = newJavaMethod();
        M m6 = newJavaMethod();
        
        M m7 = newJavaMethod();
        setReturns(m7, newType("int"));
        
        M m8 = newJavaMethod();
        setReturns(m8, newType("int"));
//        JavaClass declaringClass = mock( JavaClass.class );
//        when( declaringClass.getFullyQualifiedName() ).thenReturn( "com.foo.bar" );
        setDeclaringClass( m8, mock( JavaClass.class ) );

        assertThat(mth).isEqualTo(mth);
        assertThat(mth).isNotEqualTo(new Object());
        assertThat(mth).isEqualTo(m2);
        assertThat(m2).isEqualTo(mth);
        assertThat(mth).isNotEqualTo(m3);
        assertThat(mth).isNotEqualTo(m4);
        assertThat(mth).isNotEqualTo(null);
        assertThat(m4).isNotEqualTo(m5);
        assertThat(m5).isNotEqualTo(m4);
        assertThat(m5).isEqualTo(m6);
        assertThat(m5).isNotEqualTo(m7);
        assertThat(m7).isNotEqualTo(m8);
    }

    @Test
    public void testEqualsWithParameters() {
        JavaClass voidType = newType("void");
        JavaClass intArrayType = newType("int", 1);
        JavaClass stringArrayType = newType("java.lang.String", 2);
        JavaClass xArrayType = newType("X", 3);

        JavaParameter intArrayParam = newJavaParameter(intArrayType, "blah");
        JavaParameter stringArrayParam = newJavaParameter(stringArrayType, "thing");
        JavaParameter xArrayParameter = newJavaParameter(xArrayType, "blah");
        
        setName( mth, "thing" );
        setParameters( mth, Arrays.asList( intArrayParam, stringArrayParam, xArrayParameter ) );
        setReturns( mth, voidType );

        M m2 = newJavaMethod();
        setName( m2, "thing" );
        setParameters( m2, Arrays.asList( intArrayParam, stringArrayParam, xArrayParameter ) );
        setReturns( m2, voidType );

        M m3 = newJavaMethod();
        setName( m3, "thing" );
        setParameters( m3, Arrays.asList( intArrayParam, stringArrayParam ) );
        setReturns( m3, voidType );

        // dimension
        M m5 = newJavaMethod();
        setName( m5, "thing" );
        setParameters( m5,
                       Arrays.asList( intArrayParam, stringArrayParam, newJavaParameter( newType( "X", 9 ), "blah" ) ) );
        setReturns( m5, voidType );

        assertThat(mth).isEqualTo(m2);
        assertThat(m2).isEqualTo(mth);
        assertThat(mth).isNotEqualTo(m3);
        assertThat(mth).isNotEqualTo(m5);
    }

    @Test
    public void testHashCode()
    {
        Assertions.assertTrue(newJavaMethod( newType("void"), "" ).hashCode() != 0, "hashCode should never resolve to 0");

        JavaClass voidType = newType( "void" );
        JavaClass intType = newType( "int", 1 );
        JavaClass stringArrayType = newType( "java.lang.String", 2 );
        JavaClass xArrayType = newType( "X", 3 );

        JavaParameter intParam = newJavaParameter( intType, "blah" );
        JavaParameter stringArrayParam = newJavaParameter( stringArrayType, "thing" );
        JavaParameter xArrayParam = newJavaParameter( xArrayType, "blah" );

        setName( mth, "thing" );
        setParameters( mth, Arrays.asList( intParam, stringArrayParam, xArrayParam ) );
        setReturns( mth, voidType );

        M m2 = newJavaMethod();
        setName( m2, "thing" );
        setParameters( m2, Arrays.asList( intParam, stringArrayParam, xArrayParam ) );
        setReturns( m2, voidType );

        M m3 = newJavaMethod();
        setName( m3, "thing" );
        setParameters( m3, Arrays.asList( intParam, stringArrayParam ) );
        setReturns( m3, voidType );

        Assertions.assertEquals(mth.hashCode(), m2.hashCode());
        Assertions.assertTrue(mth.hashCode() != m3.hashCode());
    }

    @Test
   public void testSignatureMatches() {
        JavaClass intType = newType("int");
        JavaClass longArrayType = newType("long", 2);

        setName(mth, "thing");
        setParameters(mth, Arrays.asList( newJavaParameter(intType, "x"), newJavaParameter(longArrayType, "y") ));
        setReturns(mth, newType("void"));

        JavaType[] correctTypes = new JavaClass[]{
            intType,
            longArrayType
        };

        JavaType[] wrongTypes1 = new JavaClass[]{
            newType("int", 2),
            newType("long")
        };

        JavaType[] wrongTypes2 = new JavaClass[]{
            intType,
            longArrayType,
            newType("double")
        };

        Assertions.assertTrue(mth.signatureMatches("thing", Arrays.asList( correctTypes )));
        Assertions.assertFalse(mth.signatureMatches("xxx", Arrays.asList( correctTypes )));
        Assertions.assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes1 )));
        Assertions.assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes2 )));
    }
    
    @Test
    public void testVarArgSignatureMatches() {
        JavaClass intType = newType("int");
        JavaClass longArrayType = newType("long", 2);

        setName(mth, "thing");
        setParameters(mth, Arrays.asList( newJavaParameter(intType, "x"), newJavaParameter(longArrayType, "y", true) ));
        setReturns(mth, newType("void"));

        JavaType[] correctTypes = new JavaClass[]{
            intType,
            longArrayType
        };

        JavaType[] wrongTypes1 = new JavaClass[]{
            newType("int", 2),
            newType("long")
        };

        JavaType[] wrongTypes2 = new JavaClass[]{
            intType,
            longArrayType,
            newType("double")
        };

        Assertions.assertTrue(mth.signatureMatches("thing", Arrays.asList( correctTypes ), true));
        Assertions.assertFalse(mth.signatureMatches("thing", Arrays.asList( correctTypes ), false));
        Assertions.assertFalse(mth.signatureMatches("xxx", Arrays.asList( correctTypes ), true));
        Assertions.assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes1 ), true));
        Assertions.assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes2 ), true));
    }

    @Test
    public void testParentClass() {
        JavaClass clazz = mock(JavaClass.class);
        setDeclaringClass( mth, clazz );
        Assertions.assertSame(clazz, mth.getDeclaringClass());
    }

    @Test
    public void testCanGetParameterByName() {
        JavaParameter paramX = newJavaParameter(newType("int"), "x");
        setParameters(mth, Arrays.asList( paramX, newJavaParameter(newType("string"), "y") ));
        
        Assertions.assertEquals(paramX, mth.getParameterByName("x"));
        Assertions.assertEquals(null, mth.getParameterByName("z"));
    }

    @Test
    public void testToString() {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getBinaryName()).thenReturn( "java.lang.Object" );
    	M mthd = newJavaMethod(newType("boolean"),"equals");
    	setDeclaringClass(mthd, cls);
    	setModifiers(mthd, Arrays.asList(new String[]{"public"}));
    	setParameters(mthd, Collections.singletonList( newJavaParameter(newType("java.lang.Object"), null) ));
    	Assertions.assertEquals("public boolean java.lang.Object.equals(java.lang.Object)", mthd.toString());
    }
    
    @Test
    public void testIsPublic()
    {
        Assertions.assertTrue(!mth.isPublic());

        setModifiers( mth, Arrays.asList( new String[] { "public" } ) );
        Assertions.assertTrue(mth.isPublic());
    }

    @Test
    public void testIsProtected()
    {
        Assertions.assertTrue(!mth.isProtected());

        setModifiers( mth, Arrays.asList( new String[] { "protected" } ) );
        Assertions.assertTrue(mth.isProtected());
    }
    
    @Test
    public void testIsPrivate()
    {
        Assertions.assertTrue(!mth.isPrivate());

        setModifiers( mth, Arrays.asList( new String[] { "private" } ) );
        Assertions.assertTrue(mth.isPrivate());
    }

    @Test
    public void testIsAbstract()
    {
        Assertions.assertTrue(!mth.isAbstract());

        setModifiers( mth, Arrays.asList( new String[] { "abstract" } ) );
        Assertions.assertTrue(mth.isAbstract());
    }

    @Test
    public void testIsFinal()
    {
        Assertions.assertTrue(!mth.isFinal());

        setModifiers( mth, Arrays.asList( new String[] { "final" } ) );
        Assertions.assertTrue(mth.isFinal());
    }

    @Test
    public void testIsNavite()
    {
        Assertions.assertTrue(!mth.isNative());

        setModifiers( mth, Arrays.asList( new String[] { "native" } ) );
        Assertions.assertTrue(mth.isNative());
    }

    @Test
    public void testIsStatic()
    {
        Assertions.assertTrue(!mth.isStatic());

        setModifiers( mth, Arrays.asList( new String[] { "static" } ) );
        Assertions.assertTrue(mth.isStatic());
    }
    
    @Test
    public void testIsStrict()
    {
        Assertions.assertTrue(!mth.isStrictfp());

        setModifiers( mth, Arrays.asList( new String[] { "strictfp" } ) );
        Assertions.assertTrue(mth.isStrictfp());
    }

    @Test
    public void testIsSynchronized()
    {
        Assertions.assertTrue(!mth.isSynchronized());

        setModifiers( mth, Arrays.asList( new String[] { "synchronized" } ) );
        Assertions.assertTrue(mth.isSynchronized());
    }
    
    @Test
    public void testIsTransient()
    {
        Assertions.assertTrue(!mth.isTransient());

        setModifiers( mth, Arrays.asList( new String[] { "transient" } ) );
        Assertions.assertTrue(mth.isTransient());
    }
    
    @Test
    public void testIsVolatile()
    {
        Assertions.assertTrue(!mth.isVolatile());

        setModifiers( mth, Arrays.asList( new String[] { "volatile" } ) );
        Assertions.assertTrue(mth.isVolatile());
    }
    
    @Test
    public void testIsPropertyAccessor() 
    {
        M getNameMethod = newJavaMethod( newType( "java.lang.String" ), "getName" );
        Assertions.assertTrue(getNameMethod.isPropertyAccessor());

        M isValidMethod = newJavaMethod( newType( "boolean" ), "isValid" );
        Assertions.assertTrue(isValidMethod.isPropertyAccessor());

        M getNameWithParamMethod = newJavaMethod( newType( "boolean" ), "getName" );
        setParameters( getNameWithParamMethod, Collections.singletonList( mock(JavaParameter.class) ) );
        Assertions.assertFalse(getNameWithParamMethod.isPropertyAccessor());

        M gettingUpMethod = newJavaMethod( newType( "java.lang.String" ), "gettingUp" );
        Assertions.assertFalse(gettingUpMethod.isPropertyAccessor());

        M isolatedMethod = newJavaMethod( newType( "boolean" ), "isolated" );
        Assertions.assertFalse(isolatedMethod.isPropertyAccessor());

        M staticGetNameMethod = newJavaMethod( newType( "java.lang.String" ), "getName" );
        setModifiers( staticGetNameMethod, Collections.singletonList( "static" ) );
        Assertions.assertFalse(staticGetNameMethod.isPropertyAccessor());
    }
    
    @Test
    public void testIsPropertyMutator()
    {
        M setNameMethod = newJavaMethod( newType("void"), "setName" );
        setParameters( setNameMethod, Collections.singletonList( mock(JavaParameter.class) ) );
        Assertions.assertTrue(setNameMethod.isPropertyMutator());

        M setUpMethod = newJavaMethod( newType("void"), "setUp" );
        Assertions.assertFalse(setUpMethod.isPropertyMutator());

        M settingUpMethod = newJavaMethod( newType("void"), "settingUp" );
        setParameters( settingUpMethod, Collections.singletonList( mock(JavaParameter.class) ) );
        Assertions.assertFalse(settingUpMethod.isPropertyMutator());

        M staticSetNameMethod = newJavaMethod( newType("void"), "setName" );
        setModifiers( staticSetNameMethod, Collections.singletonList( "static" ) );
        setParameters( staticSetNameMethod, Collections.singletonList( mock(JavaParameter.class) ) );
        Assertions.assertFalse(staticSetNameMethod.isPropertyMutator());
    }
}