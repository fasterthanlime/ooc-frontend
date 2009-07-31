package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Nosy.Opportunist;

public class ModularAccessResolver implements Hobgoblin {

	private static final int MAX = 1024;
	boolean running;
	
	@Override
	public void process(SourceUnit unit) throws IOException {
		
		final MultiMap<Node, VariableDecl> vars = 
			unit.getDeclarations(VariableDecl.class);
		final MultiMap<Node, FunctionDecl> funcs = 
			unit.getDeclarations(FunctionDecl.class);
		
		Nosy<MustResolveAccess> nosy = new Nosy<MustResolveAccess>(
				MustResolveAccess.class, new Opportunist<MustResolveAccess>() {
			@Override
			public boolean take(MustResolveAccess node, Stack<Node> stack) throws IOException {
				
				if(!node.isResolved()) {
					System.out.println("Must resolve access of a "+node.getClass().getSimpleName());
					if(node.resolveAccess(stack, vars, funcs)) {
						// resolveAccess returned true, means we must do one more run
						running = true;
					}
				}
				
				return true;
				
			}
		});
		
		int count = 0;
		running = true;
		while(running) {
			if(count > MAX) {
				throw new Error("ModularAccessResolver going round in circles! More than "+MAX+" runs, abandoning...");
			}
			running = false;
			nosy.visit(unit);
			count++;
		}
		
	}
	
}
