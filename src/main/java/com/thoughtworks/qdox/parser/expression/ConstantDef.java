package com.thoughtworks.qdox.parser.expression;

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


public class ConstantDef extends ExpressionDef {

    private final String value;
    private Class<?> type;

    public ConstantDef( String value, Class<?> type ) {
        this.value = value;
        this.type = type;
    }

    public Class<?> getType()
    {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
	public String toString() {
        return value;
    }

    /** {@inheritDoc} */
    public <U> U transform(ElemValueTransformer<U> transformer) {
    	return transformer.transform(this);
    }
}
