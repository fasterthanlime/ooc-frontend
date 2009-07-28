package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class While extends Conditional {

	public While(Expression condition) {
		super(condition);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == condition) {
			condition = (Expression) kiddo;
			return true;
		}
		return false;
		
	}

}
