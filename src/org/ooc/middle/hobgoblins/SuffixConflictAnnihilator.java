package org.ooc.middle.hobgoblins;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

public class SuffixConflictAnnihilator implements Hobgoblin {

	@Override
	public void process(Module module, BuildParams params) throws IOException {

		final HashSet<String> funcNames = new HashSet<String>();
		final HashMap<TypeDecl, HashSet<String>> classFuncNames
			= new HashMap<TypeDecl, HashSet<String>>();
		
		Nosy.get(FunctionDecl.class, new Opportunist<FunctionDecl>() {
			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack) throws IOException {
				
				String name = node.getName();
				if(node.getTypeDecl() != null) {
					name = node.getTypeDecl().toString() + "." + name;
				}
				
				if(node.isMember()) {
					HashSet<String> set = classFuncNames.get(node.getTypeDecl());
					if(set == null) {
						set = new HashSet<String>();
						classFuncNames.put(node.getTypeDecl(), set);
					}
					if(!set.add(node.getName()+"_"+node.getSuffix())) {
						throwError(node, stack, name);
					}
				} else {
					if(!funcNames.add(node.getName()+"_"+node.getSuffix())) {
						throwError(node, stack, name);
					}
				}
				
				return true;
				
			}
		}).visit(module);
		
	}

	void throwError(FunctionDecl node, Stack<Node> stack, String name)
			throws OocCompilationError, EOFException {
		
		throw new OocCompilationError(node, stack,
				"Two functions have the same name '"+name
					+"', add suffix to one of them! e.g. "+name+": func ~suffix "+node.getArgsRepr()+" -> ReturnType");
		
	}

}
