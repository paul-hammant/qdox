package com.thoughtworks.qdox.library;

import com.thoughtworks.qdox.JavaClassContext;
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
                if( result.getPackage() != null ) {
                    context.add( result.getPackage() );
                }
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
    
    public JavaSource[] getJavaSources()
    {
        return context.getSources();
    }
    
    /**
     * 
     * 
     * @param filter
     * @return
     */
    protected final JavaSource[] getJavaSources( ClassLibraryFilter filter) {
        JavaSource[] result = null; 
        JavaSource[] thisJavaSources = null;
        JavaSource[] parentJavaSources = null;
        if ( parentClassLibrary != null ) {
            parentJavaSources = parentClassLibrary.getJavaSources( filter );
        }
        if(filter.accept(this)) {
            thisJavaSources = context.getSources();
        }
        
        if ( parentJavaSources == null || parentJavaSources.length == 0) {
            result = thisJavaSources;
        }
        else if( thisJavaSources == null || thisJavaSources.length == 0 ) {
            result = parentJavaSources;
        }
        else {
            int totalSources = thisJavaSources.length + parentJavaSources.length;
            result = new JavaSource[totalSources]; 
            System.arraycopy( thisJavaSources, 0, result, 0, thisJavaSources.length );
            System.arraycopy( parentJavaSources, 0, result, thisJavaSources.length, parentJavaSources.length );

        }
        return result;
    }
    
    /**
     * Get all the sources of the current {@link AbstractClassLibrary}.
     * Subclasses can overwrite this method by including the following code
     * <code> 
     * public JavaClass[] getClasses()
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
     */
    public JavaClass[] getJavaClasses()
    {
        return context.getClasses();
    }

    /**
     * Subclasses can call this method to gather all JavaClass object, including those from the parent.
     * 
     * @param filter
     * @return
     */
    protected final JavaClass[] getJavaClasses( ClassLibraryFilter filter) {
        JavaClass[] result = null; 
        JavaClass[] thisJavaClasses = null;
        JavaClass[] parentJavaClasses = null;
        if ( parentClassLibrary != null ) {
            parentJavaClasses = parentClassLibrary.getJavaClasses( filter );
        }
        if(filter.accept(this)) {
            thisJavaClasses = context.getClasses();
        }
        
        if ( parentJavaClasses == null || parentJavaClasses.length == 0) {
            result = thisJavaClasses;
        }
        else if( thisJavaClasses == null || thisJavaClasses.length == 0 ) {
            result = parentJavaClasses;
        }
        else {
            int totalClasses = thisJavaClasses.length + parentJavaClasses.length;
            result = new JavaClass[totalClasses]; 
            System.arraycopy( thisJavaClasses, 0, result, 0, thisJavaClasses.length );
            System.arraycopy( parentJavaClasses, 0, result, thisJavaClasses.length, parentJavaClasses.length );

        }
        return result;
    }
    
    /**
     * 
     * @return all JavaPackages of this ClassLibrary
     */
    public JavaPackage[] getJavaPackages()
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
    
    protected final JavaPackage[] getJavaPackages( ClassLibraryFilter filter) {
        JavaPackage[] result = null; 
        JavaPackage[] thisJavaPackages = null;
        JavaPackage[] parentJavaPackages = null;
        if ( parentClassLibrary != null ) {
            parentJavaPackages = parentClassLibrary.getJavaPackages( filter );
        }
        if( filter.accept( this ) ) {
            thisJavaPackages = context.getPackages();
        }
        
        if ( parentJavaPackages == null || parentJavaPackages.length == 0) {
            result = thisJavaPackages;
        }
        else if( thisJavaPackages == null || thisJavaPackages.length == 0 ) {
            result = parentJavaPackages;
        }
        else {
            int totalPackages = thisJavaPackages.length + parentJavaPackages.length;
            result = new JavaPackage[totalPackages]; 
            System.arraycopy( thisJavaPackages, 0, result, 0, thisJavaPackages.length );
            System.arraycopy( parentJavaPackages, 0, result, thisJavaPackages.length, parentJavaPackages.length );

        }
        return result;
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
        boolean accept( AbstractClassLibrary classLibrary );
    }
}