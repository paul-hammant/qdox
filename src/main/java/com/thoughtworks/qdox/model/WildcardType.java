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

import com.thoughtworks.qdox.parser.structs.WildcardTypeDef;

/**
 * This class supports both the 'super' and 'extends' wildcards.
 * For &lt;?&gt; you must use the normal Type, because ? itself can't be generic 
 * 
 * @author Robert Scholte
 *
 */
public class WildcardType extends Type {

    /**
     * A wildcardExpression is either <code>super</code> or <code>extends</code> or <code>null</code>
     */
	private String wildcardExpressionType = null;
	
	public WildcardType() {
		super("?");
	}
	
	public WildcardType(String name, String wildcardExpressionType, JavaClassParent context) {
		super(null, name, 0, context);
		this.wildcardExpressionType = wildcardExpressionType;
	}

	public String getGenericValue() {
		String result = "";
		if( wildcardExpressionType != null ) 
		{
			result += "? " + wildcardExpressionType+ " ";
		}
		result += super.getGenericValue();
		return result;
	}
}
