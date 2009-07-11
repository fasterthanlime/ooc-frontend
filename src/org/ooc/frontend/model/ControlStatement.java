package org.ooc.frontend.model;

public class ControlStatement extends Statement {

	private NodeList<Line> body;
	
	public ControlStatement() {
		this.body = new NodeList<Line>();
	}

	public NodeList<Line> getBody() {
		return body;
	}
	
}
