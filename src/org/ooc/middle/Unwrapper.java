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

		System.out.println("Here's the unwrapper...");
		
		Nosy<MustBeUnwrapped> nosy = new Nosy<MustBeUnwrapped>(MustBeUnwrapped.class, new Opportunist<MustBeUnwrapped>() {

			@Override
			public boolean take(MustBeUnwrapped node, Stack<Node> stack) {
				
				System.out.println("Trying to unwrap a "+node.getClass().getSimpleName());
				if(node.unwrap(stack)) {
					running = true;
					//System.out.println(node.getClass().getSimpleName()+" was dirty! Turning round");
					//return false;
				}
				
				//System.out.println(node.getClass().getSimpleName()+" was okay =)");
				return true;
				
			}
			
		});
		
		int count = 0;
		running = true;
		while(running) {
			if(count > MAX) {
				throw new Error("Going round in circles! More than "+MAX+" runs, abandoning...");
			}
			System.out.println("Unwrapper, round "+count+"...");
			running = false;
			nosy.visit(unit); // changes running to true if there was damage
			count++;
		}
		
	}

}
