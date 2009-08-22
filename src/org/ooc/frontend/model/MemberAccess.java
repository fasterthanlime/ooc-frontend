package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.middle.hobgoblins.Resolver;
import org.ubi.CompilationFailedError;

public class MemberAccess extends VariableAccess {
	
	protected Expression expression;

	public MemberAccess(String variable) {
		this(new VariableAccess("this"), variable);
	}
	
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
	public boolean resolve(Stack<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		Type exprType = expression.getType();
		if(exprType == null) {
			if(fatal) {
				throw new CompilationFailedError(null, "Accessing member "
						+name+" in an expression "+expression.getClass().getSimpleName()
						+" which type hasn't been resolved yet!");
			}
			return true;
		}

		//System.out.println("==========================");
		//System.out.println("Should resolve variable access "+expression+"."+variable);
		//System.out.println("exprType = "+exprType);
		//System.out.println("exprType ref = "+exprType.getRef());
		
		Declaration decl = exprType.getRef();
		if(!(decl instanceof TypeDecl)) {
			throw new Error("Trying to access to a member of not a TypeDecl, but a "
					+decl.getClass().getSimpleName());
		}
		
		TypeDecl typeDecl = (TypeDecl) decl;
		ref = typeDecl.getVariable(name);
		
		if(fatal && ref == null) {
			throw new CompilationFailedError(null, "Can't resolve access to member "
					+exprType+"."+name);
		}
		return ref != null;
	}

}
