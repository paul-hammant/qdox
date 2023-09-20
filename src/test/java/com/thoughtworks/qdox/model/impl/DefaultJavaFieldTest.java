package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaFieldTest;
import com.thoughtworks.qdox.model.JavaSource;

import java.util.List;

public class DefaultJavaFieldTest
    extends JavaFieldTest<DefaultJavaField>
{
    @Override
    public DefaultJavaField newJavaField()
    {
        JavaSource source = new DefaultJavaSource( null );
        JavaClass javaClass = new DefaultJavaClass( source );
        DefaultJavaField result = new DefaultJavaField( "NAME" );
        result.setDeclaringClass( javaClass );
        return result;
    }

    @Override
    public DefaultJavaField newJavaField( JavaClass type, String name )
    {
        return new DefaultJavaField( type, name );
    }

    @Override
    public void setComment( DefaultJavaField fld, String comment )
    {
        fld.setComment( comment );
    }

    @Override
    public void setInitializationExpression( DefaultJavaField fld, String expression )
    {
        fld.setInitializationExpression( expression );
    }

    @Override
    public void setModifiers( DefaultJavaField fld, List<String> modifiers )
    {
        fld.setModifiers( modifiers );
    }

    @Override
    public void setName( DefaultJavaField fld, String name )
    {
        fld.setName( name );
    }

    @Override
    public void setType( DefaultJavaField fld, JavaClass type )
    {
        fld.setType( type );
    }

    @Override
    public void setDeclaringClass( DefaultJavaField fld, JavaClass cls )
    {
        fld.setDeclaringClass( cls );
    }
}