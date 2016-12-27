package com.thoughtworks.qdox.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.qdox.library.ClassLoaderLibrary;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;

public class DefaultJavaClassIT
{
    private ClassLoaderLibrary library;

    @Before
    public void setUp()
    {
        library = new ClassLoaderLibrary( null );
        library.addDefaultLoader();
    }

    @Test
    public void testSuperJavaClass() throws Exception
    {
        JavaClass hashSetClass = library.getJavaClass( "java.util.HashSet" );
        assertTrue( hashSetClass instanceof DefaultJavaClass );
        
        JavaClass superClass = hashSetClass.getSuperJavaClass();
        assertEquals( "java.util.AbstractSet", superClass.getFullyQualifiedName() );
        assertEquals( "java.util.AbstractSet", Class.forName( "java.util.HashSet" ).getSuperclass().getName() );
    }

    @Test
    public void testIsAJavaClass() throws Exception
    {
        JavaClass hashSetClass = library.getJavaClass( "java.util.HashSet" );
        assertTrue( hashSetClass instanceof DefaultJavaClass );
        
        JavaClass setClass = library.getJavaClass( "java.util.Set" );
        assertTrue( hashSetClass.isA( setClass ) );
        assertTrue( hashSetClass.isA( "java.util.Set" ) );
        //watch it!! isA() is the inverse of isAssignableFrom()
        assertTrue( Class.forName( "java.util.Set" ).isAssignableFrom( Class.forName( "java.util.HashSet" ) ) );
    }
    
    @Test 
    public void testDeclaringClass() throws Exception
    {
        JavaClass entryClass = library.getJavaClass( "java.util.Map$Entry" );
        assertTrue( entryClass instanceof DefaultJavaClass );
        
        assertEquals( "java.util.Map", entryClass.getDeclaringClass().getFullyQualifiedName() );
        assertEquals( "java.util.Map", Class.forName( "java.util.Map$Entry" ).getDeclaringClass().getName() );
    }
    
    @Test 
    public void testDeclaredClasses() throws Exception {
        JavaClass mapClass = library.getJavaClass( "java.util.Map" );
        assertTrue( mapClass instanceof DefaultJavaClass );
        
        assertEquals( 1, mapClass.getNestedClasses().size() );
        assertEquals( "java.util.Map$Entry",  mapClass.getNestedClassByName( "Entry" ).getBinaryName() );
        assertEquals( "java.util.Map.Entry",  mapClass.getNestedClassByName( "Entry" ).getFullyQualifiedName() );
        assertEquals( 1, Class.forName( "java.util.Map" ).getDeclaredClasses().length );
        assertEquals( "java.util.Map$Entry", Class.forName( "java.util.Map" ).getDeclaredClasses()[0].getName() );
    }
    
    @Test
    public void testBeanProperty() throws Exception
    {
        JavaClass entryClass = library.getJavaClass( "java.util.Map$Entry" );
        BeanProperty valueBean = entryClass.getBeanProperty( "value" );
        assertNotNull( valueBean );
        assertEquals( "java.lang.Object", valueBean.getType().getFullyQualifiedName() );
        assertNotNull( valueBean.getAccessor() );
        
        assertEquals( "public abstract java.lang.Object java.util.Map$Entry.getValue()", Class.forName( "java.util.Map$Entry" ).getMethod("getValue").toString() );
        assertEquals( "public abstract java.lang.Object java.util.Map$Entry.getValue()", valueBean.getAccessor().toString() );
        assertNotNull( valueBean.getMutator() );
        assertEquals( "public abstract java.lang.Object java.util.Map$Entry.setValue(java.lang.Object)", valueBean.getMutator().toString() );
        
        BeanProperty keyBean = entryClass.getBeanProperty( "key" );
        assertNotNull( keyBean.getAccessor() );
        assertEquals( "public abstract java.lang.Object java.util.Map$Entry.getKey()", keyBean.getAccessor().toString() );
        assertNull( keyBean.getMutator() );
    }
    
    @Test
    public void testNames() throws Exception
    {
        //subclass
        JavaClass entryClass = library.getJavaClass( "java.util.Map$Entry" );
        assertTrue( entryClass instanceof DefaultJavaClass );
        
        assertEquals( "java.util.Map$Entry", entryClass.getBinaryName() );
        assertEquals( "java.util.Map.Entry", entryClass.getFullyQualifiedName() );
        assertEquals( "java.util.Map$Entry", Class.forName( "java.util.Map$Entry" ).getName() );
        assertEquals( "java.util.Map.Entry", entryClass.getCanonicalName() );
        assertEquals( "java.util.Map.Entry", Class.forName( "java.util.Map$Entry" ).getCanonicalName() );
        assertEquals( "Map.Entry", entryClass.getValue() );
    }
}
