package org.ooc.frontend.model;

public class NumberLiteral extends Literal {

	public static enum Format {
		DEC,
		OCT,
		HEX,
		BIN,
	}
	
	private long value;
	private Format format;
	
	public NumberLiteral(long value, Format format) {
		
		this.value = value;
		this.format = format;
		
	}
	
	@Override
	public Type getType() {
	
		return new Type("Int");
		
	}
	
	public long getValue() {
		return value;
	}
	
	public Format getFormat() {
		return format;
	}
	
}
