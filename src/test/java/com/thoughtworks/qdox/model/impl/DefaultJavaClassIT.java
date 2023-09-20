package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.library.ClassLoaderLibrary;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultJavaClassIT
{
    private ClassLoaderLibrary library;

    @BeforeEach
    public void setUp()
    {
        library = new ClassLoaderLibrary( null );
        library.addDefaultLoader();
    }

    @Test
    public void testSuperJavaClass() throws Exception
    {
        JavaClass hashSetClass = library.getJavaClass( "java.util.HashSet" );
        Assertions.assertTrue(hashSetClass instanceof DefaultJavaClass);
        
        JavaClass superClass = hashSetClass.getSuperJavaClass();
        Assertions.assertEquals("java.util.AbstractSet", superClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.AbstractSet", Class.forName( "java.util.HashSet" ).getSuperclass().getName());
    }

    @Test
    public void testIsAJavaClass() throws Exception
    {
        JavaClass hashSetClass = library.getJavaClass( "java.util.HashSet" );
        Assertions.assertTrue(hashSetClass instanceof DefaultJavaClass);
        
        JavaClass setClass = library.getJavaClass( "java.util.Set" );
        Assertions.assertTrue(hashSetClass.isA( setClass ));
        Assertions.assertTrue(hashSetClass.isA( "java.util.Set" ));
        //watch it!! isA() is the inverse of isAssignableFrom()
        Assertions.assertTrue(Class.forName( "java.util.Set" ).isAssignableFrom( Class.forName( "java.util.HashSet" ) ));
    }
    
    @Test
    public void testDeclaringClass() throws Exception
    {
        JavaClass entryClass = library.getJavaClass( "java.util.Map$Entry" );
        Assertions.assertTrue(entryClass instanceof DefaultJavaClass);
        
        Assertions.assertEquals("java.util.Map", entryClass.getDeclaringClass().getFullyQualifiedName());
        Assertions.assertEquals("java.util.Map", Class.forName( "java.util.Map$Entry" ).getDeclaringClass().getName());
    }
    
    @Test 
    public void testDeclaredClasses() throws Exception {
        JavaClass mapClass = library.getJavaClass( "java.util.Map" );
        Assertions.assertTrue(mapClass instanceof DefaultJavaClass);
        
        Assertions.assertEquals(1, mapClass.getNestedClasses().size());
        Assertions.assertEquals("java.util.Map$Entry", mapClass.getNestedClassByName( "Entry" ).getBinaryName());
        Assertions.assertEquals("java.util.Map.Entry", mapClass.getNestedClassByName( "Entry" ).getFullyQualifiedName());
        Assertions.assertEquals(1, Class.forName( "java.util.Map" ).getDeclaredClasses().length);
        Assertions.assertEquals("java.util.Map$Entry", Class.forName( "java.util.Map" ).getDeclaredClasses()[0].getName());
    }
    
    @Test
    public void testBeanProperty() throws Exception
    {
        JavaClass entryClass = library.getJavaClass( "java.util.Map$Entry" );
        BeanProperty valueBean = entryClass.getBeanProperty( "value" );
        Assertions.assertNotNull(valueBean);
        Assertions.assertEquals("java.lang.Object", valueBean.getType().getFullyQualifiedName());
        Assertions.assertNotNull(valueBean.getAccessor());
        
        Assertions.assertEquals("public abstract java.lang.Object java.util.Map$Entry.getValue()", Class.forName( "java.util.Map$Entry" ).getMethod("getValue").toString());
        Assertions.assertEquals("public abstract java.lang.Object java.util.Map$Entry.getValue()", valueBean.getAccessor().toString());
        Assertions.assertNotNull(valueBean.getMutator());
        Assertions.assertEquals("public abstract java.lang.Object java.util.Map$Entry.setValue(java.lang.Object)", valueBean.getMutator().toString());
        
        BeanProperty keyBean = entryClass.getBeanProperty( "key" );
        Assertions.assertNotNull(keyBean.getAccessor());
        Assertions.assertEquals("public abstract java.lang.Object java.util.Map$Entry.getKey()", keyBean.getAccessor().toString());
        Assertions.assertNull(keyBean.getMutator());
    }
    
    @Test
    public void testNames() throws Exception
    {
        //subclass
        JavaClass entryClass = library.getJavaClass( "java.util.Map$Entry" );
        Assertions.assertTrue(entryClass instanceof DefaultJavaClass);
        
        Assertions.assertEquals("java.util.Map$Entry", entryClass.getBinaryName());
        Assertions.assertEquals("java.util.Map.Entry", entryClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.Map$Entry", Class.forName( "java.util.Map$Entry" ).getName());
        Assertions.assertEquals("java.util.Map.Entry", entryClass.getCanonicalName());
        Assertions.assertEquals("java.util.Map.Entry", Class.forName( "java.util.Map$Entry" ).getCanonicalName());
        Assertions.assertEquals("Map.Entry", entryClass.getValue());
    }
}
