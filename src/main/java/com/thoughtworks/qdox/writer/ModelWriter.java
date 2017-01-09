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

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaInitializer;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaModuleDescriptor;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Interface for a custom ModelWriter.
 * 
 * QDox doesn't keep any formatting information of the original source file (if it's there).
 * With a ModelWriter you can specify the way elements look if you write them to any output. 
 * 
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface ModelWriter
{
    /**
     * Write the complete source file
     * 
     * A standard source writer should write:
     * <ul>
     *  <li>the package</li>
     *  <li>the imports</li>
     *  <li>the classes</li>
     * </ul> 
     * 
     * @param src the source
     * @return itself
     */
    ModelWriter writeSource( JavaSource src );
    
    /**
     * Write the java package
     * 
     * A standard package writer should write:
     * <ul>
     *   <li>the javadoc</li>
     *   <li>the annotations</li>
     *   <li>the package signature</li>
     * </ul>
     * 
     * @param pkg the package
     * @return itself
     */
    ModelWriter writePackage( JavaPackage pkg );

    /**
     * Write the java class
     * 
     * A standard class writer should write:
     * <ul>
     *   <li>the javadoc</li>
     *   <li>the annotations</li>
     *   <li>the class signature, containing:
     *     <ul>
     *       <li>the fields</li>
     *       <li>the constructors</li>
     *       <li>the methods</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * @param cls the class
     * @return itself
     */
    ModelWriter writeClass( JavaClass cls );
    
    /**
     * Write the java field
     * 
     * A standard field writer should write:
     * <ul>
     *   <li>the javadoc</li>
     *   <li>the annotations</li>
     *   <li>the field signature</li>
     * </ul>
     * 
     * @param fld the field
     * @return itself
     */
    ModelWriter writeField( JavaField fld );
    
    /**
     * Write the java annotation
     * 
     * A standard annotation writer should write:
     * <ul>
     *   <li>the annotation signature</li>
     * </ul>
     * 
     * @param ann the annotation 
     * @return itself
     */
    ModelWriter writeAnnotation( JavaAnnotation ann );

    /**
     * Write the java method
     * 
     * A standard method writer should write:
     * <ul>
     *   <li>the javadoc</li>
     *   <li>the annotations</li>
     *   <li>the method signature, containing:
     *     <ul>
     *       <li>the parameters</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * @param mth the method
     * @return itself
     */
    ModelWriter writeMethod( JavaMethod mth );

    /**
     * Write the java parameter
     * 
     * A standard parameter writer should write:
     * <ul>
     *   <li>the javadoc</li>
     *   <li>the annotations</li>
     *   <li>the parameter signature</li>
     * </ul>
     * 
     * @param prm the parameter
     * @return itself
     */
    ModelWriter writeParameter( JavaParameter prm );

    /**
     * Write the java constructor.
     * 
     * A standard constructor writer should write:
     * <ul>
     *   <li>the javadoc</li>
     *   <li>the annotations</li>
     *   <li>the constructor signature, containing:
     *     <ul>
     *       <li>the parameters</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * @param cns the constructor
     * @return itself
     */
    ModelWriter writeConstructor( JavaConstructor cns );

    /**
     * Write the initializer.
     * 
     * @param init the initializer
     * @return itself
     */
    ModelWriter writeInitializer( JavaInitializer init );
    
    /**
     * Write the module descriptor
     * 
     * A standard module descriptor writer should write:
     * <ul>
     *   <li>the javadoc</li>
     *   <li>the annotations</li>
     *   <li>the module signature, containing:
     *     <ul>
     *       <li>the requires statements</li>
     *       <li>the exports statements</li>
     *       <li>the opens statements</li>
     *       <li>the uses statements</li>
     *       <li>the provides statements</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * @param descriptor the module declaration
     * @return itself
     */
    ModelWriter writeModuleDescriptor( JavaModuleDescriptor descriptor );
    
    /**
     * Write the module descriptors exports
     * 
     * @param exports the exports module statement
     * @return itself
     */
    ModelWriter writeModuleExports( JavaModuleDescriptor.JavaExports exports );

    /**
     * Write the module descriptors opens
     * 
     * @param opens the opens module statement
     * @return itself
     */
    ModelWriter writeModuleOpens( JavaModuleDescriptor.JavaOpens opens );
    
    /**
     * Write the module descriptors provides
     * 
     * @param provides the provides module statement
     * @return itself
     */
    ModelWriter writeModuleProvides( JavaModuleDescriptor.JavaProvides provides );
    
    /**
     * Write the module descriptors requires
     * 
     * @param requires the requires module statement
     * @return itself
     */
    ModelWriter writeModuleRequires( JavaModuleDescriptor.JavaRequires requires );

    /**
     * Write the module descriptors uses
     * 
     * @param uses the uses module statement
     * @return itself
     */
    ModelWriter writeModuleUses( JavaModuleDescriptor.JavaUses uses );

}