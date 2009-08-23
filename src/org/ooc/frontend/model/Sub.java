package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Sub extends BinaryOperation {

	public Sub(Expression left, Expression right, Token token) {
		super(left, right, token);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

}
