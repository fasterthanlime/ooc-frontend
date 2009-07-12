package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

/**
 * Binary in the sense that it has a left and a right operand (e.g. binary op,
 * as opposed to unary op or ternary op)
 */
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
	
	@Override
	public Type getType() {
		// FIXME probably not right (haha)
		return getLeft().getType();
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		left.accept(visitor);
		right.accept(visitor);
	}
	
}
