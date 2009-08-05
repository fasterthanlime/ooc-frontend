package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.middle.hobgoblins.ModularAccessResolver;

public class MemberCall extends FunctionCall {

	private Expression expression;

	public MemberCall(Expression expression, String name, String suffix) {
		super(name, suffix);
		this.expression = expression;
	}
	
	public MemberCall(Expression expression, FunctionCall call) {
		super(call.name, call.suffix);
		this.expression = expression;
		
	}

	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		expression.accept(visitor);
		super.acceptChildren(visitor);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(super.replace(oldie, kiddo)) return true;
		
		if(oldie == expression) {
			expression = (Expression) kiddo;
			return true;
		}
		return false;
		
	}
	
	@Override
	public boolean resolveAccess(Stack<Node> stack, ModularAccessResolver res) throws IOException {
		
		Declaration decl = expression.getType().getRef();
		if(!(decl instanceof ClassDecl)) {
			throw new Error("Trying to call a member function of not a ClassDecl, but a "
					+decl.getClass().getSimpleName());
		}

		ClassDecl classDecl = (ClassDecl) decl;
		FunctionDecl funcDecl = classDecl.getFunction(name, suffix);
		if(funcDecl == null) {
			throw new Error("Member function "+name+" not found in class "+classDecl.getType());
		}
		impl = funcDecl;
		
		return impl != null;
		
	}
	
}
