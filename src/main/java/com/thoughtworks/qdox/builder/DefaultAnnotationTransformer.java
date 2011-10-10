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

import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.And;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
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
import com.thoughtworks.qdox.model.impl.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.impl.DefaultJavaAnnotation;
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
import com.thoughtworks.qdox.parser.expression.AnnotationTransformer;
import com.thoughtworks.qdox.parser.expression.AnnotationTypeRef;
import com.thoughtworks.qdox.parser.expression.AnnotationUnsignedShiftRight;
import com.thoughtworks.qdox.parser.expression.ElemValueDef;
import com.thoughtworks.qdox.parser.expression.ElemValueListDef;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

public class DefaultAnnotationTransformer implements AnnotationTransformer<AnnotationValue> {

	private AbstractBaseJavaEntity parent;
	
	public DefaultAnnotationTransformer(AbstractBaseJavaEntity parent) {
		this.parent = (AbstractBaseJavaEntity) parent;
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.structs.AnnoDef)
	 */
	public DefaultJavaAnnotation transform(AnnoDef annoDef) {
    	DefaultJavaAnnotation annotation = new DefaultJavaAnnotation(createType(annoDef.getTypeDef(), 0), annoDef.getLineNumber());
    	for(Map.Entry<String, ElemValueDef> annoVal : annoDef.getArgs().entrySet()) {
    		annotation.setProperty(annoVal.getKey(), annoVal.getValue().transform(this));
    	}
    	annotation.setContext((JavaAnnotatedElement) parent);
    	return annotation;
	}

    private Type createType(TypeDef typeDef, int dimensions) {
    	if(typeDef == null) {
    		return null;
    	}
    	return TypeAssembler.createUnresolved(typeDef, dimensions, parent.getParentClass() != null ? parent.getParentClass() : parent.getSource() );
    }
    
