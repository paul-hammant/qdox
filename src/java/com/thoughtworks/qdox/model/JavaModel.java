package com.thoughtworks.qdox.model;

public interface JavaModel
{

    public String getComment();

    public DocletTag[] getTags();

    public DocletTag[] getTagsByName( String name );

    public DocletTag getTagByName( String name );

    /**
     * Convenience method for <code>getTagByName(String).getNamedParameter(String)</code>
     * that also checks for null tag.
     * @since 1.3
     */
    public String getNamedParameter(String tagName, String parameterName);
    
    public String getCodeBlock();

    public JavaSource getSource();
    
    public int getLineNumber();

}