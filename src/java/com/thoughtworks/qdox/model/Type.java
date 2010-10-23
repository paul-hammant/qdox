package com.thoughtworks.qdox.model;

import java.io.Serializable;

import com.thoughtworks.qdox.JavaClassContext;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.WildcardTypeDef;

public class Type implements Comparable, Serializable {

    public static final Type[] EMPTY_ARRAY = new Type[0];
    public static final Type VOID = new Type("void");

    private String name;
    private JavaClassParent context;
    private String fullName;
    private int dimensions;
    private Type[] actualArgumentTypes;

    public Type(String fullName, String name, int dimensions, JavaClassParent context) {
        this.fullName = fullName;
        this.name = name;
        this.dimensions = dimensions;
        this.context = context;
    }
    
    public Type(String fullName, TypeDef typeDef, int dimensions, JavaClassParent context) {
    	this.fullName = fullName;
        this.name = typeDef.name;
        this.dimensions = typeDef.dimensions + dimensions; //in some cases dimensions can be spread. Collect them here
        if(typeDef.actualArgumentTypes != null && !typeDef.actualArgumentTypes.isEmpty()) {
        	actualArgumentTypes = new Type[typeDef.actualArgumentTypes.size()];
        	for(int index = 0; index < typeDef.actualArgumentTypes.size(); index++) {
        		actualArgumentTypes[index] = createUnresolved((TypeDef) typeDef.actualArgumentTypes.get(index), context);
        	}
        }
        this.context = context;
	}


    public Type(String fullName, int dimensions, JavaClassParent context) {
        this(fullName, (String) null, dimensions, context);
    }

    public Type(String fullName, int dimensions) {
        this(fullName, dimensions, null);
    }

    public Type(String fullName) {
        this(fullName, 0);
    }
    
	public static Type createUnresolved(String name, int dimensions, JavaClassParent context) {
        return new Type(null, name, dimensions, context);
    }
    
	public static Type createUnresolved(TypeDef typeDef, int dimensions, JavaClassParent context) {
        return new Type(null, typeDef, dimensions, context);
	}
	
	public static Type createUnresolved(TypeDef typeDef, JavaClassParent context) {
		if(typeDef instanceof WildcardTypeDef) {
			return new WildcardType((WildcardTypeDef) typeDef, context);
		}
        return new Type(null, typeDef, 0, context);
	}

    
    public JavaClassParent getJavaClassParent() {
        return context;
    }

    /**
     * 
     * @deprecated instead use getFullyQualifiedName()
     */
    public String getFullQualifiedName() {
        return getFullyQualifiedName();
    }

    /**
     * Returns the FQN of an Object or the handler of a Type
     * If the name of the can't be resolved based on the imports and the classes on the classpath the name will be returned
     * InnerClasses will use the $ sign
     * 
     * Some examples how names will be translated 
     * <pre>
     * Object > java.lang.Object
     * java.util.List > java.util.List
     * ?  > ?
     * T  > T
     * anypackage.Outer.Inner > anypackage.Outer$Inner
     * </pre>
     * 
     * @return
     */
    public String getFullyQualifiedName() {
        
        return isResolved() ? fullName : name;
    }

    /**
     * The FQN representation of an Object for code usage
     * This implementation ignores generics
     *
     * Some examples how Objects will be translated
     * <pre>
     * Object > java.lang.object
     * java.util.List<T> > java.util.List
     * ? > ?
     * T > T
     * anypackage.Outer.Inner > anypackage.Outer.Inner
     * </pre>
     * 
     * @return type representation for code usage
     */
    public String getValue() {
        String fqn = getFullyQualifiedName();
        return ( fqn == null ? "" : fqn.replaceAll( "\\$", "." ) );
    }
    
