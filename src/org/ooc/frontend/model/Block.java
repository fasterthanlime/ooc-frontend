package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Block extends Node {

	public final NodeList<Line> body;
	
	public Block() {
		body = new NodeList<Line>();
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		body.accept(visitor);
	}

	@Override
	public boolean hasChildren() {
		return body.hasChildren();
	}

}
