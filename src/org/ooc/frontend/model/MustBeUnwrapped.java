package org.ooc.frontend.model;

import java.util.Stack;

/**
 * I like to use interfaces as "tags" we can apply to classes.
 * This one specifies that a node is a valid ooc statement, but must be
 * on a separate C line, e.g. must be "unwrapped".
 * 
 * Example:
 * <code>
 * printf("The answer is %d\n", Int i = 42);
 * </code>
 * 
 * Must be translated to:
 * <code>
 * {
 *   Int i = 42;
 *   printf("The answer is %d\n", i);
 * }
 * </code>
 * 
 * @author Amos Wenger
 */
public interface MustBeUnwrapped {
	
	public boolean unwrap(Stack<Node> hierarchy);
	
}
