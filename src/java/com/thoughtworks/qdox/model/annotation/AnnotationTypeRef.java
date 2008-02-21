package com.thoughtworks.qdox.model.annotation;

import com.thoughtworks.qdox.model.Type;

public class AnnotationTypeRef implements AnnotationValue {

    private Type type;

    public AnnotationTypeRef( Type type ) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String toString() {
        return type.getValue() + ".class";
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationTypeRef( this );
    }

    public Object getParameterValue() {
        return type.getValue() + ".class";
    }
}
