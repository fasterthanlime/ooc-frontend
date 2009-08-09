package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

public class SuffixConflictAnnihilator implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		final HashSet<String> funcNames = new HashSet<String>();
		final HashMap<TypeDecl, HashSet<String>> classFuncNames
			= new HashMap<TypeDecl, HashSet<String>>();
		
		Nosy.get(FunctionDecl.class, new Opportunist<FunctionDecl>() {
			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack) throws IOException {
				
				if(node.isMember()) {
					HashSet<String> set = classFuncNames.get(node.getTypeDecl());
					if(set == null) {
						set = new HashSet<String>();
						classFuncNames.put(node.getTypeDecl(), set);
					}
					if(!set.add(node.getName()+"_"+node.getSuffix())) {
						throw new CompilationFailedError(null,
								"Conflicting function names "+node.getName()+", add suffix to one of them!");
					}
				} else {
					if(!funcNames.add(node.getName()+"_")) {
						throw new CompilationFailedError(null,
								"Conflicting function names "+node.getName()+", add suffix to one of them!");
					}
				}
				
				return true;
				
			}
		}).visit(unit);
		
	}

}
