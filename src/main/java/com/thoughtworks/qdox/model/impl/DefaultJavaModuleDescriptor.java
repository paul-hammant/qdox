package com.thoughtworks.qdox.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.thoughtworks.qdox.model.JavaModuleDescriptor;

public class DefaultJavaModuleDescriptor implements JavaModuleDescriptor
{
   
    private Collection<DefaultJavaRequires> requires = new ArrayList<DefaultJavaModuleDescriptor.DefaultJavaRequires>();
    
    public void addRequires( DefaultJavaRequires requires )
    {
        this.requires.add( requires );
    }

    public Collection<DefaultJavaRequires> getRequires()
    {
        return Collections.unmodifiableCollection( requires );
    }
    
    public static class DefaultJavaExports extends AbstractJavaModel implements JavaModuleDescriptor.JavaExports
    {
        private String source;

        private Collection<String> modifiers;
        
        private Collection<String> targets;
        
        public String getSource()
        {
            return source;
        }

        public Collection<String> getTargets()
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

        public boolean isDynamic()
        {
            return getModifiers().contains( "dynamic" );
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
            return getModelWriter().writeModuleExports( this ).toString();
        }
    }
    
    public static class DefaultJavaProvides extends AbstractJavaModel implements JavaModuleDescriptor.JavaProvides
    {
        private String service;
        
        private String provider;

        public DefaultJavaProvides( String service, String provider )
        {
            super();
            this.service = service;
            this.provider = provider;
        }
        
        public String getService()
        {
            return service;
        }
        
        public String getProvider()
        {
            return provider;
        }
        
        public String getCodeBlock()
        {
            return getModelWriter().writeModuleProvides( this ).toString();
        }
    }
    
    public static class DefaultJavaRequires extends AbstractJavaModel implements JavaModuleDescriptor.JavaRequires 
    {
        private String name;
        
        private Collection<String> modifiers;
    
        public DefaultJavaRequires( String name, Collection<String> modifiers )
        {
            this.name = name;
            this.modifiers = modifiers;
        }
    
        public String getName()
        {
            return name;
        }
    
        public boolean isPublic()
        {
            return getModifiers().contains( "public" );
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
        private String name;

        public DefaultJavaUses( String name )
        {
            this.name = name;
        }
        
        public String getName()
        {
            return name;
        }

        public String getCodeBlock()
        {
            return getModelWriter().writeModuleUses( this ).toString();
        }
    }
}
