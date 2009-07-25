package org.ooc.frontend.model;

import java.util.Stack;

import org.ooc.frontend.model.tokens.Token;

public abstract class Node implements Visitable {

	protected Token startToken;
	
	@Override
	public String toString() {
		return getClass().getName();
	}
	
	public Token getStartToken() {
		return startToken;
	}
	
	/** TODO find a better home for this function? Sucks that you can't add function to Stack<? extends Node> */
	public static <T extends Node> int find(Class<T> clazz, Stack<Node> hierarchy) {
		return find(clazz, hierarchy, hierarchy.size() - 1);
	}
		
	public static <T extends Node> int find(Class<T> clazz, Stack<Node> hierarchy, int offset) {
		
		int i = offset;
		while(i > 0) {
			Node node = hierarchy.get(i);
			if(clazz.isInstance(node)) {
				return i;
			}
			i--;
		}
		
		return -1;
		
	}
	
}
