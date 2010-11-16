package com.thoughtworks.qdox.model;

import java.util.Iterator;
import java.util.ListIterator;


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
        ListIterator<JavaClass> iter = source.getClasses().listIterator();
        while(iter.hasNext()) {
            if (iter.hasPrevious()) { 
                buffer.newline(); 
            }
            JavaClass clazz = iter.next();
            writeClass( clazz );
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
        if (clazz.getImplements().size() > 0) {
            buffer.write(clazz.isInterface() ? " extends " : " implements ");

            for (int i = 0; i < clazz.getImplements().size(); i++) {
                if (i > 0) {
                    buffer.write(", ");
                }

                buffer.write(clazz.getImplements().get(i).getValue());
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
            if (method.getExceptions().size() > 0) {
                buffer.write(" throws ");
                Iterator<Type> excIter = method.getExceptions().iterator();
                while (excIter.hasNext()) {
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
        if (entity.getComment() == null && (entity.getTags().size() == 0)) {
            return;
        } else {
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
    }
    public String toString()
    {
        return buffer.toString();
    }
}
