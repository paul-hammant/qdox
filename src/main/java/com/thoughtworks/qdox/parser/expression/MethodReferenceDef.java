package com.thoughtworks.qdox.parser.expression;

public class MethodReferenceDef extends ExpressionDef
{
    public <U> U transform( ElemValueTransformer<U> transformer )
    {
        return transformer.transform( this );
    }
}
