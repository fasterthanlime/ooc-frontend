package org.ooc.frontend.model;

import java.io.File;
import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Import extends Node {

	protected String name;
	protected SourceUnit unit;

	public Import(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public SourceUnit getUnit() {
		return unit;
	}
	
	public void setUnit(SourceUnit unit) {
		this.unit = unit;
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

	public String getPath() {
		return name.replace('.', File.separatorChar) + ".ooc";
	}
	
}
