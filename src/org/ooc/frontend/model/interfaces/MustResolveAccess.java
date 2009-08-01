package org.ooc.frontend.model.interfaces;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.middle.structs.MultiMap;

public interface MustResolveAccess {

	public boolean resolveAccess(Stack<Node> stack,
			MultiMap<Node, VariableDecl> vars, MultiMap<Node, FunctionDecl> funcs) throws IOException;
	
	public boolean isResolved();
	
}
