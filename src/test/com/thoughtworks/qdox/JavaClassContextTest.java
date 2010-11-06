package com.thoughtworks.qdox;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;

public class JavaClassContextTest
    extends TestCase
{

    private JavaClassContext context;
    
    protected void setUp()
        throws Exception
    {
        context = new JavaClassContext();
    }

    public void testGetClassByName() throws Exception
    {
        assertNull( context.getClassByName( null ) );
        assertNull( "a new context should be empty, not even contain java.lang.Object", context.getClassByName( "java.lang.Object" ) );
        JavaClass clazz = new JavaClass( "com.foo.Bar" );
        context.add( clazz );
        //check case sensitive
        assertNull( context.getClassByName( "com.foo.bar" ) ); 
        assertEquals( clazz, context.getClassByName( "com.foo.Bar" ) );
    }

    public void testRemoveClassByName() throws Exception
    {
        assertNull( context.removeClassByName( null ));
        assertNull( context.removeClassByName( "com.foo.Bar" ) );
        JavaClass clazz = new JavaClass( "com.foo.Bar" );
        context.add( clazz );
        //check case sensitive
        assertNull( context.getClassByName( "com.foo.bar" ) ); 
        assertEquals( clazz, context.removeClassByName( "com.foo.Bar" ) );
    }

    public void testGetClasses() throws Exception
    {
        assertNotNull( context.getClasses() );
        assertEquals( 0, context.getClasses().length );

        JavaClass clazz = new JavaClass( "com.foo.Bar" );
        context.add( clazz );
        assertEquals( 1, context.getClasses().length );
        //weird case, add same class 
        JavaClass clazz_copy = new JavaClass( "com.foo.Bar" );
        context.add( clazz_copy );
        assertEquals( 1, context.getClasses().length );
        
        context.removeClassByName( "com.foo.Bar" );
        
        assertNotNull( context.getClasses() );
        assertEquals( 0, context.getClasses().length );
    }

    public void testAddJavaClass() throws Exception
    {
        JavaClass clazz = new JavaClass( "com.foo.Bar" );
        context.add( clazz );
        //check case sensitive
        assertNull( context.getClassByName( "com.foo.bar" ) ); 
        assertEquals( clazz, context.getClassByName( "com.foo.Bar" ) );
        
        //weird case, should never happen
        try {
            context.add( (JavaClass) null );
        }
        catch(NullPointerException npe) {}
    }

    public void testGetPackageByName() throws Exception
    {
        assertNull( context.getPackageByName( null ) );
        assertNull( context.getPackageByName( "java.lang" ) );
        JavaPackage pckg = new JavaPackage("com.foo");
        context.add( pckg );
        assertEquals( pckg, context.getPackageByName( "com.foo" ) );
    }

    public void testRemovePackageByName() throws Exception
    {
        assertNull( context.removePackageByName( null ) );
        assertNull( context.removePackageByName( "com.foo" ) );
        JavaPackage pckg = new JavaPackage("com.foo");
        context.add( pckg );
        assertEquals( pckg, context.removePackageByName( "com.foo" ) );
    }

    public void testAddJavaPackage() throws Exception
    {
        JavaPackage pckg = new JavaPackage("com.foo");
        context.add( pckg );
        //check case sensitive
        assertNull( context.getClassByName( "com.bar" ) ); 
        assertEquals( pckg, context.getPackageByName( "com.foo" ) );
        
        //null-safe
        context.add( (JavaPackage) null );
    }

    public void testGetPackages() throws Exception
    {
        assertNotNull( context.getPackages() );
        assertEquals( 0, context.getPackages().length );

        JavaPackage pckg = new JavaPackage("com.foo");
        context.add( pckg );
        assertEquals( 1, context.getPackages().length );
        //add same package
        JavaPackage pckg_copy = new JavaPackage("com.foo");
        context.add( pckg_copy );
        assertEquals( 1, context.getPackages().length );
        
        context.removePackageByName( "com.foo" );        
        assertNotNull( context.getPackages() );
        assertEquals( 0, context.getPackages().length );
    }

    public void testAddJavaSource() throws Exception
    {
        JavaSource source = new JavaSource();
        context.add( source );
        
        //null-safe
        context.add( (JavaSource) null );
    }

    public void testGetSources() throws Exception
    {
        assertNotNull( context.getSources() );
        assertEquals( 0, context.getSources().length );

        JavaSource source = new JavaSource();
        context.add( source );
        assertEquals( 1, context.getSources().length );

        //every source is unique, just add it
        JavaSource source_copy = new JavaSource();
        context.add( source_copy );
        assertEquals( 2, context.getSources().length );
    }
    
    public void testAdd() throws Exception {
        context.add(new JavaClass("com.blah.Ping"));
        context.add(new JavaClass("com.moo.Poo"));
        assertTrue(context.getClassByName("com.blah.Ping") != null );
        assertTrue(context.getClassByName("com.moo.Poo") != null);
        assertTrue(context.getClassByName("com.not.You") == null);
    }


}
