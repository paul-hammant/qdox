package com.thoughtworks.qdox.builder;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.impl.DefaultJavaWildcardType;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.WildcardTypeDef;

/**
 * An assembler to transform a {@link TypeDef} to a {@link Type}
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public final class TypeAssembler
{
    private TypeAssembler()
    {
    }

    /**
     * this one is specific for those cases where dimensions can be part of both the type and identifier i.e. private
     * String[] matrix[]; //field public abstract String[] getMatrix[](); //method
     * 
     * @param typeDef
     * @param dimensions
     * @param context
     * @return the Type
     */
    public static Type createUnresolved( TypeDef typeDef, int dimensions, JavaClassParent context )
    {
        Type result;
        if ( typeDef instanceof WildcardTypeDef )
        {
            WildcardTypeDef wildcard = (WildcardTypeDef) typeDef;
            result = new DefaultJavaWildcardType( wildcard.getName(), wildcard.getWildcardExpressionType(), context );
        }
        else
        {
            Type typeResult = Type.createUnresolved( typeDef.getName(), typeDef.getDimensions() + dimensions, context );
            if ( typeDef.getActualArgumentTypes() != null && !typeDef.getActualArgumentTypes().isEmpty() )
            {
                List<JavaType> actualArgumentTypes = new LinkedList<JavaType>();
                for ( TypeDef actualArgType : typeDef.getActualArgumentTypes() )
                {
                    actualArgumentTypes.add( TypeAssembler.createUnresolved( actualArgType, context ) );
                }
                typeResult.setActualArgumentTypes( actualArgumentTypes );
            }
            result = typeResult;
        }
        return result;
    }

    /**
     * @param typeDef the TypeDef
     * @param context the context
     * @return the Type
     */
    public static Type createUnresolved( TypeDef typeDef, JavaClassParent context )
    {
        return createUnresolved( typeDef, 0, context );
    }

}
