package com.thoughtworks.qdox.model;

import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface DocletTagFactory extends Serializable {

    /**
     * @since 1.5
     */ 
    DocletTag createDocletTag(
        String tag, String text, 
        AbstractJavaEntity context, int lineNumber
    );

    /**
     * @deprecated please provide 
     */
    DocletTag createDocletTag(String tag, String text);

}
