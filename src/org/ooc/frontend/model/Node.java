package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Node implements Visitable {

	protected Token startToken;
	
	@Override
	public String toString() {
		return getClass().getName();
	}
	
	public Token getStartToken() {
		return startToken;
	}
	
}
