package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Include extends Node {

	public static enum Mode {
		LOCAL,
		PATHY,
	}
	
	private String include;
	private Mode mode;

	public Include(String include, Mode mode) {
		this.include = include;
		this.mode = mode;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public String getPath() {
		return include;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
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
	
	@Override
	public String toString() {
		return super.toString()+" : "+include;
	}
	
}
