package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Levenshtein;
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
		expression.accept(visitor);
		super.acceptChildren(visitor);
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
		
		if(exprType.getRef() == null) exprType.resolve(stack, res, fatal);

		if(tryResolve(stack, exprType)) return true;
		if(tryResolve(stack, exprType.getFlatType(res))) return true;
		
		if(fatal && ref == null) {
			String message = "Can't resolve access to member "+exprType+"."+name;
			String guess = guessCorrectName((TypeDecl) exprType.getRef());
			if(guess != null) {
				message += " Did you mean "+exprType+"."+guess+" ?";
			}
			throw new OocCompilationError(this, stack, message);
		}
		
		return ref == null;
	}

	private String guessCorrectName(final TypeDecl typeDeclaration) {
		
		if(typeDeclaration == null) {
			System.out.println("Null TypeDecl for class "+expression.getType());
			return null;
		}
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		for(VariableDecl decl: typeDeclaration.getVariables()) {
			int distance = Levenshtein.distance(name, decl.getName());
			if(distance < bestDistance) {
				bestDistance = distance;
				bestMatch = decl.getName();
			}
		}
		
		return bestMatch;
		
	}

	private boolean tryResolve(Stack<Node> stack, Type exprType)
			throws OocCompilationError, EOFException {
		
		Declaration decl = exprType.getRef();
		if(decl == null) return false;
		
		if(!(decl instanceof TypeDecl)) {
			throw new OocCompilationError(this, stack,
					"Trying to access to a member of not a TypeDecl, but a "+decl);
		}
		
		TypeDecl typeDecl = (TypeDecl) decl;
		ref = typeDecl.getVariable(name);
		
		if(ref == null && name.equals("size") && exprType.isArray) {
			FunctionCall sizeofArray = new FunctionCall("sizeof", "", startToken);
			sizeofArray.getArguments().add(expression);
			FunctionCall sizeofType = new FunctionCall("sizeof", "", startToken);
			 // FIXME it should probably be type.dereference()
			sizeofType.getArguments().add(new VariableAccess(expression.getType().getName(), startToken)); 
			Div div = new Div(sizeofArray, sizeofType, startToken);
			stack.peek().replace(this, new Parenthesis(div, startToken));
			return true;
		}
		
		return ref != null;
		
		
	}

}
