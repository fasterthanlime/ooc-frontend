package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class VariableDeclFromExpr extends VariableDecl {

	public VariableDeclFromExpr(String name, Expression expression, Token startToken) {
		super(null, false, false, startToken);
		atoms.add(new VariableDeclAtom(name, expression, startToken));
	}

	@Override
	public Type getType() {
		VariableDeclAtom atom = atoms.get(0);
		Expression expr = atom.getExpression();
		if(expr == null) return atom.assign.getRvalue().getType();
		return expr.getType();
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		atoms.accept(visitor);
		if(getType() != null) getType().accept(visitor);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+" of type "+getType()+" : "+getName();
	}
	
	@Override
	protected void unwrapToClassInitializers(Stack<Node> hierarchy,
			ClassDecl classDecl) {
		
		super.unwrapToClassInitializers(hierarchy, classDecl);
		atoms.get(0).setExpression(null);
		
	}
	
}
