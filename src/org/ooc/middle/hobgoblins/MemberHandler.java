package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
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
	public void process(final Module module) throws IOException {
		
		Nosy.get(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack)
					throws IOException {
				
				System.out.println("Member-handling "+node.getProtoRepr());
				
				if(node.getName().equals("main")) node.setReturnType(IntLiteral.type);
				
				if(node.isStatic()) return true; // static functions don't have a this.
				
				int index = Node.find(TypeDecl.class, stack);
				if(index == -1) {
					if(node.isConstructor()) throw new CompilationFailedError(null,
							"Declaration of a function named 'new' outside a class is prohibited!" +
					" Functions named 'new' are only used as constructors in classes.");
					node.setTypeDecl(null);
					return true;
				}
				
				if(node.getTypeDecl() != null) {
					System.out.println(node.getProtoRepr()+" Done already.");
					return true; // done already
				}
				
				TypeDecl typeDecl = (TypeDecl) stack.get(index);
				node.setTypeDecl(typeDecl);
				
				if(node.isConstructor()) {
					node.setFinal(true);
					node.setReturnType(typeDecl.getInstanceType());
				}
				
				if(typeDecl instanceof CoverDecl && node.isConstructor()) {
					VariableDecl varDecl = new VariableDecl(typeDecl.getInstanceType(), false, false, false);
					varDecl.getAtoms().add(new VariableDeclAtom("this", null));
					node.getBody().add(0, new Line(varDecl));
					node.getBody().add(new Line(new ValuedReturn(new VariableAccess(varDecl))));
				} else {
					node.getArguments().add(0, new RegularArgument(typeDecl.getInstanceType(), "this", false));
				}
				
				return true;
				
			}
			
		}).visit(module);
		
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
			
		}).visit(module);

	}

}
