package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;


public class SourceUnit extends Node {

	private String fileName;
	private NodeList<Include> includes;
	private NodeList<Import> imports;
	private NodeList<Node> body;
	
	public SourceUnit(String fileName) {
		
		this.fileName = fileName;
		this.includes = new NodeList<Include>();
		this.imports = new NodeList<Import>();
		this.body = new NodeList<Node>();
		
	}

	public String getFileName() {
		return fileName;
	}
	
	public NodeList<Include> getIncludes() {
		return includes;
	}
	
	public NodeList<Import> getImports() {
		return imports;
	}
	
	public NodeList<Node> getBody() {
		return body;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		
		for(Include include: includes) {
			include.accept(visitor);
		}
		for(Import importStatement: imports) {
			importStatement.accept(visitor);
		}
		for(Visitable node: body) {
			node.accept(visitor);
		}
		
	}
	
}
