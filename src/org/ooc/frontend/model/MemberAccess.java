package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class MemberAccess extends VariableAccess {
	
	protected Expression expression;

	public MemberAccess(String variable, Token startToken) {
		this(new VariableAccess("this", startToken), variable, startToken);
	}
	
	public MemberAccess(Expression expression, String variable, Token startToken) {
		super(variable, startToken);
		this.expression = expression;
	}
	
	public MemberAccess(Expression expression, VariableAccess variableAccess, Token startToken) {
		super(variableAccess.getName(), startToken);
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
				throw new OocCompilationError(this, stack, "Accessing member "
						+name+" in an expression "+expression.getClass().getSimpleName()
						+" which type hasn't been resolved yet!");
			}
			return true;
		}

		tryResolve(stack, exprType);
		if(ref == null) tryResolve(stack, exprType.getFlatType(res));
		
		if(fatal && ref == null) {
			throw new OocCompilationError(this, stack, "Can't resolve access to member "
					+exprType+"."+name);
		}
		return ref != null;
	}

	private void tryResolve(Stack<Node> stack, Type exprType)
			throws OocCompilationError, EOFException {
		Declaration decl = exprType.getRef();
		if(decl != null) {
			//System.out.println("decl = "+decl+" decl variables = "+decl.getTypeDecl().getVariablesRepr());
			if(!(decl instanceof TypeDecl)) {
				throw new OocCompilationError(this, stack, "Trying to access to a member of not a TypeDecl, but a "
						+decl);
			}
			
			TypeDecl typeDecl = (TypeDecl) decl;
			//System.out.println("Other type decl variables "+typeDecl.getVariablesRepr());
			ref = typeDecl.getVariable(name);
		}
	}

}
