package com.thoughtworks.qdox.model;

import java.io.Serializable;

public interface ModelBuilderFactory extends Serializable
{
    public ModelBuilder newInstance();
}
