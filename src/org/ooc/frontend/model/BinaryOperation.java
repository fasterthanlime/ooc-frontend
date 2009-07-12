package org.ooc.frontend.model;

public abstract class BinaryOperation extends Expression {

	private Expression left;
	private Expression right;
	
	public BinaryOperation(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}
	
	public Expression getLeft() {
		return left;
	}
	
	public void setLeft(Expression left) {
		this.left = left;
	}
	
	public Expression getRight() {
		return right;
	}
	
	public void setRight(Expression right) {
		this.right = right;
	}
	
}
