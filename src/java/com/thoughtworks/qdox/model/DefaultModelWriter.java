package com.thoughtworks.qdox.model;


public class DefaultModelWriter implements ModelWriter
{
    private IndentBuffer buffer = new IndentBuffer();

    public ModelWriter writeSource( JavaSource source )
    {
        // package statement
        if (source.getPackage() != null) {
            buffer.write("package ");
            buffer.write(source.getPackageName());
            buffer.write(';');
            buffer.newline();
            buffer.newline();
        }

        // import statement
        for (int i = 0; i < source.getImports().size(); i++) {
            buffer.write("import ");
            buffer.write(source.getImports().get(i));
            buffer.write(';');
            buffer.newline();
        }
        if (source.getImports().size() > 0) {
            buffer.newline();
        }

        // classes
        for (int i = 0; i < source.getClasses().size(); i++) {
            if (i > 0) buffer.newline();
            writeClass( source.getClasses().get(i) );
        }
        return this;
    }
    
    public ModelWriter writeClass( JavaClass clazz )
    {
        commentHeader( clazz );
        
        writeAccessibilityModifier(clazz);
        writeNonAccessibilityModifiers(clazz);

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
        if (clazz.getImplements().length > 0) {
            buffer.write(clazz.isInterface() ? " extends " : " implements ");

            for (int i = 0; i < clazz.getImplements().length; i++) {
                if (i > 0) {
                    buffer.write(", ");
                }

                buffer.write(clazz.getImplements()[i].getValue());
            }
        }

        buffer.write(" {");
        buffer.newline();
        buffer.indent();

        // fields
        
        for (int index = 0; index < clazz.getFields().length; index++) {
            JavaField javaField = clazz.getFields()[index];

            buffer.newline();
            writeField(javaField);
        }

        // methods
        for (int index = 0; index < clazz.getMethods().length; index++) {
            JavaMethod javaMethod = clazz.getMethods()[index];

            buffer.newline();
            writeMethod(javaMethod);
        }

        // inner-classes
        for (int index = 0; index < clazz.getNestedClasses().length; index++) {
            JavaClass javaClass = clazz.getNestedClasses()[index];

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
        
        writeAllModifiers(field);
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
            writeAccessibilityModifier( method );
            writeNonAccessibilityModifiers( method );
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
        for ( int i = 0; i < method.getParameters().length; i++ )
        {
            JavaParameter parameter = method.getParameters()[i];
            if ( i > 0 )
                buffer.write( ", " );
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
        }
        buffer.write( ')' );
        if ( isDeclaration )
        {
            if ( method.getExceptions().length > 0 )
            {
                buffer.write( " throws " );
                for ( int i = 0; i < method.getExceptions().length; i++ )
                {
                    if ( i > 0 )
                        buffer.write( ", " );
                    buffer.write( method.getExceptions()[i].getValue() );
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
    
    private void writeNonAccessibilityModifiers(JavaMember member) {
        // modifiers (anything else)
        for ( String modifier: member.getModifiers() ) {
            if (!modifier.startsWith("p")) {
                buffer.write(modifier);
                buffer.write(' ');
            }
        }
    }

    private void writeAccessibilityModifier(JavaMember member) {
        for ( String modifier: member.getModifiers() ) {
            if (modifier.startsWith("p")) {
                buffer.write(modifier);
                buffer.write(' ');
            }
        }
    }

    private void writeAllModifiers(JavaMember member) {
        for ( String modifier: member.getModifiers() ) {
            buffer.write(modifier);
            buffer.write(' ');
        }
    }
    
    private void commentHeader(JavaModel entity) {
        if (entity.getComment() == null && (entity.getTags().length == 0)) {
            return;
        } else {
            buffer.write("/**");
            buffer.newline();

            if (entity.getComment() != null && entity.getComment().length() > 0) {
                buffer.write(" * ");
                
                buffer.write(entity.getComment().replaceAll("\n", "\n * "));
                
                buffer.newline();
            }

            if (entity.getTags().length > 0) {
                if (entity.getComment() != null && entity.getComment().length() > 0) {
                    buffer.write(" *");
                    buffer.newline();
                }
                for (int i = 0; i < entity.getTags().length; i++) {
                    DocletTag docletTag = entity.getTags()[i];
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
    }
    public String toString()
    {
        return buffer.toString();
    }
}
