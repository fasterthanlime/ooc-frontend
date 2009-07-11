package org.ooc.frontend.model;

public class SingleLineComment extends Line {

	private String content;
	
	public SingleLineComment(String content) {
		super(null);
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
