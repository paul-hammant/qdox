package com.thoughtworks.qdox.model;

public interface JavaModel
{

    public String getComment();

    public DocletTag[] getTags();

    public DocletTag[] getTagsByName( String name );

    public DocletTag getTagByName( String name );

    public String getCodeBlock();

    public JavaSource getSource();

}