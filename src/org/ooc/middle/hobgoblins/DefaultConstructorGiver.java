package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.FunctionDecl.FunctionDeclType;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

public class DefaultConstructorGiver implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

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
					FunctionDecl con = new FunctionDecl(FunctionDeclType.FUNC, "new", "", false, false, false, false);
					con.setReturnType(new Type(node.getName()));
					node.getFunctions().add(con);
					System.out.println("Added default constructor to class "+node.getName());
				}
				
				return true;
				
			}
			
		}).visit(unit);
		
	}

}
