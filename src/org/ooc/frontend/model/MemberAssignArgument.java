package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ubi.CompilationFailedError;

public class MemberAssignArgument extends MemberArgument {

	public MemberAssignArgument(String name) {
		super(name);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean unwrap(Stack<Node> hierarchy) {
		
		System.out.println("Here MemberAssignArgument, should unwrap, hierarchy = "+hierarchy);
		
		int classIndex = Node.find(ClassDecl.class, hierarchy);
		if(classIndex == -1) {
			throw new CompilationFailedError(null, "Member-assign argument outside a class definition!");
		}
		
		ClassDecl classDecl = (ClassDecl) hierarchy.get(classIndex);
		VariableDecl decl = classDecl.getVariable(name);
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
		funcDecl.getBody().add(0, new Line(new Assignment(new MemberAccess(new VariableAccess("this"), name),
				new VariableAccess(name))));
		
		hierarchy.peek().replace(this, new RegularArgument(decl.getType(), decl.getName()));
		
		return false;
		
	}
	
}
