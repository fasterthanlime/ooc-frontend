package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

public class Checker implements Hobgoblin {

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
		
		Nosy.get(Type.class, new Opportunist<Type>() {
			@Override
			public boolean take(Type node, Stack<Node> stack) throws IOException {
				if(node.getRef() == null) {
					throw new CompilationFailedError(null,
							node.getClass().getSimpleName()+" "+node
							+" hasn't been resolved :(, stack = "+stack);
				}
				return true;
			}
		}).visit(module);
		
		Nosy.get(FunctionDecl.class, new Opportunist<FunctionDecl>() {
			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack) throws IOException {
				if(node.isConstructor()) {
					if(Node.find(TypeDecl.class, stack) == -1) {
						throw new CompilationFailedError(null,
							"Declaration of a function named 'new' outside a class is forbidden!" +
							" Functions named 'new' are only used as constructors in classes.");
					}
				}
				return true;
			}
		}).visit(module);
		
	}

}
