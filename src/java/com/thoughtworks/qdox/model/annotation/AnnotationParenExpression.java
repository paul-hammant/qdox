package com.thoughtworks.qdox.model.annotation;

public class AnnotationParenExpression implements AnnotationValue {

    private AnnotationValue value;

    public AnnotationParenExpression( AnnotationValue value ) {
        this.value = value;
    }

    public AnnotationValue getValue() {
        return value;
    }

    public String toString() {
        return "(" + value.toString() + ")";
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationParenExpression( this );
    }

    public Object getParameterValue() {
        return "(" + value.getParameterValue() + ")";
    }

}
