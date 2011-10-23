package com.thoughtworks.qdox.model;

import static org.hamcrest.core.IsNot.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.qdox.model.impl.DefaultJavaType;

public abstract class JavaMethodTest<M extends JavaMethod> {

    private M mth;

    //constructors
    public abstract M newJavaMethod();
    public abstract M newJavaMethod(DefaultJavaType returns, String name);

    //setters
    public abstract void setExceptions(M method, List<JavaClass> exceptions);
    public abstract void setComment(M method, String comment);
    public abstract void setName(M method, String name);
    public abstract void setModifiers(M method, List<String> modifiers);
    public abstract void setParameters(M method, List<JavaParameter> parameters);
    public abstract void setParentClass(M method, JavaClass clazz);
    public abstract void setReturns(M method, DefaultJavaType type);
    public abstract void setSourceCode(M method, String code);
    
    
    public JavaParameter newJavaParameter(DefaultJavaType type, String name)
    {
        return newJavaParameter( type, name, false );
    }
    
    public JavaParameter newJavaParameter(DefaultJavaType type, String name, boolean varArgs) 
    {
        JavaParameter result = mock(JavaParameter.class);
        when( result.getType() ).thenReturn( type );
        when( result.getName() ).thenReturn( name );
        when( result.isVarArgs() ).thenReturn( varArgs );
        return result;
    }
    
    public DefaultJavaType newType( String fullname )
    {
        return newType( fullname, 0 );
    }

    public DefaultJavaType newType(String fullname, int dimensions) 
    {
        DefaultJavaType result = mock( DefaultJavaType.class );
        when( result.getFullyQualifiedName() ).thenReturn( fullname );
        String canonicalName = fullname.replace( '$', '.' );
        when( result.getValue() ).thenReturn( canonicalName );
        when( result.getDimensions()).thenReturn( dimensions );
        for(int i = 0; i < dimensions; i++)
        {
            canonicalName += "[]";
        }
        when( result.getCanonicalName() ).thenReturn( canonicalName );
        return result;
    }
    
    @Before
    public void setUp() throws Exception {
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
        assertEquals("protected final void blah(int count, MyThing t) throws FishException, FruitException", signature);
    }

    @Test
    public void testDeclarationSignatureWithoutModifiers() {
        createSignatureTestMethod();
        String signature = mth.getDeclarationSignature(false);
        assertEquals("void blah(int count, MyThing t) throws FishException, FruitException", signature);
    }

    @Test
    public void testCallSignature() {
        createSignatureTestMethod();
        String signature = mth.getCallSignature();
        assertEquals("blah(count, t)", signature);
    }

//    public void testSignatureWithVarArgs() throws Exception {
//        mth.setName( "method" );
//        mth.addParameter( new JavaParameter(new Type("java.lang.String"), "param", true) );
//        assertEquals( mth, clazz.getMethodBySignature( "method", new Type[] { new Type("java.lang.String", true)} ) );
//    }
    
