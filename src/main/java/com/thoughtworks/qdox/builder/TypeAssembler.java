package com.thoughtworks.qdox.builder;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import com.thoughtworks.qdox.model.impl.DefaultJavaWildcardType;
import com.thoughtworks.qdox.model.impl.DefaultJavaWildcardType.BoundType;
import com.thoughtworks.qdox.model.impl.DefaultJavaType;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.WildcardTypeDef;
import com.thoughtworks.qdox.type.TypeResolver;

/**
 * An assembler to transform a {@link TypeDef} to a {@link DefaultJavaType}
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
     * @param typeDef the type definition
     * @param dimensions the array-depth
     * @param typeResolver the typeResolver
     * @return the Type
     */
    public static DefaultJavaType createUnresolved( TypeDef typeDef, int dimensions, TypeResolver typeResolver)
    {
        DefaultJavaType result;
        if ( typeDef instanceof WildcardTypeDef )
        {
            WildcardTypeDef wildcard = (WildcardTypeDef) typeDef;
            if( wildcard.getTypeDef() != null )
            {
                JavaType type = createUnresolved( wildcard.getTypeDef(), typeResolver );
                DefaultJavaWildcardType.BoundType boundType = null;
                if( "extends".equals( wildcard.getWildcardExpressionType() ) )
                {
                    boundType = BoundType.EXTENDS;
                }
                else if( "super".equals( wildcard.getWildcardExpressionType() ) )
                {
                    boundType = BoundType.SUPER;
                }
                result = new DefaultJavaWildcardType( type , boundType );
            }
            else
            {
                result = new DefaultJavaWildcardType();
            }
        }
        else
        {
            DefaultJavaParameterizedType typeResult = new DefaultJavaParameterizedType( null, typeDef.getName(), typeDef.getDimensions() + dimensions, typeResolver );
            
            if ( typeDef.getActualArgumentTypes() != null && !typeDef.getActualArgumentTypes().isEmpty() )
            {
                List<JavaType> actualArgumentTypes = new LinkedList<JavaType>();
                for ( TypeDef actualArgType : typeDef.getActualArgumentTypes() )
                {
                    actualArgumentTypes.add( TypeAssembler.createUnresolved( actualArgType, typeResolver ) );
                }
                typeResult.setActualArgumentTypes( actualArgumentTypes );
            }
            result = typeResult;
        }
        return result;
    }

    /**
     * @param typeDef the type definition
     * @param typeResolver the typeResolver
     * @return the Type
     */
    public static DefaultJavaType createUnresolved( TypeDef typeDef, TypeResolver typeResolver )
    {
        return createUnresolved( typeDef, 0, typeResolver );
    }

}
