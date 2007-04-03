package com.thoughtworks.qdox.parser.structs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AnnoDef extends LocatedDef
{
    public String name = "";
    public Map args = new HashMap();
    public AnnoDef tempAnno = null;	// holds an annotation to construct nested values

    public boolean equals(Object obj) {
        AnnoDef annoDef = (AnnoDef) obj;
        return annoDef.name.equals(name) && annoDef.args.equals(args);
    }

    public int hashCode() {
        return name.hashCode() + args.hashCode();
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append('@');
        result.append(name);
        result.append('(');
        if( !args.isEmpty() ) {
            for(Iterator i = args.entrySet().iterator(); i.hasNext();) result.append( i.next() + ",");
            result.deleteCharAt( result.length()-1 );
        }
        result.append(')');
        return result.toString();
    }
}
