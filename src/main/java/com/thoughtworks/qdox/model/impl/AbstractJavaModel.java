package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.model.JavaModel;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.writer.DefaultModelWriter;
import com.thoughtworks.qdox.writer.ModelWriter;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

/**
 * Every element of a class, including the class itself is a javaModel.
 * While being constructed by a ModelBuilder, they all <strong>must</strong> have a reference to the same source.
 * If a ModelWriterFactory is set, it <strong>must</strong> be the same for every element of this source, otherwise the defaultModelWriter is used.
 * 
 * @author Robert Scholte
 *
 */
public abstract class AbstractJavaModel implements JavaModel {

    private ModelWriterFactory modelWriterFactory;
    private JavaSource source;
    
	private int lineNumber = -1;

	public AbstractJavaModel() {
		super();
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	public void setLineNumber(int lineNumber) {
	    this.lineNumber = lineNumber;
	}

	public JavaSource getSource() {
		return source;
	}
	
	public void setSource(JavaSource source) {
		this.source = source;
	}
	
	/**
     * 
     * @param modelWriterFactory
     * @since 2.0
     */
    public void setModelWriterFactory( ModelWriterFactory modelWriterFactory )
    {
        this.modelWriterFactory = modelWriterFactory;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaSource#getModelWriter()
     */
    public ModelWriter getModelWriter()
    {
        ModelWriter result; 
        if (modelWriterFactory != null) {
            result = modelWriterFactory.newInstance();
        }
        else {
            result = new DefaultModelWriter();
        }
        return result;
    }
}