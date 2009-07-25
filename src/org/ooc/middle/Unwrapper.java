package org.ooc.middle;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.MustBeUnwrapped;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.middle.Nosy.Opportunist;

public class Unwrapper implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		System.out.println("Here's the unwrapper...");
		
		new Nosy<MustBeUnwrapped>(MustBeUnwrapped.class, new Opportunist<MustBeUnwrapped>() {

			@Override
			public void take(MustBeUnwrapped node, Stack<Node> stack) {
				System.out.println("Trying to unwrap a "+node.getClass().getSimpleName());
				node.unwrap(stack);
			}
			
		}).visit(unit);
		
	}

}
