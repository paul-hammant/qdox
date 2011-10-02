package com.thoughtworks.qdox.model;

import java.util.List;

public interface JavaType
{
    String getCanonicalName();

    String getFullyQualifiedName();

    String getGenericFullyQualifiedName();

    String getValue();

    String getGenericValue();

    List<Type> getActualTypeArguments();
}