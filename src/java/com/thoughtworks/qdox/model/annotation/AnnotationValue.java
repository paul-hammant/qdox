package com.thoughtworks.qdox.model.annotation;

import com.thoughtworks.qdox.model.Annotation;

/**
 * Interface for all annotation model elements
 * 
 * @author Jochen Kuhnle
 */
public interface AnnotationValue {

    /**
     * Accept a visitor for this value.
     * 
     * @param visitor Visitor
     * @return Visitor result
     */
    public Object accept( AnnotationVisitor visitor );

    /**
     * Get a parameter value for {@link Annotation#getNamedParameter(String)}.
     * 
     * @return Parameter value
     */
    public Object getParameterValue();

}
