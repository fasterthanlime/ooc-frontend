package org.ooc.middle;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.Scope;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.middle.Nosy.Opportunist;

public class VarAccessResolver implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		System.out.println(">>> Running VarAccessResolver");

		final MultiMap<Node, Declaration> decls = new MultiMap<Node, Declaration>();
		
		new Nosy<VariableDecl>(VariableDecl.class, new Opportunist<VariableDecl>() {
			
			@Override
			public boolean take(VariableDecl node, Stack<Node> stack) throws IOException {
				
				System.out.println("Got "+node.getClass().getSimpleName()+" "
						+node.getName()+" child of a "+stack.peek().getClass().getSimpleName());
				int index = Node.find(Scope.class, stack);
				if(index == -1) {
					throw new Error("Found declaration "+node.getName()+" of type "
							+node.getType()+" outside of any NodeList!");
				}
				decls.add(stack.get(index), node);
				return true;
				
			}
			
		}).visit(unit);
		
		System.out.println("Decls: "+decls);
		
		new Nosy<VariableAccess>(VariableAccess.class, new Opportunist<VariableAccess>() {
			
			@Override
			public boolean take(VariableAccess node, Stack<Node> stack) throws IOException {
				
				if(node.getRef() != null) return true; // already resolved
				
				System.out.println("# Should resolve access to "+node.getName()+", stack = "+stack);
				
				int index = stack.size();
				stacksearch: while(index >= 0) {
					
					index = Node.find(Scope.class, stack, index - 1);
					if(index == -1) {
						break stacksearch;
					}
					
					Node stackElement = stack.get(index);
					
					/* The magnificent this (with a small t) hack. */
					if((stackElement instanceof FunctionDecl) && node.getName().equals("this")) {
						if(Node.find(ClassDecl.class, stack, index - 1) != -1) {
							System.out.println("$$$$$$$$$$ It's a this in a FunctionDecl in a ClassDecl!!!");
							//node.setRef((ClassDecl) stackElement);
							//break stacksearch;
						}
					}
					
					for(Declaration decl: decls.get(stackElement)) {
						System.out.println("Looking through declaration "+decl.getName()+" of type "+decl.getType());
						if(decl.getName().equals(node.getName())) {
							node.setRef(decl);
							break stacksearch;
						}
					}
					
				}
				
				if(node.getRef() == null) {
					throw new Error("Couldn't resolve variable access "+node.getName());
				}
				
				return true;
				
			}
			
		}).visit(unit);
		
	}

}
