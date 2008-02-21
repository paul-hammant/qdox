package com.thoughtworks.qdox.model.annotation;

public class AnnotationExclusiveOr extends AnnotationBinaryOperator {

    public AnnotationExclusiveOr( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " ^ " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationExclusiveOr( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " ^ " + getRight().getParameterValue();
    }

}
