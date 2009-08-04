package org.ooc.frontend.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ooc.frontend.Visitor;

public class NodeList<T extends Node> extends Node implements Iterable<T> {
	
	private final List<T> nodes;
	
	public NodeList() {
		nodes = new ArrayList<T>();
	}

	public void add(T element) {
		nodes.add(element);
	}
	
	public void add(int index, T element) {
		nodes.add(index, element);
	}
	
	public boolean remove(T element) {
		return nodes.remove(element);
	}
	
	public boolean contains(T element) {
		return nodes.contains(element);
	}
	
	public int size() {
		return nodes.size();
	}
	
	public boolean isEmpty() {
		return nodes.isEmpty();
	}
	
	public T get(int i) {
		return nodes.get(i);
	}
	
	public void set(int i, T element) {
		nodes.set(i, element);
	}
	
	public void setAll(NodeList<T> list) {
		nodes.clear();
		nodes.addAll(list.nodes);
	}
	
	public T getFirst() {
		return nodes.get(0);
	}
	
	public T getLast() {
		return nodes.get(nodes.size() - 1);
	}
	
	@Override
	public Iterator<T> iterator() {
		return nodes.iterator();
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		int i = 0;
		while(i < nodes.size()) {
			nodes.get(i++).accept(visitor);
		}
	}

	@Override
	public boolean hasChildren() {
		return !nodes.isEmpty();
	}
	
	public int indexOf(T lostSheep) {
		return nodes.indexOf(lostSheep);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean replace(Node oldie, Node kiddo) {
		int index = nodes.indexOf(oldie);
		if(index == -1) {
			throw new ArrayIndexOutOfBoundsException("Trying to replace a "
					+oldie.getClass().getName()+" with a "+kiddo.getClass().getSimpleName()+
					" in a "+this.getClass().getSimpleName()+", but couldn't find node to replace.");
		}
		nodes.set(index, (T) kiddo);
		return true;
	}

	public void addBefore(Line ref, T kiddo) {
		int index = nodes.indexOf(ref);
		if(index == -1) {
			throw new ArrayIndexOutOfBoundsException("Trying to add a "
					+kiddo.getClass().getName()+" before a "+ref.getClass().getSimpleName()+
					" in a "+this.getClass().getSimpleName()+", but couldn't find reference node.");
		}
		nodes.add(index, kiddo);
	}
	
	@Override
	public String toString() {
		return nodes.toString();
	}

}
