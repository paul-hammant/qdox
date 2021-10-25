package com.thoughtworks.qdox.model.expression;

public class Lambda implements AnnotationValue
{
    @Override
    public Object getParameterValue()
    {
        return "";
    }

    @Override
    public Object accept( ExpressionVisitor visitor )
    {
        return visitor.visit( this );
    }

}
