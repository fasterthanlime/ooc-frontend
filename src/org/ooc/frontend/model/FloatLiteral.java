package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class FloatLiteral extends Literal {

	private double value;
	private static Type type = new Type("Float");

	public FloatLiteral(double value) {
		this.value = value;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public double getValue() {
		return value;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}

	@Override
	public boolean hasChildren() {
		return false;
	}

}
