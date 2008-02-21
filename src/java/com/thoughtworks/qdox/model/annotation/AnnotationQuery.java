package com.thoughtworks.qdox.model.annotation;

public class AnnotationQuery implements AnnotationValue {

    private final AnnotationValue condition;

    private final AnnotationValue trueExpression;

    private final AnnotationValue falseExpression;

    public AnnotationQuery( AnnotationValue condition, AnnotationValue trueExpression, AnnotationValue falseExpression ) {
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
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
}
