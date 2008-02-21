package com.thoughtworks.qdox.model.annotation;

public class AnnotationShiftRight extends AnnotationBinaryOperator {

    public AnnotationShiftRight( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " >> " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationShiftRight( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " >> " + getRight().getParameterValue();
    }

}
