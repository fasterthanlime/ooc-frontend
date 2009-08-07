package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.middle.hobgoblins.ModularAccessResolver;
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
	public boolean resolveAccess(Stack<Node> stack, ModularAccessResolver res, final boolean fatal) throws IOException {

		
		Type exprType = expression.getType();
		if(exprType == null) {
			if(fatal) {
				throw new CompilationFailedError(null, "Calling member function "
						+name+getArgsRepr()+" in an expression "+expression.getClass().getSimpleName()
						+" which type hasn't been resolved yet!");
			}
			return false;
		}
		Declaration decl = exprType.getRef();
		if(!(decl instanceof TypeDeclaration)) {
			throw new CompilationFailedError(null, 
					"Trying to call a member function of not a TypeDecl, but a "
					+decl.getClass().getSimpleName());
		}

		TypeDeclaration typeDeclaration = (TypeDeclaration) decl;
		impl = typeDeclaration.getFunction(this);
		
		if(fatal && impl == null) {
			throw new CompilationFailedError(null, "Member function "+name+getArgsRepr()
					+" not found in type "+typeDeclaration.getInstanceType()+" (args  = "+arguments+
					"), funcs = "+typeDeclaration.getFunctions());
		}
		
		return impl == null;
		
	}
	
}
