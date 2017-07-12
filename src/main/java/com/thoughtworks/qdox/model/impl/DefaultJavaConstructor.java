package com.thoughtworks.qdox.model.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;

/**
 * 
 * @author Robert
 * @since 2.0
 */
public class DefaultJavaConstructor
    extends DefaultJavaExecutable implements JavaConstructor
{

    private List<JavaTypeVariable<JavaConstructor>> typeParameters = Collections.emptyList();

    public void setTypeParameters( List<JavaTypeVariable<JavaConstructor>> typeParameters )
    {
        this.typeParameters = typeParameters;
    }

    /** {@inheritDoc} */
    public List<JavaTypeVariable<JavaConstructor>> getTypeParameters()
    {
        return typeParameters;
    }
    
    /** {@inheritDoc} */
    public boolean signatureMatches( List<JavaType> parameterTypes )
    {
        return signatureMatches( parameterTypes, false );
    }
    
    /** {@inheritDoc} */
    @Override
	public boolean signatureMatches( List<JavaType> parameterTypes, boolean varArgs )
    {
        return super.signatureMatches( parameterTypes, varArgs );
    }
    
    /** {@inheritDoc} */
    public String getCodeBlock()
    {
        return getModelWriter().writeConstructor( this ).toString();
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        if ( isPrivate() )
        {
            result.append( "private " );
        }
        else if ( isProtected() )
        {
            result.append( "protected " );
        }
        else if ( isPublic() )
        {
            result.append( "public " );
        }
        if ( getDeclaringClass() != null )
        {
            result.append( getDeclaringClass().getFullyQualifiedName() );
        }
        result.append( "(" );
        for ( Iterator<JavaParameter> paramIter = getParameters().iterator(); paramIter.hasNext();)
        {
            String typeValue = DefaultJavaType.getResolvedValue( paramIter.next().getType(), getTypeParameters() );
            result.append( typeValue );
            if ( paramIter.hasNext() )
            {
                result.append( "," );
            }
        }
        result.append( ")" );
        if ( getExceptions().size() > 0 )
        {
            result.append( " throws " );
            for ( Iterator<JavaClass> excIter = getExceptions().iterator(); excIter.hasNext(); )
            {
                result.append( excIter.next().getValue() );
                if ( excIter.hasNext() )
                {
                    result.append( "," );
                }
            }
        }
        return result.toString();
    }

    @Override
    public int hashCode()
    {
        int hashCode = 3 + getName().hashCode();
        hashCode *= 31 + getParameters().hashCode();
        return hashCode;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof JavaConstructor ) )
        {
            return false;
        }

        JavaConstructor c = (JavaConstructor) obj;
        
        if ( c.getDeclaringClass() != null ? !c.getDeclaringClass().equals( this.getDeclaringClass() ) : this.getDeclaringClass() != null )
        {
            return false;
        }
        
        if ( !c.getName().equals( getName() ) )
        {
            return false;
        }

        List<JavaParameter> myParams = getParameters();
        List<JavaParameter> otherParams = c.getParameters();
        if ( otherParams.size() != myParams.size() )
        {
            return false;
        }
        for ( int i = 0; i < myParams.size(); i++ )
        {
            if ( !otherParams.get( i ).equals( myParams.get( i ) ) )
            {
                return false;
            }
        }
        return this.isVarArgs() == c.isVarArgs();
    }
}
