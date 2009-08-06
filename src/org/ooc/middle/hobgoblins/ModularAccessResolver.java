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
	boolean fatal = false;
	
	public MultiMap<Node, VariableDecl> vars;
	public MultiMap<Node, FunctionDecl> funcs;
	public List<ClassDecl> classes;
	
	@Override
	public void process(SourceUnit unit) throws IOException {
		
		getInfos(unit);
		
		Nosy<MustResolveAccess> nosy = Nosy.get(
				MustResolveAccess.class, new Opportunist<MustResolveAccess>() {
			@Override
			public boolean take(MustResolveAccess node, Stack<Node> stack) throws IOException {
				
				if(!node.isResolved()) {
					if(node.resolveAccess(stack, ModularAccessResolver.this, fatal)) {
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
				fatal = true;
				nosy.start().visit(unit);
				throw new Error("ModularAccessResolver going round in circles! More than "+MAX+" runs, abandoning...");
			}
			running = false;
			getInfos(unit);
			nosy.start().visit(unit);
			count++;
		}
		
	}

	private void getInfos(SourceUnit unit) throws IOException {
		vars = unit.getDeclarationsMap(VariableDecl.class);
		funcs = unit.getDeclarationsMap(FunctionDecl.class);
		classes = unit.getDeclarationsList(ClassDecl.class);
	}
	
}
