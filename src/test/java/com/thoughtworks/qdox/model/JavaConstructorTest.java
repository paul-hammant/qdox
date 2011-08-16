package com.thoughtworks.qdox.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public abstract class JavaConstructorTest<D extends JavaConstructor>
{
    
    protected abstract D newJavaConstructor( String string );    

    protected abstract void setModifiers( D constructor, List<String> asList );
    protected abstract void setParentClass( D constructor, JavaClass cls );
    protected abstract void setParameters( D constructor, List<JavaParameter> singletonList );

    private Type newType( String name )
    {
        return newType( name, 0 );
    }
    
    private Type newType( String name, int dimensions )
    {
        Type result = mock(Type.class);
        when(result.getFullyQualifiedName()).thenReturn( name );
        when(result.getDimensions()).thenReturn( dimensions );
        return result;
        
    }
    
    private JavaParameter newJavaParameter( Type type, String name )
    {
        JavaParameter result = mock( JavaParameter.class );
        when( result.getType() ).thenReturn( type );
        when( result.getName() ).thenReturn( name );
        return result;
    }
    
    @Test
    public void testGetCodeBlockConstructor() throws Exception {
        D constructor = newJavaConstructor( "Blah" );
        setModifiers(constructor, Arrays.asList(new String[]{"public"}));
        assertEquals("public Blah() {\n}\n", constructor.getCodeBlock());
    }
    

    @Test
    public void testConstructorToString() throws Exception {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getFullyQualifiedName()).thenReturn( "a.b.Executor" );
        D constructor = newJavaConstructor( "Executor" );
        setParentClass( constructor, cls );
        assertEquals("a.b.Executor()", constructor.toString());
    }

    @Test
    public void testConstructorParameterTypes() throws Exception {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getFullyQualifiedName()).thenReturn( "a.b.Executor" );
        D constructor = newJavaConstructor( "Executor" );
        setParameters( constructor,  Collections.singletonList( newJavaParameter( newType("a.b.C"), "param" )  ));
        setParentClass( constructor, cls );
        assertEquals("a.b.C", constructor.getParameterTypes().get(0).getFullyQualifiedName());
    }
    
    @Test
    public void testHashCode() throws Exception {
        assertTrue( "hashCode should never resolve to 0", newJavaConstructor( "" ).hashCode() != 0 );
        
        D c1 = newJavaConstructor( "Thong" );
        D c2 = newJavaConstructor( "Thong" );
        
        assertEquals(c1.hashCode(), c2.hashCode());
    }
    
    @Test
    public void testEquals()
        throws Exception
    {
        D c1 = newJavaConstructor( "thing" );
        D c2 = newJavaConstructor( "Thong" );
        D c3 = newJavaConstructor( "Thong" );

        D c4 = newJavaConstructor( "Thong" );
        setParentClass( c4, mock( JavaClass.class ) );

        assertNotEquals( c1, c2 );
        assertEquals( c2, c3 );
        assertNotEquals( c3, c4 );
    }

    private void assertNotEquals( Object o1, Object o2 )
    {
        assertTrue( o1.toString() + " should not equals " + o2.toString(), !o1.equals( o2 ) );
    }
}
