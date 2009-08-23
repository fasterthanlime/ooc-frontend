package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ubi.CompilationFailedError;

public class MemberAssignArgument extends MemberArgument {

	public MemberAssignArgument(String name, Token startToken) {
		super(name, startToken);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean unwrap(Stack<Node> hierarchy) {
		
		int typeIndex = Node.find(TypeDecl.class, hierarchy);
		if(typeIndex == -1) {
			throw new CompilationFailedError(null, "Member-assign argument outside a type definition!");
		}
		
		TypeDecl typeDecl = (TypeDecl) hierarchy.get(typeIndex);
		VariableDecl decl = typeDecl.getVariable(name);
		if(decl == null) {
			throw new CompilationFailedError(null, "Member-assign argument named '"+name+"" +
					"' doesn't correspond to any real member variable.");
		}
		
		int funcIndex = Node.find(FunctionDecl.class, hierarchy);
		if(funcIndex == -1) {
			throw new CompilationFailedError(null, "Member-assign argument outside a function definition? What have" +
					" you been up to?");
		}
		
		FunctionDecl funcDecl = (FunctionDecl) hierarchy.get(funcIndex);
		funcDecl.getBody().add(0, new Line(new Assignment(
				new MemberAccess(name, startToken),
				new VariableAccess(name, startToken),
				startToken
		)));
		
		hierarchy.peek().replace(this, new RegularArgument(decl.getType(), name, startToken));
		
		return false;
		
	}
	
}
