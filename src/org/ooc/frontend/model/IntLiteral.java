package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class IntLiteral extends Literal {

	public static enum Format {
		DEC,
		OCT,
		HEX,
		BIN,
	}
	
	private long value;
	private Format format;
	public static Type type = new Type("Int");
	
	public IntLiteral(long value, Format format) {
		
		this.value = value;
		this.format = format;
		
	}
	
	@Override
	public Type getType() {
		return type;
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
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		visitor.visit(type);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
}
