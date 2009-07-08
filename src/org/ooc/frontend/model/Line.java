package org.ooc.frontend.model;

public class Line extends Node {

	private Statement statement;

	public Line(Statement statement) {
		this.statement = statement;
	}
	
	public Statement getStatement() {
		return statement;
	}
	
}
