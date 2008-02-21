package com.thoughtworks.qdox.model.annotation;

public class AnnotationNotEquals extends AnnotationBinaryOperator {

    public AnnotationNotEquals( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " != " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationNotEquals( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " != " + getRight().getParameterValue();
    }

}
