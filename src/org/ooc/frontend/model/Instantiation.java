package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.middle.hobgoblins.ModularAccessResolver;
import org.ubi.CompilationFailedError;

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
	public boolean resolveAccess(Stack<Node> stack, ModularAccessResolver res, boolean fatal) throws IOException {
		
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
		
		if(fatal && impl == null) {
			throw new CompilationFailedError(null, "Couldn't find a constructor in "+name+" for arguments "+getArgsRepr());
		}
		
		return impl != null;
		
	}

	/**
	 * Guess the type of nameless 'new' calls, e.g.
	 * <code>
	 * class Blah {}
	 * Blah b = new; // guessed: new Blah()
	 * 
	 * func accept(Blah b) {
	 *   // [...]
	 * }
	 * 
	 * accept(new); // guessed: new Blah()
	 * </code>
	 */
	private boolean guessName(Stack<Node> stack) throws Error {
		
		if(stack.peek() instanceof Assignment) {
			Assignment ass = (Assignment) stack.peek();
			if (ass.getLvalue().getType() == null) return false;
			name = ass.getLvalue().getType().getName();
		} else if(stack.peek() instanceof VariableDeclAtom) {
			VariableDeclAtom vda = (VariableDeclAtom) stack.peek();
			if(vda.getExpression() == this) {
				VariableDecl vd = (VariableDecl) stack.get(Node.find(VariableDecl.class, stack));
				name = vd.getType().getName();
			}
		} else {
			throw new Error("Couldn't guess type of 'new' (btw, we're in a "
					+stack.peek().getClass().getSimpleName()+")");
		}
		
		return true;
		
	}
	
}
