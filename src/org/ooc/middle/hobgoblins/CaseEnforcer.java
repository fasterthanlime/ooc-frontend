package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.Type;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

/**
 * Make sure class/cover names are CamelCase and func/vars camelCase
 * 
 * @author Amos Wenger
 */
public class CaseEnforcer implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		Nosy.get(Type.class, new Opportunist<Type>() {

			@Override
			public boolean take(Type node, Stack<Node> stack) throws IOException {
				
				if(Character.isLowerCase(node.getName().charAt(0))
						&& !(stack.peek() instanceof CoverDecl)
						&& !(stack.peek() instanceof FunctionDecl)
						) {
					throw new CompilationFailedError(null,
							"Lower-case type name '"+node.getName()
							+"'. Types should always begin with a capital letter, e.g. CamelCase (stack = "+stack);
				}
				return true;
				
			}
			
		}).visit(unit);
		
	}

}
