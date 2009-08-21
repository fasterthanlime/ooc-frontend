package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class StringLiteral extends Literal {

	protected String value;
	public static Type type = new Type("String");
	
	public StringLiteral(String value) {
		this.value = value;
	}
	
	@Override
	public Type getType() {
		return type;
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
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

}
