package com.thoughtworks.qdox.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Robert
 * @since 2.0
 */
public class DefaultJavaConstructor
    extends AbstractBaseMethod implements JavaConstructor
{

    private List<TypeVariable<JavaConstructor>> typeParameters = Collections.emptyList();

    public void setTypeParameters( List<TypeVariable<JavaConstructor>> typeParameters )
    {
        this.typeParameters = typeParameters;
    }

    public List<TypeVariable<JavaConstructor>> getTypeParameters()
    {
        return typeParameters;
    }
    
    public boolean signatureMatches( List<Type> parameterTypes )
    {
        return signatureMatches( parameterTypes, false );
    }
    
    public boolean signatureMatches( List<Type> parameterTypes, boolean varArgs )
    {
        return super.signatureMatches( parameterTypes, varArgs );
    }
    
    public String getCodeBlock()
    {
        return getModelWriter().writeConstructor( this ).toString();
    }
    
    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
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
        if ( getParentClass() != null )
        {
            result.append( getParentClass().getFullyQualifiedName() );
        }
        result.append( "(" );
        for ( Iterator<JavaParameter> paramIter = getParameters().iterator(); paramIter.hasNext();)
        {
            String typeValue = paramIter.next().getType().getResolvedValue( getTypeParameters() );
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
