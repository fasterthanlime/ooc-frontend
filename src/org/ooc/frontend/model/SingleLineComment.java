package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;


public class SingleLineComment extends Line implements Comment {

	private String content;
	
	public SingleLineComment(String content) {
		super(new NoOp());
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
}
