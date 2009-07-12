package org.ooc.frontend.model;

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
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {
		statement.accept(visitor);
	}
	
}
