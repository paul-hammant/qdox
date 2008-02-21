package com.thoughtworks.qdox.model.annotation;

public class AnnotationDivide extends AnnotationBinaryOperator {

    public AnnotationDivide( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " / " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationDivide( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " / " + getRight().getParameterValue();
    }

}
