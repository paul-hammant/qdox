package com.thoughtworks.qdox.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface DocletTag extends Serializable {


    String getName();

    /**
     * @return the full tag-value
     */
    String getValue();

    /**
     * @return an array of whitespace-separatedtag parameters
     */
    String[] getParameters();

    /**
     * @param key name of a named-parameter
     * @return the corresponding value, 
     *   or null if no such named-parameter was present 
     */
    String getNamedParameter(String key);

    /**
     * @return a Map containing all the named-parameters
     */
    Map getNamedParameterMap();
    
    int getLineNumber();

    /**
     * @since 1.4
     */
    AbstractJavaEntity getContext();

    /**
     * @since 1.4
     */
    void setContext(AbstractJavaEntity abstractJavaEntity);

}
