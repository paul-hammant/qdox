package com.thoughtworks.qdox.model;

public interface JavaType
{
    String getCanonicalName();

    String getFullyQualifiedName();

    String getGenericFullyQualifiedName();

    String getValue();

    String getGenericValue();

    String toGenericString();
}