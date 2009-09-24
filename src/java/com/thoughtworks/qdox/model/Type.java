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

    public String getFullyQualifiedName() {
        return isResolved() ? fullName : name;
    }

    public String getValue() {
        return getFullyQualifiedName().replaceAll( "\\$", "." );
    }
    
    /**
     * @since 1.8
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

    public boolean isArray() {
        return dimensions > 0;
    }

    public int getDimensions() {
        return dimensions;
    }

    public String toString() {
        if (dimensions == 0) return getFullyQualifiedName();
        StringBuffer buff = new StringBuffer(getFullyQualifiedName());
        for (int i = 0; i < dimensions; i++) buff.append("[]");
        String result = buff.toString();
        return result;
    }

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
	            JavaClassContext context = javaClassParent.getJavaClassContext();
	            if (context.getClassLibrary() != null) {
	            	result = context.getClassByName(getFullyQualifiedName());
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


}
