package org.ooc.backend;

import java.io.File;
import java.io.IOException;

import org.ooc.frontend.model.SourceUnit;

public abstract class Generator {

	protected SourceUnit unit;

	public Generator(File outPath, SourceUnit unit) {
		
		this.unit = unit;
		
	}
	
	public abstract void generate() throws IOException;
	
}
