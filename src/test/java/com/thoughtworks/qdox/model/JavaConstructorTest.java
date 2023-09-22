package com.thoughtworks.qdox.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        Assertions.assertEquals("public Blah() {\n}\n", constructor.getCodeBlock());
    }
    

    @Test
    public void testConstructorToString() {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getFullyQualifiedName()).thenReturn( "a.b.Executor" );
        D constructor = newJavaConstructor( "Executor" );
        setDeclaringClass( constructor, cls );
        Assertions.assertEquals("a.b.Executor()", constructor.toString());
    }

    @Test
    public void testConstructorParameterTypes() {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getFullyQualifiedName()).thenReturn( "a.b.Executor" );
        D constructor = newJavaConstructor( "Executor" );
        setParameters( constructor,  Collections.singletonList( newJavaParameter( newType("a.b.C"), "param" )  ));
        setDeclaringClass( constructor, cls );
        Assertions.assertEquals("a.b.C", constructor.getParameterTypes().get(0).getFullyQualifiedName());
    }
    
    @Test
    public void testHashCode() {
        Assertions.assertTrue(newJavaConstructor( "" ).hashCode() != 0, "hashCode should never resolve to 0");
        
        D c1 = newJavaConstructor( "Thong" );
        D c2 = newJavaConstructor( "Thong" );
        
        Assertions.assertEquals(c1.hashCode(), c2.hashCode());
    }
    
    @Test
    public void testEquals()
    {
        D c1 = newJavaConstructor( "thing" );
        D c2 = newJavaConstructor( "Thong" );
        D c3 = newJavaConstructor( "Thong" );

        D c4 = newJavaConstructor( "Thong" );
        setDeclaringClass( c4, mock( JavaClass.class ) );

        assertThat(c1).isEqualTo(c1);
        assertThat(c1).isNotEqualTo( new Object() );
        assertThat(c1).isNotEqualTo(c2);
        assertThat(c2).isEqualTo(c3);
        assertThat(c3).isNotEqualTo(c4);
    }
    
    @Test
    public void testIsPublic()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isPublic());

        setModifiers( cstr, Arrays.asList( new String[] { "public" } ) );
        Assertions.assertTrue(cstr.isPublic());
    }

    @Test
    public void testIsProtected()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isProtected());

        setModifiers( cstr, Arrays.asList( new String[] { "protected" } ) );
        Assertions.assertTrue(cstr.isProtected());
    }
    
    @Test
    public void testIsPrivate()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isPrivate());

        setModifiers( cstr, Arrays.asList( new String[] { "private" } ) );
        Assertions.assertTrue(cstr.isPrivate());
    }

    @Test
    public void testIsAbstract()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isAbstract());

        setModifiers( cstr, Arrays.asList( new String[] { "abstract" } ) );
        Assertions.assertTrue(cstr.isAbstract());
    }

    @Test
    public void testIsFinal()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isFinal());

        setModifiers( cstr, Arrays.asList( new String[] { "final" } ) );
        Assertions.assertTrue(cstr.isFinal());
    }

    @Test
    public void testIsNavite()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isNative());

        setModifiers( cstr, Arrays.asList( new String[] { "native" } ) );
        Assertions.assertTrue(cstr.isNative());
    }

    @Test
    public void testIsStatic()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isStatic());

        setModifiers( cstr, Arrays.asList( new String[] { "static" } ) );
        Assertions.assertTrue(cstr.isStatic());
    }
    
    @Test
    public void testIsStrict()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isStrictfp());

        setModifiers( cstr, Arrays.asList( new String[] { "strictfp" } ) );
        Assertions.assertTrue(cstr.isStrictfp());
    }

    @Test
    public void testIsSynchronized()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isSynchronized());

        setModifiers( cstr, Arrays.asList( new String[] { "synchronized" } ) );
        Assertions.assertTrue(cstr.isSynchronized());
    }
    
    @Test
    public void testIsTransient()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isTransient());

        setModifiers( cstr, Arrays.asList( new String[] { "transient" } ) );
        Assertions.assertTrue(cstr.isTransient());
    }
    
    @Test
    public void testIsVolatile()
    {
        D cstr = newJavaConstructor( "Constructor" );
        Assertions.assertTrue(!cstr.isVolatile());

        setModifiers( cstr, Arrays.asList( new String[] { "volatile" } ) );
        Assertions.assertTrue(cstr.isVolatile());
    }
}