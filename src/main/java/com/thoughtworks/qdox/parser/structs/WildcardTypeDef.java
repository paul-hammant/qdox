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

/**
 * WildcardTypeDef must be a subclass of TypeDef, so it can be used in other classes.
 * But here are only 2 fields interesting: typeDef and wildcardExpressionType
 * typeDef itself can be generic, so it must be resolvable
 * wildcardExpressionType is super or extends
 * 
 * 
 * @author Robert Scholte
 *
 */
public class WildcardTypeDef extends TypeDef {

	private TypeDef typeDef;
	private String wildcardExpressionType; //super or extends
	
	public WildcardTypeDef() {
		super("?");
	}
	
	public WildcardTypeDef(TypeDef typeDef, String wildcardExpressionType) {
		super( typeDef.getName(), typeDef.getDimensions() );
		this.typeDef = typeDef;
		this.wildcardExpressionType = wildcardExpressionType;
	}
	
	public TypeDef getTypeDef() {
		return typeDef;
	}
	
	public String getWildcardExpressionType() {
		return wildcardExpressionType;
	}
}
