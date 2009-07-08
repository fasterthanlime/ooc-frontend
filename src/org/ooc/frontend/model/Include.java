package org.ooc.frontend.model;

public class Include extends Node {

	private String include;

	public Include(String include) {
		this.include = include;
	}
	
	public String getInclude() {
		return include;
	}
	
}
