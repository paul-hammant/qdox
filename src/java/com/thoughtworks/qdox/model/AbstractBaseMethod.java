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

    private List<TypeVariable> typeParameters = Collections.emptyList();
    private List<JavaParameter> parameters = new LinkedList<JavaParameter>();
    protected List<Type> exceptions = Collections.emptyList();
    protected boolean varArgs;

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

    public List<Type> getExceptions()
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

    public void addParameter( JavaParameter javaParameter )
    {
        parameters.add( javaParameter );
        this.varArgs = javaParameter.isVarArgs();
    }

    public void setExceptions( List<Type> exceptions )
    {
        this.exceptions = exceptions;
    }

    public boolean signatureMatches( String name, List<Type> parameterTypes )
    {
        return signatureMatches( name, parameterTypes, false );
    }

    public boolean signatureMatches( String name, List<Type> parameterTypes, boolean varArg )
    {
        if (!name.equals(this.getName())) return false;
        
        List<Type> parameterTypeList;
        if( parameterTypes == null) {
            parameterTypeList = Collections.emptyList();
        }
        else {
            parameterTypeList = parameterTypes;
        }
        
        if (parameterTypeList.size() != this.getParameters().size()) return false;
        
        for (int i = 0; i < parameters.size(); i++) {
            if (!parameters.get(i).getType().equals(parameterTypes.get(i))) {
                return false;
            }
        }
        return (this.varArgs == varArg);
    }

    public boolean isPublic()
    {
        return super.isPublic() || (getParentClass() != null ? getParentClass().isInterface() : false);
    }

    public List<DocletTag> getTagsByName( String name, boolean inherited )
    {
        JavaClass clazz = getParentClass();
        List<Type> types = new LinkedList<Type>();
        for (JavaParameter parameter : getParameters()) {
            types.add(parameter.getType());
        }
        List<JavaMethod> methods = clazz.getMethodsBySignature(getName(), types, true);
    
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

    public void setTypeParameters( List<TypeVariable> typeParameters )
    {
    	this.typeParameters = typeParameters;
    }

    public List<TypeVariable> getTypeParameters()
    {
    	return typeParameters;
    }

    public List<Type> getParameterTypes()
    {
        return getParameterTypes( false );
    }

    public List<Type> getParameterTypes( boolean resolve )
    {
        List<Type> result = new LinkedList<Type>();
        for (JavaParameter parameter : this.getParameters()) {
            result.add( parameter.getType() );
        }
        return result;
    }

}