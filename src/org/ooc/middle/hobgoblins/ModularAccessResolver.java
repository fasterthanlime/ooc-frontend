package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

public class ModularAccessResolver implements Hobgoblin {

	private static final int MAX = 1024;
	boolean running;
	
	public MultiMap<Node, VariableDecl> vars;
	public MultiMap<Node, FunctionDecl> funcs;
	public List<ClassDecl> classes;
	
	@Override
	public void process(SourceUnit unit) throws IOException {
		
		vars = unit.getDeclarationsMap(VariableDecl.class);
		funcs = unit.getDeclarationsMap(FunctionDecl.class);
		classes = unit.getDeclarationsList(ClassDecl.class);
		
		Nosy<MustResolveAccess> nosy = Nosy.get(
				MustResolveAccess.class, new Opportunist<MustResolveAccess>() {
			@Override
			public boolean take(MustResolveAccess node, Stack<Node> stack) throws IOException {
				
				if(!node.isResolved()) {
					System.out.println("Must resolve access of a "+node.getClass().getSimpleName());
					if(node.resolveAccess(stack, ModularAccessResolver.this)) {
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
