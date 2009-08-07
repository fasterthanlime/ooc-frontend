package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class RangeLiteral extends Literal {

	private Expression lower;
	private Expression upper;
	private static Type type = new Type("Range");
	
	public RangeLiteral(Expression lower, Expression upper) {
		this.lower = lower;
		this.upper = upper;
	}
	
	public Expression getLower() {
		return lower;
	}
	
	public void setLower(Expression lower) {
		this.lower = lower;
	}
	
	public Expression getUpper() {
		return upper;
	}
	
	public void setUpper(Expression upper) {
		this.upper = upper;
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
		lower.accept(visitor);
		upper.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == lower) {
			lower = (Expression) kiddo;
			return true;
		}
		
		if(oldie == upper) {
			upper = (Expression) kiddo;
			return true;
		}
		
		return false;
		
	}

}
