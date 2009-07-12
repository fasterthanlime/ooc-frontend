package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class Add extends BinaryOperation {

	public Add(Expression left, Expression right) {
		super(left, right);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
}
