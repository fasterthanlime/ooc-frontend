package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class OocDocComment extends Comment {

	public OocDocComment(String content) {
		super(content);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) {}

}
