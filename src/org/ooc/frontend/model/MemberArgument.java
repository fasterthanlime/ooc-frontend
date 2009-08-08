package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ubi.CompilationFailedError;

public class MemberArgument extends Argument {

	public MemberArgument(String name) {
		this(name, false);
	}
	
	public MemberArgument(String name, boolean isConst) {
		super(new Type(""), name, isConst);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean unwrap(Stack<Node> hierarchy) {
		
		int classIndex = Node.find(ClassDecl.class, hierarchy);
		if(classIndex == -1) {
			throw new CompilationFailedError(null, "Member argument outside a class definition!");
		}
		
		ClassDecl classDecl = (ClassDecl) hierarchy.get(classIndex);
		VariableDecl decl = classDecl.getVariable(name);
		if(decl == null) {
			throw new CompilationFailedError(null, "Member argument named '"+name+"" +
					"' doesn't correspond to any real member variable.");
		}

		hierarchy.peek().replace(this, new RegularArgument(decl.getType(), decl.getName()));
		
		return false;
		
	}
	
}
