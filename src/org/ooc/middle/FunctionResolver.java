package org.ooc.middle;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.VarArg;
import org.ooc.middle.Nosy.Opportunist;
import org.ubi.CompilationFailedError;

public class FunctionResolver implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		final MultiMap<String, FunctionDecl> funcs = new MultiMap<String, FunctionDecl>();
		
		/* Step 1: Collect all defined symbols */
		new Nosy<FunctionDecl>(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public void take(FunctionDecl node, Stack<Node> stack) {
				System.out.println("Found function declaration "+node.getName());
				funcs.add(node.getName(), node);
			}
			
		}).visit(unit);
		
		/* Step 2: Collect all symbols to be resolved */
		new Nosy<FunctionCall>(FunctionCall.class, new Opportunist<FunctionCall>() {
			
			@Override
			public void take(FunctionCall node, Stack<Node> stack) {
				System.out.println("Call to "+node.getName());
				for(FunctionDecl decl: funcs.get(node.getName())) {
					
					System.out.println("Comparing "+decl.getName()+" and "+node.getName());
					if(decl.getArguments().size() == node.getArguments().size()) {
						System.out.println("Found impl! (same number of arguments)");
						node.setImpl(decl);
						break;
					}
					
					NodeList<Argument> args = decl.getArguments();
					if(args.get(args.size() - 1) instanceof VarArg && args.size() - 1 <= node.getArguments().size()) {
						System.out.println("Found impl! (varargs)");
						node.setImpl(decl);
						break;
					}
					
					System.out.println("Mismatch: decl args = "+args.size()+", call args = "+node.getArguments().size());
					
				}
				
				if(node.getImpl() == null) {
					throw new CompilationFailedError(null, "No implementation found for function "
						+node.getName()+"(), did you forget to import it?");
				}
			}
			
		}).visit(unit);
		
	}
	
}
