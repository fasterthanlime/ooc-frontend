package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class BinaryCombination extends BinaryOperation {

	public static enum BinaryComp {
		OR,
		AND,
	}
	
	protected BinaryComp comp;
	
	public BinaryCombination(BinaryComp comp, Expression left, Expression right) {
		super(left, right);
		this.comp = comp;
	}

	@Override
	public Type getType() {
		return BoolLiteral.type;
	}
	
	public BinaryComp getComp() {
		return comp;
	}
	
	public void setComp(BinaryComp comp) {
		this.comp = comp;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public String getOpString() {
		switch(comp) {
		case OR: return "||";
		default: return "&&";
		}
	}

}
