package com.thoughtworks.qdox.model.annotation;

public class AnnotationMultiply extends AnnotationBinaryOperator {

    public AnnotationMultiply( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " * " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationMultiply( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " * " + getRight().getParameterValue();
    }

}
