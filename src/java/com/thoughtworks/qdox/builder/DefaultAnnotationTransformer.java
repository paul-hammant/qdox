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
import com.thoughtworks.qdox.parser.expression.AnnotationAdd;
import com.thoughtworks.qdox.parser.expression.AnnotationAnd;
import com.thoughtworks.qdox.parser.expression.AnnotationCast;
import com.thoughtworks.qdox.parser.expression.AnnotationConstant;
import com.thoughtworks.qdox.parser.expression.AnnotationDivide;
import com.thoughtworks.qdox.parser.expression.AnnotationEquals;
import com.thoughtworks.qdox.parser.expression.AnnotationExclusiveOr;
import com.thoughtworks.qdox.parser.expression.AnnotationFieldRef;
import com.thoughtworks.qdox.parser.expression.AnnotationGreaterEquals;
import com.thoughtworks.qdox.parser.expression.AnnotationGreaterThan;
import com.thoughtworks.qdox.parser.expression.AnnotationLessEquals;
import com.thoughtworks.qdox.parser.expression.AnnotationLessThan;
import com.thoughtworks.qdox.parser.expression.AnnotationLogicalAnd;
import com.thoughtworks.qdox.parser.expression.AnnotationLogicalNot;
import com.thoughtworks.qdox.parser.expression.AnnotationLogicalOr;
import com.thoughtworks.qdox.parser.expression.AnnotationMinusSign;
import com.thoughtworks.qdox.parser.expression.AnnotationMultiply;
import com.thoughtworks.qdox.parser.expression.AnnotationNot;
import com.thoughtworks.qdox.parser.expression.AnnotationNotEquals;
import com.thoughtworks.qdox.parser.expression.AnnotationOr;
import com.thoughtworks.qdox.parser.expression.AnnotationParenExpression;
import com.thoughtworks.qdox.parser.expression.AnnotationPlusSign;
import com.thoughtworks.qdox.parser.expression.AnnotationQuery;
import com.thoughtworks.qdox.parser.expression.AnnotationRemainder;
import com.thoughtworks.qdox.parser.expression.AnnotationShiftLeft;
import com.thoughtworks.qdox.parser.expression.AnnotationShiftRight;
import com.thoughtworks.qdox.parser.expression.AnnotationSubtract;
import com.thoughtworks.qdox.parser.expression.AnnotationTypeRef;
import com.thoughtworks.qdox.parser.expression.AnnotationUnsignedShiftRight;
import com.thoughtworks.qdox.parser.expression.AnnotationValue;
import com.thoughtworks.qdox.parser.expression.AnnotationValueList;
import com.thoughtworks.qdox.parser.expression.ElemValueDef;
import com.thoughtworks.qdox.parser.expression.ElemValueListDef;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

public class DefaultAnnotationTransformer implements AnnotationTransformer<AnnotationValue> {

	private AbstractBaseJavaEntity parent;
	
	public DefaultAnnotationTransformer(AbstractBaseJavaEntity parent) {
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.structs.AnnoDef)
	 */
	public Annotation transform(AnnoDef annoDef) {
		return  createAnnotation(annoDef);
	}

    private Annotation createAnnotation(AnnoDef annoDef) {
    	Annotation annotation = new Annotation(createType(annoDef.typeDef, 0), annoDef.lineNumber);
    	for(Map.Entry<String, ElemValueDef> annoVal : annoDef.args.entrySet()) {
    		annotation.setProperty(annoVal.getKey(), createAnnotation(annoVal.getValue()));
    	}
    	annotation.setContext(parent);
    	return annotation;
    }
    
    private AnnotationValue createAnnotation(ElemValueDef oldValue) {
		AnnotationValue newValue;
		if(oldValue instanceof AnnoDef) {
			newValue = createAnnotation((AnnoDef) oldValue);
		}
		else if(oldValue instanceof ElemValueListDef) {
			ElemValueListDef annoValList = (ElemValueListDef) oldValue;
			List<AnnotationValue> parsedList = new LinkedList<AnnotationValue>();
			for(ElemValueDef val : annoValList.valueList) {
				parsedList.add(createAnnotation(val));
			}
			newValue = new AnnotationValueList(parsedList);
		}
		else {
			newValue = oldValue.transform(this);
		}
    	return newValue;
    }

