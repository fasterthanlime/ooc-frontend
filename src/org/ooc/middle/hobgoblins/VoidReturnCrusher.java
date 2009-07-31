package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Nosy.Opportunist;
import org.ubi.CompilationFailedError;

/**
 * The {@link VoidReturnCrusher} spits errors when a void function returns
 * a value ,e.g.:
 * <code>
 * func blah {
 *   return 69; // ERROR! Returning an Int in a void function.
 * }
 * </code>
 * 
 * @author Amos Wenger
 */
public class VoidReturnCrusher implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		new Nosy<ValuedReturn>(ValuedReturn.class, new Opportunist<ValuedReturn>() {

			@Override
			public boolean take(ValuedReturn node, Stack<Node> stack) {
				
				FunctionDecl decl = (FunctionDecl) stack.get(Node.find(FunctionDecl.class, stack));
				if(decl.getReturnType().isVoid()) {
					throw new CompilationFailedError(null, "Returning a "
							+node.getExpression().getType().getName()+" in a void function!");
				}
				
				return true;
				
			}
			
		}).visit(unit);
		
	}

}
