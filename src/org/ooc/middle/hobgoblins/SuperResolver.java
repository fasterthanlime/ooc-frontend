package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

public class SuperResolver implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		final List<ClassDecl> classes = unit.getDeclarationsList(ClassDecl.class);
		
		Nosy.get(ClassDecl.class, new Opportunist<ClassDecl>() {

			@Override
			public boolean take(ClassDecl node, Stack<Node> stack) throws IOException {
				
				if(node.getSuperRef() != null) return true;
				if(node.getSuperName().isEmpty()) return true;
				
				for(ClassDecl candidate: classes) {
					if(node.getSuperName().equals(candidate.getName())) {
						node.setSuperRef(candidate);
						System.out.println("Resolved super-class "+node.getSuperName());
						return false;
					}
				}
				
				return true;
				
			}
			
		}).visit(unit);

	}

}
