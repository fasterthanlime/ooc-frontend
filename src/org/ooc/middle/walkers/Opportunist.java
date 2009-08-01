package org.ooc.middle.walkers;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Node;

/**
 * @return false if you want to stop, true if you wanna continue.
 */
public interface Opportunist<T> {
	public boolean take(T node, Stack<Node> stack) throws IOException;
}