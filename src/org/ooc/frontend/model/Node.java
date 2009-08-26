package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Node implements Visitable {

	public final Token startToken;
	
	public Node(Token startToken) {
		this.startToken = startToken;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	/** TODO find a better home for this function? Sucks that you can't add function to Stack<? extends Node> */
	public static <T> int find(Class<T> clazz, NodeList<?> stack) {
		return find(clazz, stack, stack.size() - 1);
	}
		
	public static <T> int find(Class<T> clazz, NodeList<?> hierarchy, int offset) {
		
		int i = offset;
		while(i >= 0) {
			Object node = hierarchy.get(i);
			if(clazz.isInstance(node)) {
				return i;
			}
			i--;
		}
		
		return -1;
		
	}

	public abstract boolean replace(Node oldie, Node kiddo);
	
	public String generateTempName(String seed, NodeList<Node> stack) {
		String name = seed;
		int i = 0;
		while(hasVariable(name, stack)) {
			name = seed + (i++);
		}
		return name;
	}

	private boolean hasVariable(String name, NodeList<Node> stack) {
		return hasVariable(name, stack, Node.find(Scope.class, stack));
	}

	private boolean hasVariable(String name, NodeList<Node> stack, int index) {
		if(index == -1) return false;
		Scope scope = (Scope) stack.get(index);
		if(scope.hasVariable(name)) return true;
		return hasVariable(name, stack, Node.find(Scope.class, stack, index - 1));
	}
	
}
