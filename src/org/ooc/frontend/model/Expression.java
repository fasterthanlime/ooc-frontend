package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Expression extends Statement implements Typed {

	public Expression(Token startToken) {
		super(startToken);
	}

}
