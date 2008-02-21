package com.thoughtworks.qdox.model.annotation;

public class AnnotationEquals extends AnnotationBinaryOperator {

    public AnnotationEquals( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " == " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationEquals( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " == " + getRight().getParameterValue();
    }

}
