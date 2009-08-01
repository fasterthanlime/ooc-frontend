package org.ooc.middle.hobgoblins;

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
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

public class FuncCallResolver implements Hobgoblin {

	@Override
	public void process(final SourceUnit unit) throws IOException {
		
		final MultiMap<String, FunctionDecl> funcs = new MultiMap<String, FunctionDecl>();
		
		/* Step 1: Collect all defined symbols */
		new Nosy<FunctionDecl>(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack) {
				funcs.add(node.getName(), node);
				return true;
			}
			
		}).visit(unit);
		
		/* Step 2: Collect all symbols to be resolved */
		new Nosy<FunctionCall>(FunctionCall.class, new Opportunist<FunctionCall>() {
			
			@Override
			public boolean take(FunctionCall node, Stack<Node> stack) throws IOException {
				
				if(node instanceof MemberCall) {
					
					MemberCall member = (MemberCall) node;
					findMemberImpl(unit, member, member.getExpression().getType().getName(),
							member.getName(), member.getSuffix());
					
				} else if(node instanceof Instantiation) {
					
					Instantiation inst = (Instantiation) node;
					findMemberImpl(unit, inst, inst.getName(), "new", inst.getSuffix());
					
				} else {
					
					findImpl(funcs, node);
					
				}
				
				if(node.getImpl() == null) {
					if(node instanceof Instantiation) {
						throw new CompilationFailedError(null, "No constructor for class "
								+node.getName()+" matches the given arguments.");
					}
					throw new CompilationFailedError(null, "No implementation found for function "
							+node.getName()+"(), did you forget to import it?");
				}
				
				return true;
			}
			
		}).visit(unit);
		
	}
	
	void findMemberImpl(SourceUnit unit, final FunctionCall call, final String className,
			final String name, final String suffix) throws IOException {

		new Nosy<ClassDecl>(ClassDecl.class, new Opportunist<ClassDecl>() {
			@Override
			public boolean take(ClassDecl node, Stack<Node> stack) throws IOException {
				
				if(node.getName().equals(className)) {
					findImpl(call, node, name, suffix);
					// FIXME check if we really found it.
					return false; // we've found the holy grail.
				}
				return true;
				
			}

		}).visit(unit);
		
	}
	
	void findImpl(final FunctionCall call,
			ClassDecl root, final String name,
			final String suffix) throws IOException {
		new Nosy<FunctionDecl>(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node,
					Stack<Node> stack) {
		
				int offset = node.isStatic() ? 0 : 1;
				
				if(node.getName().equals(name) && (suffix.isEmpty() || suffix.equals(node.getSuffix()))) {
					if((node.getArguments().size() - offset) == call.getArguments().size()) {
						call.setImpl(node);
					} else if(node.getArguments().get(node.getArguments().size() - 1) instanceof VarArg
							&& node.getArguments().size() - 1 <= call.getArguments().size()) {
						call.setImpl(node);
					}
				}
				
				return false;
				
			}
			
		}).visit(root);
	}

	void findImpl(final MultiMap<String, FunctionDecl> funcs, FunctionCall node) {
		
		for(FunctionDecl decl: funcs.get(node.getName())) {
			
			// TODO should compare suffix too, if any
			if(decl.getArguments().size() == node.getArguments().size()) {
				node.setImpl(decl);
				break;
			}
			
			NodeList<Argument> args = decl.getArguments();
			if(args.get(args.size() - 1) instanceof VarArg && args.size() - 1 <= node.getArguments().size()) {
				node.setImpl(decl);
				break;
			}
			
		}
	}
	
}
