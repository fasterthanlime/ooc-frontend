package org.ooc.frontend.model.interfaces;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Node;
import org.ooc.middle.hobgoblins.Resolver;

public interface MustResolveAccess {

	/**
	 * @return true if @link {@link Resolver} should do one more run, false otherwise.
	 */
	public boolean resolve(Stack<Node> stack, Resolver res, boolean fatal) throws IOException;
	
	public boolean isResolved();
	
}
