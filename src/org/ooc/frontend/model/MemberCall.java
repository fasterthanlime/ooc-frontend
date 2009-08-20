package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.middle.hobgoblins.Resolver;
import org.ubi.CompilationFailedError;

public class MemberCall extends FunctionCall {

	private Expression expression;

	public MemberCall(Expression expression, String name, String suffix) {
		super(name, suffix);
		this.expression = expression;
	}
	
	public MemberCall(Expression expression, FunctionCall call) {
		super(call.name, call.suffix);
		this.expression = expression;
		arguments.addAll(call.getArguments());
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
	public boolean resolve(Stack<Node> mainStack, Resolver res, final boolean fatal) throws IOException {

		
		Type exprType = expression.getType();
		if(exprType == null) {
			if(fatal) {
				throw new CompilationFailedError(null, "Calling member function "
						+name+getArgsRepr()+" in an expression "+expression.getClass().getSimpleName()
						+" which type hasn't been resolved yet!");
			}
			return true;
		}
		if(exprType.getRef() == null) {
			if(fatal) {
				throw new CompilationFailedError(null, "Calling member function "
						+name+getArgsRepr()+" in an expression "+expression.getClass().getSimpleName()
						+" which type hasn't been ref'd yet, e.g. "+expression);
			}
			return true;
		}
		Declaration decl = exprType.getRef();
		if(!(decl instanceof TypeDecl)) {
			throw new CompilationFailedError(null, 
					"Trying to call a member function of not a TypeDecl, but a "
					+decl.getClass().getSimpleName());
		}

		TypeDecl typeDeclaration = (TypeDecl) decl;
		impl = typeDeclaration.getFunction(this);
		
		if(fatal && impl == null) {
			String message = "Couldn't resolve call to function "
				+typeDeclaration.getInstanceType()+"."+name+getArgsRepr()+".";
			String guess = guessCorrectName(typeDeclaration);
			if(guess != null) {
				message += " Did you mean "+typeDeclaration.getInstanceType()+"."+guess+" ?";
			} else {
				System.out.println("No guess!");
			}
			throw new CompilationFailedError(null, message);
		}
		
		return impl == null;
		
	}
	
	private String guessCorrectName(final TypeDecl typeDeclaration) {
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		for(FunctionDecl decl: typeDeclaration.getFunctionsRecursive()) {
			int distance = Levenshtein.distance(name, decl.getName());
			if(distance < bestDistance) {
				bestDistance = distance;
				bestMatch = decl.getProtoRepr();
			}
		}
		
		return bestMatch;
		
	}
	
}
