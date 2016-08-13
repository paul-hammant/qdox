package com.thoughtworks.qdox.parser.structs;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ModuleDef extends LocatedDef
{
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
    
    public static class ExportsDef extends LocatedDef
    {
        private String source;
        
        private Set<String> modifiers;
        
        private Set<String> targets = new LinkedHashSet<String>();

        public ExportsDef( String source )
        {
            this.source = source;
            this.modifiers = Collections.emptySet();
        }
        
        public ExportsDef( String source, Set<String> modifiers )
        {
            this.source = source;
            this.modifiers = modifiers;
        }

        public String getSource()
        {
            return source;
        }

        public Set<String> getModifiers()
        {
            return modifiers;
        }

        public Set<String> getTargets()
        {
            return targets;
        }
    }
    
    public static class ProvidesDef extends LocatedDef
    {
        private TypeDef service;
        
        private TypeDef implementation;

        public ProvidesDef( TypeDef service, TypeDef implementation )
        {
            this.service = service;
            this.implementation = implementation;
        }

        public TypeDef getService()
        {
            return service;
        }

        public TypeDef getImplementation()
        {
            return implementation;
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
