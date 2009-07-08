package org.ooc.frontend.model;

public class NumberLiteral extends Literal {

	public static enum Format {
		DEC,
		OCT,
		HEX,
	}
	
	int value;
	Format format;
	
	public NumberLiteral(int value, Format format) {
		
		this.value = value;
		this.format = format;
		
	}
	
	@Override
	public Type getType() {
	
		return new Type("Int");
		
	}
	
}
