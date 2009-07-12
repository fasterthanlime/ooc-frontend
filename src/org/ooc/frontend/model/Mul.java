package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Mul extends BinaryOperation {

	public Mul(Expression left, Expression right) {
		super(left, right);
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

}
