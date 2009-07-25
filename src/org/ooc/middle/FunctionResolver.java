package org.ooc.middle;

import java.io.IOException;
import java.util.HashMap;

import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.middle.Nosy.Opportunist;

public class FunctionResolver implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		HashMap<String, FunctionDecl> funcs;
		
		/* Step 1: Collect all symbols */
		new Nosy<FunctionCall>(FunctionCall.class, new Opportunist<FunctionCall>() {
			
			@Override
			public void take(FunctionCall node) {
				System.out.println("Call to "+node.getName());
			}
			
		}).visit(unit);
		
	}
	
}
