package com.thoughtworks.qdox.library;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.builder.ModelBuilderFactory;
import com.thoughtworks.qdox.builder.impl.ModelBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.impl.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

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

    private ModelWriterFactory modelWriterFactory;
    
    private JavaClassContext context = new JavaClassContext();

    /**
     * constructor for root ClassLibrary
     */
    public AbstractClassLibrary()
    {
    }

    /**
     * constructor for chained ClassLibrary
     * @param parent the parent library
     */
    public AbstractClassLibrary( AbstractClassLibrary parent )
    {
        this.parentClassLibrary = parent;
    }
    
    public Collection<JavaModule> getJavaModules()
    {
        Collection<JavaModule> modules = null;
        if ( parentClassLibrary != null )
        {
            modules = parentClassLibrary.getJavaModules();
        }
        return modules;
    }

    /**
     * First checks if there's a JavaClass available in the private context by this name. Otherwise try to resolve it by
     * the concrete class. If there's still no JavaClass, ask the parent (if available) to resolve it.
     * 
     * @param name the binary name of the class
     * @return the JavaClass matching the name, otherwise <code>null</code>
     */
    public final JavaClass getJavaClass( String name ) {
       return getJavaClass( name, false ); 
    }
    
    public final JavaClass getJavaClass( String name, boolean createStub ) {
        JavaClass result = context.getClassByName( name );
        if ( result == null )
        {
            result = resolveJavaClass( name );

            if ( result != null )  
            {
                context.add( result );
                context.add( result.getSource() );
                
                JavaPackage contextPackage = context.getPackageByName( result.getPackageName() ); 
                if( contextPackage == null ) {
                    DefaultJavaPackage newContextPackage = new DefaultJavaPackage( result.getPackageName() );
                    newContextPackage.setClassLibrary( this );
                    context.add( newContextPackage );    

                    contextPackage  = newContextPackage;
                }
                contextPackage.getClasses().addAll( result.getNestedClasses() );
            }
        }
        if ( result == null && parentClassLibrary != null )
        {
            result = parentClassLibrary.getJavaClass( name );
        }
        if (result == null && createStub) {
            result = createStub(name);
        }
        return result;
    }

    private JavaClass createStub( String name )
    {
        Builder unknownBuilder = getModelBuilder();
        unknownBuilder.beginClass( new ClassDef( name ) );
        unknownBuilder.endClass();
        JavaSource unknownSource = unknownBuilder.getSource();
        return unknownSource.getClasses().get( 0 );
    }

    /**
     * The implementation should check it's sources to see if it can build a JavaClass Model If not, just return null;
     * Once found it will be mapped, so there's no need to keep a reference to this object.
     * 
     * @param name the fully qualified name
     * @return the resolved JavaClass, otherwise <code>null</code>
     */
    protected abstract JavaClass resolveJavaClass( String name );
    
    public Collection<JavaSource> getJavaSources()
    {
        return context.getSources();
    }
    
    /**
     * 
     * 
     * @param filter the classlibrary filter
     * @return JavaSources matching the filter
     */
    protected final Collection<JavaSource> getJavaSources( ClassLibraryFilter filter) {
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
     * <pre> 
     * public List&lt;JavaClass&gt; getClasses()
     * {
     *   return getJavaClasses( new ClassLibraryFilter()
     *   {
     *      public boolean accept( AbstractClassLibrary classLibrary )
     *      {
     *          return true;
     *      }
     *   });
     * }
     * </pre>
     * This example would return all created {@link JavaClass } objects, including those from the classloaders.
     * 
     * @return all JavaClasses of this ClassLibrary
     */
    public Collection<JavaClass> getJavaClasses()
    {
        return context.getClasses();
    }

    /**
     * Subclasses can call this method to gather all JavaClass object, including those from the parent.
     * 
     * @param filter the classlibrary filter
     * @return JavaClasses matching the filter
     */
    protected final Collection<JavaClass> getJavaClasses( ClassLibraryFilter filter) {
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
     * <pre> 
     * public List&lt;JavaPackage&gt; getJavaPackages()
     * {
     *   return getJavaPackages( new ClassLibraryFilter()
     *   {
     *      public boolean accept( AbstractClassLibrary classLibrary )
     *      {
     *          return true;
     *      }
     *   });
     * }
     * </pre>
     * This example would return all created {@link JavaPackage } objects, including those from the classloaders.
     * 
     * @return all JavaPackages of this ClassLibrary
     */
    public Collection<JavaPackage> getJavaPackages()
    {
        return context.getPackages();
    }
    
    /**
     * @param name the fully qualified name
     * @return the JavaPackage matching the name, otherwise <code>null</code>
     */
    public final JavaPackage getJavaPackage( String name ) {
        JavaPackage result = context.getPackageByName( name );
        if (result == null) {
        	result = resolveJavaPackage( name );
        	if (result != null) {
        		context.add(result);
        	}
        }
        if(result == null  && parentClassLibrary != null ) {
            result = parentClassLibrary.getJavaPackage( name );
        }
        return result;
    }
    
    protected abstract JavaPackage resolveJavaPackage(String name);

	protected final Collection<JavaPackage> getJavaPackages( ClassLibraryFilter filter) {
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
     * @param name the fully qualified name
     * @return <code>true</code> if there is a reference, otherwise <code>false</code>
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
     * This method is used to detect if there's a match with this classname.
     * The name could be constructed based on imports and inner class paths.
     * 
     * @param name the fully qualified name of the class
     * @return true if this ClassLibrary has a reference to this class.
     */
    protected abstract boolean containsClassReference( String name );
    
    /**
     * Set the ModelBuilderFactory for this classLibrary. 
     * 
     * @param factory the model builder factory
     */
    public final void setModelBuilderFactory( ModelBuilderFactory factory ) {
        this.modelBuilderFactory = factory;
    }
    
    /**
     * Set the ModelWriterFactory for this class.
     * 
     * @param factory the model writer factory
     */
    public final void setModelWriterFactory( ModelWriterFactory factory )
    {
        this.modelWriterFactory = factory;
    }

    protected final ModelWriterFactory getModelWriterFactory()
    {
        return modelWriterFactory;
    }
    
    protected final ModelBuilderFactory getModelBuilderFactory()
    {
        return modelBuilderFactory;
    }
    
    /**
     * If there's a modelBuilderFactory available, ask it for a new instance.
     * Otherwise, return a default ModelBuilder.
     * In both cases, pass this library as argument.
     * 
     * @return a new instance of a ModelBuilder, never <code>null</code>
     */
    protected Builder getModelBuilder()
    {
        ModelBuilder result;
        if ( modelBuilderFactory != null )
        {
            result = modelBuilderFactory.newInstance( this );
        }
        else
        {
            result = new ModelBuilder( this, new DefaultDocletTagFactory() );
        }
        result.setModelWriterFactory( modelWriterFactory );
        return result;
    }
    
    protected Builder getModelBuilder( URL url )
    {
        Builder result = getModelBuilder();
        result.setUrl( url );
        return result;
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