package com.thoughtworks.qdox.model.annotation;

public class AnnotationLessThan extends AnnotationBinaryOperator {

    public AnnotationLessThan( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " < " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationLessThan( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " < " + getRight().getParameterValue();
    }

}
