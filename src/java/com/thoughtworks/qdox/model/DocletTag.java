package com.thoughtworks.qdox.model;

import java.io.Serializable;

/**
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface DocletTag extends Serializable {
    String getName();

    String getValue();

    String[] getParameters();

    String getNamedParameter(String key);

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
