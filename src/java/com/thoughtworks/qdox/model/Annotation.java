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
    private final Map properties = new LinkedHashMap();

    /**
     * Annotation properties as Parameters
     */
    private final Map namedParameters = new LinkedHashMap();

    private AbstractJavaEntity context;

    public Annotation(Type type,
            AbstractJavaEntity context,
            Map namedParameters,
            int lineNumber)
	{
        this.type = type;
        this.context = context;
        this.lineNumber = lineNumber;
        
        if(properties != null) {
            for(Iterator i = this.properties.entrySet().iterator(); i.hasNext(); ) {
                Entry entry = (Entry) i.next();
                String name = (String) entry.getKey();
                AnnotationValue value = (AnnotationValue) entry.getValue();
                
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
    public Map getNamedParameterMap() {
    	return namedParameters;
    }

    public final AbstractJavaEntity getContext() {
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
    
    public Map getPropertyMap() {
        return properties;
    }
    
    public AnnotationValue getProperty(String name) {
        return (AnnotationValue) properties.get( name );
    }

    public void setContext( AbstractJavaEntity context ) {
        this.context = context;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append('@');
        result.append(type.getValue());
        result.append('(');
        if( !namedParameters.isEmpty() ) {
            for(Iterator i = namedParameters.entrySet().iterator(); i.hasNext();) result.append( i.next() + ",");
            result.deleteCharAt( result.length()-1 );
        }
        result.append(')');
        return result.toString();
    }
}
