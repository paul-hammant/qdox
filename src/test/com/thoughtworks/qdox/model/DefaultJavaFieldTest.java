package com.thoughtworks.qdox.model;

import java.util.List;

public class DefaultJavaFieldTest
    extends JavaFieldTest<DefaultJavaField>
{

    public DefaultJavaFieldTest( String s )
    {
        super( s );
    }

    public DefaultJavaField newJavaField()
    {
        JavaSource source = new DefaultJavaSource( null );
        JavaClass javaClass = new DefaultJavaClass( source );
        DefaultJavaField result = new DefaultJavaField();
        result.setParentClass( javaClass );
        return result;
    }

    public DefaultJavaField newJavaField( Type type, String name )
    {
        return new DefaultJavaField(type, name);
    }

    public JavaClass newJavaClass( String fullname )
    {
        return new DefaultJavaClass(fullname);
    }

    public JavaPackage newJavaPackage( String name )
    {
        return new DefaultJavaPackage(name);
    }

    public Type newType( String fullname )
    {
        return new Type( fullname );
    }

    public Type newType( String fullname, int dimensions )
    {
        return new Type( fullname, dimensions );
    }

    public void setComment( DefaultJavaField fld, String comment )
    {
        fld.setComment( comment );
    }

    public void setInitializationExpression( DefaultJavaField fld, String expression )
    {
        fld.setInitializationExpression( expression );
    }

    public void setModifiers( DefaultJavaField fld, List<String> modifiers )
    {
        fld.setModifiers( modifiers );
    }

    public void setName( DefaultJavaField fld, String name )
    {
        fld.setName( name );
    }

    public void setType( DefaultJavaField fld, Type type )
    {
        fld.setType( type );
    }


    public void addField( JavaClass clazz, JavaField fld )
    {
        ((DefaultJavaClass) clazz).addField( fld );
        ((DefaultJavaField) fld).setParentClass( clazz );
    }

    public void addClass( JavaPackage pckg, JavaClass clazz )
    {
        ((DefaultJavaPackage) pckg).addClass( clazz );
        ((DefaultJavaClass) clazz).setJavaPackage( pckg );
    }

}
