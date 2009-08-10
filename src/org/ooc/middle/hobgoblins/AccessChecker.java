package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

public class AccessChecker implements Hobgoblin {

	@Override
	public void process(Module module) throws IOException {

		Nosy.get(VariableAccess.class, new Opportunist<VariableAccess>() {
			@Override
			public boolean take(VariableAccess node, Stack<Node> stack) throws IOException {
				if(node.getRef() == null) {
					throw new CompilationFailedError(null,
							node.getClass().getSimpleName()+" to "+node.getName()
							+" hasn't been resolved :(");
				}
				return true;
			}
		}).visit(module);
		
		Nosy.get(FunctionCall.class, new Opportunist<FunctionCall>() {
			@Override
			public boolean take(FunctionCall node, Stack<Node> stack) throws IOException {
				if(node.getImpl() == null) {
					throw new CompilationFailedError(null,
							node.getClass().getSimpleName()+" to "+node.getName()
							+" hasn't been resolved :(");
				}
				return true;
			}
		}).visit(module); 
		
	}

}
