package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Mod extends BinaryOperation {

	public Mod(Expression left, Expression right) {
		super(left, right);
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
}
