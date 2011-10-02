package com.thoughtworks.qdox.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The BaseMethod contains all methods used by both JavaMethod and JavaConstructor 
 * 
 * @author Robert Scholte
 *
 */
public abstract class AbstractBaseMethod
    extends AbstractInheritableJavaEntity
{

    private List<JavaParameter> parameters = Collections.emptyList();
    private List<JavaClass> exceptions = Collections.emptyList();
    private boolean varArgs;
    private String sourceCode;

    public List<JavaParameter> getParameters()
    {
        return parameters;
    }

    public JavaParameter getParameterByName( String name )
    {
        for (JavaParameter parameter : getParameters()) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        return null;
    }

    public List<JavaClass> getExceptions()
    {
        return exceptions;
    }

    public boolean isVarArgs()
    {
        return varArgs;
    }

    public JavaClass getDeclaringClass()
    {
        return getParentClass();
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

    protected boolean signatureMatches( List<Type> parameterTypes, boolean varArgs )
    {
        List<Type> parameterTypeList;
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

    public boolean isPublic()
    {
        return super.isPublic() || (getParentClass() != null ? getParentClass().isInterface() : false);
    }

    public List<DocletTag> getTagsByName( String name, boolean inherited )
    {
        JavaClass cls = getParentClass();
        List<Type> types = new LinkedList<Type>();
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

    public List<JavaType> getParameterTypes()
    {
        return getParameterTypes( false );
    }

    public List<JavaType> getParameterTypes( boolean resolve )
    {
        List<JavaType> result = new LinkedList<JavaType>();
        for (JavaParameter parameter : this.getParameters()) {
            result.add( parameter.getType() );
        }
        return result;
    }

    public String getSourceCode()
    {
    	return sourceCode;
    }

    public void setSourceCode( String sourceCode )
    {
    	this.sourceCode = sourceCode;
    }

}