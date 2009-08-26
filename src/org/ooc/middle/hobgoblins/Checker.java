package org.ooc.middle.hobgoblins;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

/**
 * The Checker makes sure everything has been resolved properly. It also makes
 * sure type names are CamelCase and func/vars camelCase
 * 
 * @author Amos Wenger
 */
public class Checker implements Hobgoblin {

	@Override
	public void process(Module module, BuildParams params) throws IOException {
		
		Nosy.get(Node.class, new Opportunist<Node>() {

			@Override
			public boolean take(Node node, NodeList<Node> stack) throws IOException {
				if(node instanceof Type) checkType((Type) node, stack);
				else if(node instanceof FunctionCall) checkFunctionCall((FunctionCall) node, stack);
				else if(node instanceof VariableAccess) checkVariableAccess((VariableAccess) node, stack);
				else if(node instanceof FunctionDecl) checkFunctionDecl((FunctionDecl) node, stack);
				else if(node instanceof VariableDecl) checkVariableDecl((VariableDecl) node, stack);
				else if(node instanceof TypeDecl) checkTypeDecl((TypeDecl) node, stack);
				return true;
			}

		}).visit(module);
		
	}

	protected void checkType(Type node, NodeList<Node> stack) throws IOException {
		if(node.getRef() == null) {
			throw new OocCompilationError(node, stack,
					node.getClass().getSimpleName()+" "+node
					+" hasn't been resolved :(, stack = "+stack);
		}
	}
	
	protected void checkFunctionCall(FunctionCall node, NodeList<Node> stack) throws IOException {
		if(node.getImpl() == null) {
			throw new OocCompilationError(node, stack,
					node.getClass().getSimpleName()+" to "+node.getName()
					+" hasn't been resolved :(");
		}
	}
	
	protected void checkVariableAccess(VariableAccess node, NodeList<Node> stack) throws IOException {
		if(node.getRef() == null) {
			throw new OocCompilationError(node, stack,
					node.getClass().getSimpleName()+" to "+node.getName()
					+" hasn't been resolved :( Stack = "+stack);
		}
	}
	
	protected void checkFunctionDecl(FunctionDecl node, NodeList<Node> stack) throws IOException {
		if(node.isConstructor() && Node.find(TypeDecl.class, stack) == -1) {
			// TODO forbid functions named load in modules (or __load__?)
			throw new OocCompilationError(node, stack,
				"Declaration of a function named 'new' outside a class is forbidden!" +
				" Functions named 'new' are only used as constructors in classes.");
		}
		if(node.getName().isEmpty()) return;
		if(Character.isUpperCase(node.getName().charAt(0)) && !node.isExtern()) {
			throw new OocCompilationError(node, stack,
					"Upper-case function name '"+node.getProtoRepr()
					+"'. Function should always begin with a lowercase letter, e.g. camelCase");
		}
	}
	
	protected void checkVariableDecl(VariableDecl node, NodeList<Node> stack) throws EOFException {
		Type varDeclType = node.getType();
		if(varDeclType != null && !varDeclType.getName().isEmpty() && Character.isLowerCase(varDeclType.getName().charAt(0))) {
			throw new OocCompilationError(varDeclType, stack,
					"Variable declaration has type '"+varDeclType.getName()+
					"', which begins with a lowercase letter."+
					" Types should always begin with an uppercase letter, e.g. CamelCase");
		}
		for(VariableDeclAtom atom: node.getAtoms()) {
			if(atom.getName().isEmpty()) continue;
			if(Character.isUpperCase(atom.getName().charAt(0)) && !node.isConst()
					&& node.shouldBeLowerCase()) {
				throw new OocCompilationError(atom, stack,
						"Upper-case variable name '"+atom.getName()+": "+node.getType()
						+"'. Variables should always begin with a lowercase letter, e.g. camelCase");
			}
		}
	}
	
	protected void checkTypeDecl(TypeDecl node, NodeList<Node> stack)
		throws OocCompilationError, EOFException {
		if(node.getName().isEmpty()) return;
		if(Character.isLowerCase(node.getName().charAt(0))) {
			throw new OocCompilationError(node, stack,
				"Lower-case type name '"+node.getName()
				+"'. Types should always begin with a capital letter, e.g. CamelCase (stack = "+stack);
		
		}
	}

}
