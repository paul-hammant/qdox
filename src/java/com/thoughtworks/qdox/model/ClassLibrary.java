package com.thoughtworks.qdox.model;

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
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 */
public class ClassLibrary implements Serializable {

    private final Set classNames = new TreeSet();
    private final Map classNameToClassMap = new HashMap();
    private boolean defaultClassLoadersAdded = false;
    private transient List classLoaders = new ArrayList();
    
    public ClassLibrary() {}
    
    public void add(String className) {
        classNames.add(className);
    }

    public boolean contains(String className) {
        if (classNames.contains(className)) {
            return true;
        } else {
            return getClass(className) != null;
        }
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

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        classLoaders = new ArrayList();
        if (defaultClassLoadersAdded) {
            defaultClassLoadersAdded = false;
            addDefaultLoader();
        }
    }

}