    public Type createType(TypeDef typeDef, int dimensions) {
    	if(typeDef == null) {
    		return null;
    	}
    	return Type.createUnresolved(typeDef, dimensions, parent.getParentClass() != null ? parent.getParentClass() : parent.getSource());
    }
    
    public AnnotationValue transform(ElemValueListDef elemValueListDef) {
    	List<AnnotationValue> parsedList = new LinkedList<AnnotationValue>();
		for(ElemValueDef val : elemValueListDef.valueList) {
			parsedList.add(createAnnotation(val));
		}
		return new AnnotationValueList(parsedList);
    }
    
	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationAdd)
	 */
	public AnnotationValue transform(AnnotationAdd annotationAdd) {
		AnnotationValue left = annotationAdd.lhs.transform(this);
		AnnotationValue right = annotationAdd.rhs.transform(this);
		return new AnnotationAdd(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationAnd)
	 */
	public AnnotationValue transform(AnnotationAnd annotationAnd) {
		AnnotationValue left = annotationAnd.lhs.transform(this);
		AnnotationValue right = annotationAnd.rhs.transform(this);
		return new AnnotationAnd(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationDivide)
	 */
	public AnnotationValue transform(AnnotationDivide annotationDivide) {
		AnnotationValue left = annotationDivide.lhs.transform(this);
		AnnotationValue right = annotationDivide.rhs.transform(this);
		return new AnnotationDivide(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationEquals)
	 */
	public AnnotationValue transform(AnnotationEquals annotationEquals) {
		AnnotationValue left = annotationEquals.lhs.transform(this);
		AnnotationValue right = annotationEquals.rhs.transform(this);
		return new AnnotationEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationExclusiveOr)
	 */
	public AnnotationValue transform(AnnotationExclusiveOr annotationExclusiveOr) {
		AnnotationValue left = annotationExclusiveOr.lhs.transform(this);
		AnnotationValue right = annotationExclusiveOr.rhs.transform(this);
		return new AnnotationExclusiveOr(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationGreaterEquals)
	 */
	public AnnotationValue transform(AnnotationGreaterEquals annotationGreaterEquals) {
		AnnotationValue left = annotationGreaterEquals.lhs.transform(this);
		AnnotationValue right = annotationGreaterEquals.rhs.transform(this);
		return new AnnotationGreaterEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationGreaterThan)
	 */
	public AnnotationValue transform(AnnotationGreaterThan annotationGreaterThan) {
		AnnotationValue left = annotationGreaterThan.lhs.transform(this);
		AnnotationValue right = annotationGreaterThan.rhs.transform(this);
		return new AnnotationGreaterThan(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLessEquals)
	 */
	public AnnotationValue transform(AnnotationLessEquals annotationLessEquals) {
		AnnotationValue left = annotationLessEquals.lhs.transform(this);
		AnnotationValue right = annotationLessEquals.rhs.transform(this);
		return new AnnotationLessEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLessThan)
	 */
	public AnnotationValue transform(AnnotationLessThan annotationLessThan) {
		AnnotationValue left = annotationLessThan.lhs.transform(this);
		AnnotationValue right = annotationLessThan.rhs.transform(this);
		return new AnnotationLessThan(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLogicalAnd)
	 */
	public AnnotationValue transform(AnnotationLogicalAnd annotationLogicalAnd) {
		AnnotationValue left = annotationLogicalAnd.lhs.transform(this);
		AnnotationValue right = annotationLogicalAnd.rhs.transform(this);
		return new AnnotationLogicalAnd(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLogicalOr)
	 */
	public AnnotationValue transform(AnnotationLogicalOr annotationLogicalOr) {
		AnnotationValue left = annotationLogicalOr.lhs.transform(this);
		AnnotationValue right = annotationLogicalOr.rhs.transform(this);
		return new AnnotationLogicalOr(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationMultiply)
	 */
	public AnnotationValue transform(AnnotationMultiply annotationMultiply) {
		AnnotationValue left = annotationMultiply.lhs.transform(this);
		AnnotationValue right = annotationMultiply.rhs.transform(this);
		return new AnnotationMultiply(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationNotEquals)
	 */
	public AnnotationValue transform(AnnotationNotEquals annotationNotEquals) {
		AnnotationValue left = annotationNotEquals.lhs.transform(this);
		AnnotationValue right = annotationNotEquals.rhs.transform(this);
		return new AnnotationNotEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationOr)
	 */
	public AnnotationValue transform(AnnotationOr annotationOr) {
		AnnotationValue left = annotationOr.lhs.transform(this);
		AnnotationValue right = annotationOr.rhs.transform(this);
		return new AnnotationOr(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationRemainder)
	 */
	public AnnotationValue transform(AnnotationRemainder annotationRemainder) {
		AnnotationValue left = annotationRemainder.lhs.transform(this);
		AnnotationValue right = annotationRemainder.rhs.transform(this);
		return new AnnotationRemainder(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationShiftLeft)
	 */
	public AnnotationValue transform(AnnotationShiftLeft annotationShiftLeft) {
		AnnotationValue left = annotationShiftLeft.lhs.transform(this);
		AnnotationValue right = annotationShiftLeft.rhs.transform(this);
		return new AnnotationShiftLeft(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationShiftRight)
	 */
	public AnnotationValue transform(AnnotationShiftRight annotationShiftRight) {
		AnnotationValue left = annotationShiftRight.lhs.transform(this);
		AnnotationValue right = annotationShiftRight.rhs.transform(this);
		return new AnnotationShiftRight(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationSubtract)
	 */
	public AnnotationValue transform(AnnotationSubtract annotationSubtract) {
		AnnotationValue left = annotationSubtract.lhs.transform(this);
		AnnotationValue right = annotationSubtract.rhs.transform(this);
		return new AnnotationSubtract(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationUnsignedShiftRight)
	 */
	public AnnotationValue transform(AnnotationUnsignedShiftRight annotationUnsignedShiftRight) {
		AnnotationValue left = annotationUnsignedShiftRight.lhs.transform(this);
		AnnotationValue right = annotationUnsignedShiftRight.rhs.transform(this);
		return new AnnotationUnsignedShiftRight(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationCast)
	 */
	public AnnotationValue transform(AnnotationCast annotationCast) {
		Type type = createType(annotationCast.typeDef, annotationCast.typeDef.dimensions);
		AnnotationValue value = annotationCast.elemDef.transform(this);
		return new AnnotationCast(type, value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationConstant)
	 */
	public AnnotationValue transform(AnnotationConstant annotationConstant) {
		Object value = annotationConstant.getValue();
		String image = annotationConstant.getImage();
		return new AnnotationConstant(value, image);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationFieldRef)
	 */
	public AnnotationValue transform(AnnotationFieldRef annotationFieldRef) {
		AnnotationFieldRef result;
		String name = annotationFieldRef.getName();
		result = new AnnotationFieldRef(name);
		result.setContext(parent);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLogicalNot)
	 */
	public AnnotationValue transform(AnnotationLogicalNot annotationLogicalNot) {
		AnnotationValue value = annotationLogicalNot.elemValueDef.transform(this);
		return new AnnotationLogicalNot(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationMinusSign)
	 */
	public AnnotationValue transform(AnnotationMinusSign annotationMinusSign) {
		AnnotationValue value = annotationMinusSign.elemValueDef.transform(this);
		return new AnnotationMinusSign(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationNot)
	 */
	public AnnotationValue transform(AnnotationNot annotationNot) {
		AnnotationValue value = annotationNot.elemValueDef.transform(this);
		return new AnnotationNot(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationParenExpression)
	 */
	public AnnotationValue transform(AnnotationParenExpression annotationParenExpression) {
		AnnotationValue value = annotationParenExpression.elemValueDef.transform(this);
		return new AnnotationParenExpression(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationPlusSign)
	 */
	public AnnotationValue transform(AnnotationPlusSign annotationPlusSign) {
		AnnotationValue value = annotationPlusSign.elemValueDef.transform(this);
		return new AnnotationPlusSign(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationQuery)
	 */
	public AnnotationValue transform(AnnotationQuery annotationQuery) {
		AnnotationValue condition = annotationQuery.cond.transform(this);
		AnnotationValue trueExpression = annotationQuery.trueExpr.transform(this);
		AnnotationValue falseExpression = annotationQuery.falseExpr.transform(this);
		return new AnnotationQuery(condition, trueExpression, falseExpression);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationTypeRef)
	 */
	public AnnotationValue transform(AnnotationTypeRef annotationTypeRef) {
		Type type = createType(annotationTypeRef.typeDef, annotationTypeRef.typeDef.dimensions);
		return new AnnotationTypeRef(type);
	}

}