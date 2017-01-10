package com.thoughtworks.qdox.parser.structs;

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

import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.qdox.parser.expression.ElemValueTransformer;
import com.thoughtworks.qdox.parser.expression.ElemValueDef;

public class AnnoDef extends LocatedDef implements ElemValueDef
{
    private TypeDef typeDef;
    private final Map<String, ElemValueDef> args = new LinkedHashMap<String, ElemValueDef>();

    public AnnoDef( TypeDef typeDef )
    {
        this.typeDef = typeDef;
    }
    
    public AnnoDef getValue() {
    	return this;
    }
    
    /** {@inheritDoc} */
    public <U> U transform(ElemValueTransformer<U> transformer) {
    	return transformer.transform(this);
    }

    public TypeDef getTypeDef()
    {
        return typeDef;
    }

    public Map<String, ElemValueDef> getArgs()
    {
        return args;
    }
}
