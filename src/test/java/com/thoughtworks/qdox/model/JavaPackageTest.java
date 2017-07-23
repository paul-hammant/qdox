package com.thoughtworks.qdox.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public abstract class JavaPackageTest<P extends JavaPackage>
{

    public abstract P newJavaPackage( String name );

    @Test
    public void testToStringJavaLang()
    {
        P pckg = newJavaPackage( "java.lang" );
        assertEquals( "package java.lang", pckg.toString() );
    }

    @Test
    public void testEquals()
    {
        P pckg = newJavaPackage( "java.lang" );

        assertTrue( pckg.equals( pckg ) );
        assertFalse( pckg.equals( null ) );
        assertFalse( pckg.equals( new Object() ) );

        JavaPackage mockPckg = mock( JavaPackage.class );
        when( mockPckg.getName() ).thenReturn( "java.lang" );

        assertTrue( pckg.equals( mockPckg ) );
    }
    
    public void testHashCode() 
    {
        assertTrue( "hashCode should never resolve to 0", newJavaPackage( "" ).hashCode() != 0 );
    }
    
    
    @Test
    public void testListAccessors() {
        P pckg = newJavaPackage( "com.foo.bar" );
        assertNotNull( pckg.getSubPackages() );
        assertEquals( 0, pckg.getSubPackages().size() );
    }
}
