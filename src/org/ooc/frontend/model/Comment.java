package org.ooc.frontend.model;

public abstract class Comment extends Node {

	private String content;

	public Comment(String content) {
		setContent(content);
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
}
