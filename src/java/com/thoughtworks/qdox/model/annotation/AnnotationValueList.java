package com.thoughtworks.qdox.model.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class AnnotationValueList implements AnnotationValue {

    private final List valueList;

    public AnnotationValueList( List valueList ) {
        this.valueList = valueList;
    }

    public List getValueList() {
        return valueList;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append( "{" );

        int pos = buf.length();

        for( ListIterator i = valueList.listIterator(); i.hasNext(); ) {
            buf.append( i.next().toString() );
            buf.append( ", " );
        }

        if( buf.length() > pos ) {
            buf.setLength( buf.length() - 2 );
        }

        buf.append( "}" );

        return buf.toString();
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationValueList( this );
    }

    public Object getParameterValue() {
        List list = new ArrayList();

        for( ListIterator i = valueList.listIterator(); i.hasNext(); ) {
            AnnotationValue value = (AnnotationValue) i.next();
            list.add( value.getParameterValue() );
        }

        return list;
    }
}
