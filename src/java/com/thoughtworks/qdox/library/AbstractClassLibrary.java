package com.thoughtworks.qdox.library;

import com.thoughtworks.qdox.JavaClassContext;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * A ClassLibrary can be compared with a java classloader. Its main task is to serve a JavaClass based on a FQN.
 * This AbstractClassLibrary should be inherited by all ClassLibraries. 
 * You can refer to a prent library, so these can be chained.
 * Besides that it contains a context only for this library. It will hold the definitions of JavaClasses and JavaPackages 
 * 
 * @author Robert Scholte
 */
public abstract class AbstractClassLibrary
    implements ClassLibrary
{
    private ClassLibrary parent;

    private JavaClassContext context = new JavaClassContext();

    public AbstractClassLibrary()
    {
    }

    public AbstractClassLibrary( ClassLibrary parent )
    {
        this.parent = parent;
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
        }
        if ( result != null )
        {
            context.add( result );
        }
        else if ( parent != null )
        {
            result = parent.getJavaClass( name );
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
}
