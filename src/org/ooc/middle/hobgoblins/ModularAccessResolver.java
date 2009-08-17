package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

public class ModularAccessResolver implements Hobgoblin {

	private static final int MAX = 10;
	boolean running;
	boolean fatal = false;
	
	public MultiMap<Node, VariableDecl> vars;
	public MultiMap<Node, FunctionDecl> funcs;
	public List<TypeDecl> types;
	
	@Override
	public void process(Module module) throws IOException {
		
		getInfos(module);
		
		Nosy<MustResolveAccess> nosy = Nosy.get(
				MustResolveAccess.class, new Opportunist<MustResolveAccess>() {
			@Override
			public boolean take(MustResolveAccess node, Stack<Node> stack) throws IOException {
				
				if(!node.isResolved()) {
					if(node.resolveAccess(stack, ModularAccessResolver.this, fatal)) {
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
				nosy.start().visit(module);
				throw new Error("ModularAccessResolver going round in circles! More than "+MAX+" runs, abandoning...");
			}
			running = false;
			getInfos(module);
			nosy.start().visit(module);
			count++;
		}
		
	}

	private void getInfos(Module module) throws IOException {
		vars = module.getDeclarationsMap(VariableDecl.class);
		funcs = module.getDeclarationsMap(FunctionDecl.class);
		types = module.getDeclarationsList(TypeDecl.class);
	}
	
}
