package org.ooc.frontend.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.Visitor;

public class Include extends Node {

	public static enum Mode {
		LOCAL,
		PATHY,
	}
	
	protected String include;
	protected Mode mode;
	protected final List<String> defines;

	public Include(String include, Mode mode) {
		this.include = include;
		this.mode = mode;
		this.defines = new ArrayList<String>();
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public String getPath() {
		return include;
	}
	
	public List<String> getDefines() {
		return defines;
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
