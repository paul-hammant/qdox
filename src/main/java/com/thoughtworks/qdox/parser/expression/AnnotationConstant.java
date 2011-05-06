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

import java.io.Serializable;

import com.thoughtworks.qdox.builder.AnnotationTransformer;

public class AnnotationConstant implements ElemValueDef, Serializable {

    private final Object value;
    private final String image;

    public AnnotationConstant( Object value, String image ) {
        this.value = value;
        this.image = image;
    }

    public Object getValue() {
        return value;
    }

    public String getImage() {
        return image;
    }

    public String toString() {
        return image;
    }

    public <U> U transform(AnnotationTransformer<U> transformer) {
    	return transformer.transform(this);
    }
}
