package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Include.Define;

public class IncludeWriter {

	public static void write(Include include, CGenerator cgen) throws IOException {
		for(Define define: include.getDefines()) {
			cgen.current.nl().app("#define ").app(define.name);
			if(define.value != null) cgen.current.app(' ').app(define.value);
		}
		cgen.current.nl().app("#include <").app(include.getPath()).app(".h>");
		for(Define define: include.getDefines()) {
			cgen.current.nl().app("#undef ").app(define.name).app(' ');
		}
	}
	
}
