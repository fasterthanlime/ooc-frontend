package org.ooc.frontend.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;


public class SourceUnit extends Node implements Scope {

	private String simpleName;
	private String name;
	private String fileName;
	private NodeList<Include> includes;
	private NodeList<Import> imports;
	private NodeList<Node> body;
	
	public SourceUnit(String fileName) {
		
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
		final Set<SourceUnit> done = new HashSet<SourceUnit>();
		getDeclarationsMap(clazz, decls, done);
		return decls;
		
	}

	private <T extends Declaration> void getDeclarationsMap(final Class<T> clazz,
			final MultiMap<Node, T> decls, final Set<SourceUnit> done) throws IOException {
		
		done.add(this);
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
		
		for(Import imp: imports) {
			if(!done.contains(imp.getUnit())) {
				System.out.println("Adding "+clazz.getSimpleName()+" decls in "+imp.getUnit().getName());
				imp.getUnit().getDeclarationsMap(clazz, decls, done);
			}
		}
		
	}
	
	public <T extends Declaration> List<T> getDeclarationsList(final Class<T> clazz) throws IOException {

		final List<T> decls = new ArrayList<T>();
		final Set<SourceUnit> done = new HashSet<SourceUnit>();
		getDeclarationsList(clazz, decls, done);
		return decls;
		
	}

	private <T extends Declaration> void getDeclarationsList(final Class<T> clazz,
			final List<T> decls, final Set<SourceUnit> done) throws IOException {
		done.add(this);
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
		
		for(Import imp: imports) {
			if(!done.contains(imp.getUnit())) {
				System.out.println("Adding "+clazz.getSimpleName()+" decls in "+imp.getUnit().getName());
				imp.getUnit().getDeclarationsList(clazz, decls, done);
			}
		}
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
}
