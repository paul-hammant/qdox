package com.thoughtworks.qdox.model.annotation;

public class AnnotationLogicalNot extends AnnotationUnaryOperator {

    public AnnotationLogicalNot( AnnotationValue value ) {
        super( value );
    }

    public String toString() {
        return "!" + getValue().toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationLogicalNot( this );
    }

    public Object getParameterValue() {
        return "!" + getValue().toString();
    }
}
