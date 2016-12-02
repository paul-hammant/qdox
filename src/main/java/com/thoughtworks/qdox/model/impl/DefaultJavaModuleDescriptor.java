package com.thoughtworks.qdox.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaModuleDescriptor;
import com.thoughtworks.qdox.model.JavaPackage;

public class DefaultJavaModuleDescriptor implements JavaModuleDescriptor
{
    private String name; 

    private boolean open;
    
    private Collection<DefaultJavaRequires> requires = new ArrayList<DefaultJavaRequires>();

    private Collection<DefaultJavaExports> exports = new ArrayList<DefaultJavaExports>();

    private Collection<DefaultJavaOpens> opens = new ArrayList<DefaultJavaOpens>();

    private Collection<DefaultJavaUses> uses = new ArrayList<DefaultJavaUses>();

    private Collection<DefaultJavaProvides> provides = new ArrayList<DefaultJavaProvides>();

    public DefaultJavaModuleDescriptor( String name )
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }

    public void setOpen( boolean open )
    {
        this.open = open;
    }
    
    public boolean isOpen()
    {
        return open;
    }
    
    public void addExports( DefaultJavaExports exports )
    {
        this.exports.add( exports );
    }

    public Collection<JavaExports> getExports()
    {
        return Collections.<JavaExports>unmodifiableCollection( exports );
    } 

    public void addOpens( DefaultJavaOpens opens )
    {
        this.opens.add( opens );
    }
    
    public Collection<JavaOpens> getOpens()
    {
        return Collections.<JavaOpens>unmodifiableCollection( opens );
    } 
    
    public void addRequires( DefaultJavaRequires requires )
    {
        this.requires.add( requires );
    }

    public Collection<JavaRequires> getRequires()
    {
        return Collections.<JavaRequires>unmodifiableCollection( requires );
    }

    public void addProvides( DefaultJavaProvides provides )
    {
        this.provides.add( provides);
    }

    public Collection<JavaProvides> getProvides()
    {
        return Collections.<JavaProvides>unmodifiableCollection( provides );
    }

    public void addUses( DefaultJavaUses uses)
    {
        this.uses.add( uses );
    }

    public Collection<JavaUses> getUses()
    {
        return Collections.<JavaUses>unmodifiableCollection( uses );
    } 

    public static class DefaultJavaExports extends AbstractJavaModel implements JavaModuleDescriptor.JavaExports
    {
        private JavaPackage source;

        private Collection<JavaModule> targets;

        public DefaultJavaExports( JavaPackage source, Collection<JavaModule> targets )
        {
            this.source = source;
            this.targets = targets;
        }

        public JavaPackage getSource()
        {
            return source;
        }

        public Collection<JavaModule> getTargets()
        {
            if( targets == null )
            {
                return Collections.emptyList();
            }
            else
            {
                return targets;
            }
        }

        public String getCodeBlock()
        {
            return getModelWriter().writeModuleExports( this ).toString();
        }
    }
    
    public static class DefaultJavaOpens extends AbstractJavaModel implements JavaModuleDescriptor.JavaOpens
    {
        private JavaPackage source;

        private Collection<JavaModule> targets;

        public DefaultJavaOpens( JavaPackage source, Collection<JavaModule> targets )
        {
            this.source = source;
            this.targets = targets;
        }

        public JavaPackage getSource()
        {
            return source;
        }

        public Collection<JavaModule> getTargets()
        {
            if( targets == null )
            {
                return Collections.emptyList();
            }
            else
            {
                return targets;
            }
        }

        public String getCodeBlock()
        {
            return getModelWriter().writeModuleOpens( this ).toString();
        }
    }
    
    public static class DefaultJavaProvides extends AbstractJavaModel implements JavaModuleDescriptor.JavaProvides
    {
        private JavaClass service;
        
        private List<JavaClass> implementations;

        public DefaultJavaProvides( JavaClass service, List<JavaClass> implementations )
        {
            super();
            this.service = service;
            this.implementations = implementations;
        }
        
        public JavaClass getService()
        {
            return service;
        }
        
        public List<JavaClass> getImplementations()
        {
            return implementations;
        }
        
        public String getCodeBlock()
        {
            return getModelWriter().writeModuleProvides( this ).toString();
        }
    }
    
    public static class DefaultJavaRequires extends AbstractJavaModel implements JavaModuleDescriptor.JavaRequires 
    {
        private JavaModule module;
        
        private Collection<String> modifiers;
    
        public DefaultJavaRequires( JavaModule module, Collection<String> modifiers )
        {
            this.module = module;
            this.modifiers = modifiers;
        }
    
        public JavaModule getModule()
        {
            return module;
        }
    
        public boolean isTransitive()
        {
            return getModifiers().contains( "transitive" );
        }
    
        public boolean isStatic()
        {
            return getModifiers().contains( "static" );
        }
    
        public Collection<String> getModifiers()
        {
            if( modifiers == null )
            {
                return Collections.emptyList();
            }
            else
            {
                return modifiers;
            }
        }
        
        public String getCodeBlock()
        {
            return getModelWriter().writeModuleRequires( this ).toString();
        }
    }
    
    public static class DefaultJavaUses extends AbstractJavaModel implements JavaModuleDescriptor.JavaUses
    {
        private JavaClass service;

        public DefaultJavaUses( JavaClass service )
        {
            this.service = service;
        }

        public JavaClass getService()
        {
            return service;
        }

        public String getCodeBlock()
        {
            return getModelWriter().writeModuleUses( this ).toString();
        }
    }
}
