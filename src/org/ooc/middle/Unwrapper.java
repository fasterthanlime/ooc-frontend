package org.ooc.middle;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.MustBeUnwrapped;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.middle.Nosy.Opportunist;

/**
 * The {@link Unwrapper} transforms this kind of statement
 * <code>
 * printf("The answer is %d\n", Int i = 42);
 * </code>
 * 
 * Into this kind of statement:
 * <code>
 * {
 *   Int i = 42;
 *   printf("The answer is %d\n", i);
 * }
 * </code>
 * 
 * Of course, the {@link Unwrapper} does nothing by itself,
 * it's all specified in node classes which implement
 * the {@link MustBeUnwrapped} interface.
 * 
 * @author Amos Wenger
 */
public class Unwrapper implements Hobgoblin {

	private static final int MAX = 1024;
	boolean running;
	
	@Override
	public void process(SourceUnit unit) throws IOException {

		Nosy<MustBeUnwrapped> nosy = new Nosy<MustBeUnwrapped>(MustBeUnwrapped.class, new Opportunist<MustBeUnwrapped>() {

			@Override
			public boolean take(MustBeUnwrapped node, Stack<Node> stack) {
				
				if(node.unwrap(stack)) {
					running = true;
				}
				return true;
				
			}
			
		});
		
		int count = 0;
		running = true;
		while(running) {
			if(count > MAX) {
				throw new Error("Unwrapper going round in circles! More than "+MAX+" runs, abandoning...");
			}
			running = false;
			nosy.visit(unit); // changes running to true if there was damage
			count++;
		}
		
	}

}
