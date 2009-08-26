package org.ooc.middle.hobgoblins;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

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
	public void process(Module module, BuildParams params) throws IOException {

		Nosy.get(ValuedReturn.class, new Opportunist<ValuedReturn>() {

			@Override
			public boolean take(ValuedReturn node, NodeList<Node> stack) throws OocCompilationError, EOFException {
				
				FunctionDecl decl = (FunctionDecl) stack.get(Node.find(FunctionDecl.class, stack));
				if(decl.getReturnType().isVoid()) {
					throw new OocCompilationError(node, stack,
							"Returning a value in function "+decl.getProtoRepr()
								+" which is declared as returning nothing");
				}
				
				return true;
				
			}
			
		}).visit(module);
		
		Nosy.get(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node, NodeList<Node> stack) throws IOException {
				
				if(node.getReturnType().isVoid()) return true;
				if(node.isConstructor() && Node.find(ClassDecl.class, stack) != -1) return true;
				if(node.isExtern() || node.isAbstract()) return true;
				
				if(node.getBody().isEmpty()) {
					if(node.getName().equals("main")) {
						node.getBody().add(new Line(new ValuedReturn(
								new IntLiteral(0, Format.DEC, node.startToken), node.startToken)));
					} else {
						throw new OocCompilationError(node, stack,
								"Returning nothing in function "+node.getProtoRepr()
									+" that should return a "+node.getReturnType());
					}
					
				}
				
				Line line = node.getBody().getLast();
				if(!(line.getStatement() instanceof Return)) {
					if(node.getName().equals("main")) {
						node.getBody().add(new Line(new ValuedReturn(
								new IntLiteral(0, Format.DEC, node.startToken), node.startToken)));
					} else if(line.getStatement() instanceof Expression) {
						line.setStatement(new ValuedReturn((Expression) line.getStatement(),
								line.getStatement().startToken));
					} else {
						throw new OocCompilationError(node, stack,
								"Returning nothing in function "+node.getProtoRepr()
									+" that should return a "+node.getReturnType());
					}
				}
				return true;
				
			}
			
		}).visit(module);
		
	}

}
