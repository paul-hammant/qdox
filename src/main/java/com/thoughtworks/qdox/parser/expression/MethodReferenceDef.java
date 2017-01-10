package com.thoughtworks.qdox.parser.expression;

public class MethodReferenceDef extends ExpressionDef
{
    /** {@inheritDoc} */
    public <U> U transform( ElemValueTransformer<U> transformer )
    {
        return transformer.transform( this );
    }
}
