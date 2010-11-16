package com.thoughtworks.qdox.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface DocletTag extends Serializable {

    /**
     * @return the tag name
     */
    String getName();

    /**
     * @return the full tag-value
     */
    String getValue();

    /**
     * @return an array of whitespace-separatedtag parameters
     */
    List<String> getParameters();

    /**
     * @param key name of a named-parameter
     * @return the corresponding value, 
     *   or null if no such named-parameter was present 
     */
    String getNamedParameter(String key);

    /**
     * @return a Map containing all the named-parameters
     */
    Map<String, String> getNamedParameterMap();
    
    /**
     * @return the line-number where the tag occurred
     */
    int getLineNumber();

    /**
     * @return the language element to which this tag applies
     * @since 1.4
     */
    AbstractBaseJavaEntity getContext();

}
