package org.ooc.frontend.model;

import java.io.IOException;

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
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

}
