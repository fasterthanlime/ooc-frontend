package org.ooc.frontend.model.interfaces;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Node;
import org.ooc.middle.hobgoblins.ModularAccessResolver;

public interface MustResolveAccess {

	public boolean resolveAccess(Stack<Node> stack, ModularAccessResolver res) throws IOException;
	
	public boolean isResolved();
	
}
