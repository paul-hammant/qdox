package com.thoughtworks.qdox.model;

import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface DocletTagFactory extends Serializable {
    /**
     * @since 1.3
     */ 
    DocletTag createDocletTag(String tag, String text, int lineNumber);
    DocletTag createDocletTag(String tag, String text);
}
