package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class CharLiteral extends Literal {

	private char value;
	private static Type type = new Type("Char");
	
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
		return type;
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
