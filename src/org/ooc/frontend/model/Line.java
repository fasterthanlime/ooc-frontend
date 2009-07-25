package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Line extends Node {

	private Statement statement;

	public Line(Statement statement) {
		this.statement = statement;
	}
	
	public Statement getStatement() {
		return statement;
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
		statement.accept(visitor);
	}
	
}
