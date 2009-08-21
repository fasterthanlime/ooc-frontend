package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class BinaryCombination extends BinaryOperation {

	public static enum BinaryComp {
		LOGICAL_OR,
		LOGICAL_AND,
		BINARY_OR,
		BINARY_AND,
	}
	
	protected BinaryComp comp;
	
	public BinaryCombination(BinaryComp comp, Expression left, Expression right) {
		super(left, right);
		this.comp = comp;
	}

	@Override
	public Type getType() {
		if(comp == BinaryComp.LOGICAL_OR || comp == BinaryComp.LOGICAL_AND) {
			return BoolLiteral.type;
		}
		return left.getType();
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
		case LOGICAL_OR: return "||";
		case LOGICAL_AND: return "&&";
		case BINARY_OR: return "|";
		case BINARY_AND: default: return "&";
		}
	}

}
