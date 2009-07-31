package org.ooc.frontend.model;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Nosy.Opportunist;


public class SourceUnit extends Node implements Scope {

	private String simpleName;
	private String name;
	private String fileName;
	private NodeList<Include> includes;
	private NodeList<Import> imports;
	private NodeList<Node> body;
	
	public SourceUnit(String fileName) {
		
		this.fileName = fileName;
		this.name = fileName.substring(0, fileName.lastIndexOf('.'));
		
		this.simpleName = name;
		int index1 = name.lastIndexOf(File.separatorChar);
		if(index1 != -1) simpleName = name.substring(index1);
		int index2 = name.lastIndexOf('/');
		if(index2 != -1) simpleName = simpleName.substring(index2); // just to make sure
		
		this.includes = new NodeList<Include>();
		this.imports = new NodeList<Import>();
		this.body = new NodeList<Node>();
		
	}
	
	public String getSimpleName() {
		return simpleName;
	}
	
	public String getName() {
		return name;
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
	public boolean hasChildren() {
		return true;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		includes.accept(visitor);
		imports.accept(visitor);
		body.accept(visitor);
	}
	
	public <T extends Declaration> MultiMap<Node, T> getDeclarations(final Class<T> clazz) throws IOException {

		final MultiMap<Node, T> decls = new MultiMap<Node, T>();
		
		new Nosy<T> (clazz, new Opportunist<T>() {
	
			@Override
			public boolean take(T node, Stack<Node> stack) throws IOException {
				
				int index = Node.find(Scope.class, stack);
				if(index == -1) {
					throw new Error("Found declaration "+node.getName()+" of type "
							+node.getType()+" outside of any NodeList!");
				}
				decls.add(stack.get(index), clazz.cast(node));
				return true;
				
			}
			
		}).visit(this);
		return decls;
		
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
}
