package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Nosy.Opportunist;
import org.ubi.CompilationFailedError;

public class AccessChecker implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		new Nosy<VariableAccess>(VariableAccess.class, new Opportunist<VariableAccess>() {
			@Override
			public boolean take(VariableAccess node, Stack<Node> stack) throws IOException {
				if(node.getRef() == null) {
					throw new CompilationFailedError(null,
							node.getClass().getSimpleName()+" to "+node.getName()
							+" hasn't been resolved :(");
				}
				return true;
			}
		}).visit(unit);
		
		new Nosy<FunctionCall>(FunctionCall.class, new Opportunist<FunctionCall>() {
			@Override
			public boolean take(FunctionCall node, Stack<Node> stack) throws IOException {
				if(node.getImpl() == null) {
					throw new CompilationFailedError(null,
							node.getClass().getSimpleName()+" to "+node.getName()
							+" hasn't been resolved :(");
				}
				return true;
			}
		}).visit(unit); 
		
	}

}
