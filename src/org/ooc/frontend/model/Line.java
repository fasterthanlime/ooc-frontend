package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Line extends Node {

	private Statement statement;

	public Line(Statement statement) {
		if(statement == null) {
			throw new Error("Who's giving me a null statement? Heck.");
		}
		this.statement = statement;
	}
	
	public Statement getStatement() {
		return statement;
	}
	
	public void setStatement(Statement statement) {
		this.statement = statement;
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
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == statement) {
			statement = (Statement) kiddo;
			return true;
		}
		
		return false;
		
	}
	
}
