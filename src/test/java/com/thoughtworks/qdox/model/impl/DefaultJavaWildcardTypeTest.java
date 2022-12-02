package com.thoughtworks.qdox.model.impl;


import com.thoughtworks.qdox.model.JavaWildcardTypeTest;
import com.thoughtworks.qdox.type.TypeResolver;

public class DefaultJavaWildcardTypeTest extends JavaWildcardTypeTest<DefaultJavaWildcardType>
{
    @Override
    public DefaultJavaWildcardType newWildcardType()
    {
        return new DefaultJavaWildcardType();
    }

    @Override
    public DefaultJavaWildcardType newWildcardType( String fullname, TypeResolver typeResolver, DefaultJavaWildcardType.BoundType boundType )
    {
        DefaultJavaType defaultJavaType = new DefaultJavaType( fullname, typeResolver );
        return new DefaultJavaWildcardType( defaultJavaType, boundType );
    }
}
