package com.thoughtworks.qdox.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

public abstract class JavaParameterTest<P extends JavaParameter>
    extends TestCase
{

    public JavaParameterTest( String s )
    {
        super( s );
    }

    // constructors
    protected abstract P newJavaParameter( Type type, String name );

    protected abstract P newJavaParameter( Type type, String name, boolean varArgs );

    // setters
    protected abstract void setMethod( P parameter, JavaMethod method );

    protected Type newType( String typeName )
    {
        Type result = mock( Type.class );
        when( result.getFullyQualifiedName() ).thenReturn( typeName );
        return result;
    }

    public void testHashCode()
    {
        assertTrue( "hashCode should never resolve to 0", newJavaParameter( Type.VOID, "" ).hashCode() != 0 );

        P simpleParameter = newJavaParameter( Type.VOID, "", false );
        P varArgParameter = newJavaParameter( Type.VOID, "", true );

        assertTrue( simpleParameter.hashCode() != varArgParameter.hashCode() );
    }

    public void testEquals()
    {
        P simpleParameter = newJavaParameter( Type.VOID, "", false );
        P varArgParameter = newJavaParameter( Type.VOID, "", true );
        assertTrue( !simpleParameter.equals( varArgParameter ) );

        // name of parameter shouldn't matter
        P fooParameter = newJavaParameter( Type.VOID, "foo" );
        P barParameter = newJavaParameter( Type.VOID, "bar" );
        assertEquals( fooParameter, barParameter );
    }

    public void testParentMethod()
        throws Exception
    {
        P p = newJavaParameter( newType( "x" ), "x" );
        assertNull( p.getParentMethod() );

        JavaMethod m = mock( JavaMethod.class );
        setMethod( p, m );
        assertSame( m, p.getParentMethod() );
    }

}
