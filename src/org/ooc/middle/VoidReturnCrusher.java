package org.ooc.middle;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.middle.Nosy.Opportunist;
import org.ubi.CompilationFailedError;

public class VoidReturnCrusher implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		new Nosy<ValuedReturn>(ValuedReturn.class, new Opportunist<ValuedReturn>() {

			@Override
			public boolean take(ValuedReturn node, Stack<Node> stack) {
				
				FunctionDecl decl = (FunctionDecl) stack.get(Node.find(FunctionDecl.class, stack));
				if(decl.getReturnType().isVoid()) {
					throw new CompilationFailedError(null, "Returning a value in a void function!");
				}
				
				return true;
				
			}
			
		}).visit(unit);
		
	}

}
