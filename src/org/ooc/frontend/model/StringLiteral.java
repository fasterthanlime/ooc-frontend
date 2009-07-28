package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

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
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

}
