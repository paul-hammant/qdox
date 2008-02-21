package com.thoughtworks.qdox.model.annotation;

public class AnnotationLogicalOr extends AnnotationBinaryOperator {

    public AnnotationLogicalOr( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " || " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationLogicalOr( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " || " + getRight().getParameterValue();
    }

}
