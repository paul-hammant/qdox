package com.thoughtworks.qdox.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author Eric Redmond
 */
public class Annotation implements Serializable
{
    private final Type type;
    private final int lineNumber;

    private Map namedParameters;
    private AbstractJavaEntity context;

    public Annotation(Type type,
            AbstractJavaEntity context,
            Map namedParameters,
            int lineNumber)
	{
		this.type = type;
		this.context = context;
    	this.namedParameters = namedParameters == null ? new HashMap(0) : namedParameters;
		this.lineNumber = lineNumber;
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
