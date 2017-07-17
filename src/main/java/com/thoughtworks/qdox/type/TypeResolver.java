package com.thoughtworks.qdox.type;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * A per JavaClass resolver of types
 *  
 * @author Robert Scholte
 * @since 2.0
 */
public class TypeResolver
{
    private static final Set<String> PRIMITIVE_TYPES = new HashSet<String>();

    private final Map<String, String> resolvedTypeCache = new HashMap<String, String>();

    private final String pckg;
    
    private final String declaringClass;

    private final ClassLibrary classLibrary;
    
    private final Collection<String> imports;
    
    static {
        PRIMITIVE_TYPES.add("boolean");
        PRIMITIVE_TYPES.add("byte");
        PRIMITIVE_TYPES.add("char");
        PRIMITIVE_TYPES.add("double");
        PRIMITIVE_TYPES.add("float");
        PRIMITIVE_TYPES.add("int");
        PRIMITIVE_TYPES.add("long");
        PRIMITIVE_TYPES.add("short");
        PRIMITIVE_TYPES.add("void");
    }

    private TypeResolver( String pckg, String declaringClass, ClassLibrary classLibrary, Collection<String> imports )
    {
        this.pckg = pckg;
        this.declaringClass = declaringClass;
        this.classLibrary = classLibrary;
        this.imports = imports != null ? imports : Collections.<String>emptyList();
    }
    
    /**
     * Type resolver in case there's no declaring class, e.g. using extends, implements and annotations on a toplevel class
     * 
     * @param binaryName the binary name of the package
     * @param classLibrary the class library
     * @param imports the imports, can be {@code null}
     * @return the typeResolver
     */
    public static TypeResolver byPackageName( String binaryName, ClassLibrary classLibrary, Collection<String> imports )
    {
        return new TypeResolver( binaryName, null, classLibrary, imports );
    }
    
    /**
     * 
     * @param binaryName the class in which context a type is used.
     * @param classLibrary the class library
     * @param imports the imports, can be {@code null}
     * @return the typeResolver
     */
    public static TypeResolver byClassName( String binaryName, ClassLibrary classLibrary, Collection<String> imports )
    {
        int dotIndex = binaryName.lastIndexOf( '.' );
        String pckg = dotIndex > 0 ? binaryName.substring( 0, dotIndex ) : null;   
        return new TypeResolver( pckg, binaryName, classLibrary, imports );
    }
    
    public JavaClass resolveJavaClass( String typeName )
    {
        return classLibrary.getJavaClass( resolveType( typeName ) );
    }

    public JavaClass getJavaClass( String binaryName )
    {
        return classLibrary.getJavaClass( binaryName );
    }

    public String resolveType( String typeName )
    {
        String result = resolvedTypeCache.get( typeName );
        if ( result == null )
        {
            if ( declaringClass != null )
            {
                
                int dollarIndex = declaringClass.indexOf( '$' );
                
                while( result == null && dollarIndex > 0 )
                {
                    String subType = declaringClass.substring( 0, dollarIndex + 1 ) + typeName;
                    
                    result = resolveFromLibrary( subType );
                    
                    dollarIndex = declaringClass.indexOf( '$', dollarIndex + 1 );
                }

                if (result == null)
                {
                    String nestedBinaryName = declaringClass + '$' + typeName.replace( '.', '$' );

                    result = resolveFromLibrary( nestedBinaryName );
                }
            }

            if ( result == null )
            {
                result = resolveTypeInternal( typeName );
            }

            if ( result != null )
            {
                resolvedTypeCache.put( typeName, result );
            }
        }
        return result;
    }
    
    /**
     * Resolves a type name
     * <p>
     * Follows the <a href="http://java.sun.com/docs/books/jls/third_edition/html/packages.html#7.5.1">
     * Java Language Specification, Version 3.0</a>.
     * <p>
     * Current resolution order is:
     * <ol>
     * <li>Single-Type-Import Declaration</li>
     * <li>Type-Import-on-Demand Declaration</li>
     * <li>Automatic Imports</li>
     * </ol>
     * 
     * @param typeName the name to resolve
     * @return the resolved type name, otherwise <code>null</code>
     */
    private String resolveTypeInternal( String typeName )
    {
        String resolvedName = null;

        lookup:
        {
            // primitive types
            if ( PRIMITIVE_TYPES.contains( typeName ) )
            {
                resolvedName = typeName;
                break lookup;
            }

            String outerName = typeName;
            String nestedName = typeName.replace( '.', '$' );
            int dotpos = typeName.indexOf( '.' );

            if ( dotpos >= 0 )
            {
                outerName = typeName.substring( 0, dotpos );
            }

            // Check single-type-import with fully qualified name
            resolvedName = resolveImportedType( typeName, nestedName, true );

            if ( resolvedName != null )
            {
                break lookup;
            }

            // Check single-type-import with outer name
            resolvedName = resolveImportedType( outerName, nestedName, false );

            if ( resolvedName != null )
            {
                break lookup;
            }

            // check for class in the same package
            if ( pckg != null )
            {
                resolvedName = resolveFullyQualifiedType( pckg + '.' + typeName );

                if ( resolvedName != null )
                {
                    break lookup;
                }
            }

            // check for a class globally
            resolvedName = resolveFullyQualifiedType( typeName );

            if ( resolvedName != null )
            {
                break lookup;
            }

            // check for a class in the same package
            if ( pckg != null )
            {
                resolvedName = resolveFromLibrary( pckg + "$" + nestedName );
                if ( resolvedName != null )
                {
                    break lookup;
                }
            }

            // try java.lang.*
            resolvedName = resolveFromLibrary( "java.lang." + nestedName );
            if ( resolvedName != null )
            {
                break lookup;
            }

            // Check type-import-on-demand
            resolvedName = resolveImportedType( "*", nestedName, false );

            if ( resolvedName != null )
            {
                break lookup;
            }
        }

        return resolvedName;
    }

    private String resolveImportedType( String importSpec, String typeName, boolean fullMatch )
    {
        String resolvedName = null;
        String dotSuffix = "." + importSpec;

        for ( String imprt : imports )
        {
            // static imports can refer to inner classes
            if ( imprt.startsWith( "static " ) )
            {
                imprt = imprt.substring( 7 );
            }
            if ( imprt.equals( importSpec ) || ( !fullMatch && imprt.endsWith( dotSuffix ) ) )
            {
                String candidateName = imprt.equals(importSpec) ? imprt
                                : imprt.substring(0, imprt.length() - importSpec.length()) + typeName;

                resolvedName = resolveFullyQualifiedType( candidateName );
                if ( resolvedName == null && !"*".equals( importSpec ) )
                {
                    resolvedName = candidateName;
                }
                if ( resolvedName != null )
                {
                    break;
                }
            }
        }

        return resolvedName;
    }

    private String resolveFromLibrary(String typeName) {
        return classLibrary.hasClassReference( typeName ) ? typeName : null;
    }

    private String resolveFullyQualifiedType(String typeName) {
        int indexOfLastDot = typeName.lastIndexOf('.');
        
        if (indexOfLastDot >= 0) {
            String root = typeName.substring(0,indexOfLastDot);
            String leaf = typeName.substring(indexOfLastDot+1);
            String resolvedTypeName = resolveFullyQualifiedType(root + '$' + leaf);
            
            if(resolvedTypeName != null) {
                return resolvedTypeName;
            }
        }

        if( classLibrary.hasClassReference( typeName )) 
        {
            return typeName;
        }
        return null;
    }
}
