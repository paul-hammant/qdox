package com.thoughtworks.qdox.builder.impl;

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

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.builder.TypeAssembler;
import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaExecutable;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.expression.Expression;
import com.thoughtworks.qdox.model.impl.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.impl.DefaultJavaClass;
import com.thoughtworks.qdox.model.impl.DefaultJavaConstructor;
import com.thoughtworks.qdox.model.impl.DefaultJavaField;
import com.thoughtworks.qdox.model.impl.DefaultJavaInitializer;
import com.thoughtworks.qdox.model.impl.DefaultJavaMethod;
import com.thoughtworks.qdox.model.impl.DefaultJavaModule;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaExports;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaOpens;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaProvides;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaRequires;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaUses;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameter;
import com.thoughtworks.qdox.model.impl.DefaultJavaSource;
import com.thoughtworks.qdox.model.impl.DefaultJavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaTypeVariable;
import com.thoughtworks.qdox.parser.expression.ExpressionDef;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.InitDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.ExportsDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.OpensDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.ProvidesDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.RequiresDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.UsesDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.TypeVariableDef;
import com.thoughtworks.qdox.type.TypeResolver;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Robert Scholte
 */
public class ModelBuilder implements Builder {

    private final DefaultJavaSource source;
    
    private DefaultJavaModule module;
    
    private DefaultJavaModuleDescriptor moduleDescriptor;

    private LinkedList<DefaultJavaClass> classStack = new LinkedList<DefaultJavaClass>();

    private List<DefaultJavaParameter> parameterList = new LinkedList<DefaultJavaParameter>();

    private DefaultJavaConstructor currentConstructor;

    private DefaultJavaMethod currentMethod;

    private DefaultJavaField currentField;

    private List<AnnoDef> currentAnnoDefs;
    
    private List<ExpressionDef> currentArguments;

    private String lastComment;

    private List<TagDef> lastTagSet = new LinkedList<TagDef>();

    private ClassLibrary classLibrary;
    
    private DocletTagFactory docletTagFactory;

    private ModelWriterFactory modelWriterFactory;

    public ModelBuilder( ClassLibrary classLibrary, DocletTagFactory docletTagFactory )
    {
        this.classLibrary = classLibrary;
        this.docletTagFactory = docletTagFactory;
        this.source = new DefaultJavaSource( classLibrary );
        this.currentAnnoDefs = new LinkedList<AnnoDef>();
        this.currentArguments = new LinkedList<ExpressionDef>();
    }
    
    /** {@inheritDoc} */
    public void setModelWriterFactory( ModelWriterFactory modelWriterFactory )
    {
        this.modelWriterFactory = modelWriterFactory;
        source.setModelWriterFactory( modelWriterFactory );
    }
    
    /** {@inheritDoc} */
    public void setModule( final ModuleDef moduleDef )
    {
        this.moduleDescriptor = new DefaultJavaModuleDescriptor(moduleDef.getName());
        this.module = new DefaultJavaModule(moduleDef.getName(), moduleDescriptor);
    }
    
    /** {@inheritDoc} */
    public void addExports( ExportsDef exportsDef )
    {
        // for now use anonymous modules
        List<JavaModule> targets = new ArrayList<JavaModule>(exportsDef.getTargets().size());
        for ( String moduleName : exportsDef.getTargets() )
        {
            targets.add( new DefaultJavaModule( moduleName, null ) );
        }
        
        DefaultJavaExports exports =
            new DefaultJavaExports( new DefaultJavaPackage(exportsDef.getSource()), targets );
        exports.setLineNumber( exportsDef.getLineNumber() );
        exports.setModelWriterFactory( modelWriterFactory );
        moduleDescriptor.addExports( exports );
    }
    
    /** {@inheritDoc} */
    public void addOpens( OpensDef opensDef )
    {
     // for now use anonymous modules
        List<JavaModule> targets = new ArrayList<JavaModule>(opensDef.getTargets().size());
        for ( String moduleName : opensDef.getTargets() )
        {
            targets.add( new DefaultJavaModule( moduleName, null ) );
        }
        
        DefaultJavaOpens exports =
            new DefaultJavaOpens( new DefaultJavaPackage(opensDef.getSource()), targets );
        exports.setLineNumber( opensDef.getLineNumber() );
        exports.setModelWriterFactory( modelWriterFactory );
        moduleDescriptor.addOpens( exports );
    }

