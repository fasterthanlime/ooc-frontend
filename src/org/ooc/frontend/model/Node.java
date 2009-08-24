package org.ooc.frontend.model;

import java.util.List;
import java.util.Stack;

import org.ooc.frontend.model.tokens.Token;

public abstract class Node implements Visitable {

	public final Token startToken;
	//public final String stackTrace;
	
	public Node(Token startToken) {
		this.startToken = startToken;
		/*
		String trace = "\n";
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for(StackTraceElement elem: stack) {
			trace += "\tat "+elem.getClassName()+"("+elem.getFileName()+":"
				+elem.getLineNumber()+") in "+elem.getMethodName()+"\n";
		}
		stackTrace = trace;
		*/
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
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
	
	public String generateTempName(String seed, Stack<Node> stack) {
		String name = seed;
		int i = 0;
		while(hasVariable(name, stack)) {
			name = seed + (i++);
		}
		return name;
	}

	private boolean hasVariable(String name, Stack<Node> stack) {
		return hasVariable(name, stack, Node.find(Scope.class, stack));
	}

	private boolean hasVariable(String name, Stack<Node> stack, int index) {
		if(index == -1) return false;
		Scope scope = (Scope) stack.get(index);
		if(scope.hasVariable(name)) return true;
		return hasVariable(name, stack, Node.find(Scope.class, stack, index - 1));
	}
	
}
