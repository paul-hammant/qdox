package com.thoughtworks.qdox.model;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public abstract class JavaFieldTest<F extends JavaField> extends TestCase {

    public JavaFieldTest(String s) {
        super(s);
    }
    
    //constructors
    public abstract F newJavaField();
    public abstract F newJavaField(JavaClass type, String name);
    
    //setters
    public abstract void setComment(F fld, String comment);
    public abstract void setInitializationExpression(F fld, String expression);
    public abstract void setModifiers(F fld, List<String> modifiers);
    public abstract void setName(F fld, String name);
    public abstract void setType(F fld, JavaClass type);
    public abstract void setDeclaringClass(F fld, JavaClass cls);
    
    public JavaClass newJavaClass( String fullname )
    {
        return newJavaClass( fullname, 0 );
    }

    public JavaClass newJavaClass(String fullname, int dimensions) 
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
    
    public void testHashCode()
    {
        assertTrue( "hashCode should never resolve to 0", newJavaField().hashCode() != 0 );
    }
    
    public void testGetCodeBlock() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int"));
        assertEquals("int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithModifiers() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("public final int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithComment() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int"));
        setComment(fld, "Hello");
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "int count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlock1dArray() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int", 1));
        String expected = "int[] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlock2dArray() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int", 2));
        String expected = "int[][] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlockWithValue() {
        F fld = newJavaField();
        setName(fld, "stuff");
        setType(fld, newJavaClass("String"));
        setInitializationExpression(fld, "STUFF + getThing()");
        String expected = "String stuff = STUFF + getThing();\n";
        assertEquals(expected, fld.getCodeBlock());
    }
    
    public void testToStringThreadMIN_PRIORITY() {
    	JavaClass cls = mock(JavaClass.class);
    	when(cls.getFullyQualifiedName()).thenReturn( "java.lang.Thread" );
    	F fld = newJavaField(newJavaClass("int"), "MIN_PRIORITY");
    	setModifiers(fld, Arrays.asList(new String[] {"final", "static", "public"}));
    	setDeclaringClass( fld, cls );
    	assertEquals("public static final int java.lang.Thread.MIN_PRIORITY", fld.toString());
    }
    
    public void testToStringFieldDescriptorFd() {
    	JavaClass cls = mock(JavaClass.class);
    	when(cls.getFullyQualifiedName()).thenReturn("java.io.FileDescriptor");
    	F fld =  newJavaField(newJavaClass("int"), "fd");
    	setModifiers(fld, Arrays.asList(new String[]{"private"}));
    	setDeclaringClass( fld, cls );
    	assertEquals("private int java.io.FileDescriptor.fd", fld.toString());
    }
    
    public void testIsPublic()
    {
        F fld = newJavaField();
        assertTrue( !fld.isPublic() );

        setModifiers( fld, Arrays.asList( new String[] { "public" } ) );
        assertTrue( fld.isPublic() );
    }

    public void testIsProtected()
    {
        F fld = newJavaField();
        assertTrue( !fld.isProtected() );

        setModifiers( fld, Arrays.asList( new String[] { "protected" } ) );
        assertTrue( fld.isProtected() );
    }
    
    public void testIsPrivate()
    {
        F fld = newJavaField();
        assertTrue( !fld.isPrivate() );

        setModifiers( fld, Arrays.asList( new String[] { "private" } ) );
        assertTrue( fld.isPrivate() );
    }

    public void testIsAbstract()
    {
        F fld = newJavaField();
        assertTrue( !fld.isAbstract() );

        setModifiers( fld, Arrays.asList( new String[] { "abstract" } ) );
        assertTrue( fld.isAbstract() );
    }

    public void testIsFinal()
    {
        F fld = newJavaField();
        assertTrue( !fld.isFinal() );

        setModifiers( fld, Arrays.asList( new String[] { "final" } ) );
        assertTrue( fld.isFinal() );
    }

    public void testIsNavite()
    {
        F fld = newJavaField();
        assertTrue( !fld.isNative() );

        setModifiers( fld, Arrays.asList( new String[] { "native" } ) );
        assertTrue( fld.isNative() );
    }

    public void testIsStatic()
    {
        F fld = newJavaField();
        assertTrue( !fld.isStatic() );

        setModifiers( fld, Arrays.asList( new String[] { "static" } ) );
        assertTrue( fld.isStatic() );
    }
    
    public void testIsStrict()
    {
        F fld = newJavaField();
        assertTrue( !fld.isStrictfp() );

        setModifiers( fld, Arrays.asList( new String[] { "strictfp" } ) );
        assertTrue( fld.isStrictfp() );
    }

    public void testIsSynchronized()
    {
        F fld = newJavaField();
        assertTrue( !fld.isSynchronized() );

        setModifiers( fld, Arrays.asList( new String[] { "synchronized" } ) );
        assertTrue( fld.isSynchronized() );
    }
    
    public void testIsTransient()
    {
        F fld = newJavaField();
        assertTrue( !fld.isTransient() );

        setModifiers( fld, Arrays.asList( new String[] { "transient" } ) );
        assertTrue( fld.isTransient() );
    }
    
    public void testIsVolatile()
    {
        F fld = newJavaField();
        assertTrue( !fld.isVolatile() );

        setModifiers( fld, Arrays.asList( new String[] { "volatile" } ) );
        assertTrue( fld.isVolatile() );
    }
    
    public void testEquals()
    {
        JavaClass type = mock(JavaClass.class);
        JavaClass declaringClass = mock(JavaClass.class);
        F fld1 = newJavaField( type, "field");
        F fld2 = newJavaField( type, "field");
        setDeclaringClass( fld1, declaringClass );
        setDeclaringClass( fld2, declaringClass );
        
        assertEquals( fld1, fld2 );
    }
}