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
		return hasVariable(name, stack, stack.find(Scope.class));
	}

	private boolean hasVariable(String name, NodeList<Node> stack, int index) {
		if(index == -1) return false;
		Scope scope = (Scope) stack.get(index);
		if(scope.hasVariable(name)) return true;
		return hasVariable(name, stack, stack.find(Scope.class, index - 1));
	}
	
}
