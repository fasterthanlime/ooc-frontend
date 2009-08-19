package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
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
	public void process(Module module) throws IOException {

		Nosy.get(Declaration.class, new Opportunist<Declaration>() {

			@Override
			public boolean take(Declaration node, Stack<Node> stack) throws IOException {
				
				if(node instanceof VariableDecl) {
					VariableDecl varDecl = (VariableDecl) node;
					for(VariableDeclAtom atom: varDecl.getAtoms()) {
						if(atom.getName().isEmpty()) continue;
						if(Character.isUpperCase(atom.getName().charAt(0)) && !varDecl.isConst()&& !varDecl.isExtern()) {
							throw new CompilationFailedError(null,
									"Upper-case variable name '"+atom.getName()+": "+node.getType()
									+"'. Variables should always begin with a lowercase letter, e.g. camelCase");
						}
					}
					return true;
				}
				
				if(node.getName().isEmpty()) return true;
				
				if(node instanceof FunctionDecl) {
					if(Character.isUpperCase(node.getName().charAt(0)) && !((FunctionDecl) node).isExtern()) {
						throw new CompilationFailedError(null,
								"Upper-case function name '"+((FunctionDecl) node).getProtoRepr()
								+"'. Function should always begin with a lowercase letter, e.g. camelCase");
					}
				}
				
				if(node instanceof TypeDecl) {
					if(Character.isLowerCase(node.getName().charAt(0))) {
					throw new CompilationFailedError(null,
							"Lower-case type name '"+node.getName()
							+"'. Types should always begin with a capital letter, e.g. CamelCase (stack = "+stack);
					
					}
				}
					
				return true;
				
			}
			
		}).visit(module);
		
	}

}
