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
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.And;
import com.thoughtworks.qdox.model.expression.Cast;
import com.thoughtworks.qdox.model.expression.Constant;
import com.thoughtworks.qdox.model.expression.Divide;
import com.thoughtworks.qdox.model.expression.Equals;
import com.thoughtworks.qdox.model.expression.ExclusiveOr;
import com.thoughtworks.qdox.model.expression.FieldRef;
import com.thoughtworks.qdox.model.expression.GreaterEquals;
import com.thoughtworks.qdox.model.expression.GreaterThan;
import com.thoughtworks.qdox.model.expression.LessEquals;
import com.thoughtworks.qdox.model.expression.LessThan;
import com.thoughtworks.qdox.model.expression.LogicalAnd;
import com.thoughtworks.qdox.model.expression.LogicalNot;
import com.thoughtworks.qdox.model.expression.LogicalOr;
import com.thoughtworks.qdox.model.expression.MinusSign;
import com.thoughtworks.qdox.model.expression.Multiply;
import com.thoughtworks.qdox.model.expression.Not;
import com.thoughtworks.qdox.model.expression.NotEquals;
import com.thoughtworks.qdox.model.expression.Or;
import com.thoughtworks.qdox.model.expression.ParenExpression;
import com.thoughtworks.qdox.model.expression.PlusSign;
import com.thoughtworks.qdox.model.expression.Query;
import com.thoughtworks.qdox.model.expression.Remainder;
import com.thoughtworks.qdox.model.expression.ShiftLeft;
import com.thoughtworks.qdox.model.expression.ShiftRight;
import com.thoughtworks.qdox.model.expression.Subtract;
import com.thoughtworks.qdox.model.expression.TypeRef;
import com.thoughtworks.qdox.model.expression.UnsignedShiftRight;
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
    	Annotation annotation = new Annotation(createType(annoDef.typeDef, 0), annoDef.lineNumber);
    	for(Map.Entry<String, ElemValueDef> annoVal : annoDef.args.entrySet()) {
    		annotation.setProperty(annoVal.getKey(), annoVal.getValue().transform(this));
    	}
    	annotation.setContext(parent);
    	return annotation;
	}

    private Type createType(TypeDef typeDef, int dimensions) {
    	if(typeDef == null) {
    		return null;
    	}
    	return Type.createUnresolved(typeDef, dimensions, parent.getParentClass() != null ? parent.getParentClass() : parent.getSource());
    }
    
    public AnnotationValue transform(ElemValueListDef elemValueListDef) {
    	List<AnnotationValue> parsedList = new LinkedList<AnnotationValue>();
		for(ElemValueDef val : elemValueListDef.valueList) {
			parsedList.add(val.transform(this));
		}
		return new AnnotationValueList(parsedList);
    }
    
	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationAdd)
	 */
	public AnnotationValue transform(AnnotationAdd annotationAdd) {
		AnnotationValue left = annotationAdd.lhs.transform(this);
		AnnotationValue right = annotationAdd.rhs.transform(this);
		return new Add(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationAnd)
	 */
	public AnnotationValue transform(AnnotationAnd annotationAnd) {
		AnnotationValue left = annotationAnd.lhs.transform(this);
		AnnotationValue right = annotationAnd.rhs.transform(this);
		return new And(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationDivide)
	 */
	public AnnotationValue transform(AnnotationDivide annotationDivide) {
		AnnotationValue left = annotationDivide.lhs.transform(this);
		AnnotationValue right = annotationDivide.rhs.transform(this);
		return new Divide(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationEquals)
	 */
	public AnnotationValue transform(AnnotationEquals annotationEquals) {
		AnnotationValue left = annotationEquals.lhs.transform(this);
		AnnotationValue right = annotationEquals.rhs.transform(this);
		return new Equals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationExclusiveOr)
	 */
	public AnnotationValue transform(AnnotationExclusiveOr annotationExclusiveOr) {
		AnnotationValue left = annotationExclusiveOr.lhs.transform(this);
		AnnotationValue right = annotationExclusiveOr.rhs.transform(this);
		return new ExclusiveOr(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationGreaterEquals)
	 */
	public AnnotationValue transform(AnnotationGreaterEquals annotationGreaterEquals) {
		AnnotationValue left = annotationGreaterEquals.lhs.transform(this);
		AnnotationValue right = annotationGreaterEquals.rhs.transform(this);
		return new GreaterEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationGreaterThan)
	 */
	public AnnotationValue transform(AnnotationGreaterThan annotationGreaterThan) {
		AnnotationValue left = annotationGreaterThan.lhs.transform(this);
		AnnotationValue right = annotationGreaterThan.rhs.transform(this);
		return new GreaterThan(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLessEquals)
	 */
	public AnnotationValue transform(AnnotationLessEquals annotationLessEquals) {
		AnnotationValue left = annotationLessEquals.lhs.transform(this);
		AnnotationValue right = annotationLessEquals.rhs.transform(this);
		return new LessEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLessThan)
	 */
	public AnnotationValue transform(AnnotationLessThan annotationLessThan) {
		AnnotationValue left = annotationLessThan.lhs.transform(this);
		AnnotationValue right = annotationLessThan.rhs.transform(this);
		return new LessThan(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLogicalAnd)
	 */
	public AnnotationValue transform(AnnotationLogicalAnd annotationLogicalAnd) {
		AnnotationValue left = annotationLogicalAnd.lhs.transform(this);
		AnnotationValue right = annotationLogicalAnd.rhs.transform(this);
		return new LogicalAnd(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLogicalOr)
	 */
	public AnnotationValue transform(AnnotationLogicalOr annotationLogicalOr) {
		AnnotationValue left = annotationLogicalOr.lhs.transform(this);
		AnnotationValue right = annotationLogicalOr.rhs.transform(this);
		return new LogicalOr(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationMultiply)
	 */
	public AnnotationValue transform(AnnotationMultiply annotationMultiply) {
		AnnotationValue left = annotationMultiply.lhs.transform(this);
		AnnotationValue right = annotationMultiply.rhs.transform(this);
		return new Multiply(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationNotEquals)
	 */
	public AnnotationValue transform(AnnotationNotEquals annotationNotEquals) {
		AnnotationValue left = annotationNotEquals.lhs.transform(this);
		AnnotationValue right = annotationNotEquals.rhs.transform(this);
		return new NotEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationOr)
	 */
	public AnnotationValue transform(AnnotationOr annotationOr) {
		AnnotationValue left = annotationOr.lhs.transform(this);
		AnnotationValue right = annotationOr.rhs.transform(this);
		return new Or(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationRemainder)
	 */
	public AnnotationValue transform(AnnotationRemainder annotationRemainder) {
		AnnotationValue left = annotationRemainder.lhs.transform(this);
		AnnotationValue right = annotationRemainder.rhs.transform(this);
		return new Remainder(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationShiftLeft)
	 */
	public AnnotationValue transform(AnnotationShiftLeft annotationShiftLeft) {
		AnnotationValue left = annotationShiftLeft.lhs.transform(this);
		AnnotationValue right = annotationShiftLeft.rhs.transform(this);
		return new ShiftLeft(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationShiftRight)
	 */
	public AnnotationValue transform(AnnotationShiftRight annotationShiftRight) {
		AnnotationValue left = annotationShiftRight.lhs.transform(this);
		AnnotationValue right = annotationShiftRight.rhs.transform(this);
		return new ShiftRight(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationSubtract)
	 */
	public AnnotationValue transform(AnnotationSubtract annotationSubtract) {
		AnnotationValue left = annotationSubtract.lhs.transform(this);
		AnnotationValue right = annotationSubtract.rhs.transform(this);
		return new Subtract(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationUnsignedShiftRight)
	 */
	public AnnotationValue transform(AnnotationUnsignedShiftRight annotationUnsignedShiftRight) {
		AnnotationValue left = annotationUnsignedShiftRight.lhs.transform(this);
		AnnotationValue right = annotationUnsignedShiftRight.rhs.transform(this);
		return new UnsignedShiftRight(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationCast)
	 */
	public AnnotationValue transform(AnnotationCast annotationCast) {
		Type type = createType(annotationCast.typeDef, annotationCast.typeDef.dimensions);
		AnnotationValue value = annotationCast.elemDef.transform(this);
		return new Cast(type, value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationConstant)
	 */
	public AnnotationValue transform(AnnotationConstant annotationConstant) {
		Object value = annotationConstant.getValue();
		String image = annotationConstant.getImage();
		return new Constant(value, image);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationFieldRef)
	 */
	public AnnotationValue transform(AnnotationFieldRef annotationFieldRef) {
		FieldRef result;
		String name = annotationFieldRef.getName();
		result = new FieldRef(name);
		result.setContext(parent);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLogicalNot)
	 */
	public AnnotationValue transform(AnnotationLogicalNot annotationLogicalNot) {
		AnnotationValue value = annotationLogicalNot.elemValueDef.transform(this);
		return new LogicalNot(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationMinusSign)
	 */
	public AnnotationValue transform(AnnotationMinusSign annotationMinusSign) {
		AnnotationValue value = annotationMinusSign.elemValueDef.transform(this);
		return new MinusSign(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationNot)
	 */
	public AnnotationValue transform(AnnotationNot annotationNot) {
		AnnotationValue value = annotationNot.elemValueDef.transform(this);
		return new Not(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationParenExpression)
	 */
	public AnnotationValue transform(AnnotationParenExpression annotationParenExpression) {
		AnnotationValue value = annotationParenExpression.elemValueDef.transform(this);
		return new ParenExpression(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationPlusSign)
	 */
	public AnnotationValue transform(AnnotationPlusSign annotationPlusSign) {
		AnnotationValue value = annotationPlusSign.elemValueDef.transform(this);
		return new PlusSign(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationQuery)
	 */
	public AnnotationValue transform(AnnotationQuery annotationQuery) {
		AnnotationValue condition = annotationQuery.cond.transform(this);
		AnnotationValue trueExpression = annotationQuery.trueExpr.transform(this);
		AnnotationValue falseExpression = annotationQuery.falseExpr.transform(this);
		return new Query(condition, trueExpression, falseExpression);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationTypeRef)
	 */
	public AnnotationValue transform(AnnotationTypeRef annotationTypeRef) {
		Type type = createType(annotationTypeRef.typeDef, annotationTypeRef.typeDef.dimensions);
		return new TypeRef(type);
	}

}
