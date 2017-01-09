package com.thoughtworks.qdox.library;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.Test;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaModuleDescriptor;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaExports;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaOpens;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaProvides;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaRequires;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaUses;

public class SourceFolderLibraryTest
{
    private SourceFolderLibrary library = new SourceFolderLibrary( null );

    @Test
    public void testClassisFolder() {
        JavaModule directModule = library.addSourceFolder( new File("src/test/resources/qdox-140") ); 
        assertEquals( null, directModule);
    }
    
    @Test
    public void testModuleInfo()
    {
        library.addSource( new StringReader("package V;\n"
                        + "public interface W {}") );
        library.addSource( new StringReader("package X;\n"
                        + "public interface Y {}") );
        library.addSource( new StringReader("package Z1;\n"
                        + "public class Z2 implements X.Y {}") );
        library.addSource( new StringReader("package Z3;\n"
                        + "public class Z4 implements X.Y {}") );
        
        JavaModule directModule = library.addSourceFolder( new File("src/test/resources/jigsaw/example1") );
        assertEquals( "M.N", directModule.getName() );
        
        JavaModule module = library.getJavaModules().iterator().next();
        assertEquals( "M.N", module.getName() );
        JavaModuleDescriptor descriptor = module.getDescriptor();
        assertEquals( 4, descriptor.getRequires().size() );
        Iterator<JavaRequires> requiresIter = descriptor.getRequires().iterator();
        JavaRequires req = requiresIter.next();
        assertEquals( "A.B", req.getModule().getName() );
        assertEquals( false, req.isTransitive() );
        assertEquals( false, req.isStatic() );
        req = requiresIter.next();
        assertEquals( "C.D", req.getModule().getName() );
        assertEquals( true, req.isTransitive() );
        assertEquals( false, req.isStatic() );
        req = requiresIter.next();
        assertEquals( "E.F", req.getModule().getName() );
        assertEquals( false, req.isTransitive() );
        assertEquals( true, req.isStatic() );
        req = requiresIter.next();
        assertEquals( "G.H", req.getModule().getName() );
        assertEquals( true, req.isTransitive() );
        assertEquals( true, req.isStatic() );
        
        assertEquals( 2, module.getDescriptor().getExports().size());
        Iterator<JavaExports> exportsIter = descriptor.getExports().iterator();
        JavaExports exp = exportsIter.next();
        assertEquals( "P.Q", exp.getSource().getName() );
        assertArrayEquals( new String[0], exp.getTargets().toArray( new String[0] ) );
        exp = exportsIter.next();
        assertEquals( "R.S", exp.getSource().getName() );
        assertEquals( 2, exp.getTargets().size());
        Iterator<JavaModule> moduleIter = exp.getTargets().iterator();
        JavaModule mdl = moduleIter.next();
        assertEquals( "T1.U1", mdl.getName() );
        mdl = moduleIter.next();
        assertEquals( "T2.U2", mdl.getName() );
        
        assertEquals( 2, module.getDescriptor().getOpens().size() );
        Iterator<JavaOpens> opensIter = descriptor.getOpens().iterator();
        JavaOpens opn = opensIter.next();
        assertEquals( "P.Q", opn.getSource().getName() );
        assertArrayEquals( new String[0], opn.getTargets().toArray( new String[0] ) );
        opn = opensIter.next();
        assertEquals( "R.S", opn.getSource().getName() );
        assertEquals( 2, opn.getTargets().size());
        moduleIter = opn.getTargets().iterator();
        mdl = moduleIter.next();
        assertEquals( "T1.U1", mdl.getName() );
        mdl = moduleIter.next();
        assertEquals( "T2.U2", mdl.getName() );
        
        assertEquals( 1, module.getDescriptor().getUses().size() );
        Iterator<JavaUses> usesIter = descriptor.getUses().iterator();
        JavaUses uses = usesIter.next();
        assertEquals( "V.W", uses.getService().getFullyQualifiedName() );
        
        assertEquals( 1, module.getDescriptor().getProvides().size() );
        Iterator<JavaProvides> providesIter = descriptor.getProvides().iterator();
        JavaProvides provides = providesIter.next();
        assertEquals( "X.Y", provides.getService().getFullyQualifiedName() );
        Iterator<JavaClass> classIter = provides.getProviders().iterator(); 
        JavaClass cls = classIter.next();
        assertEquals( "Z1.Z2", cls.getFullyQualifiedName() );
        cls = classIter.next();
        assertEquals( "Z3.Z4", cls.getFullyQualifiedName() );
    }
    
}
