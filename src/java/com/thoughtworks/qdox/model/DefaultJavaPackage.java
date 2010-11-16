package com.thoughtworks.qdox.model;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.library.JavaClassContext;

/**
 * A representation of a package.
 * @since 1.9
 */
public class DefaultJavaPackage extends AbstractBaseJavaEntity implements JavaPackage {

    private JavaClassContext context;
	private String name;
	private int lineNumber = -1;
	private List<JavaClass> classes = new LinkedList<JavaClass>();

    public DefaultJavaPackage() {
	}

    public DefaultJavaPackage(String name) {
		this.name= name;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public void setContext( JavaClassContext context )
    {
        this.context = context;
    }

	public void addClass(JavaClass clazz) {
		classes.add(clazz);
	}

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaPackage#getClasses()
     */
	public List<JavaClass> getClasses() {
	    //avoid infinitive  recursion
	    if (this == context.getPackageByName( name )) {
	        return classes;
	    }
	    else {
	        return context.getPackageByName( name ).getClasses();
	    }
	}

    public JavaPackage getParentPackage() {
        String parentName = name.substring(0,name.lastIndexOf("."));
        return context.getPackageByName( parentName );
    }

    public List<JavaPackage> getSubPackages() {
        String expected = name + ".";
        List<JavaPackage> jPackages = context.getPackages();
        List<JavaPackage> retList = new LinkedList<JavaPackage>();
        for (JavaPackage jPackage : jPackages) {
            String pName = jPackage.getName();
            if (pName.startsWith(expected) && !(pName.substring(expected.length()).indexOf(".") > -1)) {
                retList.add(context.getPackageByName( pName ));
            }
        }
        return retList;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof JavaPackage)) return false;

        JavaPackage that = (JavaPackage) o;

        if (!name.equals(that.getName())) return false;

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }
    
    /**
     * @see http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Package.html#toString()
     */
    public String toString() {
    	return "package " + name;
    }
}
