package com.thoughtworks.qdox.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class JavaPackageTest<P extends JavaPackage>
{

    public abstract P newJavaPackage( String name );

    @Test
    public void testToStringJavaLang()
    {
        P pckg = newJavaPackage( "java.lang" );
        Assertions.assertEquals("package java.lang", pckg.toString());
    }

    @Test
    public void testEquals()
    {
        P pckg = newJavaPackage( "java.lang" );

        Assertions.assertTrue(pckg.equals( pckg ));
        Assertions.assertFalse(pckg.equals( null ));
        Assertions.assertFalse(pckg.equals( new Object() ));

        JavaPackage mockPckg = mock( JavaPackage.class );
        when( mockPckg.getName() ).thenReturn( "java.lang" );

        Assertions.assertTrue(pckg.equals( mockPckg ));
    }
    
    public void testHashCode() 
    {
        Assertions.assertTrue(newJavaPackage( "" ).hashCode() != 0, "hashCode should never resolve to 0");
    }
    
    
    @Test
    public void testListAccessors() {
        P pckg = newJavaPackage( "com.foo.bar" );
        Assertions.assertNotNull(pckg.getSubPackages());
        Assertions.assertEquals(0, pckg.getSubPackages().size());
    }
}