    /** {@inheritDoc} */
    public void addProvides( ProvidesDef providesDef )
    {
        JavaClass service = createType( providesDef.getService(), 0 );
        List<JavaClass> implementations = new LinkedList<JavaClass>();
        for ( TypeDef implementType : providesDef.getImplementations() )
        {
            implementations.add( createType( implementType, 0 ) );
        }
        DefaultJavaProvides provides = new DefaultJavaProvides( service, implementations );
        provides.setLineNumber( providesDef.getLineNumber() );
        provides.setModelWriterFactory( modelWriterFactory );
        moduleDescriptor.addProvides( provides );
    }

    /** {@inheritDoc} */
    public void addRequires( RequiresDef requiresDef )
    {
        JavaModule module = new DefaultJavaModule( requiresDef.getName(), null );
        DefaultJavaRequires requires = new DefaultJavaRequires( module, requiresDef.getModifiers() );
        requires.setLineNumber( requiresDef.getLineNumber() );
        requires.setModelWriterFactory( modelWriterFactory );
        moduleDescriptor.addRequires( requires );
    }

    /** {@inheritDoc} */
    public void addUses( UsesDef usesDef )
    {
        DefaultJavaUses uses = new DefaultJavaUses( createType( usesDef.getService(), 0 ) );
        uses.setLineNumber( usesDef.getLineNumber() );
        uses.setModelWriterFactory( modelWriterFactory );
        moduleDescriptor.addUses( uses );
    }
    

    /** {@inheritDoc} */
    public void addPackage( PackageDef packageDef )
    {
        DefaultJavaPackage jPackage = new DefaultJavaPackage( packageDef.getName() );
        jPackage.setClassLibrary( classLibrary );
        jPackage.setLineNumber( packageDef.getLineNumber() );
        jPackage.setModelWriterFactory( modelWriterFactory );
        addJavaDoc( jPackage );
        setAnnotations( jPackage );
        source.setPackage( jPackage );
    }

    /** {@inheritDoc} */
    public void addImport( String importName )
    {
        source.addImport( importName );
    }

    /** {@inheritDoc} */
    public void addJavaDoc( String text )
    {
        lastComment = text;
    }

    /** {@inheritDoc} */
    public void addJavaDocTag( TagDef tagDef )
    {
        lastTagSet.add( tagDef );
    }

    /** {@inheritDoc} */
    public void beginClass(ClassDef def)
    {
        DefaultJavaClass newClass = new DefaultJavaClass( source );
        newClass.setLineNumber( def.getLineNumber() );
        newClass.setModelWriterFactory( modelWriterFactory );

        // basic details
        newClass.setName( def.getName() );
        newClass.setInterface( ClassDef.INTERFACE.equals( def.getType() ) );
        newClass.setEnum( ClassDef.ENUM.equals( def.getType() ) );
        newClass.setAnnotation( ClassDef.ANNOTATION_TYPE.equals( def.getType() ) );

        // superclass
        if ( newClass.isInterface() )
        {
            newClass.setSuperClass( null );
        }
        else if ( !newClass.isEnum() )
        {
            newClass.setSuperClass( def.getExtends().size() > 0 ? createType( def.getExtends().iterator().next(), 0 )
                            : null );
        }

        // implements
        Set<TypeDef> implementSet = newClass.isInterface() ? def.getExtends() : def.getImplements();
        List<JavaClass> implementz = new LinkedList<JavaClass>();
        for ( TypeDef implementType : implementSet )
        {
            implementz.add( createType( implementType, 0 ) );
        }
        newClass.setImplementz( implementz );

        // modifiers
        newClass.setModifiers( new LinkedList<String>( def.getModifiers() ) );
        
        // typeParameters
        if ( def.getTypeParameters() != null )
        {
            List<DefaultJavaTypeVariable<JavaClass>> typeParams = new LinkedList<DefaultJavaTypeVariable<JavaClass>>();
            for ( TypeVariableDef typeVariableDef : def.getTypeParameters() )
            {
                typeParams.add( createTypeVariable( typeVariableDef, (JavaClass) newClass ) );
            }
            newClass.setTypeParameters( typeParams );
        }

        // javadoc
        addJavaDoc( newClass );

//        // ignore annotation types (for now)
//        if (ClassDef.ANNOTATION_TYPE.equals(def.type)) {
//        	System.out.println( currentClass.getFullyQualifiedName() );
//            return;
//        }

        // annotations
        setAnnotations( newClass );
        
        classStack.addFirst( bindClass( newClass ) );
    }

