package com.thoughtworks.qdox.model;

public class DefaultJavaConstructor
    extends AbstractBaseMethod implements JavaConstructor
{

    public int compareTo( Object o )
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getCodeBlock()
    {
        return getModelWriter().writeConstructor( this ).toString();
    }
}
