package com.thoughtworks.qdox.model.annotation;

public class AnnotationGreaterEquals extends AnnotationBinaryOperator {

    public AnnotationGreaterEquals( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " >= " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationGreaterEquals( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " >= " + getRight().getParameterValue();
    }

}
