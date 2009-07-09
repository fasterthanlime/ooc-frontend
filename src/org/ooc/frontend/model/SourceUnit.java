package org.ooc.frontend.model;


public class SourceUnit extends Node {

	private String fileName;
	private NodeList<Node> body;
	private NodeList<Include> includes;
	
	public SourceUnit(String fileName) {
		
		this.fileName = fileName;
		this.body = new NodeList<Node>();
		this.includes = new NodeList<Include>();
		
	}

	public String getFileName() {
		return fileName;
	}
	
	public NodeList<Node> getBody() {
		return body;
	}
	
	public NodeList<Include> getIncludes() {
		return includes;
	}
	
}
