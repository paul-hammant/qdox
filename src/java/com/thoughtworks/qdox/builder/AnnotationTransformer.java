package com.thoughtworks.qdox.builder;

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
import com.thoughtworks.qdox.parser.expression.ElemValueListDef;
import com.thoughtworks.qdox.parser.structs.AnnoDef;

public interface AnnotationTransformer<U> {

	public abstract U transform(AnnoDef annoDef);

	public abstract U transform(AnnotationAdd annotationAdd);

	public abstract U transform(AnnotationAnd annotationAnd);

	public abstract U transform(AnnotationDivide annotationDivide);

	public abstract U transform(AnnotationEquals annotationEquals);

	public abstract U transform(
			AnnotationExclusiveOr annotationExclusiveOr);

	public abstract U transform(
			AnnotationGreaterEquals annotationGreaterEquals);

	public abstract U transform(
			AnnotationGreaterThan annotationGreaterThan);

	public abstract U transform(
			AnnotationLessEquals annotationLessEquals);

	public abstract U transform(
			AnnotationLessThan annotationLessThan);

	public abstract U transform(
			AnnotationLogicalAnd annotationLogicalAnd);

	public abstract U transform(
			AnnotationLogicalOr annotationLogicalOr);

	public abstract U transform(
			AnnotationMultiply annotationMultiply);

	public abstract U transform(
			AnnotationNotEquals annotationNotEquals);

	public abstract U transform(AnnotationOr annotationOr);

	public abstract U transform(
			AnnotationRemainder annotationRemainder);

	public abstract U transform(
			AnnotationShiftLeft annotationShiftLeft);

	public abstract U transform(
			AnnotationShiftRight annotationShiftRight);

	public abstract U transform(
			AnnotationSubtract annotationSubtract);

	public abstract U transform(
			AnnotationUnsignedShiftRight annotationUnsignedShiftRight);

	public abstract U transform(AnnotationCast annotationCast);

	public abstract U transform(
			AnnotationConstant annotationConstant);

	public abstract U transform(
			AnnotationFieldRef annotationFieldRef);

	public abstract U transform(
			AnnotationLogicalNot annotationLogicalNot);

	public abstract U transform(
			AnnotationMinusSign annotationMinusSign);

	public abstract U transform(AnnotationNot annotationNot);

	public abstract U transform(
			AnnotationParenExpression annotationParenExpression);

	public abstract U transform(
			AnnotationPlusSign annotationPlusSign);

	public abstract U transform(AnnotationQuery annotationQuery);

	public abstract U transform(
			AnnotationTypeRef annotationTypeRef);

	public abstract U transform(ElemValueListDef elemValueListDef);

}