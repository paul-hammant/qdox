package com.thoughtworks.qdox.library;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.impl.DefaultJavaClass;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;
import com.thoughtworks.qdox.model.impl.DefaultJavaSource;

public class JavaClassContextTest
    extends TestCase
{

    private JavaClassContext context;
    
    @Override
	protected void setUp()
        throws Exception
    {
        context = new JavaClassContext();
    }

    public void testGetClassByName()
    {
        assertNull( context.getClassByName( null ) );
        assertNull( "a new context should be empty, not even contain java.lang.Object", context.getClassByName( "java.lang.Object" ) );
        JavaClass clazz = new DefaultJavaClass( "com.foo.Bar" );
        context.add( clazz );
        //check case sensitive
        assertNull( context.getClassByName( "com.foo.bar" ) ); 
        assertEquals( clazz, context.getClassByName( "com.foo.Bar" ) );
    }

    public void testRemoveClassByName()
    {
        assertNull( context.removeClassByName( null ));
        assertNull( context.removeClassByName( "com.foo.Bar" ) );
        JavaClass clazz = new DefaultJavaClass( "com.foo.Bar" );
        context.add( clazz );
        //check case sensitive
        assertNull( context.getClassByName( "com.foo.bar" ) ); 
        assertEquals( clazz, context.removeClassByName( "com.foo.Bar" ) );
    }

    public void testGetClasses()
    {
        assertNotNull( context.getClasses() );
        assertEquals( 0, context.getClasses().size() );

        JavaClass clazz = new DefaultJavaClass( "com.foo.Bar" );
        context.add( clazz );
        assertEquals( 1, context.getClasses().size() );
        //weird case, add same class 
        JavaClass clazz_copy = new DefaultJavaClass( "com.foo.Bar" );
        context.add( clazz_copy );
        assertEquals( 1, context.getClasses().size() );
        
        context.removeClassByName( "com.foo.Bar" );
        
        assertNotNull( context.getClasses() );
        assertEquals( 0, context.getClasses().size() );
    }

    public void testAddJavaClass()
    {
        JavaClass clazz = new DefaultJavaClass( "com.foo.Bar" );
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

    public void testGetPackageByName()
    {
        assertNull( context.getPackageByName( null ) );
        assertNull( context.getPackageByName( "java.lang" ) );
        JavaPackage pckg = new DefaultJavaPackage("com.foo");
        context.add( pckg );
        assertEquals( pckg, context.getPackageByName( "com.foo" ) );
    }

    public void testRemovePackageByName()
    {
        assertNull( context.removePackageByName( null ) );
        assertNull( context.removePackageByName( "com.foo" ) );
        JavaPackage pckg = new DefaultJavaPackage("com.foo");
        context.add( pckg );
        assertEquals( pckg, context.removePackageByName( "com.foo" ) );
    }

    public void testAddJavaPackage()
    {
        JavaPackage pckg = new DefaultJavaPackage("com.foo");
        context.add( pckg );
        //check case sensitive
        assertNull( context.getClassByName( "com.bar" ) ); 
        assertEquals( pckg, context.getPackageByName( "com.foo" ) );
        
        //null-safe
        context.add( (JavaPackage) null );
    }

    public void testGetPackages()
    {
        assertNotNull( context.getPackages() );
        assertEquals( 0, context.getPackages().size() );

        JavaPackage pckg = new DefaultJavaPackage("com.foo");
        context.add( pckg );
        assertEquals( 1, context.getPackages().size() );
        //add same package
        JavaPackage pckg_copy = new DefaultJavaPackage("com.foo");
        context.add( pckg_copy );
        assertEquals( 1, context.getPackages().size() );
        
        context.removePackageByName( "com.foo" );        
        assertNotNull( context.getPackages() );
        assertEquals( 0, context.getPackages().size() );
    }

    public void testAddJavaSource()
    {
        JavaSource source = new DefaultJavaSource(null);
        context.add( source );
        
        //null-safe
        context.add( (JavaSource) null );
    }

    public void testGetSources()
    {
        assertNotNull( context.getSources() );
        assertEquals( 0, context.getSources().size() );

        JavaSource source = new DefaultJavaSource(null);
        context.add( source );
        assertEquals( 1, context.getSources().size() );

        //every source is unique, just add it
        JavaSource source_copy = new DefaultJavaSource(null);
        context.add( source_copy );
        assertEquals( 2, context.getSources().size() );
    }
    
    public void testAdd() {
        context.add(new DefaultJavaClass("com.blah.Ping"));
        context.add(new DefaultJavaClass("com.moo.Poo"));
        assertTrue(context.getClassByName("com.blah.Ping") != null );
        assertTrue(context.getClassByName("com.moo.Poo") != null);
        assertTrue(context.getClassByName("com.not.You") == null);
    }


}
