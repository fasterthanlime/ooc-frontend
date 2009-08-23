package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Compare extends BinaryOperation {

	public static enum CompareType {
		GREATER,
		GREATER_OR_EQUAL,
		LESSER,
		LESSER_OR_EQUAL,
		EQUAL,
		NOT_EQUAL,
	}

	protected CompareType compareType;
	
	public Compare(Expression left, Expression right, CompareType compareType, Token token) {
		super(left, right, token);
		this.compareType = compareType;
	}
	
	public CompareType getCompareType() {
		return compareType;
	}
	
	public void setCompareType(CompareType compareType) {
		this.compareType = compareType;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

}
