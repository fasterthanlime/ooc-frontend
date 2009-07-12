package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;


public class NullLiteral extends Literal {

	public NullLiteral() {
		// blahbedi blah, blahbidi blah, eeky eeky, ooogoozooooooooo :(
	}
	
	@Override
	public Type getType() {
		return new Type("Void", 1);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {}

}
