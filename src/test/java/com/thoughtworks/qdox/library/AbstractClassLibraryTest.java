package com.thoughtworks.qdox.library;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.impl.DefaultJavaClass;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;

public class AbstractClassLibraryTest
    extends TestCase
{

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
    
    @Override
	protected void setUp() throws Exception
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
    public void testGetJavaSources() {
        assertEquals( 0, nullClassLibrary.getJavaSources().size() );
    }
    
    /*
     * Never null, empty by default
     */
    public void testGetJavaClasses() {
        assertEquals( 0, nullClassLibrary.getJavaClasses().size() );
    }

    public void testGetJavaClassByName() {
        assertEquals( null, nullClassLibrary.getJavaClass( "java.lang.String" ) );
        assertEquals( null, nullClassLibrary.getJavaClass( "com.thoughtworks.qdox.JavaProjectBuilder" ) );
    }
    
    public void testChainedJavaClass() {
        //prepare libraries
        parentClassLibrary.getJavaClass( "ParentClass" );
        filledChildClassLibrary.getJavaClass( "ChildClass" );
        
        Collection<JavaClass> parentClasses = parentClassLibrary.getJavaClasses( new AbstractClassLibrary.ClassLibraryFilter(){
            public boolean accept(AbstractClassLibrary classLibrary) { return true; }
        } ); 
        assertEquals(1, parentClasses.size());
        assertEquals( "ParentClass", parentClasses.iterator().next().getFullyQualifiedName() );
        
        Collection<JavaClass> filledClasses = filledChildClassLibrary.getJavaClasses(new AbstractClassLibrary.ClassLibraryFilter(){
            public boolean accept(AbstractClassLibrary classLibrary) { return true; }
        } ); 
        assertEquals(2, filledClasses.size() );
        Iterator<JavaClass> iter = filledClasses.iterator();
        assertEquals( "ChildClass", iter.next().getFullyQualifiedName() );
        assertEquals( "ParentClass", iter.next().getFullyQualifiedName() );
        
        Collection<JavaClass> emptyClasses = emptyChildClassLibrary.getJavaClasses(new AbstractClassLibrary.ClassLibraryFilter(){
            public boolean accept(AbstractClassLibrary classLibrary) { return true; }
        } ); 
        assertEquals(1, emptyClasses.size() );
        assertEquals( "ParentClass", emptyClasses.iterator().next().getFullyQualifiedName() );
    }
    
    
    /*
     * Never null, empty by default
     */
    public void testGetJavaPackages() {
        assertEquals( 0, nullClassLibrary.getJavaPackages().size() );
    }
    
    public void testGetJavaPackageByName() {
        assertEquals( null, nullClassLibrary.getJavaPackage( "java.lang" ) );
        assertEquals( null, nullClassLibrary.getJavaPackage( "com.thoughtworks" ) );
    }
    
    public void testModuleInfo()
    {
        assertNull( nullClassLibrary.getJavaModules() );
        assertNull( parentClassLibrary.getJavaModules() );
        assertNull( filledChildClassLibrary.getJavaModules() );
        assertNull( emptyChildClassLibrary.getJavaModules() );
    }
}
