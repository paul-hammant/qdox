package com.thoughtworks.qdox.model;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

/**
 * <strong>Important!! Be sure to add a classloader with the bootstrap classes.</strong>
 * 
 * <p>
 * Normally you can generate your classLibrary like this:<br/>
 * <code>
 * 	ClassLibrary classLibrary = new ClassLibrary();
 *  classLibrary.addDefaultLoader();
 * </code>
 * </p>
 * 
 * <p>
 * If you want full control over the classLoaders you might want to create your library like:<br/> 
 * <code>
 * ClassLibrary classLibrary = new ClassLibrary( ClassLoader.getSystemClassLoader() )
 * </code>  
 * </p>
 * 
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @author Robert Scholte
 */
public class ClassLibrary implements Serializable {

    private final Set classNames = new TreeSet();
    private final Map classNameToClassMap = new HashMap();
    private boolean defaultClassLoadersAdded = false;
    private transient List classLoaders = new ArrayList();
    private List sourceFolders = new ArrayList(); //<File>
    
    /**
     * Remember to add bootstrap classes
     */
    public ClassLibrary() {}

    /**
     * Remember to add bootstrap classes
     */
    public ClassLibrary(ClassLoader loader) {
    	classLoaders.add(loader);
    }
    
    public void add(String className) {
        classNames.add(className);
    }

    public boolean contains(String className) {
        if (classNames.contains(className)) {
            return true;
        }
        else if (getSourceFile(className) != null) {
            return true;
        } else {
            return getClass(className) != null;
        }
    }

    public File getSourceFile( String className )
    {
        for(Iterator iterator = sourceFolders.iterator(); iterator.hasNext();) {
            File sourceFolder = (File) iterator.next();
            String mainClassName = className.split( "\\$" )[0];
            File classFile = new File(sourceFolder, mainClassName.replace( '.', File.separatorChar ) + ".java");
            if ( classFile.exists() && classFile.isFile() ) {
                return classFile;
            }
        }
        return null;
    }

    public Class getClass(String className) {
        Class cachedClass = (Class) classNameToClassMap.get(className);
        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (Iterator iterator = classLoaders.iterator(); iterator.hasNext();) {
                ClassLoader classLoader = (ClassLoader) iterator.next();
                if (classLoader == null) {
                    continue;
                }
                try {
                    Class clazz = classLoader.loadClass(className);
                    if (clazz != null) {
                        classNameToClassMap.put(className, clazz);
                        return clazz;
                    }
                } catch (ClassNotFoundException e) {
                    // continue
                } catch (NoClassDefFoundError e) {
                    // continue
                }
            }
        }
        return null;
    }

    public Collection all() {
        return Collections.unmodifiableCollection(classNames);
    }

    public void addClassLoader(ClassLoader classLoader) {
        classLoaders.add(classLoader);
    }

    public void addDefaultLoader() {
        if (!defaultClassLoadersAdded) {
            classLoaders.add(getClass().getClassLoader());
            classLoaders.add(Thread.currentThread().getContextClassLoader());
        }
        defaultClassLoadersAdded = true;
    }

    public void addSourceFolder( File sourceFolder ) {
        sourceFolders.add( sourceFolder );
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        classLoaders = new ArrayList();
        if (defaultClassLoadersAdded) {
            defaultClassLoadersAdded = false;
            addDefaultLoader();
        }
    }

}
