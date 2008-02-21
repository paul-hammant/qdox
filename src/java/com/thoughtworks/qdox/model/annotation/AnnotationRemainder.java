package com.thoughtworks.qdox.model.annotation;

public class AnnotationRemainder extends AnnotationBinaryOperator {

    public AnnotationRemainder( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " * " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationRemainder( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " * " + getRight().getParameterValue();
    }

}
