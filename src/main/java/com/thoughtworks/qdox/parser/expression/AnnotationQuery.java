package com.thoughtworks.qdox.parser.expression;

import com.thoughtworks.qdox.builder.AnnotationTransformer;

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

public class AnnotationQuery implements AnnotationValue, ElemValueDef {

    private final AnnotationValue condition;

    private final AnnotationValue trueExpression;

    private final AnnotationValue falseExpression;

	public ElemValueDef cond;
	public ElemValueDef trueExpr;
	public ElemValueDef falseExpr;

    public AnnotationQuery( AnnotationValue condition, AnnotationValue trueExpression, AnnotationValue falseExpression ) {
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    public AnnotationQuery(ElemValueDef cond, ElemValueDef trueExpr,
			ElemValueDef falseExpr) {
    	condition = trueExpression = falseExpression = null;
    	this.cond = cond;
    	this.trueExpr = trueExpr;
    	this.falseExpr = falseExpr;
	}

	public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationQuery( this );
    }

    public AnnotationValue getCondition() {
        return this.condition;
    }

    public AnnotationValue getTrueExpression() {
        return this.trueExpression;
    }

    public AnnotationValue getFalseExpression() {
        return this.falseExpression;
    }

    public Object getParameterValue() {
        return condition.getParameterValue().toString() + " ? " + trueExpression.getParameterValue() + " : "
            + falseExpression.getParameterValue();
    }

    public String toString() {
        return condition.toString() + " ? " + trueExpression.toString() + " : " + falseExpression.toString();
    }
    
    public <U> U transform(AnnotationTransformer<U> transformer) {
    	return transformer.transform(this);
    }
}
