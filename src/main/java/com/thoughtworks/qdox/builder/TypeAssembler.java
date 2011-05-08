package com.thoughtworks.qdox.builder;

import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.WildcardType;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.WildcardTypeDef;

public class TypeAssembler
{
    private TypeAssembler()
    {
    }
    
    /**
     * this one is specific for those cases where dimensions can be part of both the type and identifier
     * i.e. private String[] matrix[]; //field
     *      public abstract String[] getMatrix[](); //method  
     *      
     * @param typeDef
     * @param dimensions
     * @param context
     * @return the Type
     */
    public static Type createUnresolved(TypeDef typeDef, int dimensions, JavaClassParent context) {
        return new Type(typeDef, dimensions, context);
    }

    public static Type createUnresolved(TypeDef typeDef, JavaClassParent context) 
    {
        Type result;
    	if(typeDef instanceof WildcardTypeDef) 
    	{
    		result = new WildcardType((WildcardTypeDef) typeDef, context);
    	}
    	else 
    	{
    	    result = new Type(typeDef, 0, context);
    	}
        return result;
    }

}
