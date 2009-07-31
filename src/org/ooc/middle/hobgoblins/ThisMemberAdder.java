package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Nosy.Opportunist;
import org.ubi.CompilationFailedError;

/**
 * Adds as a first parameter 'this' to every member function, and make constructors
 * static.
 * 
 * @author Amos Wenger
 */
public class ThisMemberAdder implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		new Nosy<FunctionDecl>(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack)
					throws IOException {
				
				if(node.getName().equals("new")) {
					node.setStatic(true); // constructors *are* static
					int index = Node.find(ClassDecl.class, stack);
					if(index == -1) {
						throw new CompilationFailedError(null,
								"Declaration of a function named 'new' outside a class is prohibited!" +
								" Function named 'new' are only used as constructors in classes.");
					}
					node.setReturnType(((ClassDecl) stack.get(index)).getInstanceType());
				}
				
				if(node.isStatic()) return true; // static functions don't have a this.
				
				int index = Node.find(ClassDecl.class, stack);
				if(index == -1) return true;
				
				ClassDecl classDecl = (ClassDecl) stack.get(index);
				node.getArguments().add(0, new RegularArgument(classDecl.getInstanceType(), "this", false));
				
				return true;
				
			}
			
		}).visit(unit);

	}

}
