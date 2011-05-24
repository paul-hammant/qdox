package com.thoughtworks.qdox.builder;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.WildcardType;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.WildcardTypeDef;

/**
 * An assembler to transform a {@link TypeDef} to a {@link Type}
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public class TypeAssembler
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
            result = new WildcardType( wildcard.name, wildcard.getWildcardExpressionType(), context );
        }
        else
        {
            result = Type.createUnresolved( typeDef.name, typeDef.dimensions + dimensions, context );
            if ( typeDef.actualArgumentTypes != null && !typeDef.actualArgumentTypes.isEmpty() )
            {
                List<Type> actualArgumentTypes = new LinkedList<Type>();
                for ( TypeDef actualArgType : typeDef.actualArgumentTypes )
                {
                    actualArgumentTypes.add( TypeAssembler.createUnresolved( actualArgType, context ) );
                }
                result.setActualArgumentTypes( actualArgumentTypes );
            }
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
