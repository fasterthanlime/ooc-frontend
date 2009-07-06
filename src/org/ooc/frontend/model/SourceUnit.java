package org.ooc.frontend.model;

import org.ubi.FileLocation;

public class SourceUnit extends Node {

	private FileLocation location;
	private NodeList<Declaration> body;
	
	public SourceUnit(FileLocation location) {
		
		this.location = location;
		body = new NodeList<Declaration>();
		
	}
	
	public FileLocation getLocation() {
		
		return location;
		
	}
	
	public NodeList<Declaration> getBody() {
		
		return body;
		
	}

}
