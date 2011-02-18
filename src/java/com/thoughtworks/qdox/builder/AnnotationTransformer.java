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
import java.util.Map;

import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.parser.expression.AnnotationFieldRef;
import com.thoughtworks.qdox.parser.expression.AnnotationValue;
import com.thoughtworks.qdox.parser.expression.AnnotationValueList;
import com.thoughtworks.qdox.parser.expression.AnnotationVisitor;
import com.thoughtworks.qdox.parser.expression.RecursiveAnnotationVisitor;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

public class AnnotationTransformer extends RecursiveAnnotationVisitor implements AnnotationVisitor {

	private AbstractBaseJavaEntity parent;
	
	public AnnotationTransformer(AbstractBaseJavaEntity parent) {
		this.parent = parent;
	}

	public Annotation transform(AnnoDef annoDef) {
		Annotation result = createAnnotation(annoDef);
		visitAnnotation(result);
		return result;
	}

    private Annotation createAnnotation(AnnoDef annoDef) {
    	Annotation annotation = new Annotation(createType(annoDef.typeDef, 0), annoDef.lineNumber);
    	for(Map.Entry<String, AnnotationValue> annoVal : annoDef.args.entrySet()) {
    		annotation.setProperty(annoVal.getKey(), createAnnotation(annoVal.getValue()));
    	}
    	return annotation;
    }
    
    private AnnotationValue createAnnotation(AnnotationValue oldValue) {
		AnnotationValue newValue;
		if(oldValue instanceof AnnoDef) {
			newValue = createAnnotation((AnnoDef) oldValue);
		}
		else if(oldValue instanceof AnnotationValueList) {
			AnnotationValueList annoValList = (AnnotationValueList) oldValue;
			List<AnnotationValue> parsedList = new LinkedList<AnnotationValue>();
			for(AnnotationValue val : annoValList.getValueList()) {
				parsedList.add(createAnnotation(val));
			}
			newValue = new AnnotationValueList(parsedList);
		}
		else {
			newValue = oldValue;
		}
    	return newValue;
    }

    public Type createType(TypeDef typeDef, int dimensions) {
    	if(typeDef == null) {
    		return null;
    	}
    	return Type.createUnresolved(typeDef, dimensions, parent.getParentClass() != null ? parent.getParentClass() : parent.getSource());
    }

    public Object visitAnnotation( Annotation annotation ) {
        annotation.setContext( parent );
        return super.visitAnnotation( annotation );
    }
    
    public Object visitAnnotationFieldRef( AnnotationFieldRef fieldRef ) {
        fieldRef.setContext( parent );
        return super.visitAnnotationFieldRef( fieldRef );
    }

}
