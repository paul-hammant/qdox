package com.thoughtworks.qdox.model.annotation;

public class AnnotationOr extends AnnotationBinaryOperator {

    public AnnotationOr( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " | " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationOr( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " | " + getRight().getParameterValue();
    }

}
