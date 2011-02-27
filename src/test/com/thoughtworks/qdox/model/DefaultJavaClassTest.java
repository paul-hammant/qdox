package com.thoughtworks.qdox.model;

import java.util.List;

import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;

public class DefaultJavaClassTest
    extends JavaClassTest<DefaultJavaClass>
{

    public DefaultJavaClassTest( String s )
    {
        super( s );
    }

    public DefaultJavaClass newJavaClass()
    {
        return new DefaultJavaClass();
    }

    public DefaultJavaClass newJavaClass( String name )
    {
        return new DefaultJavaClass( name );
    }

    public JavaField newJavaField(JavaClass parentClass)
    {
        DefaultJavaField result = new DefaultJavaField();
        result.setParentClass( parentClass );
        return result;
    }

    public JavaMethod newJavaMethod()
    {
        return new DefaultJavaMethod();
    }

    public JavaMethod newJavaMethod( String name )
    {
        return new DefaultJavaMethod( name );
    }

    public JavaMethod newJavaMethod( Type returns, String name )
    {
        return new DefaultJavaMethod( returns, name );
    }

    public JavaPackage newJavaPackage( String name )
    {
        return new DefaultJavaPackage( name );
    }

    public JavaParameter newJavaParameter( Type type, String name )
    {
        return new DefaultJavaParameter( type, name );
    }

    public JavaParameter newJavaParameter( Type type, String name, boolean varArgs )
    {
        return new DefaultJavaParameter( type, name, varArgs );
    }

    public JavaSource newJavaSource()
    {
        return new DefaultJavaSource(new SortedClassLibraryBuilder().getClassLibrary());
    }

    public Type newType( String fullname )
    {
        return new Type( fullname );
    }

    // Add-methods
    public void addClass( JavaClass clazz, JavaClass innerClazz )
    {
        ((DefaultJavaClass) clazz).addClass( innerClazz );
        ((DefaultJavaClass) innerClazz).setParentClass( clazz );
    }

    public void addClass( JavaPackage pckg, JavaClass clazz )
    {
        ((DefaultJavaPackage) pckg).addClass( clazz );
        ((DefaultJavaClass) clazz).setJavaPackage( pckg );
    }

    public void addClass( JavaSource source, JavaClass clazz )
    {
        ((DefaultJavaClass) clazz).setSource( source );
        ((DefaultJavaSource) source).addClass( clazz );
    }

    public void addMethod( JavaClass clazz, JavaMethod method )
    {
        ((DefaultJavaClass)clazz).addMethod( method );
        ((AbstractBaseMethod) method).setParentClass( clazz );
    }

    public void addParameter( JavaMethod method, JavaParameter parameter )
    {
        ((AbstractBaseMethod) method).addParameter( parameter );
        ((DefaultJavaParameter) parameter).setParentMethod( method );
    }

    // Set-methods
    public void setComment( DefaultJavaClass clazz, String comment )
    {
        clazz.setComment( comment );
    }

    public void setComment( JavaField field, String comment )
    {
        ((DefaultJavaField) field).setComment( comment );
    }

    public void setComment( JavaMethod method, String comment )
    {
        ((AbstractBaseMethod) method).setComment( comment );
    }

    public void setEnum( DefaultJavaClass clazz, boolean isEnum )
    {
        clazz.setEnum( isEnum );
    }

    public void setImplementz( DefaultJavaClass clazz, List<Type> implementz )
    {
        clazz.setImplementz( implementz );
    }

    public void setInterface( DefaultJavaClass clazz, boolean isInterface )
    {
        clazz.setInterface( isInterface );
    }

    public void setModifiers( DefaultJavaClass clazz, List<String> modifiers )
    {
        clazz.setModifiers( modifiers );
    }

    public void setModifiers( JavaField field, List<String> modifiers )
    {
        ((DefaultJavaField) field).setModifiers( modifiers );
    }

    public void setName( DefaultJavaClass clazz, String name )
    {
        clazz.setName( name );
    }

    public void setName( JavaField field, String name )
    {
        ((DefaultJavaField) field).setName( name );
    }

    public void setName( JavaMethod method, String name )
    {
        ((AbstractBaseMethod) method).setName( name );
    }

    public void setPackage( JavaSource source, JavaPackage pckg )
    {
        ((DefaultJavaSource) source).setPackage( pckg );
    }

    public void setReturns( JavaMethod method, Type returns )
    {
        ((DefaultJavaMethod) method).setReturns( returns );
    }

    public void setSuperClass( DefaultJavaClass clazz, Type type )
    {
        clazz.setSuperClass( type );
    }

    public void setType( JavaField field, Type type )
    {
        ((DefaultJavaField) field).setType( type );
    }
    
    @Override
    public void setFields( DefaultJavaClass clazz, List<JavaField> fields )
    {
        for(JavaField field : fields) {
            clazz.addField( field );
        }
    }
    
    @Override
    public void setSource( DefaultJavaClass clazz, JavaSource source )
    {
        clazz.setSource( source );
    }

}
