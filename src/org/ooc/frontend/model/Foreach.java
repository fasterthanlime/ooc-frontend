package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Foreach extends ControlStatement {

	protected Expression variable;
	protected Expression collection; // must be of type Range or Iterable
	
	public Foreach(VariableDecl variable, Expression collection, Token startToken) {
		super(startToken);
		this.variable = variable;
		this.collection = collection;
	}
	
	public Expression getVariable() {
		return variable;
	}
	
	public void setVariable(VariableDecl variable) {
		this.variable = variable;
	}
	
	public Expression getCollection() {
		return collection;
	}
	
	public void setCollection(Expression range) {
		this.collection = range;
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
		variable.accept(visitor);
		collection.accept(visitor);
		body.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == variable) {
			variable = (Expression) kiddo;
			return true;
		}
		
		if(oldie == collection) {
			collection = (Expression) kiddo;
			return true;
		}
		
		return false;
		
	}
	
}
