package com.thoughtworks.qdox.model;

import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface DocletTagFactory extends Serializable {
    DocletTag createDocletTag(String tag, String text);
}
