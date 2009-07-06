package org.ooc.backend;

import java.io.File;
import java.io.IOException;

import org.ooc.frontend.model.SourceUnit;

public interface Generator {

	public void generate(File outPath, SourceUnit unit) throws IOException;
	
}
