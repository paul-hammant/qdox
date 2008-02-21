package com.thoughtworks.qdox.model.annotation;

public class AnnotationAdd extends AnnotationBinaryOperator {

    public AnnotationAdd( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " + " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationAdd( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " + " + getRight().getParameterValue();
    }

}
