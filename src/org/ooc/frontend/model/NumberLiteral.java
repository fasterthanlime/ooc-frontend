package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

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
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
}
