package com.thoughtworks.qdox.model.annotation;

public class AnnotationGreaterThan extends AnnotationBinaryOperator {

    public AnnotationGreaterThan( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " > " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationGreaterThan( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " > " + getRight().getParameterValue();
    }

}
