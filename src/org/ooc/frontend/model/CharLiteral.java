package org.ooc.frontend.model;

public class CharLiteral extends Literal {

	private char value;
	
	public CharLiteral(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return value;
	}
	
	public void setValue(char value) {
		this.value = value;
	}
	
	@Override
	public Type getType() {
		return new Type("Char");
	}

}
