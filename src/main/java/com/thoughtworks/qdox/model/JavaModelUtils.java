package com.thoughtworks.qdox.model;

public final class JavaModelUtils
{

    private JavaModelUtils()
    {
        // 
    }
    
    public static JavaClass getClassByName( JavaClass cls, String name )
    {
        JavaClass result = null;
        if ( cls.getFullyQualifiedName().equals( name ) )
        {
            result = cls;
        }
        else if ( cls.getName().equals( name ) )
        {
            result = cls;
        }
        else
        {
            for ( JavaClass innerCls : cls.getClasses() )
            {
                result = getClassByName( innerCls, name );
                if ( result != null )
                {
                    break;
                }
            }
        }
        return result;
    }
}
