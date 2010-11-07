package com.thoughtworks.qdox.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.qdox.JavaClassContext;

/**
 * A representation of a package.
 * @since 1.9
 */
public class JavaPackage extends AbstractBaseJavaEntity implements JavaAnnotatedElement {

    private JavaClassContext context;
	private String name;
    private Annotation[] annotations = new Annotation[0];
	private int lineNumber = -1;
	private List classes = new ArrayList();

    public JavaPackage() {
	}

    public JavaPackage(String name) {
		this.name= name;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
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

    /**
     * Returns all the classes found for the package.
     *
     * @return all the classes found for the package
     */
	public JavaClass[] getClasses() {
	    //avoid infinitive  recursion
	    if (this == context.getPackageByName( name )) {
	        return (JavaClass[]) classes.toArray(new JavaClass[classes.size()]);
	    }
	    else {
	        return context.getPackageByName( name ).getClasses();
	    }
	}

    public JavaPackage getParentPackage() {
        String parentName = name.substring(0,name.lastIndexOf("."));
        return (JavaPackage) context.getPackageByName( parentName );
    }

    public JavaPackage[] getSubPackages() {
        String expected = name + ".";
        JavaPackage[] jPackages = context.getPackages();
        List retList = new ArrayList();
        for (int index = 0; index < jPackages.length;index++) {
            String pName = jPackages[index].getName();
            if (pName.startsWith(expected) && !(pName.substring(expected.length()).indexOf(".") > -1)) {
                retList.add(context.getPackageByName( pName ));
            }
        }
        return (JavaPackage[]) retList.toArray(new JavaPackage[retList.size()]);
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
