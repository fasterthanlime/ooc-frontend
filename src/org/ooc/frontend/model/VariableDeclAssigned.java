package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;

public class VariableDeclAssigned extends VariableDecl {

	protected Expression expression;

	public VariableDeclAssigned(Type type, String name, Expression expression,
			boolean isConst, boolean isStatic) {
		super(type, name, isConst, isStatic);
		this.expression = expression;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		super.acceptChildren(visitor);
		expression.accept(visitor);
	}
	
	@Override
	public boolean unwrap(Stack<Node> hierarchy) {
		
		int index = Node.find(ClassDecl.class, hierarchy);
		if(index == -1) {
			return super.unwrap(hierarchy);
		}
		
		ClassDecl classDecl = (ClassDecl) hierarchy.get(index);
		classDecl.getInitializer().getBody().add(new Line(new Assignment(new MemberAccess(
				new VariableAccess("this"), name), expression)));
		hierarchy.peek().replace(this, new VariableDecl(type, name,isConst, isStatic));
		
		return false;
		
	}
	
}

