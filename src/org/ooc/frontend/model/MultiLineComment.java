package org.ooc.frontend.model;

public abstract class MultiLineComment extends Node implements Comment {

	private String content;

	public MultiLineComment(String content) {
		setContent(content);
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
}
