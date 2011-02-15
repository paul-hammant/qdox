package com.thoughtworks.qdox.io;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;


public class DefaultModelWriter implements ModelWriter
{
    private IndentBuffer buffer = new IndentBuffer();

    public ModelWriter writeSource( JavaSource source )
    {
        // package statement
    	writePackage(source.getPackage());

        // import statement
        for (String imprt : source.getImports()) {
            buffer.write("import ");
            buffer.write(imprt);
            buffer.write(';');
            buffer.newline();
        }
        if (source.getImports().size() > 0) {
            buffer.newline();
        }

        // classes
        for(ListIterator<JavaClass> iter = source.getClasses().listIterator();iter.hasNext();) {
            JavaClass clazz = iter.next();
            writeClass( clazz );
            if (iter.hasNext()) { 
                buffer.newline(); 
            }
        }
        return this;
    }
    
    public ModelWriter writePackage(JavaPackage pckg) {
        if (pckg != null) {
        	commentHeader(pckg);
            buffer.write("package ");
            buffer.write(pckg.getName());
            buffer.write(';');
            buffer.newline();
            buffer.newline();
        }
        return this;
    }
    
    public ModelWriter writeClass( JavaClass clazz )
    {
        commentHeader( clazz );
        
        writeAccessibilityModifier(clazz.getModifiers());
        writeNonAccessibilityModifiers(clazz.getModifiers());

        buffer.write(clazz.isEnum() ? "enum " : 
            clazz.isInterface() ? "interface " : 
                clazz.isAnnotation() ? "@interface " : "class ");
        buffer.write(clazz.getName());

        // subclass
        if (clazz.getSuperClass() != null) {
            String className = clazz.getSuperClass().getValue();
            if(!"java.lang.Object".equals(className) && !"java.lang.Enum".equals(className)) {
                buffer.write(" extends ");
                buffer.write(clazz.getSuperClass().getValue());
            }
        }

        // implements
        if (clazz.getImplements().size() > 0) {
            buffer.write(clazz.isInterface() ? " extends " : " implements ");
            
            for (ListIterator<Type> iter = clazz.getImplements().listIterator(); iter.hasNext();) {
                buffer.write(iter.next().getValue());
                if ( iter.hasNext() ) {
                    buffer.write(", ");
                }
            }
        }

        buffer.write(" {");
        buffer.newline();
        buffer.indent();

        // fields
        for (JavaField javaField : clazz.getFields()) {
            buffer.newline();
            writeField(javaField);
        }

        // methods
        for (JavaMethod javaMethod : clazz.getMethods()) {
            buffer.newline();
            writeMethod(javaMethod);
        }

        // inner-classes
        for (JavaClass javaClass : clazz.getNestedClasses()) {
            buffer.newline();
            writeClass(javaClass);
        }

        buffer.deindent();
        buffer.newline();
        buffer.write('}');
        buffer.newline();
        return this;
    }
    
    public ModelWriter writeField(JavaField field) {
        commentHeader( field );
        
        writeAllModifiers(field.getModifiers());
        buffer.write(field.getType().toString());
        buffer.write(' ');
        buffer.write(field.getName());
        if(field.getInitializationExpression() != null && field.getInitializationExpression().length() > 0){
          buffer.write(" = ");
          buffer.write(field.getInitializationExpression());
        }
        buffer.write(';');
        buffer.newline();
        return this;
    }
    
    public ModelWriter writeMethod( JavaMethod method )
    {
        commentHeader( method );
        writeMethodBody( method, true, true, true );
        return this;
    }

    private void writeMethodBody( JavaMethod method, boolean withModifiers, boolean isDeclaration, boolean isPrettyPrint )
    {
        if ( withModifiers )
        {
            writeAccessibilityModifier( method.getModifiers() );
            writeNonAccessibilityModifiers( method.getModifiers() );
        }

        if ( !method.isConstructor() )
        {
            if ( isDeclaration )
            {
                buffer.write( method.getReturns().toString() );
                buffer.write( ' ' );
            }
        }

        buffer.write( method.getName() );
        buffer.write( '(' );
        for(ListIterator<JavaParameter> iter = method.getParameters().listIterator(); iter.hasNext();)
        {
            JavaParameter parameter = iter.next();
            if ( isDeclaration )
            {
                buffer.write( parameter.getType().toString() );
                if ( parameter.isVarArgs() )
                {
                    buffer.write( "..." );
                }
                buffer.write( ' ' );
            }
            buffer.write( parameter.getName() );
            if ( iter.hasNext() ) {
                buffer.write( ", " );
            }

        }
        buffer.write( ')' );
        if ( isDeclaration )
        {
            if (method.getExceptions().size() > 0) {
                buffer.write(" throws ");
                for (Iterator<Type> excIter = method.getExceptions().iterator();excIter.hasNext();) {
                    buffer.write(excIter.next().getValue());
                    if(excIter.hasNext()) {
                        buffer.write(", ");
                    }
                }
            }
        }
        if ( isPrettyPrint )
        {
            if ( method.getSourceCode() != null && method.getSourceCode().length() > 0 )
            {
                buffer.write( " {" );
                buffer.newline();
                buffer.write( method.getSourceCode() );
                buffer.write( "}" );
                buffer.newline();
            }
            else
            {
                buffer.write( ';' );
                buffer.newline();
            }
        }
    }
    
    private void writeNonAccessibilityModifiers(List<String> modifiers) {
        for ( String modifier: modifiers ) {
            if (!modifier.startsWith("p")) {
                buffer.write(modifier);
                buffer.write(' ');
            }
        }
    }

    private void writeAccessibilityModifier(List<String> modifiers) {
        for ( String modifier: modifiers ) {
            if (modifier.startsWith("p")) {
                buffer.write(modifier);
                buffer.write(' ');
            }
        }
    }

    private void writeAllModifiers(List<String> modifiers) {
        for ( String modifier: modifiers ) {
            buffer.write(modifier);
            buffer.write(' ');
        }
    }
    
    public ModelWriter writeParameter(JavaParameter parameter) {
    	commentHeader(parameter);
    	buffer.write( parameter.getType().toString() );
        if ( parameter.isVarArgs() )
        {
            buffer.write( "..." );
        }
    	return this;
    }
    
    private void commentHeader(JavaAnnotatedElement entity) {
        if (entity.getComment() != null || (entity.getTags().size() > 0)) {
            buffer.write("/**");
            buffer.newline();

            if (entity.getComment() != null && entity.getComment().length() > 0) {
                buffer.write(" * ");
                
                buffer.write(entity.getComment().replaceAll("\n", "\n * "));
                
                buffer.newline();
            }

            if (entity.getTags().size() > 0) {
                if (entity.getComment() != null && entity.getComment().length() > 0) {
                    buffer.write(" *");
                    buffer.newline();
                }
                for (DocletTag docletTag : entity.getTags()) {
                    buffer.write(" * @");
                    buffer.write(docletTag.getName());
                    if (docletTag.getValue().length() > 0) {
                        buffer.write(' ');
                        buffer.write(docletTag.getValue());
                    }
                    buffer.newline();
                }
            }

            buffer.write(" */");
            buffer.newline();
        }
        if(entity.getAnnotations() != null) {
        	for(Annotation annotation : entity.getAnnotations()) {
        		buffer.write(annotation.toString());
        		buffer.newline();
        	}
        }
    }
    public String toString()
    {
        return buffer.toString();
    }
}
