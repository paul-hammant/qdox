package com.thoughtworks.qdox.model.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaExecutable;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;

/**
 * The BaseMethod contains all methods used by both JavaMethod and JavaConstructor 
 * 
 * @author Robert Scholte
 *
 */
public abstract class DefaultJavaExecutable 
    extends AbstractInheritableJavaEntity implements JavaExecutable
{

    private List<JavaParameter> parameters = Collections.emptyList();
    private List<JavaClass> exceptions = Collections.emptyList();
    private boolean varArgs;
    private String sourceCode;

    /** {@inheritDoc} */
    public List<JavaParameter> getParameters()
    {
        return parameters;
    }

    /** {@inheritDoc} */
    public JavaParameter getParameterByName( String name )
    {
        for (JavaParameter parameter : getParameters()) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<JavaClass> getExceptions()
    {
        return new LinkedList<JavaClass>( exceptions );
    }

    /** {@inheritDoc} */
    public List<JavaType> getExceptionTypes()
    {
        return new LinkedList<JavaType>( exceptions );
    }

    /** {@inheritDoc} */
    public boolean isVarArgs()
    {
        return varArgs;
    }

    public void setParameters( List<JavaParameter> javaParameters )
    {
        parameters = javaParameters;
        this.varArgs = javaParameters.get( javaParameters.size() -1 ).isVarArgs();
    }

    public void setExceptions( List<JavaClass> exceptions )
    {
        this.exceptions = exceptions;
    }

    protected boolean signatureMatches( List<JavaType> parameterTypes, boolean varArgs )
    {
        List<JavaType> parameterTypeList;
        if( parameterTypes == null) {
            parameterTypeList = Collections.emptyList();
        }
        else {
            parameterTypeList = parameterTypes;
        }
        
        if (parameterTypeList.size() != this.getParameters().size()) 
        {
            return false;   
        }
        
        for (int i = 0; i < getParameters().size(); i++) 
        {
            if (!getParameters().get(i).getType().equals(parameterTypes.get(i))) {
                return false;
            }
        }
        return (this.varArgs == varArgs);
    }

    /** {@inheritDoc} */
    @Override
	public boolean isPublic()
    {
        return super.isPublic() || (getDeclaringClass() != null ? getDeclaringClass().isInterface() : false);
    }

    /** {@inheritDoc} */
    @Override
	public List<DocletTag> getTagsByName( String name, boolean inherited )
    {
        JavaClass cls = getDeclaringClass();
        List<JavaType> types = new LinkedList<JavaType>();
        for (JavaParameter parameter : getParameters()) {
            types.add(parameter.getType());
        }
        List<JavaMethod> methods = cls.getMethodsBySignature(getName(), types, true);
    
        List<DocletTag> result = new LinkedList<DocletTag>();
        for (JavaMethod method : methods) {
            List<DocletTag> tags = method.getTagsByName(name);
            for (DocletTag tag : tags) {
                if(!result.contains(tag)) {
                    result.add(tag);
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    public List<JavaType> getParameterTypes()
    {
        return getParameterTypes( false );
    }

    /** {@inheritDoc} */
    public List<JavaType> getParameterTypes( boolean resolve )
    {
        List<JavaType> result = new LinkedList<JavaType>();
        for ( JavaParameter parameter : this.getParameters() )
        {
            result.add( parameter.getType() );
        }
        return result;
    }

    /** {@inheritDoc} */
    public String getSourceCode()
    {
    	return sourceCode;
    }

    public void setSourceCode( String sourceCode )
    {
    	this.sourceCode = sourceCode;
    }

    public String getCallSignature()
    {
        return getSignature( false,  false );
    }
    
    // only here for shared signature-code.
    protected JavaType getReturns()
    {
        return null;
    }

    /**
     * @since 1.3
     */
    protected String getSignature( boolean withModifiers, boolean isDeclaration )
    {
        StringBuilder result = new StringBuilder();
        if ( withModifiers )
        {
            for ( String modifier : getModifiers() )
            {
                // check for public, protected and private
                if ( modifier.startsWith( "p" ) )
                {
                    result.append( modifier ).append( ' ' );
                }
            }
            for ( String modifier : getModifiers() )
            {
                // check for public, protected and private
                if ( !modifier.startsWith( "p" ) )
                {
                    result.append( modifier ).append( ' ' );
                }
            }
        }
    
        if ( isDeclaration && getReturns() != null )
        {
            
            result.append( getReturns().getGenericCanonicalName() );
            result.append( ' ' );
        }
    
        result.append( getName() );
        result.append( '(' );
        for ( ListIterator<JavaParameter> iter = getParameters().listIterator(); iter.hasNext(); )
        {
            JavaParameter parameter = iter.next();
            if ( isDeclaration )
            {
                result.append( parameter.getType().getGenericCanonicalName() );
                if ( parameter.isVarArgs() )
                {
                    result.append( "..." );
                }
                result.append( ' ' );
            }
            result.append( parameter.getName() );
            if ( iter.hasNext() )
            {
                result.append( ", " );
            }
        }
        result.append( ')' );
        if ( isDeclaration && !getExceptions().isEmpty() )
        {
            result.append( " throws " );
            for ( Iterator<JavaClass> excIter = getExceptions().iterator(); excIter.hasNext(); )
            {
                result.append( excIter.next().getGenericCanonicalName() );
                if ( excIter.hasNext() )
                {
                    result.append( ", " );
                }
            }
        }
        return result.toString();
    }

}