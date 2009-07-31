package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.Scope;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Nosy.Opportunist;
import org.ubi.CompilationFailedError;

/**
 * Resolve variable accesses, e.g.
 * <code>
 * Int i = 3;
 * printf("value = %d\n", i);
 * </code>
 * 
 * Resolves the variable access one line 2 to the variable declaration
 * on line 1.
 * 
 * It also resolves member variable access, e.g. this.blah, and parenthesis-less
 * function calls, e.g. this.getBlah
 * 
 * @author Amos Wenger
 */
public class VarAccessResolver implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		final MultiMap<Node, VariableDecl> vars = unit.getDeclarations(VariableDecl.class);
		final MultiMap<Node, FunctionDecl> funcs = unit.getDeclarations(FunctionDecl.class);
		
		new Nosy<VariableAccess>(VariableAccess.class, new Opportunist<VariableAccess>() {
			
			@Override
			public boolean take(VariableAccess node, Stack<Node> stack) throws IOException {
				
				if(node.getRef() != null) return true; // already resolved
				
				if(node instanceof MemberAccess) {
					
					MemberAccess memberAccess = (MemberAccess) node;
					if(!(memberAccess.getExpression().getType().getRef() instanceof ClassDecl)) {
						throw new CompilationFailedError(null, "Can't access to field "
								+node.getName()+" of a "+memberAccess.getExpression().getClass().getSimpleName()
								+", it's not a class!");
					}
					ClassDecl decl = (ClassDecl) memberAccess.getExpression().getType().getRef();
					VariableDecl var = decl.getVariable(node.getName());
					if(var != null) {
						node.setRef(var);
					}
					
				}
				
				int index = stack.size();
				stacksearch: while(index >= 0) {
					
					index = Node.find(Scope.class, stack, index - 1);
					if(index == -1) {
						break stacksearch;
					}
					
					Node stackElement = stack.get(index);
					
					for(Declaration decl: vars.get(stackElement)) {
						if(decl.getName().equals(node.getName())) {
							node.setRef(decl);
							break stacksearch;
						}
					}
					
					for(FunctionDecl decl: funcs.get(stackElement)) {
						if((decl.getArguments().size() == 0 || decl.getArguments().get(decl.getArguments().size() - 1)
								instanceof VarArg) && decl.getName().equals(node.getName())) {
							FunctionCall call = new FunctionCall(node.getName(), "");
							call.setImpl(decl); // save FuncCallResolver the trouble.
							stack.peek().replace(node, call);
							return true; // We're done here
						}
					}
					
				}
				
				if(node.getRef() == null) {
					// FIXME this is the infamous "resolve this in a constructor" hack. Not pretty, I must say.
					while(true) {
						if(node.getName().equals("this")) {
							int fIndex = Node.find(FunctionDecl.class, stack);
							if(fIndex != -1) {
								FunctionDecl fDecl = (FunctionDecl) stack.get(fIndex);
								if(fDecl.getName().equals("new")) {
									break; // hidden goto, hihi.
								}
							}
						}
						throw new Error("Couldn't resolve variable access "+node.getName());
					}
				}
				
				return true;
				
			}
			
		}).visit(unit);
		
	}

}