    /**
     * The FQN representation of an Object for code usage
     * This implementation ignores generics
     *
     * Some examples how Objects will be translated
     * <pre>
     * Object > java.lang.object
     * java.util.List<T> > java.util.List
     * ? > ?
     * T > T
     * anypackage.Outer.Inner > anypackage.Outer.Inner
     * </pre>

     * @since 1.8
     * @return generic type representation for code usage 
     */
    public String getGenericValue() {
    	StringBuffer result = new StringBuffer(getValue());
    	if(actualArgumentTypes != null && actualArgumentTypes.length > 0) {
    		result.append("<");
    		for(int index = 0;index < actualArgumentTypes.length; index++) {
    			result.append(actualArgumentTypes[index].getGenericValue());
    			if(index + 1 != actualArgumentTypes.length) {
    				result.append(",");
    			}
    		}
    		result.append(">");
    	}
    	for (int i = 0; i < dimensions; i++) result.append("[]");
        return result.toString();
    }
    
    protected String getGenericValue(TypeVariable[] typeVariableList) {
    	StringBuffer result = new StringBuffer(getResolvedValue(typeVariableList));
    	if(actualArgumentTypes != null && actualArgumentTypes.length > 0) {
    		for(int index = 0;index < actualArgumentTypes.length; index++) {
    			result.append(actualArgumentTypes[index].getResolvedGenericValue(typeVariableList));
    			if(index + 1 != actualArgumentTypes.length) {
    				result.append(",");
    			}
    		}
    	}
        return result.toString();
    }
    
    protected String getResolvedValue(TypeVariable[] typeParameters) {
    	String result = getValue();
    	for(int typeIndex=0;typeIndex<typeParameters.length; typeIndex++) {
			if(typeParameters[typeIndex].getName().equals(getValue())) {
				result = typeParameters[typeIndex].getValue();
				break;
			}
		}
    	return result;
    }
    
    protected String getResolvedGenericValue(TypeVariable[] typeParameters) {
    	String result = getGenericValue(typeParameters);
    	for(int typeIndex=0;typeIndex<typeParameters.length; typeIndex++) {
			if(typeParameters[typeIndex].getName().equals(getValue())) {
				result = typeParameters[typeIndex].getGenericValue();
				break;
			}
		}
    	return result;
    }

