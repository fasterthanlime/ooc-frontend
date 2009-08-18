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
	
	// FIXME too much similar code with MemberAssignArgument. Share it somehow.
	@Override
	public boolean unwrap(Stack<Node> hierarchy) {
		
		int typeIndex = Node.find(TypeDecl.class, hierarchy);
		if(typeIndex == -1) {
			throw new CompilationFailedError(null, "Member argument outside a class definition!");
		}
		
		TypeDecl typeDecl = (TypeDecl) hierarchy.get(typeIndex);
		VariableDecl decl = typeDecl.getVariable(name);
		if(decl == null) {
			throw new CompilationFailedError(null, "Member argument named '"+name+"" +
					"' doesn't correspond to any real member variable.");
		}

		hierarchy.peek().replace(this, new RegularArgument(decl.getType(), name));
		
		return false;
		
	}
	
}
