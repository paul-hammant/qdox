package com.thoughtworks.qdox.library;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;

import org.junit.Test;

import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaModuleDescriptor;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaExports;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaProvides;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaRequires;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaUses;

public class SourceFolderLibraryTest
{
    private SourceFolderLibrary library = new SourceFolderLibrary( null );

    @Test
    public void testModuleInfo()
    {
        library.addSourceFolder( new File("src/test/resources/jigsaw") );
        
        JavaModule module = library.getJavaModule();
        assertEquals( "M.N", module.getName() );
        JavaModuleDescriptor descriptor = module.getDescriptor();
        assertEquals( 4, descriptor.getRequires().size() );
        Iterator<JavaRequires> requiresIter = descriptor.getRequires().iterator();
        JavaRequires req = requiresIter.next();
        assertEquals( "A.B", req.getModule().getName() );
        assertEquals( false, req.isPublic() );
        assertEquals( false, req.isStatic() );
        req = requiresIter.next();
        assertEquals( "C.D", req.getModule().getName() );
        assertEquals( true, req.isPublic() );
        assertEquals( false, req.isStatic() );
        req = requiresIter.next();
        assertEquals( "E.F", req.getModule().getName() );
        assertEquals( false, req.isPublic() );
        assertEquals( true, req.isStatic() );
        req = requiresIter.next();
        assertEquals( "G.H", req.getModule().getName() );
        assertEquals( true, req.isPublic() );
        assertEquals( true, req.isStatic() );
        
        assertEquals( 4, module.getDescriptor().getExports().size());
        Iterator<JavaExports> exportsIter = descriptor.getExports().iterator();
        JavaExports exp = exportsIter.next();
        assertEquals( "P.Q", exp.getSource().getName() );
        assertArrayEquals( new String[0], exp.getTargets().toArray( new String[0] ) );
        assertEquals( false, exp.isDynamic() );
        exp = exportsIter.next();
        assertEquals( "R.S", exp.getSource().getName() );
        assertEquals( 2, exp.getTargets().size());
        Iterator<JavaModule> moduleIter = exp.getTargets().iterator();
        JavaModule mdl = moduleIter.next();
        assertEquals( "T1.U1", mdl.getName() );
        mdl = moduleIter.next();
        assertEquals( "T2.U2", mdl.getName() );
        assertEquals( false, exp.isDynamic() );
        exp = exportsIter.next();
        assertEquals( "PP.QQ", exp.getSource().getName() );
        assertArrayEquals( new String[0], exp.getTargets().toArray( new String[0] ) );
        assertEquals( true, exp.isDynamic() );
        exp = exportsIter.next();
        assertEquals( "RR.SS", exp.getSource().getName() );
        assertEquals( 2, exp.getTargets().size());
        moduleIter = exp.getTargets().iterator();
        mdl = moduleIter.next();
        assertEquals( "T1.U1", mdl.getName() );
        mdl = moduleIter.next();
        assertEquals( "T2.U2", mdl.getName() );
        assertEquals( true, exp.isDynamic() );
        
        assertEquals( 1, module.getDescriptor().getUses().size() );
        Iterator<JavaUses> usesIter = descriptor.getUses().iterator();
        JavaUses uses = usesIter.next();
        assertEquals( "V.W", uses.getService().getName() );
        
        assertEquals( 2, module.getDescriptor().getProvides().size() );
        Iterator<JavaProvides> providesIter = descriptor.getProvides().iterator();
        JavaProvides provides = providesIter.next();
        assertEquals( "X.Y", provides.getService().getName() );
        assertEquals( "Z1.Z2", provides.getProvider().getName() );
        provides = providesIter.next();
        assertEquals( "X.Y", provides.getService().getName() );
        assertEquals( "Z3.Z4", provides.getProvider().getName() );
    }
    
}