    @Test
    public void testGetCodeBlockSimple() throws Exception {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String"));
        assertEquals("java.lang.String doSomething();\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockOneParam() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters( mth, Collections.singletonList( newJavaParameter(newType("String"), "thingy") ) );
        assertEquals("void blah(String thingy);\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockTwoParams() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int"), "count"), newJavaParameter(newType("MyThing"), "t") ) );
        assertEquals("void blah(int count, MyThing t);\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockThreeParams() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters(mth, Arrays.asList( newJavaParameter(newType("int"), "count"), newJavaParameter(newType("MyThing"), "t"), newJavaParameter(newType("java.lang.Meat"), "beef") ));
        assertEquals("void blah(int count, MyThing t, java.lang.Meat beef);\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockModifiersWithAccessLevelFirst() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setModifiers(mth, Arrays.asList(new String[]{"synchronized", "public", "final"}));
        assertEquals("public synchronized final void blah();\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockOneException() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions( mth, Arrays.asList( new JavaClass[] { newType( "RuntimeException" ) } ) );
        assertEquals("void blah() throws RuntimeException;\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockTwoException() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new JavaClass[]{newType("RuntimeException"), newType("java.lang.SheepException")}));
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException;\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockThreeException() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new JavaClass[]{newType("RuntimeException"), newType("java.lang.SheepException"), newType("CowException")}));
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException, CowException;\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockWithComment() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setComment(mth, "Hello");
        String expect = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "void blah();\n";
        assertEquals(expect, mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlock1dArray() throws Exception {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String", 1));
        assertEquals("java.lang.String[] doSomething();\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlock2dArray() throws Exception {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String", 2));
        assertEquals("java.lang.String[][] doSomething();\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockParamArray() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setParameters( mth, Arrays.asList( newJavaParameter( newType("int", 2), "count"), newJavaParameter( newType("MyThing", 1), "t") ) );
        assertEquals("void blah(int[][] count, MyThing[] t);\n", mth.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockWithBody() throws Exception {
        setName(mth, "doStuff");
        setReturns(mth, newType("java.lang.String"));
        setSourceCode(mth, "  int x = 2;\n  return STUFF;\n");

        assertEquals("" +
                "java.lang.String doStuff() {\n" +
                "  int x = 2;\n" +
                "  return STUFF;\n" +
                "}\n",
                mth.getCodeBlock());
    }
    
    @Test
    public void testEquals() throws Exception {
        DefaultJavaType voidType = newType("void");

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
        setParentClass( m8, mock( JavaClass.class ) );

        assertEquals(mth, m2);
        assertEquals(m2, mth);
        assertThat(mth, not(m3));
        assertThat(mth, not(m4));
        assertFalse(mth.equals(null));
        assertThat( m4, not(m5) );
        assertThat( m5, not(m4) );
        assertEquals( m5, m6 );
        assertThat( m5, not(m7) );
        assertThat( m7, not(m8) );
    }

    @Test
    public void testEqualsWithParameters() throws Exception {
        DefaultJavaType voidType = newType("void");
        DefaultJavaType intArrayType = newType("int", 1);
        DefaultJavaType stringArrayType = newType("java.lang.String", 2);
        DefaultJavaType xArrayType = newType("X", 3);

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

        assertEquals( mth, m2 );
        assertEquals( m2, mth );
        assertThat( mth, not(m3) );
        assertThat( mth, not(m5) );
    }

    @Test
    public void testHashCode()
        throws Exception
    {
        assertTrue( "hashCode should never resolve to 0", newJavaMethod( DefaultJavaType.VOID, "" ).hashCode() != 0 );

        DefaultJavaType voidType = newType( "void" );
        DefaultJavaType intType = newType( "int", 1 );
        DefaultJavaType stringArrayType = newType( "java.lang.String", 2 );
        DefaultJavaType xArrayType = newType( "X", 3 );

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

        assertEquals( mth.hashCode(), m2.hashCode() );
        assertTrue( mth.hashCode() != m3.hashCode() );
    }

    @Test
   public void testSignatureMatches() throws Exception {
        DefaultJavaType intType = newType("int");
        DefaultJavaType longArrayType = newType("long", 2);

        setName(mth, "thing");
        setParameters(mth, Arrays.asList( newJavaParameter(intType, "x"), newJavaParameter(longArrayType, "y") ));
        setReturns(mth, newType("void"));

        JavaType[] correctTypes = new DefaultJavaType[]{
            intType,
            longArrayType
        };

        JavaType[] wrongTypes1 = new DefaultJavaType[]{
            newType("int", 2),
            newType("long")
        };

        JavaType[] wrongTypes2 = new DefaultJavaType[]{
            intType,
            longArrayType,
            newType("double")
        };

        assertTrue(mth.signatureMatches("thing", Arrays.asList( correctTypes )));
        assertFalse(mth.signatureMatches("xxx", Arrays.asList( correctTypes )));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes1 )));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes2 )));
    }
    
    @Test
    public void testVarArgSignatureMatches() throws Exception {
        DefaultJavaType intType = newType("int");
        DefaultJavaType longArrayType = newType("long", 2);

        setName(mth, "thing");
        setParameters(mth, Arrays.asList( newJavaParameter(intType, "x"), newJavaParameter(longArrayType, "y", true) ));
        setReturns(mth, newType("void"));

        JavaType[] correctTypes = new DefaultJavaType[]{
            intType,
            longArrayType
        };

        JavaType[] wrongTypes1 = new DefaultJavaType[]{
            newType("int", 2),
            newType("long")
        };

        JavaType[] wrongTypes2 = new DefaultJavaType[]{
            intType,
            longArrayType,
            newType("double")
        };

        assertTrue(mth.signatureMatches("thing", Arrays.asList( correctTypes ), true));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( correctTypes ), false));
        assertFalse(mth.signatureMatches("xxx", Arrays.asList( correctTypes ), true));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes1 ), true));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes2 ), true));
    }

    @Test
    public void testParentClass() throws Exception {
        JavaClass clazz = mock(JavaClass.class);
        setParentClass( mth, clazz );
        assertSame(clazz, mth.getParentClass());
    }

    @Test
    public void testCanGetParameterByName() throws Exception {
        JavaParameter paramX = newJavaParameter(newType("int"), "x");
        setParameters(mth, Arrays.asList( paramX, newJavaParameter(newType("string"), "y") ));
        
        assertEquals(paramX, mth.getParameterByName("x"));
        assertEquals(null, mth.getParameterByName("z"));
    }

    @Test
    public void testToString() throws Exception {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getFullyQualifiedName()).thenReturn( "java.lang.Object" );
    	M mthd = newJavaMethod(newType("boolean"),"equals");
    	setParentClass(mthd, cls);
    	setModifiers(mthd, Arrays.asList(new String[]{"public"}));
    	setParameters(mthd, Collections.singletonList( newJavaParameter(newType("java.lang.Object"), null) ));
    	assertEquals("public boolean java.lang.Object.equals(java.lang.Object)", mthd.toString());
    }
    
    @Test
    public void testIsPublic()
    {
        assertTrue( !mth.isPublic() );

        setModifiers( mth, Arrays.asList( new String[] { "public" } ) );
        assertTrue( mth.isPublic() );
    }

    @Test
    public void testIsProtected()
    {
        assertTrue( !mth.isProtected() );

        setModifiers( mth, Arrays.asList( new String[] { "protected" } ) );
        assertTrue( mth.isProtected() );
    }
    
    @Test
    public void testIsPrivate()
    {
        assertTrue( !mth.isPrivate() );

        setModifiers( mth, Arrays.asList( new String[] { "private" } ) );
        assertTrue( mth.isPrivate() );
    }

    @Test
    public void testIsAbstract()
    {
        assertTrue( !mth.isAbstract() );

        setModifiers( mth, Arrays.asList( new String[] { "abstract" } ) );
        assertTrue( mth.isAbstract() );
    }

    @Test
    public void testIsFinal()
    {
        assertTrue( !mth.isFinal() );

        setModifiers( mth, Arrays.asList( new String[] { "final" } ) );
        assertTrue( mth.isFinal() );
    }

    @Test
    public void testIsNavite()
    {
        assertTrue( !mth.isNative() );

        setModifiers( mth, Arrays.asList( new String[] { "native" } ) );
        assertTrue( mth.isNative() );
    }

    @Test
    public void testIsStatic()
    {
        assertTrue( !mth.isStatic() );

        setModifiers( mth, Arrays.asList( new String[] { "static" } ) );
        assertTrue( mth.isStatic() );
    }
    
    @Test
    public void testIsStrict()
    {
        assertTrue( !mth.isStrictfp() );

        setModifiers( mth, Arrays.asList( new String[] { "strictfp" } ) );
        assertTrue( mth.isStrictfp() );
    }

    @Test
    public void testIsSynchronized()
    {
        assertTrue( !mth.isSynchronized() );

        setModifiers( mth, Arrays.asList( new String[] { "synchronized" } ) );
        assertTrue( mth.isSynchronized() );
    }
    
    @Test
    public void testIsTransient()
    {
        assertTrue( !mth.isTransient() );

        setModifiers( mth, Arrays.asList( new String[] { "transient" } ) );
        assertTrue( mth.isTransient() );
    }
    
    @Test
    public void testIsVolatile()
    {
        assertTrue( !mth.isVolatile() );

        setModifiers( mth, Arrays.asList( new String[] { "volatile" } ) );
        assertTrue( mth.isVolatile() );
    }
}