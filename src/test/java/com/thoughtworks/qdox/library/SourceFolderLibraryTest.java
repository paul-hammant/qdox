package com.thoughtworks.qdox.library;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaModuleDescriptor;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;

public class SourceFolderLibraryTest
{
    private SourceFolderLibrary library = new SourceFolderLibrary( null );

    @Test
    public void testClassisFolder() {
        JavaModule directModule = library.addSourceFolder( new File("src/test/resources/qdox-140") ); 
        Assertions.assertEquals(null, directModule);
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
        Assertions.assertEquals("M.N", directModule.getName());
        
        JavaModule module = library.getJavaModules().iterator().next();
        Assertions.assertEquals("M.N", module.getName());
        JavaModuleDescriptor descriptor = module.getDescriptor();
        Assertions.assertEquals(4, descriptor.getRequires().size());
        Iterator<JavaRequires> requiresIter = descriptor.getRequires().iterator();
        JavaRequires req = requiresIter.next();
        Assertions.assertEquals("A.B", req.getModule().getName());
        Assertions.assertEquals(false, req.isTransitive());
        Assertions.assertEquals(false, req.isStatic());
        req = requiresIter.next();
        Assertions.assertEquals("C.D", req.getModule().getName());
        Assertions.assertEquals(true, req.isTransitive());
        Assertions.assertEquals(false, req.isStatic());
        req = requiresIter.next();
        Assertions.assertEquals("E.F", req.getModule().getName());
        Assertions.assertEquals(false, req.isTransitive());
        Assertions.assertEquals(true, req.isStatic());
        req = requiresIter.next();
        Assertions.assertEquals("G.H", req.getModule().getName());
        Assertions.assertEquals(true, req.isTransitive());
        Assertions.assertEquals(true, req.isStatic());
        
        Assertions.assertEquals(2, module.getDescriptor().getExports().size());
        Iterator<JavaExports> exportsIter = descriptor.getExports().iterator();
        JavaExports exp = exportsIter.next();
        Assertions.assertEquals("P.Q", exp.getSource().getName());
        Assertions.assertArrayEquals(new String[0], exp.getTargets().toArray( new String[0] ));
        exp = exportsIter.next();
        Assertions.assertEquals("R.S", exp.getSource().getName());
        Assertions.assertEquals(2, exp.getTargets().size());
        Iterator<JavaModule> moduleIter = exp.getTargets().iterator();
        JavaModule mdl = moduleIter.next();
        Assertions.assertEquals("T1.U1", mdl.getName());
        mdl = moduleIter.next();
        Assertions.assertEquals("T2.U2", mdl.getName());
        
        Assertions.assertEquals(2, module.getDescriptor().getOpens().size());
        Iterator<JavaOpens> opensIter = descriptor.getOpens().iterator();
        JavaOpens opn = opensIter.next();
        Assertions.assertEquals("P.Q", opn.getSource().getName());
        Assertions.assertArrayEquals(new String[0], opn.getTargets().toArray( new String[0] ));
        opn = opensIter.next();
        Assertions.assertEquals("R.S", opn.getSource().getName());
        Assertions.assertEquals(2, opn.getTargets().size());
        moduleIter = opn.getTargets().iterator();
        mdl = moduleIter.next();
        Assertions.assertEquals("T1.U1", mdl.getName());
        mdl = moduleIter.next();
        Assertions.assertEquals("T2.U2", mdl.getName());
        
        Assertions.assertEquals(1, module.getDescriptor().getUses().size());
        Iterator<JavaUses> usesIter = descriptor.getUses().iterator();
        JavaUses uses = usesIter.next();
        Assertions.assertEquals("V.W", uses.getService().getFullyQualifiedName());
        
        Assertions.assertEquals(1, module.getDescriptor().getProvides().size());
        Iterator<JavaProvides> providesIter = descriptor.getProvides().iterator();
        JavaProvides provides = providesIter.next();
        Assertions.assertEquals("X.Y", provides.getService().getFullyQualifiedName());
        Iterator<JavaClass> classIter = provides.getProviders().iterator(); 
        JavaClass cls = classIter.next();
        Assertions.assertEquals("Z1.Z2", cls.getFullyQualifiedName());
        cls = classIter.next();
        Assertions.assertEquals("Z3.Z4", cls.getFullyQualifiedName());
    }

    @Test
    public void testHasClassReference() {
        library.addSourceFolder( new File("src/test/resources") );
        Assertions.assertFalse(library.hasClassReference( "Integer" ));
        Assertions.assertTrue(library.hasClassReference( "com.thoughtworks.qdox.testdata.DefaultCtor" ));
        // the following (non-existing) FQCN is called e.g. from TypeResolver
        Assertions.assertFalse(library.hasClassReference( "com.thoughtworks.qdox.testdata.DefaultCtor$Integer" ));
    }
}
