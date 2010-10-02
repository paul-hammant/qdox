package com.thoughtworks.qdox.library;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.model.ModelBuilderFactory;
import com.thoughtworks.qdox.parser.impl.BinaryClassParser;

/**
 * <strong>Important!! Be sure to add a classloader with the bootstrap classes.</strong>
 * <p>
 * Normally you can generate your classLibrary like this:<br/>
 * <code>
 *  ClassLibrary classLibrary = new ClassLibrary();
 *  classLibrary.addDefaultLoader();
 * </code>
 * </p>
 * <p>
 * If you want full control over the classLoaders you might want to create your library like:<br/>
 * <code>
 * ClassLibrary classLibrary = new ClassLibrary( ClassLoader.getSystemClassLoader() )
 * </code>
 * </p>
 * 
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @author Robert Scholte
 */
public class ClassLoaderLibrary
    extends AbstractClassLibrary
{
    private transient List classLoaders = new ArrayList(); // <java.lang.ClassLoader>

    private boolean defaultClassLoadersAdded = false;

    public ClassLoaderLibrary( ClassLibrary parent )
    {
        super( parent );
    }

    public ClassLoaderLibrary( ClassLoader classLoader )
    {
        this.classLoaders.add( classLoader );
    }

    public ClassLoaderLibrary( ClassLoader classLoader, ClassLibrary parent )
    {
        super( parent );
        this.classLoaders.add( classLoader );
    }

    public void addClassLoader( ClassLoader classLoader )
    {
        classLoaders.add( classLoader );
    }

    public void addDefaultLoader()
    {
        if ( !defaultClassLoadersAdded )
        {
            classLoaders.add( getClass().getClassLoader() );
            classLoaders.add( Thread.currentThread().getContextClassLoader() );
        }
        defaultClassLoadersAdded = true;
    }

    /**
     * {@inheritDoc}
     */
    protected JavaClass resolveJavaClass( String name )
    {
        JavaClass result = null;
        Iterator iter = classLoaders.iterator();
        while ( iter.hasNext() )
        {
            ClassLoader classLoader = (ClassLoader) iter.next();
            try
            {
                Class clazz = classLoader.loadClass( name );
                ModelBuilder builder = getModelBuilder();
                BinaryClassParser parser = new BinaryClassParser( clazz, builder );
                if ( parser.parse() )
                {
                    result = builder.getSource().getClasses()[0];
                }
                break;
            }
            catch ( ClassNotFoundException e )
            {
            }
        }
        return result;
    }

    private void readObject( ObjectInputStream in )
        throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        classLoaders = new ArrayList();
        if ( defaultClassLoadersAdded )
        {
            defaultClassLoadersAdded = false;
            addDefaultLoader();
        }
    }
    
    protected boolean containsClassByName( String name )
    {
        boolean result = false;
        Iterator iter = classLoaders.iterator();
        while ( iter.hasNext() )
        {
            ClassLoader classLoader = (ClassLoader) iter.next();
            try
            {
                Class clazz = classLoader.loadClass( name );
                result = true;
            }
            catch ( ClassNotFoundException e )
            {
            }
        }
        return result;
    }
}
