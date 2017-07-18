package com.thoughtworks.qdox.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import junit.framework.TestCase;

public abstract class JavaParameterTest<P extends JavaParameter>
    extends TestCase
{
    private final JavaClass VOID = newType( "void" );

    public JavaParameterTest( String s )
    {
        super( s );
    }

    // constructors
    protected abstract P newJavaParameter( JavaClass type, String name );

    protected abstract P newJavaParameter( JavaClass type, String name, boolean varArgs );

    // setters
    protected abstract void setJavaExecutable( P parameter, JavaExecutable executable );

    protected JavaClass newType( String typeName )
    {
        JavaClass result = mock( JavaClass.class );
        when( result.getFullyQualifiedName() ).thenReturn( typeName );
        return result;
    }

    public void testHashCode()
    {
        assertTrue( "hashCode should never resolve to 0", newJavaParameter( VOID, "" ).hashCode() != 0 );

        P simpleParameter = newJavaParameter( VOID, "", false );
        P varArgParameter = newJavaParameter( VOID, "", true );

        assertTrue( simpleParameter.hashCode() != varArgParameter.hashCode() );
    }

    public void testEquals()
    {
        P simpleParameter = newJavaParameter( VOID, "", false );
        P varArgParameter = newJavaParameter( VOID, "", true );
        assertTrue( !simpleParameter.equals( varArgParameter ) );

        // name of parameter shouldn't matter
        P fooParameter = newJavaParameter( VOID, "foo" );
        P barParameter = newJavaParameter( VOID, "bar" );
        assertEquals( fooParameter, barParameter );
    }

    public void testExecutableDeclarator()
    {
        P p = newJavaParameter( newType( "x" ), "x" );
        assertNull( p.getExecutable() );

        JavaExecutable e = mock( JavaExecutable.class );
        setJavaExecutable( p, e );
        assertSame( e, p.getExecutable() );
    }
}
