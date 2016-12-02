package com.thoughtworks.qdox.parser.structs;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ModuleDef extends LocatedDef
{
    private boolean open;
    
    private String name;

    public ModuleDef( String name )
    {
        super();
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
    
    public static class ExportsDef extends LocatedDef
    {
        private String source;
        
        private Set<String> targets = new LinkedHashSet<String>();

        public ExportsDef( String source )
        {
            this.source = source;
        }
        
        public ExportsDef( String source, Set<String> modifiers )
        {
            this.source = source;
        }

        public String getSource()
        {
            return source;
        }

        public Set<String> getTargets()
        {
            return targets;
        }
    }
    
    public static class OpensDef extends LocatedDef
    {
        private String source;
        
        private Set<String> targets = new LinkedHashSet<String>();

        public OpensDef( String source )
        {
            this.source = source;
        }
        
        public OpensDef( String source, Set<String> modifiers )
        {
            this.source = source;
        }

        public String getSource()
        {
            return source;
        }

        public Set<String> getTargets()
        {
            return targets;
        }
    }
    
    public static class ProvidesDef extends LocatedDef
    {
        private TypeDef service;
        
        private List<TypeDef> implementations = new LinkedList<TypeDef>();

        public ProvidesDef( TypeDef service )
        {
            this.service = service;
        }

        public TypeDef getService()
        {
            return service;
        }

        public List<TypeDef> getImplementations()
        {
            return implementations;
        }
    }
    
    public static class RequiresDef extends LocatedDef
    {
        private String name;
        
        private Set<String> modifiers;
        
        public RequiresDef( String name, Set<String> modifiers )
        {
            super();
            this.name = name;
            this.modifiers = modifiers;
        }

        public String getName()
        {
            return name;
        }
        
        public Set<String> getModifiers()
        {
            return modifiers;
        }
    }
    
    public static class UsesDef extends LocatedDef
    {
        private TypeDef service;

        public UsesDef( TypeDef service )
        {
            this.service = service;
        }

        public TypeDef getService()
        {
            return service;
        } 
    }
}
