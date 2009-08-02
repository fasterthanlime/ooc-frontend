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
		
		for(ClassDecl decl: res.classes) {
			if(!decl.getName().equals(name)) continue;
			
			System.out.println("Found match for instantiation of "+name);
			for(FunctionDecl func: decl.getFunctions()) {
				if(!func.getName().equals("new")) continue;
				if(!suffix.isEmpty() && !func.getSuffix().equals(suffix)) continue;
				int numArgs = func.getArguments().size();				
				if(numArgs == arguments.size()
					|| ((func.getArguments().get(numArgs - 1) instanceof VarArg)
					&& (func.getArguments().size() - 1 <= arguments.size()))) {
					System.out.println("Found constructor match =)");
					setImpl(func);
					return false;
				}
			}
		}
		
		return impl != null;
		
	}
	
}