    protected DefaultJavaClass bindClass( DefaultJavaClass newClass )
    {
        if ( currentField != null )
        {
            classStack.getFirst().addClass( newClass );
            currentField.setEnumConstantClass( newClass );
        }
        else if ( !classStack.isEmpty() )
        {
            classStack.getFirst().addClass( newClass );
            newClass.setDeclaringClass( classStack.getFirst() );
        }
        else
        {
            source.addClass( newClass );
        }
        return newClass;
    }

    /** {@inheritDoc} */
    public void endClass()
    {
        classStack.removeFirst();
    }

    /**
     * this one is specific for those cases where dimensions can be part of both the type and identifier
     * i.e. private String[] matrix[]; //field
     * 		public abstract String[] getMatrix[](); //method  
     *      
     * @param typeDef
     * @param dimensions
     * @return the Type
     */
    private DefaultJavaType createType( TypeDef typeDef, int dimensions )
    {
        if ( typeDef == null )
        {
            return null;
        }
        TypeResolver typeResolver;
        if(classStack.isEmpty())
        {
            typeResolver = TypeResolver.byPackageName( source.getPackageName(), classLibrary, source.getImports() );
        }
        else
        {
            typeResolver = TypeResolver.byClassName( classStack.getFirst().getBinaryName(), classLibrary, source.getImports() );
        }
        
        return TypeAssembler.createUnresolved( typeDef, dimensions, typeResolver );
    }

    private void addJavaDoc( AbstractBaseJavaEntity entity )
    {
        entity.setComment( lastComment );
        List<DocletTag> tagList = new LinkedList<DocletTag>();
        for ( TagDef tagDef : lastTagSet )
        {
            tagList.add( docletTagFactory.createDocletTag( tagDef.getName(), tagDef.getText(),
                                                           (JavaAnnotatedElement) entity, tagDef.getLineNumber() ) );
        }
        entity.setTags( tagList );

        lastTagSet.clear();
        lastComment = null;
    }
    
    /** {@inheritDoc} */
    public void addInitializer( InitDef def )
    {
        DefaultJavaInitializer initializer = new DefaultJavaInitializer();
        initializer.setLineNumber( def.getLineNumber() );

        initializer.setBlock( def.getBlockContent() );
        initializer.setStatic( def.isStatic() );
        
        classStack.getFirst().addInitializer( initializer );
        
    }
    
    /** {@inheritDoc} */
    public void beginConstructor()
    {
        currentConstructor = new DefaultJavaConstructor();

        currentConstructor.setDeclaringClass( classStack.getFirst() );

        currentConstructor.setModelWriterFactory( modelWriterFactory );

        addJavaDoc( currentConstructor );
        setAnnotations( currentConstructor );

        classStack.getFirst().addConstructor( currentConstructor );
    }

    /** {@inheritDoc} */
    public void endConstructor( MethodDef def )
    {
        currentConstructor.setLineNumber( def.getLineNumber() );

        // basic details
        currentConstructor.setName( def.getName() );

        // typeParameters
        if ( def.getTypeParams() != null )
        {
            List<JavaTypeVariable<JavaConstructor>> typeParams =
                new LinkedList<JavaTypeVariable<JavaConstructor>>();
            for ( TypeVariableDef typeVariableDef : def.getTypeParams() )
            {
                typeParams.add( createTypeVariable( typeVariableDef, (JavaConstructor) currentConstructor ) );
            }
            currentConstructor.setTypeParameters( typeParams );
        }

        // exceptions
        List<JavaClass> exceptions = new LinkedList<JavaClass>();
        for ( TypeDef type : def.getExceptions() )
        {
            exceptions.add( createType( type, 0 ) );
        }
        currentConstructor.setExceptions( exceptions );

        // modifiers
        currentConstructor.setModifiers( new LinkedList<String>( def.getModifiers() ) );

        if ( !parameterList.isEmpty() )
        {
            currentConstructor.setParameters( new ArrayList<JavaParameter>( parameterList ) );
            parameterList.clear();
        }

        currentConstructor.setSourceCode( def.getBody() );
    }

