package com.thoughtworks.qdox.model;

import java.io.Serializable;

/**
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface DocletTag extends Serializable {
    String getName();

    String getValue();

    String[] getParameters();

    String getNamedParameter(String key);
}
