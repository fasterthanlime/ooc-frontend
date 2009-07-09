package org.ooc.frontend.model;

public class NumberLiteral extends Literal {

	public static enum Format {
		DEC,
		OCT,
		HEX,
	}
	
	private int value;
	private Format format;
	
	public NumberLiteral(int value, Format format) {
		
		this.value = value;
		this.format = format;
		
	}
	
	@Override
	public Type getType() {
	
		return new Type("Int");
		
	}
	
	public int getValue() {
		return value;
	}
	
	public Format getFormat() {
		return format;
	}
	
}
