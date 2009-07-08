package org.ooc.frontend.model;


public class SourceUnit extends Node {

	private NodeList<Node> body;
	private String fileName;
	
	public SourceUnit(String fileName) {
		
		this.fileName = fileName;
		body = new NodeList<Node>();
		
	}

	public String getFileName() {
		return fileName;
	}
	
	public NodeList<Node> getBody() {
		
		return body;
		
	}

}
