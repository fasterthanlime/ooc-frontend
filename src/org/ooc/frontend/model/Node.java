package org.ooc.frontend.model;

import java.util.List;

import org.ooc.frontend.model.tokens.Token;

public abstract class Node implements Visitable {

	protected Token startToken;
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public Token getStartToken() {
		return startToken;
	}
	
	/** TODO find a better home for this function? Sucks that you can't add function to Stack<? extends Node> */
	public static <T> int find(Class<T> clazz, List<?> stack) {
		return find(clazz, stack, stack.size() - 1);
	}
		
	public static <T> int find(Class<T> clazz, List<?> hierarchy, int offset) {
		
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
	
}
