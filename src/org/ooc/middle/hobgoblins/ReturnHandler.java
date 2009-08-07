package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NumberLiteral;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.NumberLiteral.Format;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

/**
 * The {@link ReturnHandler} spits errors when a void function returns
 * a value ,e.g.:
 * <code>
 * func blah {
 *   return 69; // ERROR! Returning an Int in a void function.
 * }
 * </code>
 * 
 * @author Amos Wenger
 */
public class ReturnHandler implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		Nosy.get(ValuedReturn.class, new Opportunist<ValuedReturn>() {

			@Override
			public boolean take(ValuedReturn node, Stack<Node> stack) {
				
				FunctionDecl decl = (FunctionDecl) stack.get(Node.find(FunctionDecl.class, stack));
				if(decl.getReturnType().isVoid()) {
					throw new CompilationFailedError(null, "Returning a value in void function "+decl.getProtoRepr());
				}
				
				return true;
				
			}
			
		}).visit(unit);
		
		Nosy.get(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack) throws IOException {
				
				if(node.getReturnType().isVoid()) return true;
				if(node.isExtern() || node.isAbstract()) return true;
				
				if(node.getBody().isEmpty()) {
					throw new CompilationFailedError(null, "Returning nothing in on-void function "+node.getProtoRepr());
				}
				
				Line line = node.getBody().getLast();
				if(!(line.getStatement() instanceof Return)) {
					if(node.getName().equals("main")) {
						node.getBody().add(new Line(new ValuedReturn(new NumberLiteral(0, Format.DEC))));
					} else if(line.getStatement() instanceof Expression) {
						line.setStatement(new ValuedReturn((Expression) line.getStatement()));
					} else {
						throw new CompilationFailedError(null, "No return at the end of non-void function "+node.getProtoRepr());
					}
				}
				return true;
				
			}
			
		}).visit(unit);
		
	}

}
