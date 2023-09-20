package com.thoughtworks.qdox.library;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.impl.DefaultJavaClass;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;
import com.thoughtworks.qdox.model.impl.DefaultJavaSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JavaClassContextTest {

    private JavaClassContext context;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        context = new JavaClassContext();
    }

    @Test
    public void testGetClassByName()
    {
        Assertions.assertNull(context.getClassByName( null ));
        Assertions.assertNull(context.getClassByName( "java.lang.Object" ), "a new context should be empty, not even contain java.lang.Object");
        JavaClass clazz = new DefaultJavaClass( "com.foo.Bar" );
        context.add( clazz );
        //check case sensitive
        Assertions.assertNull(context.getClassByName( "com.foo.bar" ));
        Assertions.assertEquals(clazz, context.getClassByName( "com.foo.Bar" ));
    }

    @Test
    public void testRemoveClassByName()
    {
        Assertions.assertNull(context.removeClassByName( null ));
        Assertions.assertNull(context.removeClassByName( "com.foo.Bar" ));
        JavaClass clazz = new DefaultJavaClass( "com.foo.Bar" );
        context.add( clazz );
        //check case sensitive
        Assertions.assertNull(context.getClassByName( "com.foo.bar" ));
        Assertions.assertEquals(clazz, context.removeClassByName( "com.foo.Bar" ));
    }

    @Test
    public void testGetClasses()
    {
        Assertions.assertNotNull(context.getClasses());
        Assertions.assertEquals(0, context.getClasses().size());

        JavaClass clazz = new DefaultJavaClass( "com.foo.Bar" );
        context.add( clazz );
        Assertions.assertEquals(1, context.getClasses().size());
        //weird case, add same class 
        JavaClass clazz_copy = new DefaultJavaClass( "com.foo.Bar" );
        context.add( clazz_copy );
        Assertions.assertEquals(1, context.getClasses().size());
        
        context.removeClassByName( "com.foo.Bar" );
        
        Assertions.assertNotNull(context.getClasses());
        Assertions.assertEquals(0, context.getClasses().size());
    }

    @Test
    public void testAddJavaClass()
    {
        JavaClass clazz = new DefaultJavaClass( "com.foo.Bar" );
        context.add( clazz );
        //check case sensitive
        Assertions.assertNull(context.getClassByName( "com.foo.bar" ));
        Assertions.assertEquals(clazz, context.getClassByName( "com.foo.Bar" ));
        
        //weird case, should never happen
        try {
            context.add( (JavaClass) null );
        }
        catch(NullPointerException npe) {}
    }

    @Test
    public void testGetPackageByName()
    {
        Assertions.assertNull(context.getPackageByName( null ));
        Assertions.assertNull(context.getPackageByName( "java.lang" ));
        JavaPackage pckg = new DefaultJavaPackage("com.foo");
        context.add( pckg );
        Assertions.assertEquals(pckg, context.getPackageByName( "com.foo" ));
    }

    @Test
    public void testRemovePackageByName()
    {
        Assertions.assertNull(context.removePackageByName( null ));
        Assertions.assertNull(context.removePackageByName( "com.foo" ));
        JavaPackage pckg = new DefaultJavaPackage("com.foo");
        context.add( pckg );
        Assertions.assertEquals(pckg, context.removePackageByName( "com.foo" ));
    }

    @Test
    public void testAddJavaPackage()
    {
        JavaPackage pckg = new DefaultJavaPackage("com.foo");
        context.add( pckg );
        //check case sensitive
        Assertions.assertNull(context.getClassByName( "com.bar" ));
        Assertions.assertEquals(pckg, context.getPackageByName( "com.foo" ));
        
        //null-safe
        context.add( (JavaPackage) null );
    }

    @Test
    public void testGetPackages()
    {
        Assertions.assertNotNull(context.getPackages());
        Assertions.assertEquals(0, context.getPackages().size());

        JavaPackage pckg = new DefaultJavaPackage("com.foo");
        context.add( pckg );
        Assertions.assertEquals(1, context.getPackages().size());
        //add same package
        JavaPackage pckg_copy = new DefaultJavaPackage("com.foo");
        context.add( pckg_copy );
        Assertions.assertEquals(1, context.getPackages().size());
        
        context.removePackageByName( "com.foo" );        
        Assertions.assertNotNull(context.getPackages());
        Assertions.assertEquals(0, context.getPackages().size());
    }

    @Test
    public void testAddJavaSource()
    {
        JavaSource source = new DefaultJavaSource(null);
        context.add( source );
        
        //null-safe
        context.add( (JavaSource) null );
    }

    @Test
    public void testGetSources()
    {
        Assertions.assertNotNull(context.getSources());
        Assertions.assertEquals(0, context.getSources().size());

        JavaSource source = new DefaultJavaSource(null);
        context.add( source );
        Assertions.assertEquals(1, context.getSources().size());

        //every source is unique, just add it
        JavaSource source_copy = new DefaultJavaSource(null);
        context.add( source_copy );
        Assertions.assertEquals(2, context.getSources().size());
    }

    @Test
    public void testAdd() {
        context.add(new DefaultJavaClass("com.blah.Ping"));
        context.add(new DefaultJavaClass("com.moo.Poo"));
        Assertions.assertTrue(context.getClassByName("com.blah.Ping") != null);
        Assertions.assertTrue(context.getClassByName("com.moo.Poo") != null);
        Assertions.assertTrue(context.getClassByName("com.not.You") == null);
    }


}
