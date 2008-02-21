package com.thoughtworks.qdox.model.annotation;

public class AnnotationLessEquals extends AnnotationBinaryOperator {

    public AnnotationLessEquals( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " <= " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationLessEquals( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " <= " + getRight().getParameterValue();
    }

}
