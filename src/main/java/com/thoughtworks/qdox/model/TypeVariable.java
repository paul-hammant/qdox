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

import java.util.Iterator;
import java.util.List;
/**
 * 
 * 
 * @author Robert Scholte
 * @since 1.10
 */
public class TypeVariable extends Type {

	private List<Type> bounds;

    public TypeVariable( String fullName, String name, JavaClassParent context ) {
        super( fullName, name, 0, context );
    }
    
	/**
     * @return the bounds
     */
    public List<Type> getBounds()
    {
        return bounds;
    }

    /**
     * @param bounds the bounds to set
     */
    public void setBounds( List<Type> bounds )
    {
        this.bounds = bounds;
    }

	public String getValue() {
		return ( bounds == null || bounds.isEmpty() ? ""  : bounds.get(0).getValue() );
	}
	
	public String getGenericValue() {
		StringBuffer result = new StringBuffer("<");
		result.append(super.getValue());
		if(bounds != null && !bounds.isEmpty()) {
			result.append(" extends ");
			for(Iterator<Type> iter = bounds.iterator(); iter.hasNext();) {
				result.append(iter.next().getGenericValue());
                if(iter.hasNext()) {
                    result.append(",");
                }
			}
		}
		result.append(">");
		return result.toString();
	}
	
	public String getName() {
		return super.getValue();
	}

}
