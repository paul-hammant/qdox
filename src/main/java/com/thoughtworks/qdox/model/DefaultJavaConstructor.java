package com.thoughtworks.qdox.model;

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
        for ( int paramIndex = 0; paramIndex < getParameters().size(); paramIndex++ )
        {
            if ( paramIndex > 0 )
            {
                result.append( "," );
            }
            String typeValue = getParameters().get( paramIndex ).getType().getResolvedValue( getTypeParameters() );
            result.append( typeValue );
        }
        result.append( ")" );
        if ( getExceptions().size() > 0 )
        {
            result.append( " throws " );
            for ( Iterator<Type> excIter = getExceptions().iterator(); excIter.hasNext(); )
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
    public int hashCode() {
        int hashCode = getName().hashCode();
        hashCode *= getParameters().size();
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
