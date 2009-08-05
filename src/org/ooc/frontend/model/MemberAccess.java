package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.middle.hobgoblins.ModularAccessResolver;

public class MemberAccess extends VariableAccess {
	
	private Expression expression;

	public MemberAccess(Expression expression, String variable) {
		super(variable);
		this.expression = expression;
	}
	
	public MemberAccess(Expression expression, VariableAccess variableAccess) {
		super(variableAccess.getName());
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
		super.acceptChildren(visitor);
		expression.accept(visitor);
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
	public boolean resolveAccess(Stack<Node> stack, ModularAccessResolver res)
			throws IOException {
		
		Declaration decl = expression.getType().getRef();
		if(!(decl instanceof ClassDecl)) {
			throw new Error("Trying to access to a member of not a ClassDecl, but a "
					+decl.getClass().getSimpleName());
		}

		ClassDecl classDecl = (ClassDecl) decl;
		
		VariableDecl varDecl = classDecl.getVariable(variable);
		if(varDecl == null) {
			FunctionDecl funcDecl = classDecl.getFunction(variable, "");
			if(funcDecl != null && (funcDecl.getArguments().isEmpty()
					|| funcDecl.getArguments().getLast() instanceof VarArg
					|| funcDecl.isMember() && (funcDecl.getArguments().size() == 1
							|| funcDecl.getArguments().getBeforeLast() instanceof VarArg))) {
				System.out.println("Found as a function call");
				MemberCall membCall = new MemberCall(expression, variable, "");
				membCall.setImpl(funcDecl);
				stack.peek().replace(this, membCall);
				return true;
			}
		}
		ref = varDecl;
		
		return ref != null;
		
	}

}
