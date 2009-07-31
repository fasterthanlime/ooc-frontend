package org.ooc.frontend.model.interfaces;

import java.util.Stack;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.middle.MultiMap;

public interface MustResolveAccess {

	public boolean resolveAccess(Stack<Node> stack,
			MultiMap<Node, VariableDecl> vars, MultiMap<Node, FunctionDecl> funcs);
	
	public boolean isResolved();
	
}
