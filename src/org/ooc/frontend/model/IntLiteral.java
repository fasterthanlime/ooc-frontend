package org.ooc.frontend.model;

public class IntLiteral extends Literal {

	static enum Format {
		DEC,
		OCT,
		HEX,
	}
	
	int value;
	Format format;
	
	public IntLiteral(int value, Format format) {
		
		this.value = value;
		this.format = format;
		
	}
	
	@Override
	public Type getType() {
	
		return new Type("Int");
		
	}
	
}
