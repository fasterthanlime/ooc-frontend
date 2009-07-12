package org.ooc.frontend.model;


public class NullLiteral extends Literal {

	public NullLiteral() {
		// blahbedi blah, blahbidi blah, eeky eeky, ooogoozooooooooo :(
	}
	
	@Override
	public Type getType() {
		return new Type("Void", 1);
	}

}
