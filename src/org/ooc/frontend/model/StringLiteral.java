package org.ooc.frontend.model;

public class StringLiteral extends Literal {

	private String value;
	
	public StringLiteral(String value) {
		
		this.value = value;
		
	}
	
	@Override
	public Type getType() {

		return new Type("String");
		
	}
	
	public String getValue() {
		
		return value;
		
	}

}
