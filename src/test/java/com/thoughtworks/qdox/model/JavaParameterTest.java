package com.thoughtworks.qdox.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.thoughtworks.qdox.model.impl.DefaultJavaType;

import junit.framework.TestCase;

public abstract class JavaParameterTest<P extends JavaParameter>
    extends TestCase
{

    public JavaParameterTest( String s )
    {
        super( s );
    }

    // constructors
    protected abstract P newJavaParameter( JavaClass type, String name );

    protected abstract P newJavaParameter( JavaClass type, String name, boolean varArgs );

    // setters
    protected abstract void setMethod( P parameter, JavaMethod method );

    protected abstract void setConstructor( P parameter, JavaConstructor constructor );

    protected JavaClass newType( String typeName )
    {
        JavaClass result = mock( JavaClass.class );
        when( result.getFullyQualifiedName() ).thenReturn( typeName );
        return result;
    }

    public void testHashCode()
    {
        assertTrue( "hashCode should never resolve to 0", newJavaParameter( DefaultJavaType.VOID, "" ).hashCode() != 0 );

        P simpleParameter = newJavaParameter( DefaultJavaType.VOID, "", false );
        P varArgParameter = newJavaParameter( DefaultJavaType.VOID, "", true );

        assertTrue( simpleParameter.hashCode() != varArgParameter.hashCode() );
    }

    public void testEquals()
    {
        P simpleParameter = newJavaParameter( DefaultJavaType.VOID, "", false );
        P varArgParameter = newJavaParameter( DefaultJavaType.VOID, "", true );
        assertTrue( !simpleParameter.equals( varArgParameter ) );

        // name of parameter shouldn't matter
        P fooParameter = newJavaParameter( DefaultJavaType.VOID, "foo" );
        P barParameter = newJavaParameter( DefaultJavaType.VOID, "bar" );
        assertEquals( fooParameter, barParameter );
    }

    public void testMethodDeclarator()
        throws Exception
    {
        P p = newJavaParameter( newType( "x" ), "x" );
        assertNull( p.getDeclarator() );

        JavaMethod m = mock( JavaMethod.class );
        setMethod( p, m );
        assertSame( m, p.getDeclarator() );
    }

    public void testConstgructorDeclarator()
        throws Exception
    {
        P p = newJavaParameter( newType( "x" ), "x" );
        assertNull( p.getDeclarator() );

        JavaConstructor c = mock( JavaConstructor.class );
        setConstructor( p, c );
        assertSame( c, p.getDeclarator() );
    }

}
