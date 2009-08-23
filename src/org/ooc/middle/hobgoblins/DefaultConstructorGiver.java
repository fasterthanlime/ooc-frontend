package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

public class DefaultConstructorGiver implements Hobgoblin {

	@Override
	public void process(Module module, BuildParams params) throws IOException {

		new Nosy<ClassDecl>(ClassDecl.class, new Opportunist<ClassDecl>() {

			@Override
			public boolean take(ClassDecl node, Stack<Node> stack) {
				
				boolean hasNew = false;
				
				for(FunctionDecl decl: node.getFunctions()) {
					if(decl.isConstructor()) {
						hasNew = true;
						break;
					}
				}
				
				if(!hasNew) {
					FunctionDecl con = new FunctionDecl("new", "", false, false, false, false, node.startToken);
					node.addFunction(con);
				}
				
				return true;
				
			}
			
		}).visit(module);
		
	}

}
