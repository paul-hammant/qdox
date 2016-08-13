package com.thoughtworks.qdox.model;

import java.util.Collection;

public interface JavaModuleDescriptor
{
     Collection<JavaExports> getExports();

    Collection<JavaProvides> getProvides();

    Collection<JavaRequires> getRequires();

    Collection<JavaUses> getUses();

    /**
     * Represents the following ModuleStatement:<br>
     * <code>
     * exports [dynamic] SOURCE [to TARGETS{, TARGET}];
     * </code>
     * where SOURCE matches a PackageName and TARGET matches a ModuleName
     *  
     * @author Robert Scholte
     *
     */
    static interface JavaExports {

        JavaPackage getSource();
        
        Collection<JavaModule> getTargets();
        
        boolean isDynamic();

        Collection<String> getModifiers();
    }

    /**
     * Represents the following ModuleStatement:<br>
     * <code>
     * provides SERVICE with PROVIDER;
     * </code>
     * where SERVICE matches a TypeName and TARGET matches a TypeName
     * 
     * @author Robert Scholte
     *
     */
    static interface JavaProvides {
        JavaClass getService();
        
        JavaClass getProvider();
    }
    
    /**
     * Represents the following ModuleStatement:<br>
     * <code>
     * requires [public] [static] NAME;
     * </code>
     * Where NAME matches a ModuleName
     * 
     * @author Robert Scholte
     *
     */
    static interface JavaRequires {
    
        JavaModule getModule();
        
        boolean isPublic();
        
        boolean isStatic();

        Collection<String> getModifiers();
    }
    
    /**
     * Represents the following ModuleStatement:<br>
     * <code>
     * uses NAME;
     * </code>
     * Where NAME matches a TypeName
     * 
     * @author Robert Scholte
     *
     */
    static interface JavaUses 
    {
        JavaClass getService();
    }
    
    
}
