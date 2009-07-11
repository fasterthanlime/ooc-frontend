package org.ooc.frontend.parser;

import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.Type;

public class NullLiteral extends Literal {

	public NullLiteral() {
		// blahbedi blah, blahbidi blah, eeky eeky, ooogoozooooooooo :(
	}
	
	@Override
	public Type getType() {
		return new Type("Void", 1);
	}

}
