package com.thoughtworks.qdox.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.qdox.model.annotation.AnnotationValue;
import com.thoughtworks.qdox.model.annotation.AnnotationVisitor;

/**
 * 
 * @author Eric Redmond
 */
public class Annotation implements AnnotationValue, Serializable
{
    private final Type type;
    private final int lineNumber;

    /**
     * Annotation properties as AnnotationValues
     * <p>
     * This map contains the parsed AnnotationValue for each property and allows
     * access to the full parse tree, including typerefs and expressions.
     */
    private final Map<String, AnnotationValue> properties = new LinkedHashMap<String, AnnotationValue>();

    /**
     * Annotation properties as Parameters
     */
    private final Map<String, Object> namedParameters = new LinkedHashMap<String, Object>();

    private AbstractBaseJavaEntity context;

    public Annotation(Type type,
            AbstractBaseJavaEntity context,
            Map<String, Object> namedParameters,
            int lineNumber)
	{
        this.type = type;
        this.context = context;
        this.lineNumber = lineNumber;
        
        if(properties != null) {
            for(Iterator<Entry<String, AnnotationValue>> i = this.properties.entrySet().iterator(); i.hasNext(); ) {
                Entry<String, AnnotationValue> entry = i.next();
                String name = entry.getKey();
                AnnotationValue value = entry.getValue();
                
                setProperty(name, value);
            }
        }
	}

    public Annotation( Type type, int line ) {
        this(type, null, null, line);
    }

    public void setProperty(String name, AnnotationValue value) {
        properties.put( name, value );
        namedParameters.put( name, value.getParameterValue() );
    }

    /**
     * @return the annotation type
     */
    public Type getType() {
    	return type;
    }

    /**
     * @param key name of a named-parameter
     * @return the corresponding value,
     *   or null if no such named-parameter was present
     */
    public Object getNamedParameter(String key) {
    	return namedParameters.get( key );
    }

    /**
     * @return a Map containing all the named-parameters
     */
    public Map<String, Object> getNamedParameterMap() {
    	return namedParameters;
    }

    public final AbstractBaseJavaEntity getContext() {
        return context;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotation( this );
    }

    public Object getParameterValue() {
        return this;
    }
    
    public Map<String, AnnotationValue> getPropertyMap() {
        return properties;
    }
    
    public AnnotationValue getProperty(String name) {
        return properties.get( name );
    }

    public void setContext( AbstractBaseJavaEntity context ) {
        this.context = context;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append('@');
        result.append(type.getValue());
        result.append('(');
        if( !namedParameters.isEmpty() ) {
            for(Iterator<Entry<String, Object>> i = namedParameters.entrySet().iterator(); i.hasNext();) result.append( i.next() + ",");
            result.deleteCharAt( result.length()-1 );
        }
        result.append(')');
        return result.toString();
    }
}
