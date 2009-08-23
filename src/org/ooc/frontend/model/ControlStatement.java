package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class ControlStatement extends Statement {

	protected NodeList<Line> body;
	
	public ControlStatement(Token startToken) {
		super(startToken);
		this.body = new NodeList<Line>(startToken);
	}

	public NodeList<Line> getBody() {
		return body;
	}
	
}
