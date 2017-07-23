package com.thoughtworks.qdox.model;

import static org.hamcrest.core.IsNot.*;
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
    protected abstract void setDeclaringClass( D constructor, JavaClass cls );
    protected abstract void setParameters( D constructor, List<JavaParameter> singletonList );

    private JavaType newType( String name )
    {
        return newType( name, 0 );
    }
    
    private JavaType newType( String name, int dimensions )
    {
        JavaType result = mock(JavaType.class);
        when(result.getFullyQualifiedName()).thenReturn( name );
        return result;
        
    }
    
    private JavaParameter newJavaParameter( JavaType type, String name )
    {
        JavaParameter result = mock( JavaParameter.class );
        when( result.getType() ).thenReturn( type );
        when( result.getName() ).thenReturn( name );
        return result;
    }
    
    @Test
    public void testGetCodeBlockConstructor() {
        D constructor = newJavaConstructor( "Blah" );
        setModifiers(constructor, Arrays.asList(new String[]{"public"}));
        assertEquals("public Blah() {\n}\n", constructor.getCodeBlock());
    }
    

    @Test
    public void testConstructorToString() {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getFullyQualifiedName()).thenReturn( "a.b.Executor" );
        D constructor = newJavaConstructor( "Executor" );
        setDeclaringClass( constructor, cls );
        assertEquals("a.b.Executor()", constructor.toString());
    }

    @Test
    public void testConstructorParameterTypes() {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getFullyQualifiedName()).thenReturn( "a.b.Executor" );
        D constructor = newJavaConstructor( "Executor" );
        setParameters( constructor,  Collections.singletonList( newJavaParameter( newType("a.b.C"), "param" )  ));
        setDeclaringClass( constructor, cls );
        assertEquals("a.b.C", constructor.getParameterTypes().get(0).getFullyQualifiedName());
    }
    
    @Test
    public void testHashCode() {
        assertTrue( "hashCode should never resolve to 0", newJavaConstructor( "" ).hashCode() != 0 );
        
        D c1 = newJavaConstructor( "Thong" );
        D c2 = newJavaConstructor( "Thong" );
        
        assertEquals(c1.hashCode(), c2.hashCode());
    }
    
    @Test
    public void testEquals()
    {
        D c1 = newJavaConstructor( "thing" );
        D c2 = newJavaConstructor( "Thong" );
        D c3 = newJavaConstructor( "Thong" );

        D c4 = newJavaConstructor( "Thong" );
        setDeclaringClass( c4, mock( JavaClass.class ) );

        assertEquals( c1, c1 );
        assertThat( c1, not( new Object() ) );
        assertThat( c1, not(c2) );
        assertEquals( c2, c3 );
        assertThat( c3, not(c4) );
    }
    
    @Test
    public void testIsPublic()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isPublic() );

        setModifiers( cstr, Arrays.asList( new String[] { "public" } ) );
        assertTrue( cstr.isPublic() );
    }

    @Test
    public void testIsProtected()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isProtected() );

        setModifiers( cstr, Arrays.asList( new String[] { "protected" } ) );
        assertTrue( cstr.isProtected() );
    }
    
    @Test
    public void testIsPrivate()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isPrivate() );

        setModifiers( cstr, Arrays.asList( new String[] { "private" } ) );
        assertTrue( cstr.isPrivate() );
    }

    @Test
    public void testIsAbstract()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isAbstract() );

        setModifiers( cstr, Arrays.asList( new String[] { "abstract" } ) );
        assertTrue( cstr.isAbstract() );
    }

    @Test
    public void testIsFinal()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isFinal() );

        setModifiers( cstr, Arrays.asList( new String[] { "final" } ) );
        assertTrue( cstr.isFinal() );
    }

    @Test
    public void testIsNavite()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isNative() );

        setModifiers( cstr, Arrays.asList( new String[] { "native" } ) );
        assertTrue( cstr.isNative() );
    }

    @Test
    public void testIsStatic()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isStatic() );

        setModifiers( cstr, Arrays.asList( new String[] { "static" } ) );
        assertTrue( cstr.isStatic() );
    }
    
    @Test
    public void testIsStrict()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isStrictfp() );

        setModifiers( cstr, Arrays.asList( new String[] { "strictfp" } ) );
        assertTrue( cstr.isStrictfp() );
    }

    @Test
    public void testIsSynchronized()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isSynchronized() );

        setModifiers( cstr, Arrays.asList( new String[] { "synchronized" } ) );
        assertTrue( cstr.isSynchronized() );
    }
    
    @Test
    public void testIsTransient()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isTransient() );

        setModifiers( cstr, Arrays.asList( new String[] { "transient" } ) );
        assertTrue( cstr.isTransient() );
    }
    
    @Test
    public void testIsVolatile()
    {
        D cstr = newJavaConstructor( "Constructor" );
        assertTrue( !cstr.isVolatile() );

        setModifiers( cstr, Arrays.asList( new String[] { "volatile" } ) );
        assertTrue( cstr.isVolatile() );
    }
}