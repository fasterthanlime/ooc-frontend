package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class Mul extends BinaryOperation {

	public Mul(Expression left, Expression right) {
		super(left, right);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
