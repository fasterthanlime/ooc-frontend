package org.ooc.middle;

import java.io.IOException;

import org.ooc.frontend.model.SourceUnit;

public interface Hobgoblin {

	public void process(SourceUnit unit) throws IOException;
	
}
