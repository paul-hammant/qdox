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

    private final Set classes = new TreeSet();
    private final Map classNameToClassMap = new HashMap();
    private boolean defaultLoaders = false;
    private transient List classLoaders = new ArrayList();
    private JavaClassCache cache;

    public ClassLibrary(JavaClassCache cache) {
        this.cache = cache;
    }

    public void add(String fullClassName) {
        classes.add(fullClassName);
    }

    public JavaClass getClassByName(String name) {
        return cache.getClassByName(name);
    }

    public boolean contains(String fullClassName) {
        if (classes.contains(fullClassName)) {
            return true;
        } else {
            return getClass(fullClassName) != null;
        }
    }

    public Class getClass(String fullClassName) {
        Class cachedClass = (Class) classNameToClassMap.get(fullClassName);
        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (Iterator iterator = classLoaders.iterator(); iterator.hasNext();) {
                ClassLoader classLoader = (ClassLoader) iterator.next();
                if (classLoader == null) {
                    continue;
                }
                try {
                    Class clazz = classLoader.loadClass(fullClassName);
                    if (clazz != null) {
                        classNameToClassMap.put(fullClassName, clazz);
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
        return Collections.unmodifiableCollection(classes);
    }

    public void addClassLoader(ClassLoader classLoader) {
        classLoaders.add(classLoader);
    }

    public void addDefaultLoader() {
        if (!defaultLoaders) {
            classLoaders.add(getClass().getClassLoader());
            classLoaders.add(Thread.currentThread().getContextClassLoader());
        }
        defaultLoaders = true;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        classLoaders = new ArrayList();
        if (defaultLoaders) {
            defaultLoaders = false;
            addDefaultLoader();
        }
    }

}
