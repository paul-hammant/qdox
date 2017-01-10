package com.thoughtworks.qdox.writer.impl;

import java.util.Collection;

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
import java.util.Map;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaInitializer;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaModuleDescriptor;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaExports;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaOpens;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaProvides;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaRequires;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaUses;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.Expression;
import com.thoughtworks.qdox.writer.ModelWriter;

public class DefaultModelWriter
    implements ModelWriter
{
    private IndentBuffer buffer = new IndentBuffer();
    
    /**
     * All information is written to this buffer.
     * When extending this class you should write to this buffer
     * 
     * @return the buffer
     */
    protected final IndentBuffer getBuffer()
    {
        return buffer;
    }
    
    /** {@inheritDoc} */
    public ModelWriter writeSource( JavaSource source )
    {
        // package statement
        writePackage( source.getPackage() );

        // import statement
        for ( String imprt : source.getImports() )
        {
            buffer.write( "import " );
            buffer.write( imprt );
            buffer.write( ';' );
            buffer.newline();
        }
        if ( source.getImports().size() > 0 )
        {
            buffer.newline();
        }

        // classes
        for ( ListIterator<JavaClass> iter = source.getClasses().listIterator(); iter.hasNext(); )
        {
            JavaClass cls = iter.next();
            writeClass( cls );
            if ( iter.hasNext() )
            {
                buffer.newline();
            }
        }
        return this;
    }

    /** {@inheritDoc} */
    public ModelWriter writePackage( JavaPackage pckg )
    {
        if ( pckg != null )
        {
            commentHeader( pckg );
            buffer.write( "package " );
            buffer.write( pckg.getName() );
            buffer.write( ';' );
            buffer.newline();
            buffer.newline();
        }
        return this;
    }

    /** {@inheritDoc} */
    public ModelWriter writeClass( JavaClass cls )
    {
        commentHeader( cls );

        writeAccessibilityModifier( cls.getModifiers() );
        writeNonAccessibilityModifiers( cls.getModifiers() );

        buffer.write( cls.isEnum() ? "enum " : cls.isInterface() ? "interface " : cls.isAnnotation() ? "@interface "
                        : "class " );
        buffer.write( cls.getName() );

        // subclass
        if ( cls.getSuperClass() != null )
        {
            String className = cls.getSuperClass().getFullyQualifiedName();
            if ( !"java.lang.Object".equals( className ) && !"java.lang.Enum".equals( className ) )
            {
                buffer.write( " extends " );
                buffer.write( cls.getSuperClass().getGenericCanonicalName() );
            }
        }

        // implements
        if ( cls.getImplements().size() > 0 )
        {
            buffer.write( cls.isInterface() ? " extends " : " implements " );

            for ( ListIterator<JavaType> iter = cls.getImplements().listIterator(); iter.hasNext(); )
            {
                buffer.write( iter.next().getGenericCanonicalName() );
                if ( iter.hasNext() )
                {
                    buffer.write( ", " );
                }
            }
        }

        return writeClassBody( cls );
    }

    private ModelWriter writeClassBody( JavaClass cls )
    {
        buffer.write( " {" );
        buffer.newline();
        buffer.indent();

        // fields
        for ( JavaField javaField : cls.getFields() )
        {
            buffer.newline();
            writeField( javaField );
        }

        // constructors
        for ( JavaConstructor javaConstructor : cls.getConstructors() )
        {
            buffer.newline();
            writeConstructor( javaConstructor );
        }
        
        // methods
        for ( JavaMethod javaMethod : cls.getMethods() )
        {
            buffer.newline();
            writeMethod( javaMethod );
        }

        // inner-classes
        for ( JavaClass innerCls : cls.getNestedClasses() )
        {
            buffer.newline();
            writeClass( innerCls );
        }

        buffer.deindent();
        buffer.newline();
        buffer.write( '}' );
        buffer.newline();
        return this;
    }
    
    /** {@inheritDoc} */
    public ModelWriter writeInitializer( JavaInitializer init )
    {
        if ( init.isStatic() )
        {
            buffer.write( "static " );
        }
        buffer.write( '{' );
        buffer.newline();
        buffer.indent();

        buffer.write( init.getBlockContent() );
        
        buffer.deindent();
        buffer.newline();
        buffer.write( '}' );
        buffer.newline();
        return this;
    }

    /** {@inheritDoc} */
    public ModelWriter writeField( JavaField field )
    {
        commentHeader( field );

        writeAllModifiers( field.getModifiers() );
        if ( !field.isEnumConstant() )
        {
            buffer.write( field.getType().getGenericCanonicalName() );
            buffer.write( ' ' );
        }
        buffer.write( field.getName() );
        
        if ( field.isEnumConstant() )
        {
            if ( field.getEnumConstantArguments() != null && !field.getEnumConstantArguments().isEmpty() )
            {
                buffer.write( "( " );
                for( Iterator<Expression> iter = field.getEnumConstantArguments().listIterator(); iter.hasNext(); )
                {
                    buffer.write( iter.next().getParameterValue().toString() );
                    if( iter.hasNext() )
                    {
                        buffer.write( ", " );
                    }
                }
                buffer.write( " )" );
            }
            if ( field.getEnumConstantClass() != null )
            {
                writeClassBody( field.getEnumConstantClass() );
            }
        }
        else
        {
            if ( field.getInitializationExpression() != null && field.getInitializationExpression().length() > 0 )
            {
                {
                    buffer.write( " = " );
                }
                buffer.write( field.getInitializationExpression() );
            }
        }
        buffer.write( ';' );
        buffer.newline();
        return this;
    }

    /** {@inheritDoc} */
    public ModelWriter writeConstructor( JavaConstructor constructor )
    {
        commentHeader( constructor );
        writeAllModifiers( constructor.getModifiers() );

        buffer.write( constructor.getName() );
        buffer.write( '(' );
        for ( ListIterator<JavaParameter> iter = constructor.getParameters().listIterator(); iter.hasNext(); )
        {
            writeParameter( iter.next() );
            if ( iter.hasNext() )
            {
                buffer.write( ", " );
            }
        }
        buffer.write( ')' );

        if ( constructor.getExceptions().size() > 0 )
        {
            buffer.write( " throws " );
            for ( Iterator<JavaClass> excIter = constructor.getExceptions().iterator(); excIter.hasNext(); )
            {
                buffer.write( excIter.next().getGenericCanonicalName() );
                if ( excIter.hasNext() )
                {
                    buffer.write( ", " );
                }
            }
        }

        buffer.write( " {" );
        buffer.newline();
        if ( constructor.getSourceCode() != null )
        {
            buffer.write( constructor.getSourceCode() );
        }
        buffer.write( '}' );
        buffer.newline();

        return this;
    }

    /** {@inheritDoc} */
    public ModelWriter writeMethod( JavaMethod method )
    {
        commentHeader( method );
        writeAccessibilityModifier( method.getModifiers() );
        writeNonAccessibilityModifiers( method.getModifiers() );
        buffer.write( method.getReturnType().getGenericCanonicalName() );
        buffer.write( ' ' );
        buffer.write( method.getName() );
        buffer.write( '(' );
        for ( ListIterator<JavaParameter> iter = method.getParameters().listIterator(); iter.hasNext(); )
        {
            writeParameter( iter.next() );
            if ( iter.hasNext() )
            {
                buffer.write( ", " );
            }

        }
        buffer.write( ')' );
        if ( method.getExceptions().size() > 0 )
        {
            buffer.write( " throws " );
            for ( Iterator<JavaClass> excIter = method.getExceptions().iterator(); excIter.hasNext(); )
            {
                buffer.write( excIter.next().getGenericCanonicalName() );
                if ( excIter.hasNext() )
                {
                    buffer.write( ", " );
                }
            }
        }
        if ( method.getSourceCode() != null && method.getSourceCode().length() > 0 )
        {
            buffer.write( " {" );
            buffer.newline();
            buffer.write( method.getSourceCode() );
            buffer.write( '}' );
            buffer.newline();
        }
        else
        {
            buffer.write( ';' );
            buffer.newline();
        }
        return this;
    }

    private void writeNonAccessibilityModifiers( Collection<String> modifiers )
    {
        for ( String modifier : modifiers )
        {
            if ( !modifier.startsWith( "p" ) )
            {
                buffer.write( modifier );
                buffer.write( ' ' );
            }
        }
    }

    private void writeAccessibilityModifier( Collection<String> modifiers )
    {
        for ( String modifier : modifiers )
        {
            if ( modifier.startsWith( "p" ) )
            {
                buffer.write( modifier );
                buffer.write( ' ' );
            }
        }
    }

    private void writeAllModifiers( List<String> modifiers )
    {
        for ( String modifier : modifiers )
        {
            buffer.write( modifier );
            buffer.write( ' ' );
        }
    }

    /** {@inheritDoc} */
    public ModelWriter writeAnnotation( JavaAnnotation annotation )
    {
        buffer.write( '@' );
        buffer.write( annotation.getType().getGenericCanonicalName() );
        if ( !annotation.getPropertyMap().isEmpty() )
        {
            buffer.indent();
            buffer.write( '(' );
            Iterator<Map.Entry<String, AnnotationValue>> iterator = annotation.getPropertyMap().entrySet().iterator();
            while ( iterator.hasNext() )
            {
                Map.Entry<String, AnnotationValue> entry = iterator.next();
                buffer.write( entry.getKey() );
                buffer.write( '=' );
                buffer.write( entry.getValue().toString() );
                if ( iterator.hasNext() )
                {
                    buffer.write( ',' );
                    buffer.newline();
                }
            }
            buffer.write( ')' );
            buffer.deindent();
        }
        buffer.newline();
        return this;
    }

    /** {@inheritDoc} */
    public ModelWriter writeParameter( JavaParameter parameter )
    {
        commentHeader( parameter );
        buffer.write( parameter.getGenericCanonicalName() );
        if ( parameter.isVarArgs() )
        {
            buffer.write( "..." );
        }
        buffer.write( ' ' );
        buffer.write(  parameter.getName() );
        return this;
    }

    protected void commentHeader( JavaAnnotatedElement entity )
    {
        if ( entity.getComment() != null || ( entity.getTags().size() > 0 ) )
        {
            buffer.write( "/**" );
            buffer.newline();

            if ( entity.getComment() != null && entity.getComment().length() > 0 )
            {
                buffer.write( " * " );

                buffer.write( entity.getComment().replaceAll( "\n", "\n * " ) );

                buffer.newline();
            }

            if ( entity.getTags().size() > 0 )
            {
                if ( entity.getComment() != null && entity.getComment().length() > 0 )
                {
                    buffer.write( " *" );
                    buffer.newline();
                }
                for ( DocletTag docletTag : entity.getTags() )
                {
                    buffer.write( " * @" );
                    buffer.write( docletTag.getName() );
                    if ( docletTag.getValue().length() > 0 )
                    {
                        buffer.write( ' ' );
                        buffer.write( docletTag.getValue() );
                    }
                    buffer.newline();
                }
            }

            buffer.write( " */" );
            buffer.newline();
        }
        if ( entity.getAnnotations() != null )
        {
            for ( JavaAnnotation annotation : entity.getAnnotations() )
            {
                writeAnnotation( annotation );
            }
        }
    }

    /** {@inheritDoc} */
    public ModelWriter writeModuleDescriptor( JavaModuleDescriptor descriptor )
    {
        if( descriptor.isOpen() ) {
            buffer.write( "open " );
        }
        buffer.write( "module " + descriptor.getName() +  " {");
        buffer.newline();
        buffer.indent();

        for ( JavaRequires requires : descriptor.getRequires() )
        {
            buffer.newline();
            writeModuleRequires( requires );
        }

        for ( JavaExports exports : descriptor.getExports() )
        {
            buffer.newline();
            writeModuleExports( exports );
        }
        
        for ( JavaOpens opens : descriptor.getOpens() )
        {
            buffer.newline();
            writeModuleOpens( opens );
        }

        for ( JavaProvides provides : descriptor.getProvides() )
        {
            buffer.newline();
            writeModuleProvides( provides );
        }

        for ( JavaUses uses : descriptor.getUses() )
        {
            buffer.newline();
            writeModuleUses( uses );
        }

        buffer.newline();
        buffer.deindent();
        buffer.write( '}' );
        buffer.newline();
        return this;
    }

    /** {@inheritDoc} */
    public ModelWriter writeModuleExports( JavaExports exports )
    {
        buffer.write( "exports " );
        buffer.write( exports.getSource().getName() );
        if( !exports.getTargets().isEmpty() )
        {
            buffer.write( " to " );
            Iterator<JavaModule> targets = exports.getTargets().iterator();
            while( targets.hasNext() )
            {
                JavaModule target = targets.next();
                buffer.write( target.getName() );
                if( targets.hasNext() )
                {
                    buffer.write( ", " );
                }
            }
        }
        buffer.write( ';' );
        buffer.newline();
        return this;
    }
    
    /** {@inheritDoc} */
    public ModelWriter writeModuleOpens( JavaOpens opens )
    {
        buffer.write( "opens " );
        buffer.write( opens.getSource().getName() );
        if( !opens.getTargets().isEmpty() )
        {
            buffer.write( " to " );
            Iterator<JavaModule> targets = opens.getTargets().iterator();
            while( targets.hasNext() )
            {
                JavaModule target = targets.next();
                buffer.write( target.getName() );
                if( targets.hasNext() )
                {
                    buffer.write( ", " );
                }
            }
        }
        buffer.write( ';' );
        buffer.newline();
        return this;
    }

    /** {@inheritDoc} */
    public ModelWriter writeModuleProvides( JavaProvides provides )
    {
        buffer.write( "provides " );
        buffer.write( provides.getService().getName() );
        buffer.write( " with " );
        Iterator<JavaClass> providers = provides.getProviders().iterator();
        while( providers.hasNext() )
        {
            JavaClass provider = providers.next();
            buffer.write( provider.getName() );
            if( providers.hasNext() )
            {
                buffer.write( ", " );
            }
        }
        buffer.write( ';' );
        buffer.newline();
        return null;
    }
    
    /** {@inheritDoc} */
    public ModelWriter writeModuleRequires( JavaRequires requires )
    {
        buffer.write( "requires " );
        writeAccessibilityModifier( requires.getModifiers() );
        writeNonAccessibilityModifiers( requires.getModifiers() );
        buffer.write( requires.getModule().getName() );
        buffer.write( ';' );
        buffer.newline();
        return this;
    }
    
    /** {@inheritDoc} */
    public ModelWriter writeModuleUses( JavaUses uses )
    {
        buffer.write( "uses " );
        buffer.write( uses.getService().getName() );
        buffer.write( ';' );
        buffer.newline();
        return this;
    }

    @Override
    public String toString()
    {
        return buffer.toString();
    }
}