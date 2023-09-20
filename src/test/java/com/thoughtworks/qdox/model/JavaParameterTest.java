package com.thoughtworks.qdox.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class JavaParameterTest<P extends JavaParameter> {
    private final JavaClass VOID = newType( "void" );

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

    @Test
    public void testHashCode()
    {
        Assertions.assertTrue(newJavaParameter( VOID, "" ).hashCode() != 0, "hashCode should never resolve to 0");

        P simpleParameter = newJavaParameter( VOID, "", false );
        P varArgParameter = newJavaParameter( VOID, "", true );

        Assertions.assertTrue(simpleParameter.hashCode() != varArgParameter.hashCode());
    }

    @Test
    public void testEquals()
    {
        P simpleParameter = newJavaParameter( VOID, "", false );
        P varArgParameter = newJavaParameter( VOID, "", true );
        Assertions.assertTrue(!simpleParameter.equals( varArgParameter ));

        // name of parameter shouldn't matter
        P fooParameter = newJavaParameter( VOID, "foo" );
        P barParameter = newJavaParameter( VOID, "bar" );
        Assertions.assertEquals(fooParameter, barParameter);
    }

    @Test
    public void testExecutableDeclarator()
    {
        P p = newJavaParameter( newType( "x" ), "x" );
        Assertions.assertNull(p.getExecutable());

        JavaExecutable e = mock( JavaExecutable.class );
        setJavaExecutable( p, e );
        Assertions.assertSame(e, p.getExecutable());
    }
}
