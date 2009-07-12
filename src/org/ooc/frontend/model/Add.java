package org.ooc.frontend.model;

public class Add extends BinaryOperation {

	@Override
	public Type getType() {
		return getLeft().getType();
	}
	
}