    /**
     * Checks if the FQN of this Type is resolved 
     * 
     * @return 
     */
    public boolean isResolved() {
        if (fullName == null && context != null) {
            fullName = context.resolveType(name);
        }
        return (fullName != null);
    }

    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object o) {
        if (!(o instanceof Type))
            return 0;

        return getValue().compareTo(((Type) o).getValue());
    }

    /**
     * Returns true if this Type is an array
     * 
     * @return
     */
    public boolean isArray() {
        return dimensions > 0;
    }

    /**
     * Returns the depth of this array, 0 if it's not an array
     * 
     * @return The depth of this array
     */
    public int getDimensions() {
        return dimensions;
    }

    /**
     * 
     * @return the actualTypeArguments or null
     */
    public Type[] getActualTypeArguments()
    {
        return actualArgumentTypes;
    }
    
    /**
     * Returns getValue() extended with the array information 
     * 
     * @return
     */
    public String toString() {
        if (dimensions == 0) return getValue();
        StringBuffer buff = new StringBuffer(getValue());
        for (int i = 0; i < dimensions; i++) buff.append("[]");
        String result = buff.toString();
        return result;
    }

    /**
     * Returns getGenericValue() extended with the array information
     * 
     * <pre>
     * Object > java.lang.Object
     * Object[] > java.lang.Object[]
     * List<Object> > java.lang.List<java.lang.Object>
     * Outer.Inner > Outer.Inner 
     * Outer.Inner<Object>[][] > Outer.Inner<java.lang.Object>[][] 
     * </pre>
     * @return 
     */
    public String toGenericString() {
        if (dimensions == 0) return getGenericValue();
        StringBuffer buff = new StringBuffer(getGenericValue());
        for (int i = 0; i < dimensions; i++) buff.append("[]");
        String result = buff.toString();
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        Type t = (Type) obj;
        return getValue().equals(t.getValue()) && t.getDimensions() == getDimensions();
    }

    public int hashCode() {
        return getValue().hashCode();
    }

    public JavaClass getJavaClass() {
    	JavaClass result = null;
    	
        JavaClassParent javaClassParent = getJavaClassParent();
        if (javaClassParent != null) {
        	result = javaClassParent.getNestedClassByName(getFullyQualifiedName());
	        if(result == null) {
	            if(javaClassParent.getJavaClassLibrary() != null) {
	                result = javaClassParent.getJavaClassLibrary().getJavaClass( getFullyQualifiedName() );
	            }
	            else if (javaClassParent.getClassLibrary() != null) {
                    result = javaClassParent.getClassLibrary().getJavaClass(getFullyQualifiedName());
	            }
	        }
        }
        return result;
    }

    /**
     * @since 1.3
     */
    public boolean isA(Type type) {
        if (this.equals(type)) {
            return true;
        } else {
            JavaClass javaClass = getJavaClass();
            if (javaClass != null) {
                // ask our interfaces
                Type[] implementz = javaClass.getImplements();
                for (int i = 0; i < implementz.length; i++) {
                    if (implementz[i].isA(type)) {
                        return true;
                    }
                }

                // ask our superclass
                Type supertype = javaClass.getSuperClass();
                if (supertype != null) {
                    if (supertype.isA(type)) {
                        return true;
                    }
                }
            }
        }
        // We'we walked up the hierarchy and found nothing.
        return false;
    }

    /**
     * @since 1.6
     */
    public boolean isPrimitive() {
        String value = getValue();
        if (value == null || value.length() == 0 || value.indexOf('.') > -1) {
            return false;
        } else {
           return "void".equals(value)           
            || "boolean".equals(value)
            || "byte".equals(value)
            || "char".equals(value)
            || "short".equals(value)
            || "int".equals(value)
            || "long".equals(value)
            || "float".equals(value)
            || "double".equals(value);
        }
    }

    /**
     * @since 1.6
     */
    public boolean isVoid() {
        return "void".equals(getValue());
    }

    /**
     * 
     * @param superClass
     * @return
     * @since 1.12
     */
    protected int getTypeVariableIndex( JavaClass superClass ) {
        TypeVariable[] typeVariables = superClass.getTypeParameters();
        for(int typeIndex=0;typeIndex<typeVariables.length; typeIndex++) {
            if(typeVariables[typeIndex].getFullyQualifiedName().equals( getFullyQualifiedName())) {
                return typeIndex;
            }
        }
        return -1;
    }

    /**
     * 
     * @param parentClass
     * @return
     * @since 1.12
     */
    protected Type resolve( JavaClass parentClass )
    {
        return resolve( parentClass, parentClass );
    }

    /**
     * 
     * @param parentClass
     * @param subclass
     * @return
     * @since 1.12
     */
    protected Type resolve( JavaClass parentClass, JavaClass subclass )
    {
        Type result = this;
        int typeIndex = getTypeVariableIndex( parentClass );
        if ( typeIndex >= 0 )
        {
            String fqn = parentClass.getFullyQualifiedName();
            if ( subclass.getSuperClass() != null && fqn.equals( subclass.getSuperClass().getFullyQualifiedName() ) ) {
                result = subclass.getSuperClass().getActualTypeArguments()[typeIndex];    
            }
            else if ( subclass.getImplementedInterfaces() != null )
            {
                for ( int i = 0; i < subclass.getImplementedInterfaces().length; i++ )
                {
                    if ( fqn.equals( subclass.getImplements()[i].getFullyQualifiedName() ) ) 
                    {
                        result = subclass.getImplements()[i].getActualTypeArguments()[typeIndex].resolve( subclass.getImplementedInterfaces()[i] );
                        break;
                    }
                }
                //no direct interface available, try indirect
            }
        }
        
        if ( this.actualArgumentTypes != null ) {
            result = new Type( this.fullName, this.name, this.dimensions, this.context );
            
            result.actualArgumentTypes = new Type[this.actualArgumentTypes.length];
            for (int i = 0; i < this.getActualTypeArguments().length; i++ )
            {
                result.actualArgumentTypes[i] = this.actualArgumentTypes[i].resolve( parentClass, subclass );
            }
        }
        return result;
    }

}
