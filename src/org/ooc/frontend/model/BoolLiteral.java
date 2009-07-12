package org.ooc.frontend.model;


public class BoolLiteral extends Literal {

	private boolean value;
	
	public BoolLiteral(boolean value) {
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public Type getType() {
		return new Type("Bool");
	}

}
