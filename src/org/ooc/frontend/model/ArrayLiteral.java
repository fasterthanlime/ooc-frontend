package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class ArrayLiteral extends Literal {

	private Type type = new Type("Pointer");
	
	public ArrayLiteral() {
		
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

}
