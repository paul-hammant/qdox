package com.thoughtworks.qdox.model.annotation;

public class AnnotationUnsignedShiftRight extends AnnotationBinaryOperator {

    public AnnotationUnsignedShiftRight( AnnotationValue left, AnnotationValue right ) {
        super( left, right );
    }

    public String toString() {
        return getLeft().toString() + " >>> " + getRight().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationUnsignedShiftRight( this );
    }

    public Object getParameterValue() {
        return getLeft().getParameterValue() + " >>> " + getRight().getParameterValue();
    }

}
