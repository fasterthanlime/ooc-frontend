package org.ooc.frontend.model.interfaces;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Node;
import org.ooc.middle.hobgoblins.ModularAccessResolver;

public interface MustResolveAccess {

	/**
	 * @return true if @link {@link ModularAccessResolver} should do one more run, false otherwise.
	 */
	public boolean resolveAccess(Stack<Node> stack, ModularAccessResolver res, boolean fatal) throws IOException;
	
	public boolean isResolved();
	
}
