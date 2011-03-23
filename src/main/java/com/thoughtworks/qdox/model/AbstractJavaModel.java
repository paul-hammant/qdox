package com.thoughtworks.qdox.model;

import java.io.Serializable;

import com.thoughtworks.qdox.io.DefaultModelWriter;
import com.thoughtworks.qdox.io.ModelWriter;
import com.thoughtworks.qdox.io.ModelWriterFactory;

public abstract class AbstractJavaModel implements JavaModel, Serializable {

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