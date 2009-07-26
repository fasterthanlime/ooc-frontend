package org.ooc.middle;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.VarArg;
import org.ooc.middle.Nosy.Opportunist;
import org.ubi.CompilationFailedError;

public class FuncCallResolver implements Hobgoblin {

	@Override
	public void process(final SourceUnit unit) throws IOException {
		
		final MultiMap<String, FunctionDecl> funcs = new MultiMap<String, FunctionDecl>();
		
		/* Step 1: Collect all defined symbols */
		new Nosy<FunctionDecl>(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack) {
				System.out.println("Found function declaration "+node.getName());
				funcs.add(node.getName(), node);
				return true;
			}
			
		}).visit(unit);
		
		/* Step 2: Collect all symbols to be resolved */
		new Nosy<FunctionCall>(FunctionCall.class, new Opportunist<FunctionCall>() {
			
			@Override
			public boolean take(FunctionCall node, Stack<Node> stack) throws IOException {
				System.out.println("Call to "+node.getName());
				
				if(node instanceof MemberCall) {
					
					System.out.println("Member call!");
					MemberCall member = (MemberCall) node;
					System.out.println(member.getExpression().getType().getName()+"."+node.getName());
					
				} else if(node instanceof Instantiation) {
					
					System.out.println("Instantiation call!");
					Instantiation inst = (Instantiation) node;
					System.out.println("new "+inst.getName());
					
					findConstructor(unit, inst);
					
				} else {
					
					findRegularFunc(funcs, node);
					
				}
				
				if(node.getImpl() == null) {
					throw new CompilationFailedError(null, "No implementation found for function "
						+node.getName()+"(), did you forget to import it?");
				}
				
				return true;
			}
			
		}).visit(unit);
		
	}
	
	void findConstructor(SourceUnit unit, final Instantiation inst) throws IOException {

		new Nosy<ClassDecl>(ClassDecl.class, new Opportunist<ClassDecl>() {
			@Override
			public boolean take(ClassDecl node, Stack<Node> stack) throws IOException {
				
				if(node.getName().equals(inst.getName())) {
					final String name = "new";
					final String suffix = inst.getSuffix();
					findImpl(inst, node, name, suffix);
					// FIXME check if really found it.
					return false; // we've found the holy grail.
				}
				return true;
				
			}

		}).visit(unit);
		
	}
	
	void findImpl(final Instantiation call,
			ClassDecl root, final String name,
			final String suffix) throws IOException {
		new Nosy<FunctionDecl>(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node,
					Stack<Node> stack) {
		
				if(node.getName().equals(name) && (suffix.isEmpty() || suffix.equals(node.getSuffix()))) {
					if(node.getArguments().size() == call.getArguments().size()) {
						System.out.println("Found impl (same number of arguments)");
						call.setImpl(node);
					} else if(node.getArguments().get(node.getArguments().size() - 1) instanceof VarArg
							&& node.getArguments().size() - 1 <= call.getArguments().size()) {
						System.out.println("Found impl (vararg)");
						call.setImpl(node);
					}
					
					System.out.println("Mismatch: decl args = "+call.getArguments().size()
							+", call args = "+node.getArguments().size());
				}
				
				return false;
				
			}
			
		}).visit(root);
	}

	void findRegularFunc(final MultiMap<String, FunctionDecl> funcs, FunctionCall node) {
		
		for(FunctionDecl decl: funcs.get(node.getName())) {
			
			// TODO should compare suffix too, if any
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
	}
	
}
