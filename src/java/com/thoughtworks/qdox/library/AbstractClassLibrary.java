package com.thoughtworks.qdox.library;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.model.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.model.ModelBuilderFactory;

/**
 * A ClassLibrary can be compared with a java classloader. 
 * Its main task is to serve JavaClasses based on the Fully Qualified Name.
 * AbstractClassLibraries hold a reference a parent library, in which way they can be chained.
 * Besides that it contains a context only for this library. 
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public abstract class AbstractClassLibrary
    implements ClassLibrary
{
    private AbstractClassLibrary parentClassLibrary;
    
    private ModelBuilderFactory modelBuilderFactory;

    private JavaClassContext context = new JavaClassContext();
    
    /**
     * constructor for root ClassLibrary
     */
    public AbstractClassLibrary()
    {
    }

    /**
     * constructor for chained ClassLibrary
     */
    public AbstractClassLibrary( AbstractClassLibrary parent )
    {
        this.parentClassLibrary = parent;
    }

    /**
     * First checks if there's a JavaClass available in the private context by this name. Otherwise try to resolve it by
     * the concrete class. If there's still no JavaClass, ask the parent (if available) to resolve it.
     * 
     * @param name
     * @return
     */
    public final JavaClass getJavaClass( String name )
    {
        JavaClass result = context.getClassByName( name );
        if ( result == null )
        {
            result = resolveJavaClass( name );

            if ( result != null )  
            {
                context.add( result );
                context.add( result.getPackage() );
                context.add( result.getSource() ); 
            }
        }
        if ( result == null && parentClassLibrary != null )
        {
            result = parentClassLibrary.getJavaClass( name );
        }
        return result;
    }

    /**
     * The implementation should check it's sources to see if it can build a JavaClass Model If not, just return null;
     * Once found it will be mapped, so there's no need to keep a reference to this object.
     * 
     * @param name
     * @return
     */
    protected abstract JavaClass resolveJavaClass( String name );
    
    public List<JavaSource> getJavaSources()
    {
        return context.getSources();
    }
    
    /**
     * 
     * 
     * @param filter
     * @return
     */
    protected final List<JavaSource> getJavaSources( ClassLibraryFilter filter) {
        List<JavaSource> result = new LinkedList<JavaSource>(); 
        if(filter.accept(this)) {
            result.addAll( context.getSources() );
        }
        if ( parentClassLibrary != null ) {
            result.addAll( parentClassLibrary.getJavaSources( filter ) );
        }
        return Collections.unmodifiableList( result );
    }
    
    /**
     * Get all the classes of the current {@link AbstractClassLibrary}.
     * Subclasses can overwrite this method by including the following code
     * <code> 
     * public List<JavaClass> getClasses()
     * {
     *   return getJavaClasses( new ClassLibraryFilter()
     *   {
     *      public boolean accept( AbstractClassLibrary classLibrary )
     *      {
     *          return true;
     *      }
     *   });
     * }
     * </code>
     * This example would return all created {@link JavaClass } objects, including those from the classloaders.
     * 
     * @return all JavaClasses of this ClassLibrary
     */
    public List<JavaClass> getJavaClasses()
    {
        return context.getClasses();
    }

    /**
     * Subclasses can call this method to gather all JavaClass object, including those from the parent.
     * 
     * @param filter
     * @return
     */
    protected final List<JavaClass> getJavaClasses( ClassLibraryFilter filter) {
        List<JavaClass> result = new LinkedList<JavaClass>(); 
        if(filter.accept(this)) {
            result.addAll( context.getClasses() );
        }
        if ( parentClassLibrary != null ) {
            result.addAll( parentClassLibrary.getJavaClasses( filter ) );
        }
        return Collections.unmodifiableList( result );
    }
    
    /**
     * Get all packages of the current {@link AbstractClassLibrary}.
     * Subclasses can overwrite this method by including the following code
     * <code> 
     * public List<JavaPackage> getJavaPackages()
     * {
     *   return getJavaPackages( new ClassLibraryFilter()
     *   {
     *      public boolean accept( AbstractClassLibrary classLibrary )
     *      {
     *          return true;
     *      }
     *   });
     * }
     * </code>
     * This example would return all created {@link JavaPackage } objects, including those from the classloaders.
     * 
     * @return all JavaPackages of this ClassLibrary
     */
    public List<JavaPackage> getJavaPackages()
    {
        return context.getPackages();
    }
    
    public JavaPackage getJavaPackage( String name ) {
        JavaPackage result = context.getPackageByName( name );
        if(result == null  && parentClassLibrary != null ) {
            result = parentClassLibrary.getJavaPackage( name );
        }
        return result;
    }
    
    protected final List<JavaPackage> getJavaPackages( ClassLibraryFilter filter) {
        List<JavaPackage> result = new LinkedList<JavaPackage>(); 
        if( filter.accept( this ) ) {
            result.addAll( context.getPackages() );
        }
        if ( parentClassLibrary != null ) {
            result.addAll( parentClassLibrary.getJavaPackages( filter ) );
        }
        return Collections.unmodifiableList( result );
    }
    
    /**
     * First checks if the context already has a JavaClass with this name.
     * If not, find out if this classlibrary is able to build a model for this class
     * Otherwise ask the parent if it could build a JavaClass.
     * 
     */
    public boolean hasClassReference( String name )
    {
        boolean result = context.getClassByName( name ) != null;
        if ( !result ) {
            result = containsClassReference( name );
        }
        if ( !result && parentClassLibrary != null ) {
            result = parentClassLibrary.hasClassReference( name );
        }
        return result;
    }
    
    /**
     * This method is used to detect if there´s a match with this classname.
     * The name could be constructed based on imports and inner class paths.
     * 
     * @param name the fully qualifed name of the class
     * @return true if this ClassLibrary has a reference to this class.
     */
    protected abstract boolean containsClassReference( String name );
    
    /**
     * Set the ModelBuilderFactory for this classLibrary. 
     * 
     * @param factory
     */
    public final void setModelBuilderFactory( ModelBuilderFactory factory ) {
        this.modelBuilderFactory = factory;
    }
    
    /**
     * If there's a modelBuilderFactory available, ask it for a new instance.
     * Otherwise, return a default ModelBuilder.
     * In both cases, pass this library as argument.
     * 
     * @return a new instance of a ModelBuilder, never <code>null</code>
     */
    protected ModelBuilder getModelBuilder() {
        if ( modelBuilderFactory != null) {
            return modelBuilderFactory.newInstance( this ); 
        }
        else {
            return new ModelBuilder( this, new DefaultDocletTagFactory());
        }
    }
    
    /**
     * A filter to use when checking all ancestors.
     * 
     * @author Robert Scholte
     */
    interface ClassLibraryFilter
    {
        /**
         * 
         * @param classLibrary
         * @return
         */
        boolean accept( AbstractClassLibrary classLibrary );
    }
}