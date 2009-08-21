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

	protected String underName;
	protected String fullName;
	protected String name;
	protected NodeList<Include> includes;
	protected NodeList<Import> imports;
	protected NodeList<Use> uses;
	protected NodeList<Node> body;
	protected String fileName;
	protected FunctionDecl loadFunc;
	
	public Module(String fullName) {
		
		this.fullName = fullName; // just to make sure
		this.fileName = fullName.replace('.', File.separatorChar);
		int index = fullName.lastIndexOf('.');
		if(index == -1) name = fullName;
		else name = fullName.substring(index + 1);
		this.underName = "_"+fullName.replaceAll("[^a-zA-Z0-9_]", "_");
		
		this.includes = new NodeList<Include>();
		this.imports = new NodeList<Import>();
		this.uses = new NodeList<Use>();
		this.body = new NodeList<Node>();
		
		this.loadFunc = new FunctionDecl(underName + "_load", "", false, false, false, false);
		
		if(!fullName.endsWith("ooclib"))
			imports.add(new Import("ooclib"));
		
	}
	
	public String getSimpleName() {
		return name;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getPath() {
		return getPath(".ooc");
	}

	public String getPath(String extension) {
		return getFileName() + extension;
	}

	public NodeList<Include> getIncludes() {
		return includes;
	}
	
	public NodeList<Import> getImports() {
		return imports;
	}
	
	public NodeList<Use> getUses() {
		return uses;
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
		uses.accept(visitor);
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

	protected <T extends Declaration> void getDeclarationsMap(final Class<T> clazz,
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

	protected <T extends Declaration> void getDeclarationsList(final Class<T> clazz,
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
	
	@Override
	public String toString() {
		return super.toString()+" : "+name;
	}
	
	public String getUnderName() {
		return underName;
	}

	public String getLoadFuncName() {
		return loadFunc.getName();
	}
	
	public FunctionDecl getLoadFunc() {
		return loadFunc;
	}
	
}