    public AnnotationValue transform(ElemValueListDef elemValueListDef) {
    	List<AnnotationValue> parsedList = new LinkedList<AnnotationValue>();
		for(ElemValueDef val : elemValueListDef.getValueList()) {
			parsedList.add(val.transform(this));
		}
		return new AnnotationValueList(parsedList);
    }
    
	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationAdd)
	 */
	public AnnotationValue transform(AnnotationAdd annotationAdd) {
		AnnotationValue left = annotationAdd.getLeft().transform(this);
		AnnotationValue right = annotationAdd.getRight().transform(this);
		return new Add(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationAnd)
	 */
	public AnnotationValue transform(AnnotationAnd annotationAnd) {
		AnnotationValue left = annotationAnd.getLeft().transform(this);
		AnnotationValue right = annotationAnd.getRight().transform(this);
		return new And(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationDivide)
	 */
	public AnnotationValue transform(AnnotationDivide annotationDivide) {
		AnnotationValue left = annotationDivide.getLeft().transform(this);
		AnnotationValue right = annotationDivide.getRight().transform(this);
		return new Divide(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationEquals)
	 */
	public AnnotationValue transform(AnnotationEquals annotationEquals) {
		AnnotationValue left = annotationEquals.getLeft().transform(this);
		AnnotationValue right = annotationEquals.getRight().transform(this);
		return new Equals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationExclusiveOr)
	 */
	public AnnotationValue transform(AnnotationExclusiveOr annotationExclusiveOr) {
		AnnotationValue left = annotationExclusiveOr.getLeft().transform(this);
		AnnotationValue right = annotationExclusiveOr.getRight().transform(this);
		return new ExclusiveOr(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationGreaterEquals)
	 */
	public AnnotationValue transform(AnnotationGreaterEquals annotationGreaterEquals) {
		AnnotationValue left = annotationGreaterEquals.getLeft().transform(this);
		AnnotationValue right = annotationGreaterEquals.getRight().transform(this);
		return new GreaterEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationGreaterThan)
	 */
	public AnnotationValue transform(AnnotationGreaterThan annotationGreaterThan) {
		AnnotationValue left = annotationGreaterThan.getLeft().transform(this);
		AnnotationValue right = annotationGreaterThan.getRight().transform(this);
		return new GreaterThan(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLessEquals)
	 */
	public AnnotationValue transform(AnnotationLessEquals annotationLessEquals) {
		AnnotationValue left = annotationLessEquals.getLeft().transform(this);
		AnnotationValue right = annotationLessEquals.getRight().transform(this);
		return new LessEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLessThan)
	 */
	public AnnotationValue transform(AnnotationLessThan annotationLessThan) {
		AnnotationValue left = annotationLessThan.getLeft().transform(this);
		AnnotationValue right = annotationLessThan.getRight().transform(this);
		return new LessThan(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLogicalAnd)
	 */
	public AnnotationValue transform(AnnotationLogicalAnd annotationLogicalAnd) {
		AnnotationValue left = annotationLogicalAnd.getLeft().transform(this);
		AnnotationValue right = annotationLogicalAnd.getRight().transform(this);
		return new LogicalAnd(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLogicalOr)
	 */
	public AnnotationValue transform(AnnotationLogicalOr annotationLogicalOr) {
		AnnotationValue left = annotationLogicalOr.getLeft().transform(this);
		AnnotationValue right = annotationLogicalOr.getRight().transform(this);
		return new LogicalOr(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationMultiply)
	 */
	public AnnotationValue transform(AnnotationMultiply annotationMultiply) {
		AnnotationValue left = annotationMultiply.getLeft().transform(this);
		AnnotationValue right = annotationMultiply.getRight().transform(this);
		return new Multiply(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationNotEquals)
	 */
	public AnnotationValue transform(AnnotationNotEquals annotationNotEquals) {
		AnnotationValue left = annotationNotEquals.getLeft().transform(this);
		AnnotationValue right = annotationNotEquals.getRight().transform(this);
		return new NotEquals(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationOr)
	 */
	public AnnotationValue transform(AnnotationOr annotationOr) {
		AnnotationValue left = annotationOr.getLeft().transform(this);
		AnnotationValue right = annotationOr.getRight().transform(this);
		return new Or(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationRemainder)
	 */
	public AnnotationValue transform(AnnotationRemainder annotationRemainder) {
		AnnotationValue left = annotationRemainder.getLeft().transform(this);
		AnnotationValue right = annotationRemainder.getRight().transform(this);
		return new Remainder(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationShiftLeft)
	 */
	public AnnotationValue transform(AnnotationShiftLeft annotationShiftLeft) {
		AnnotationValue left = annotationShiftLeft.getLeft().transform(this);
		AnnotationValue right = annotationShiftLeft.getRight().transform(this);
		return new ShiftLeft(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationShiftRight)
	 */
	public AnnotationValue transform(AnnotationShiftRight annotationShiftRight) {
		AnnotationValue left = annotationShiftRight.getLeft().transform(this);
		AnnotationValue right = annotationShiftRight.getRight().transform(this);
		return new ShiftRight(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationSubtract)
	 */
	public AnnotationValue transform(AnnotationSubtract annotationSubtract) {
		AnnotationValue left = annotationSubtract.getLeft().transform(this);
		AnnotationValue right = annotationSubtract.getRight().transform(this);
		return new Subtract(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationUnsignedShiftRight)
	 */
	public AnnotationValue transform(AnnotationUnsignedShiftRight annotationUnsignedShiftRight) {
		AnnotationValue left = annotationUnsignedShiftRight.getLeft().transform(this);
		AnnotationValue right = annotationUnsignedShiftRight.getRight().transform(this);
		return new UnsignedShiftRight(left, right);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationCast)
	 */
	public AnnotationValue transform(AnnotationCast annotationCast) {
		Type type = createType(annotationCast.getTypeDef(), annotationCast.getTypeDef().getDimensions() );
		AnnotationValue value = annotationCast.getElemDef().transform(this);
		return new Cast(type, value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationConstant)
	 */
	public AnnotationValue transform(AnnotationConstant annotationConstant) {
	    AnnotationValue result;
		String image = annotationConstant.getValue();
		Class<?> type = annotationConstant.getType();
		if( type == Integer.class )
		{
		    result = Constant.newIntegerLiteral( image );
		}
		else if ( type == String.class )
		{
		    result = Constant.newStringLiteral( image );
		}
		else if ( type == Boolean.class ) 
		{
		    result = Constant.newBooleanLiteral( image );
		}
		else if ( type == Character.class ) 
		{
		  result = Constant.newCharacterLiteral( image );    
		}
		else if ( type == Float.class ) {
		    result = Constant.newFloatingPointLiteral( image );
		}
		else {
		    result = null; //unknown??
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationFieldRef)
	 */
	public AnnotationValue transform(AnnotationFieldRef annotationFieldRef) {
		FieldRef result;
		String name = annotationFieldRef.getName();
		result = new FieldRef(name);
		result.setContext((JavaAnnotatedElement) parent);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationLogicalNot)
	 */
	public AnnotationValue transform(AnnotationLogicalNot annotationLogicalNot) {
		AnnotationValue value = annotationLogicalNot.getElemValueDef().transform(this);
		return new LogicalNot(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationMinusSign)
	 */
	public AnnotationValue transform(AnnotationMinusSign annotationMinusSign) {
		AnnotationValue value = annotationMinusSign.getElemValueDef().transform(this);
		return new MinusSign(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationNot)
	 */
	public AnnotationValue transform(AnnotationNot annotationNot) {
		AnnotationValue value = annotationNot.getElemValueDef().transform(this);
		return new Not(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationParenExpression)
	 */
	public AnnotationValue transform(AnnotationParenExpression annotationParenExpression) {
		AnnotationValue value = annotationParenExpression.getElemValueDef().transform(this);
		return new ParenExpression(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationPlusSign)
	 */
	public AnnotationValue transform(AnnotationPlusSign annotationPlusSign) {
		AnnotationValue value = annotationPlusSign.getElemValueDef().transform(this);
		return new PlusSign(value);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationQuery)
	 */
	public AnnotationValue transform(AnnotationQuery annotationQuery) {
		AnnotationValue condition = annotationQuery.getCondition().transform(this);
		AnnotationValue trueExpression = annotationQuery.getTrueExpression().transform(this);
		AnnotationValue falseExpression = annotationQuery.getFalseExpression().transform(this);
		return new Query(condition, trueExpression, falseExpression);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.qdox.builder.AnnotationTransformer#transform(com.thoughtworks.qdox.parser.expression.AnnotationTypeRef)
	 */
	public AnnotationValue transform(AnnotationTypeRef annotationTypeRef) {
		Type type = createType(annotationTypeRef.getTypeDef(), annotationTypeRef.getTypeDef().getDimensions() );
		return new TypeRef(type);
	}

}
