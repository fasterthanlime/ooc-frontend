package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

public class Checker implements Hobgoblin {

	@Override
	public void process(Module module, BuildParams params) throws IOException {

		Nosy.get(VariableAccess.class, new Opportunist<VariableAccess>() {
			@Override
			public boolean take(VariableAccess node, Stack<Node> stack) throws IOException {
				if(node.getRef() == null) {
					throw new OocCompilationError(node, stack,
							node.getClass().getSimpleName()+" to "+node.getName()
							+" hasn't been resolved :( Stack = "+stack);
				}
				return true;
			}
		}).visit(module);
		
		Nosy.get(FunctionCall.class, new Opportunist<FunctionCall>() {
			@Override
			public boolean take(FunctionCall node, Stack<Node> stack) throws IOException {
				if(node.getImpl() == null) {
					throw new OocCompilationError(node, stack,
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
					throw new OocCompilationError(node, stack,
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
						// TODO forbid functions named load in modules (or __load__?)
						throw new OocCompilationError(node, stack,
							"Declaration of a function named 'new' outside a class is forbidden!" +
							" Functions named 'new' are only used as constructors in classes.");
					}
				}
				return true;
			}
		}).visit(module);
		
		Nosy.get(Node.class, new Opportunist<Node>() {
			@Override
			public boolean take(Node node, Stack<Node> stack) throws IOException {
				if(node.startToken == null && !(node instanceof NodeList<?>)) {
					throw new OocCompilationError(node, stack,
						"Null startToken for a "+node.getClass().getSimpleName()
							+" = "+node+", stack = "+stack+"\nStack Trace: "+node.stackTrace);
				}
				return true;
			}
		}).visit(module);
		
	}

}
