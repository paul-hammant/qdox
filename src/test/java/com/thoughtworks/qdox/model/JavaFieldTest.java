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
    public abstract F newJavaField(Type type, String name);
    
    //setters
    public abstract void setComment(F fld, String comment);
    public abstract void setInitializationExpression(F fld, String expression);
    public abstract void setModifiers(F fld, List<String> modifiers);
    public abstract void setName(F fld, String name);
    public abstract void setType(F fld, Type type);
    public abstract void setDeclaringClass(F fld, JavaClass cls);
    
    public Type newType( String fullname )
    {
        return newType( fullname, 0 );
    }

    public Type newType(String fullname, int dimensions) 
    {
        Type result = mock( Type.class );
        when( result.getFullyQualifiedName() ).thenReturn( fullname );
        when( result.getValue() ).thenReturn( fullname );
        //@todo fix
        String toString = fullname;
        when( result.getDimensions()).thenReturn( dimensions );
        for(int i = 0; i < dimensions; i++)
        {
            toString += "[]";
        }
        when( result.toString() ).thenReturn( toString );
        return result;
    }
    
    public void testHashCode()
    {
        assertTrue( "hashCode should never resolve to 0", newJavaField().hashCode() != 0 );
    }
    
    public void testGetCodeBlock() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        assertEquals("int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithModifiers() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("public final int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithComment() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setComment(fld, "Hello");
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "int count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlock1dArray() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int", 1));
        String expected = "int[] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlock2dArray() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int", 2));
        String expected = "int[][] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlockWithValue() throws Exception {
        F fld = newJavaField();
        setName(fld, "stuff");
        setType(fld, newType("String"));
        setInitializationExpression(fld, "STUFF + getThing()");
        String expected = "String stuff = STUFF + getThing();\n";
        assertEquals(expected, fld.getCodeBlock());
    }
    
    public void testShouldReturnFieldNameForCallSignature() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("count", fld.getCallSignature());
    }

    public void testShouldReturnProperDeclarationSignatureWithModifiers() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("public final int count", fld.getDeclarationSignature(true));
    }

    public void testShouldReturnProperDeclarationSignatureWithoutModifiers() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("int count", fld.getDeclarationSignature(false));
    }
    
    public void testToStringThreadMIN_PRIORITY() throws Exception {
    	JavaClass cls = mock(JavaClass.class);
    	when(cls.getFullyQualifiedName()).thenReturn( "java.lang.Thread" );
    	F fld = newJavaField(newType("int"), "MIN_PRIORITY");
    	setModifiers(fld, Arrays.asList(new String[] {"final", "static", "public"}));
    	setDeclaringClass( fld, cls );
    	assertEquals("public static final int java.lang.Thread.MIN_PRIORITY", fld.toString());
    }
    
    public void testToStringFieldDescriptorFd() throws Exception {
    	JavaClass cls = mock(JavaClass.class);
    	when(cls.getFullyQualifiedName()).thenReturn("java.io.FileDescriptor");
    	F fld =  newJavaField(newType("int"), "fd");
    	setModifiers(fld, Arrays.asList(new String[]{"private"}));
    	setDeclaringClass( fld, cls );
    	assertEquals("private int java.io.FileDescriptor.fd", fld.toString());
    }
}
