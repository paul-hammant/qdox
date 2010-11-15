package com.thoughtworks.qdox.model;

import java.util.List;

/**
 * This class can be used to access overridden methods while keeping a reference to the original class.
 * This is especially useful when trying to resolve generics
 * 
 * @author Robert Scholte
 * @since 1.12
 */
public class JavaMethodDelegate extends JavaMethod
{

    private JavaClass callingClass;
    private JavaMethod originalMethod;
    
    public JavaMethodDelegate( JavaClass callingClass, JavaMethod originalMethod )
    {
        this.callingClass = callingClass;
        this.originalMethod = originalMethod;
    }
    
    public Type getReturnType( boolean resolve )
    {
        Type returnType = originalMethod.getReturnType( resolve, callingClass );
        return returnType.resolve( originalMethod.getParentClass(), callingClass );
    }

    protected Type getReturnType( boolean resolve, JavaClass _callingClass )
    {
        //watch it!! use callingclass of constructor
        return originalMethod.getReturnType( resolve, this.callingClass );
    }
    
    public Type[] getParameterTypes( boolean resolve )
    {
        Type[] parameterTypes = originalMethod.getParameterTypes( resolve, callingClass );
        for ( int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++ )
        {
            parameterTypes[paramIndex] = parameterTypes[paramIndex].resolve( originalMethod.getParentClass(), callingClass  );
        }
        return parameterTypes;
    }
    
    protected Type[] getParameterTypes( boolean resolve, JavaClass _callingClass )
    {
        //watch it!! use callingclass of constructor
        return originalMethod.getParameterTypes( resolve, this.callingClass );
    }
    
    //Delegating methods
    
    public void addParameter( JavaParameter javaParameter )
    {
        originalMethod.addParameter( javaParameter );
    }

    public int compareTo( Object o )
    {
        return originalMethod.compareTo( o );
    }

    public boolean equals( Object obj )
    {
        return originalMethod.equals( obj );
    }

    public List<Annotation> getAnnotations()
    {
        return originalMethod.getAnnotations();
    }

    public String getCallSignature()
    {
        return originalMethod.getCallSignature();
    }

    public String getCodeBlock()
    {
        return originalMethod.getCodeBlock();
    }

    public String getComment()
    {
        return originalMethod.getComment();
    }

    public String getDeclarationSignature( boolean withModifiers )
    {
        return originalMethod.getDeclarationSignature( withModifiers );
    }

    public Type[] getExceptions()
    {
        return originalMethod.getExceptions();
    }

    public Type getGenericReturnType()
    {
        return originalMethod.getGenericReturnType();
    }

    public int getLineNumber()
    {
        return originalMethod.getLineNumber();
    }

    public List<String> getModifiers()
    {
        return originalMethod.getModifiers();
    }

    public String getName()
    {
        return originalMethod.getName();
    }

    public String getNamedParameter( String tagName, String parameterName )
    {
        return originalMethod.getNamedParameter( tagName, parameterName );
    }

    public JavaParameter getParameterByName( String name )
    {
        return originalMethod.getParameterByName( name );
    }

    public JavaParameter[] getParameters()
    {
        return originalMethod.getParameters();
    }
    
    public Type[] getParameterTypes()
    {
        return originalMethod.getParameterTypes();
    }

    public JavaClassParent getParent()
    {
        return originalMethod.getParent();
    }

    public JavaClass getParentClass()
    {
        return originalMethod.getParentClass();
    }

    public String getPropertyName()
    {
        return originalMethod.getPropertyName();
    }

    public Type getPropertyType()
    {
        return originalMethod.getPropertyType();
    }

    public Type getReturns()
    {
        return originalMethod.getReturns();
    }

    public Type getReturnType()
    {
        return getReturnType( false );
    }

    public JavaSource getSource()
    {
        return originalMethod.getSource();
    }

    public String getSourceCode()
    {
        return originalMethod.getSourceCode();
    }

    public DocletTag getTagByName( String name, boolean inherited )
    {
        return originalMethod.getTagByName( name, inherited );
    }

    public DocletTag getTagByName( String name )
    {
        return originalMethod.getTagByName( name );
    }

    public List<DocletTag> getTags()
    {
        return originalMethod.getTags();
    }

    public List<DocletTag> getTagsByName( String name, boolean inherited )
    {
        return originalMethod.getTagsByName( name, inherited );
    }

    public List<DocletTag> getTagsByName( String name )
    {
        return originalMethod.getTagsByName( name );
    }

    public TypeVariable[] getTypeParameters()
    {
        return originalMethod.getTypeParameters();
    }

    public int hashCode()
    {
        return originalMethod.hashCode();
    }

    public boolean isAbstract()
    {
        return originalMethod.isAbstract();
    }

    public boolean isConstructor()
    {
        return originalMethod.isConstructor();
    }

    public boolean isFinal()
    {
        return originalMethod.isFinal();
    }

    public boolean isNative()
    {
        return originalMethod.isNative();
    }

    public boolean isPrivate()
    {
        return originalMethod.isPrivate();
    }

    public boolean isPropertyAccessor()
    {
        return originalMethod.isPropertyAccessor();
    }

    public boolean isPropertyMutator()
    {
        return originalMethod.isPropertyMutator();
    }

    public boolean isProtected()
    {
        return originalMethod.isProtected();
    }

    public boolean isPublic()
    {
        return originalMethod.isPublic();
    }

    public boolean isStatic()
    {
        return originalMethod.isStatic();
    }

    public boolean isStrictfp()
    {
        return originalMethod.isStrictfp();
    }

    public boolean isSynchronized()
    {
        return originalMethod.isSynchronized();
    }

    public boolean isTransient()
    {
        return originalMethod.isTransient();
    }

    public boolean isVarArgs()
    {
        return originalMethod.isVarArgs();
    }

    public boolean isVolatile()
    {
        return originalMethod.isVolatile();
    }

    public void setAnnotations( Annotation[] annotations )
    {
        originalMethod.setAnnotations( annotations );
    }

    public void setComment( String comment )
    {
        originalMethod.setComment( comment );
    }

    public void setConstructor( boolean constructor )
    {
        originalMethod.setConstructor( constructor );
    }

    public void setExceptions( Type[] exceptions )
    {
        originalMethod.setExceptions( exceptions );
    }

    public void setLineNumber( int lineNumber )
    {
        originalMethod.setLineNumber( lineNumber );
    }

    public void setModifiers( String[] modifiers )
    {
        originalMethod.setModifiers( modifiers );
    }

    public void setName( String name )
    {
        originalMethod.setName( name );
    }

    public void setParent( JavaClassParent parent )
    {
        originalMethod.setParent( parent );
    }

    public void setParentClass( JavaClass parentClass )
    {
        originalMethod.setParentClass( parentClass );
    }

    public void setReturns( Type returns )
    {
        originalMethod.setReturns( returns );
    }

    public void setSourceCode( String sourceCode )
    {
        originalMethod.setSourceCode( sourceCode );
    }

    public void setTags( List tagList )
    {
        originalMethod.setTags( tagList );
    }

    public void setTypeParameters( TypeVariable[] typeParameters )
    {
        originalMethod.setTypeParameters( typeParameters );
    }

    public boolean signatureMatches( String name, Type[] parameterTypes, boolean varArg )
    {
        return originalMethod.signatureMatches( name, parameterTypes, varArg );
    }

    public boolean signatureMatches( String name, Type[] parameterTypes )
    {
        return originalMethod.signatureMatches( name, parameterTypes );
    }

    public String toString()
    {
        return originalMethod.toString();
    }
}
