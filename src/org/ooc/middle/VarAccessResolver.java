package org.ooc.middle;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.Scope;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.middle.Nosy.Opportunist;

/**
 * Resolve variable accesses, e.g.
 * <code>
 * Int i = 3;
 * printf("value = %d\n", i);
 * </code>
 * 
 * Resolves the variable access one line 2 to the variable declaration
 * on line 1.
 * 
 * It also resolves member variable access, e.g. this.blah, and parenthesis-less
 * function calls, e.g. this.getBlah
 * 
 * @author Amos Wenger
 */
public class VarAccessResolver implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		System.out.println(">>> Running VarAccessResolver");

		final MultiMap<Node, VariableDecl> vars = unit.getDeclarations(VariableDecl.class);
		System.out.println("Vars: "+vars);
		final MultiMap<Node, FunctionDecl> funcs = unit.getDeclarations(FunctionDecl.class);
		System.out.println("Funcs: "+funcs);
		
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
					
					for(Declaration decl: vars.get(stackElement)) {
						System.out.println("Looking through declaration "+decl.getName()+" of type "+decl.getType());
						if(decl.getName().equals(node.getName())) {
							node.setRef(decl);
							break stacksearch;
						}
					}
					
					for(FunctionDecl decl: funcs.get(stackElement)) {
						if((decl.getArguments().size() == 0 || decl.getArguments().get(decl.getArguments().size() - 1)
								instanceof VarArg) && decl.getName().equals(node.getName())) {
							FunctionCall call = new FunctionCall(node.getName(), "");
							call.setImpl(decl); // save FuncCallResolver the trouble.
							stack.peek().replace(node, call);
							return true; // We're done here
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
