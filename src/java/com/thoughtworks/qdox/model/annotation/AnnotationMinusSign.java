package com.thoughtworks.qdox.model.annotation;

public class AnnotationMinusSign extends AnnotationUnaryOperator {

    public AnnotationMinusSign( AnnotationValue value ) {
        super( value );
    }

    public String toString() {
        return "-" + getValue().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationMinusSign( this );
    }

    public Object getParameterValue() {
        return "-" + getValue().toString();
    }
}
