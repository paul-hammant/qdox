package com.thoughtworks.qdox.library;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.impl.DefaultJavaClass;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;

public class AbstractClassLibraryTest {

    private AbstractClassLibrary nullClassLibrary = new AbstractClassLibrary()
    {
        @Override
		protected JavaClass resolveJavaClass( String name )
        {
            return null;
        }
        @Override
		protected JavaPackage resolveJavaPackage(String name) {
        	return null;
        }
        @Override
		protected boolean containsClassReference( String name )
        {
            return false;
        }
    };
    private AbstractClassLibrary parentClassLibrary;
    private AbstractClassLibrary filledChildClassLibrary;
    private AbstractClassLibrary emptyChildClassLibrary;

    @BeforeEach
    public void setUp() throws Exception
    {
        parentClassLibrary = new AbstractClassLibrary()
        {
            @Override
			protected JavaClass resolveJavaClass( String name )
            {
                return new DefaultJavaClass(name);
            }
            @Override
			protected JavaPackage resolveJavaPackage(String name) 
            {
            	return new DefaultJavaPackage(name);
            }
            @Override
			protected boolean containsClassReference( String name )
            {
                throw new RuntimeException();
            }
        };
        
        filledChildClassLibrary = new AbstractClassLibrary(parentClassLibrary)
        {
            @Override
			protected JavaClass resolveJavaClass( String name )
            {
                return new DefaultJavaClass(name);
            }
            @Override
			protected JavaPackage resolveJavaPackage(String name) 
            {
            	return new DefaultJavaPackage(name);
            }
            @Override
			protected boolean containsClassReference( String name )
            {
                throw new RuntimeException();
            }
        };
        emptyChildClassLibrary = new AbstractClassLibrary(parentClassLibrary)
        {
            @Override
			protected JavaClass resolveJavaClass( String name )
            {
                return null;
            }
            @Override
			protected JavaPackage resolveJavaPackage(String name) 
            {
            	return null;
            }
            @Override
			protected boolean containsClassReference( String name )
            {
                throw new RuntimeException();
            }
        };
    }
    
    /*
     * Never null, empty by default
     */
    @Test
    public void testGetJavaSources() {
        Assertions.assertEquals(0, nullClassLibrary.getJavaSources().size());
    }
    
    /*
     * Never null, empty by default
     */
    @Test
    public void testGetJavaClasses() {
        Assertions.assertEquals(0, nullClassLibrary.getJavaClasses().size());
    }

    @Test
    public void testGetJavaClassByName() {
        Assertions.assertEquals(null, nullClassLibrary.getJavaClass( "java.lang.String" ));
        Assertions.assertEquals(null, nullClassLibrary.getJavaClass( "com.thoughtworks.qdox.JavaProjectBuilder" ));
    }

    @Test
    public void testChainedJavaClass() {
        //prepare libraries
        parentClassLibrary.getJavaClass( "ParentClass" );
        filledChildClassLibrary.getJavaClass( "ChildClass" );
        
        Collection<JavaClass> parentClasses = parentClassLibrary.getJavaClasses( new AbstractClassLibrary.ClassLibraryFilter(){
            public boolean accept(AbstractClassLibrary classLibrary) { return true; }
        } ); 
        Assertions.assertEquals(1, parentClasses.size());
        Assertions.assertEquals("ParentClass", parentClasses.iterator().next().getFullyQualifiedName());
        
        Collection<JavaClass> filledClasses = filledChildClassLibrary.getJavaClasses(new AbstractClassLibrary.ClassLibraryFilter(){
            public boolean accept(AbstractClassLibrary classLibrary) { return true; }
        } ); 
        Assertions.assertEquals(2, filledClasses.size());
        Iterator<JavaClass> iter = filledClasses.iterator();
        Assertions.assertEquals("ChildClass", iter.next().getFullyQualifiedName());
        Assertions.assertEquals("ParentClass", iter.next().getFullyQualifiedName());
        
        Collection<JavaClass> emptyClasses = emptyChildClassLibrary.getJavaClasses(new AbstractClassLibrary.ClassLibraryFilter(){
            public boolean accept(AbstractClassLibrary classLibrary) { return true; }
        } ); 
        Assertions.assertEquals(1, emptyClasses.size());
        Assertions.assertEquals("ParentClass", emptyClasses.iterator().next().getFullyQualifiedName());
    }
    
    
    /*
     * Never null, empty by default
     */
    @Test
    public void testGetJavaPackages() {
        Assertions.assertEquals(0, nullClassLibrary.getJavaPackages().size());
    }

    @Test
    public void testGetJavaPackageByName() {
        Assertions.assertEquals(null, nullClassLibrary.getJavaPackage( "java.lang" ));
        Assertions.assertEquals(null, nullClassLibrary.getJavaPackage( "com.thoughtworks" ));
    }

    @Test
    public void testModuleInfo()
    {
        Assertions.assertNull(nullClassLibrary.getJavaModules());
        Assertions.assertNull(parentClassLibrary.getJavaModules());
        Assertions.assertNull(filledChildClassLibrary.getJavaModules());
        Assertions.assertNull(emptyChildClassLibrary.getJavaModules());
    }
}
