package com.thoughtworks.qdox.model.annotation;

public class AnnotationShiftLeft extends AnnotationBinaryOperator {

    public AnnotationShiftLeft( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " << " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationShiftLeft( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " << " + getRight().getParameterValue();
    }

}
