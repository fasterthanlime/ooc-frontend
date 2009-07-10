package org.ooc.frontend.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodeList<T extends Node> extends Node implements Iterable<T> {
	
	private final List<T> nodes;
	
	public NodeList() {
		nodes = new ArrayList<T>();
	}

	public void add(T element) {
		nodes.add(element);
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
	
	@Override
	public Iterator<T> iterator() {
		return nodes.iterator();
	}

}
