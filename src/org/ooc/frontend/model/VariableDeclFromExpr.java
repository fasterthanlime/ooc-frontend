package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class VariableDeclFromExpr extends VariableDecl {

	public VariableDeclFromExpr(String name, Expression expression) {
		super(null, false, false, false);
		atoms.add(new VariableDeclAtom(name, expression));
	}

	@Override
	public Type getType() {
		return atoms.get(0).getExpression().getType();
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
	
}
