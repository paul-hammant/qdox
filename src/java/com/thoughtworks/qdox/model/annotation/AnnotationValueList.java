package com.thoughtworks.qdox.model.annotation;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class AnnotationValueList implements AnnotationValue {

    private final List<AnnotationValue> valueList;

    public AnnotationValueList( List<AnnotationValue> valueList ) {
        this.valueList = valueList;
    }

    public List<AnnotationValue> getValueList() {
        return valueList;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append( "{" );

        for( ListIterator<AnnotationValue> i = valueList.listIterator(); i.hasNext(); ) {
            buf.append( i.next().toString() );
            
            if(i.hasNext()) {
                buf.append( ", " );
            }
        }

        buf.append( "}" );

        return buf.toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationValueList( this );
    }

    public Object getParameterValue() {
        List<Object> list = new LinkedList<Object>();

        for( ListIterator<AnnotationValue> i = valueList.listIterator(); i.hasNext(); ) {
            list.add( i.next().getParameterValue() );
        }

        return list;
    }
}
