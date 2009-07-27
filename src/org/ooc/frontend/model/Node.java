package org.ooc.frontend.model;

import java.util.Stack;

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
	public static <T> int find(Class<T> clazz, Stack<?> hierarchy) {
		return find(clazz, hierarchy, hierarchy.size() - 1);
	}
		
	public static <T> int find(Class<T> clazz, Stack<?> hierarchy, int offset) {
		
		int i = offset;
		while(i >= 0) {
			Object node = hierarchy.get(i);
			if(clazz.isInstance(node)) {
				return i;
			}
			System.out.println(node.getClass().getSimpleName()+" is not a "+clazz.getSimpleName()+", skipping..");
			i--;
		}
		
		return -1;
		
	}
	
}
