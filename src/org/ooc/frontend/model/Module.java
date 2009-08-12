package org.ooc.frontend.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;


public class Module extends Node implements Scope {

	private String simpleName;
	private String name;
	private String fileName;
	private NodeList<Include> includes;
	private NodeList<Import> imports;
	private NodeList<Node> body;
	
	public Module(String fileName) {
		
		this.fileName = fileName;
		this.name = fileName.substring(0, fileName.lastIndexOf('.'))
					.replace(File.separatorChar, '.').replace('/', '.'); // just to make sure
		
		this.simpleName = name;
		int index = name.lastIndexOf('.');
		if(index != -1) simpleName = name.substring(index + 1);
		
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
	
	public <T extends Declaration> MultiMap<Node, T> getDeclarationsMap(final Class<T> clazz) throws IOException {

		final MultiMap<Node, T> decls = new MultiMap<Node, T>();
		this.getDeclarationsMap(clazz, decls);
		for(Import imp: imports) {
			imp.getModule().getDeclarationsMap(clazz, decls);
		}
		return decls;
		
	}

	private <T extends Declaration> void getDeclarationsMap(final Class<T> clazz,
			final MultiMap<Node, T> decls) throws IOException {
		
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
		
	}
	
	public <T extends Declaration> List<T> getDeclarationsList(final Class<T> clazz) throws IOException {

		final List<T> decls = new ArrayList<T>();
		this.getDeclarationsList(clazz, decls);
		for(Import imp: imports) {
			imp.getModule().getDeclarationsList(clazz, decls);
		}
		return decls;
		
	}

	private <T extends Declaration> void getDeclarationsList(final Class<T> clazz,
			final List<T> decls) throws IOException {
		new Nosy<T> (clazz, new Opportunist<T>() {
	
			@Override
			public boolean take(T node, Stack<Node> stack) throws IOException {
				
				int index = Node.find(Scope.class, stack);
				if(index == -1) {
					throw new Error("Found declaration "+node.getName()+" of type "
							+node.getType()+" outside of any NodeList!");
				}
				decls.add(clazz.cast(node));
				return true;
				
			}
			
		}).visit(this);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	public String getPath() {
		String extension = ".ooc";
		return getPath(extension);
	}

	public String getPath(String extension) {
		return name.replace('.', File.separatorChar) + extension;
	}
	
}
