package com.thoughtworks.qdox.writer;

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
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

public class DefaultModelWriter
    implements ModelWriter
{
    private IndentBuffer buffer = new IndentBuffer();

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.writer.ModelWriter#writeSource(com.thoughtworks.qdox.model.JavaSource)
     */
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

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.writer.ModelWriter#writePackage(com.thoughtworks.qdox.model.JavaPackage)
     */
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

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.writer.ModelWriter#writeClass(com.thoughtworks.qdox.model.JavaClass)
     */
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
            String className = cls.getSuperClass().getValue();
            if ( !"java.lang.Object".equals( className ) && !"java.lang.Enum".equals( className ) )
            {
                buffer.write( " extends " );
                buffer.write( cls.getSuperClass().getCanonicalName() );
            }
        }

        // implements
        if ( cls.getImplements().size() > 0 )
        {
            buffer.write( cls.isInterface() ? " extends " : " implements " );

            for ( ListIterator<Type> iter = cls.getImplements().listIterator(); iter.hasNext(); )
            {
                buffer.write( iter.next().getCanonicalName() );
                if ( iter.hasNext() )
                {
                    buffer.write( ", " );
                }
            }
        }

        buffer.write( " {" );
        buffer.newline();
        buffer.indent();

        // fields
        for ( JavaField javaField : cls.getFields() )
        {
            buffer.newline();
            writeField( javaField );
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

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.writer.ModelWriter#writeField(com.thoughtworks.qdox.model.JavaField)
     */
    public ModelWriter writeField( JavaField field )
    {
        commentHeader( field );

        writeAllModifiers( field.getModifiers() );
        buffer.write( field.getType().getCanonicalName() );
        buffer.write( ' ' );
        buffer.write( field.getName() );
        if ( field.getInitializationExpression() != null && field.getInitializationExpression().length() > 0 )
        {
            buffer.write( " = " );
            buffer.write( field.getInitializationExpression() );
        }
        buffer.write( ';' );
        buffer.newline();
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.writer.ModelWriter#writeConstructor(com.thoughtworks.qdox.model.JavaConstructor)
     */
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
                buffer.write( excIter.next().getCanonicalName() );
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
        buffer.write( "}" );
        buffer.newline();

        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.writer.ModelWriter#writeMethod(com.thoughtworks.qdox.model.JavaMethod)
     */
    public ModelWriter writeMethod( JavaMethod method )
    {
        commentHeader( method );
        writeAccessibilityModifier( method.getModifiers() );
        writeNonAccessibilityModifiers( method.getModifiers() );
        buffer.write( method.getReturns().getCanonicalName() );
        buffer.write( ' ' );
        buffer.write( method.getName() );
        buffer.write( '(' );
        for ( ListIterator<JavaParameter> iter = method.getParameters().listIterator(); iter.hasNext(); )
        {
            JavaParameter parameter = iter.next();
            buffer.write( parameter.getType().getCanonicalName() );
            if ( parameter.isVarArgs() )
            {
                buffer.write( "..." );
            }
            buffer.write( ' ' );
            buffer.write( parameter.getName() );
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
                buffer.write( excIter.next().getCanonicalName() );
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
            buffer.write( "}" );
            buffer.newline();
        }
        else
        {
            buffer.write( ';' );
            buffer.newline();
        }
        return this;
    }

    private void writeNonAccessibilityModifiers( List<String> modifiers )
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

    private void writeAccessibilityModifier( List<String> modifiers )
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

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.writer.ModelWriter#writeAnnotation(com.thoughtworks.qdox.model.JavaAnnotation)
     */
    public ModelWriter writeAnnotation( JavaAnnotation annotation )
    {
        buffer.write( '@' );
        buffer.write( annotation.getType().getFullyQualifiedName() );
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
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.writer.ModelWriter#writeParameter(com.thoughtworks.qdox.model.JavaParameter)
     */
    public ModelWriter writeParameter( JavaParameter parameter )
    {
        commentHeader( parameter );
        buffer.write( parameter.getType().getCanonicalName() );
        if ( parameter.isVarArgs() )
        {
            buffer.write( "..." );
        }
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

    public String toString()
    {
        return buffer.toString();
    }
}
