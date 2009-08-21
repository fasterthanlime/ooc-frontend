package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class OocDocComment extends Node {

	private String content;

	public OocDocComment(String content) {
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
		//visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

}
