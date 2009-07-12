package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;


public class Sub extends BinaryOperation {

	public Sub(Expression left, Expression right) {
		super(left, right);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
