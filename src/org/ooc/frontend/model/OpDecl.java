package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class OpDecl extends Declaration {

	public OpDecl(String name) {
		super(name);
	}

	public enum OpType {
		SQUARE_BRACKETS,
		SQUARE_BRACKETS_ASSIGN,
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Type getType() {
		return new Type("Operator");
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		// TODO check if it should really not be visited
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

}
