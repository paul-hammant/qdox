package com.thoughtworks.qdox.model;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import junit.framework.TestCase;

public abstract class JavaParameterTest<P extends JavaParameter> extends TestCase {

    public JavaParameterTest(String s) {
        super(s);
    }
    
    //constructors
    protected abstract P newJavaParameter(Type type, String name);
    
    //setters
    protected abstract void setMethod(P parameter, JavaMethod method);
    
    public void testHashCode()
    {
        assertTrue( "hashCode should never resolve to 0", newJavaParameter( Type.VOID, "" ).hashCode() != 0 );
    }
    
    protected Type newType(String typeName) {
        Type result = mock(Type.class);
        when( result.getFullyQualifiedName()).thenReturn( typeName );
        return result;
    }

    public void testParentMethod() throws Exception {
        P p = newJavaParameter(newType("x"), "x");
        assertNull(p.getParentMethod());

        JavaMethod m = mock(JavaMethod.class);
        setMethod( p, m );
        assertSame(m, p.getParentMethod());
    }

}
