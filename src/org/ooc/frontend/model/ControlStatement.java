package org.ooc.frontend.model;

public abstract class ControlStatement extends Statement {

	protected NodeList<Line> body;
	
	public ControlStatement() {
		this.body = new NodeList<Line>();
	}

	public NodeList<Line> getBody() {
		return body;
	}
	
}
