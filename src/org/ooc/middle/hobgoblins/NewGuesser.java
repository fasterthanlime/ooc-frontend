package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.VariableDeclAssigned;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Nosy.Opportunist;

/**
 * Guess the type of nameless 'new' calls, e.g.
 * <code>
 * class Blah {}
 * Blah b = new; // guessed: new Blah()
 * 
 * func accept(Blah b) {
 *   // [...]
 * }
 * 
 * accept(new); // guessed: new Blah()
 * </code>
 * 
 * @author Amos Wenger
 */
public class NewGuesser implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		new Nosy<Instantiation>(Instantiation.class, new Opportunist<Instantiation>() {

			@Override
			public boolean take(Instantiation node, Stack<Node> stack)
					throws IOException {
				
				if(node.getName().isEmpty()) {
					System.out.println("Found empty new! Guessing..");
					if(stack.peek() instanceof Assignment) {
						System.out.println("We're in an assignment, setting to type of the left-hand side.");
						Assignment ass = (Assignment) stack.peek();
						node.setName(ass.getLvalue().getType().getName());
					} else if(stack.peek() instanceof VariableDeclAssigned) {
						System.out.println("We're in an assigned variable declaration, setting to type of the left-hand side.");
						VariableDeclAssigned vda = (VariableDeclAssigned) stack.peek();
						node.setName(vda.getType().getName());
					} else {
						throw new Error("Couldn't guess type of 'new' (btw, we're in a "
								+stack.peek().getClass().getSimpleName()+")");
					}
				}
				
				return true;
				
			}
			
		}).visit(unit);

	}

}