    /** {@inheritDoc} */
    public void beginMethod()
    {
        currentMethod = new DefaultJavaMethod();
        if ( currentField == null )
        {
            currentMethod.setDeclaringClass( classStack.getFirst() );
            classStack.getFirst().addMethod( currentMethod );
        }
        currentMethod.setModelWriterFactory( modelWriterFactory );

        addJavaDoc( currentMethod );
        setAnnotations( currentMethod );
    }
    
    /** {@inheritDoc} */
    public void endMethod( MethodDef def )
    {
        currentMethod.setLineNumber( def.getLineNumber() );

        // basic details
        currentMethod.setName( def.getName() );
        currentMethod.setReturns( createType( def.getReturnType(), def.getDimensions() ) );

        // typeParameters
        if ( def.getTypeParams() != null )
        {
            List<JavaTypeVariable<JavaMethod>> typeParams =
                new LinkedList<JavaTypeVariable<JavaMethod>>();
            for ( TypeVariableDef typeVariableDef : def.getTypeParams() )
            {
                typeParams.add( createTypeVariable( typeVariableDef, (JavaMethod) currentMethod ) );
            }
            currentMethod.setTypeParameters( typeParams );
        }

        // exceptions
        List<JavaClass> exceptions = new LinkedList<JavaClass>();
        for ( TypeDef type : def.getExceptions() )
        {
            exceptions.add( createType( type, 0 ) );
        }
        currentMethod.setExceptions( exceptions );

        // modifiers
        currentMethod.setDefault( def.getModifiers().remove( "default" ) );
        currentMethod.setModifiers( new LinkedList<String>( def.getModifiers() ) );

        if ( !parameterList.isEmpty() )
        {
            currentMethod.setParameters( new ArrayList<JavaParameter>( parameterList ) );
            parameterList.clear();
        }

        currentMethod.setSourceCode( def.getBody() );
    }

    private <G extends JavaGenericDeclaration> DefaultJavaTypeVariable<G> createTypeVariable( TypeVariableDef typeVariableDef, G genericDeclaration)
    {
        if ( typeVariableDef == null )
        {
            return null;
        }
        
        JavaClass declaringClass = getContext( genericDeclaration );
        // can't select a declaring class based on the genericDeclaration
        // likely method specifies its own genericDecleration
        if ( declaringClass == null )
        {
            return null;
        }
        
        TypeResolver typeResolver = TypeResolver.byClassName( declaringClass.getBinaryName(), classLibrary, source.getImports() );
        
        DefaultJavaTypeVariable<G> result = new DefaultJavaTypeVariable<G>( typeVariableDef.getName(), typeResolver );

        if ( typeVariableDef.getBounds() != null && !typeVariableDef.getBounds().isEmpty() )
        {
            List<JavaType> bounds = new LinkedList<JavaType>();
            for ( TypeDef typeDef : typeVariableDef.getBounds() )
            {
                bounds.add( createType( typeDef, 0 ) );
            }
            result.setBounds( bounds );
        }
        return result;
    }
    
    private static JavaClass getContext( JavaGenericDeclaration genericDeclaration )
    {
        JavaClass result;
        if ( genericDeclaration instanceof JavaClass )
        {
            result = (JavaClass) genericDeclaration;
        }
        else if ( genericDeclaration instanceof JavaExecutable )
        {
            result = ( (JavaExecutable) genericDeclaration ).getDeclaringClass();
        }
        else
        {
            throw new IllegalArgumentException( "Unknown JavaGenericDeclaration implementation" );
        }
        return result;
    }

