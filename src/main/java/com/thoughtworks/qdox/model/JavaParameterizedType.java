package com.thoughtworks.qdox.model;

import java.util.List;

public interface JavaParameterizedType extends JavaType
{

    List<JavaType> getActualTypeArguments();

}
