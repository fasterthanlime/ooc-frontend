package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.middle.hobgoblins.ModularAccessResolver;

public class Instantiation extends FunctionCall {

	public Instantiation(FunctionCall call) {
		super(call.name, call.suffix);
		arguments.setAll(call.arguments);
	}

	public Instantiation(String name, String suffix) {
		super(name, suffix);
	}
	
	public Instantiation() {
		super("", "");
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean resolveAccess(Stack<Node> stack, ModularAccessResolver res) throws IOException {
		
		if(name.isEmpty()) {
			guessName(stack);
		}
		
		for(ClassDecl decl: res.classes) {
			if(!decl.getName().equals(name)) continue;
			
			for(FunctionDecl func: decl.getFunctions()) {
				if(!func.isConstructor()) continue;
				if(!suffix.isEmpty() && !func.getSuffix().equals(suffix)) continue;
				int numArgs = func.getArguments().size() - 1;
				if(numArgs == arguments.size()
					|| ((!func.getArguments().isEmpty() && func.getArguments().getLast() instanceof VarArg)
					&& (numArgs <= arguments.size()))) {
					setImpl(func);
					return false;
				}
			}
		}
		
		return impl != null;
		
	}

	private void guessName(Stack<Node> stack) throws Error {
		
		if(stack.peek() instanceof Assignment) {
			Assignment ass = (Assignment) stack.peek();
			name = ass.getLvalue().getType().getName();
		} else if(stack.peek() instanceof VariableDeclAssigned) {
			VariableDeclAssigned vda = (VariableDeclAssigned) stack.peek();
			name = vda.getType().getName();
		} else {
			throw new Error("Couldn't guess type of 'new' (btw, we're in a "
					+stack.peek().getClass().getSimpleName()+")");
		}
		
	}
	
}