    /** {@inheritDoc} */
    public void beginField( FieldDef def )
    {
        currentField = new DefaultJavaField( def.getName() );
        currentField.setDeclaringClass( classStack.getFirst() );
        currentField.setLineNumber( def.getLineNumber() );
        currentField.setModelWriterFactory( modelWriterFactory );

        currentField.setType( createType( def.getType(), def.getDimensions() ) );
        
        currentField.setEnumConstant( def.isEnumConstant() );

        // modifiers
        {
            currentField.setModifiers( new LinkedList<String>( def.getModifiers() ) );
        }

        // code body
        currentField.setInitializationExpression( def.getBody() );

        // javadoc
        addJavaDoc( currentField );

        // annotations
        setAnnotations( currentField );
    }
	
    /** {@inheritDoc} */
	public void endField() 
	{
	    if ( currentArguments != null && !currentArguments.isEmpty() )
        {
	        TypeResolver typeResolver;
	        if( classStack.isEmpty() )
	        {
	            typeResolver = TypeResolver.byPackageName( source.getPackageName(), classLibrary, source.getImports() );
	        }
	        else
	        {
	            typeResolver = TypeResolver.byClassName( classStack.getFirst().getBinaryName(), classLibrary, source.getImports() );
	        }
	        
	        //DefaultExpressionTransformer?? 
            DefaultJavaAnnotationAssembler assembler = new DefaultJavaAnnotationAssembler( currentField.getDeclaringClass(), classLibrary, typeResolver );

            List<Expression> arguments = new LinkedList<Expression>();
            for ( ExpressionDef annoDef : currentArguments )
            {
                arguments.add( assembler.assemble( annoDef ) );
            }
            currentField.setEnumConstantArguments( arguments );
            currentArguments.clear();
        }
	    
        classStack.getFirst().addField(currentField);
        
        currentField = null;
	}
	
    /** {@inheritDoc} */
    public void addParameter( FieldDef fieldDef )
    {
        DefaultJavaParameter jParam =
            new DefaultJavaParameter( createType( fieldDef.getType(), fieldDef.getDimensions() ), fieldDef.getName(),
                                      fieldDef.isVarArgs() );
        if( currentMethod != null )
        {
            jParam.setExecutable( currentMethod );
        }
        else
        {
            jParam.setExecutable( currentConstructor );
        }
        jParam.setModelWriterFactory( modelWriterFactory );
        addJavaDoc( jParam );
        setAnnotations( jParam );
        parameterList.add( jParam );
    }

    private void setAnnotations( final AbstractBaseJavaEntity entity )
    {
        if ( !currentAnnoDefs.isEmpty() )
        {
            TypeResolver typeResolver;
            if( classStack.isEmpty() )
            {
                typeResolver = TypeResolver.byPackageName( source.getPackageName(), classLibrary, source.getImports() );
            }
            else
            {
                typeResolver = TypeResolver.byClassName( classStack.getFirst().getBinaryName(), classLibrary, source.getImports() );
            }
            
            DefaultJavaAnnotationAssembler assembler = new DefaultJavaAnnotationAssembler( entity.getDeclaringClass(), classLibrary, typeResolver );

            List<JavaAnnotation> annotations = new LinkedList<JavaAnnotation>();
            for ( AnnoDef annoDef : currentAnnoDefs )
            {
                annotations.add( assembler.assemble( annoDef ) );
            }
            entity.setAnnotations( annotations );
            currentAnnoDefs.clear();
        }
    }

    // Don't resolve until we need it... class hasn't been defined yet.
    /** {@inheritDoc} */
    public void addAnnotation( AnnoDef annotation )
    {
        currentAnnoDefs.add( annotation );
    }
    
    /** {@inheritDoc} */
    public void addArgument( ExpressionDef argument )
    {
        currentArguments.add( argument );
    }

    /** {@inheritDoc} */
    public JavaSource getSource()
    {
        return source;
    }
    
    /** {@inheritDoc} */
    public JavaModule getModuleInfo()
    {
        return module;
    }

    /** {@inheritDoc} */
    public void setUrl( URL url )
    {
        source.setURL( url );
    }
}