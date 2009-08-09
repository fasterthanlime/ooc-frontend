package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

/**
 * Adds as a first parameter 'this' to every member function, and make constructors
 * static.
 * 
 * @author Amos Wenger
 */
public class MemberHandler implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		Nosy.get(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack)
					throws IOException {
				
				if(node.getName().equals("new")) {
					node.setFinal(true);
					int index = Node.find(ClassDecl.class, stack);
					if(index == -1) {
						throw new CompilationFailedError(null,
								"Declaration of a function named 'new' outside a class is prohibited!" +
								" Function named 'new' are only used as constructors in classes.");
					}
					node.setReturnType(((ClassDecl) stack.get(index)).getInstanceType());
				}
				
				if(node.isStatic()) return true; // static functions don't have a this.
				
				int index = Node.find(TypeDecl.class, stack);
				if(index == -1) {
					node.setTypeDecl(null);
					return true;
				}
				
				TypeDecl classDecl = (TypeDecl) stack.get(index);
				node.getArguments().add(0, new RegularArgument(classDecl.getInstanceType(), "this", false));
				node.setTypeDecl(classDecl);
				System.out.println("Added this to function "+node);
				
				return true;
				
			}
			
		}).visit(unit);
		
		Nosy.get(VariableDecl.class, new Opportunist<VariableDecl>() {

			@Override
			public boolean take(VariableDecl node, Stack<Node> stack) throws IOException {
				
				if(stack.size() >= 2) {
					Node decl = stack.get(stack.size() - 2);
					if(decl instanceof TypeDecl) {
						node.setTypeDecl((TypeDecl) decl);
					}
				}
				return true;
				
			}
			
		}).visit(unit);

	}

}
