package com.thoughtworks.qdox.model;

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

/**
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanProperty {
    
    private final String name;
    private JavaMethod accessor;
    private JavaMethod mutator;
    private JavaClass type;

    public BeanProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(JavaClass type) {
        this.type = type;
    }

    public JavaClass getType() {
        return type;
    }

    public JavaMethod getAccessor() {
        return accessor;
    }

    public void setAccessor(JavaMethod accessor) {
        this.accessor = accessor;
    }

    public JavaMethod getMutator() {
        return mutator;
    }

    public void setMutator(JavaMethod mutator) {
        this.mutator = mutator;
    }
}
