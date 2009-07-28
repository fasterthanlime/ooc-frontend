package org.ooc.middle;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.Scope;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.Type;
import org.ooc.middle.Nosy.Opportunist;

public class TypeResolver implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {

		System.out.println(">>> Running TypeResolver");
		
		final MultiMap<Node, Declaration> decls = new MultiMap<Node, Declaration>();
		addBuiltins(decls, unit);
		
		System.out.println("Builtin decls: "+decls);
		
		new Nosy<Declaration>(Declaration.class, new Opportunist<Declaration>() {
			
			@Override
			public boolean take(Declaration node, Stack<Node> stack) throws IOException {
				
				if(!(node instanceof ClassDecl || node instanceof CoverDecl)) return true;
				
				System.out.println("Got "+node.getClass().getSimpleName()+" "
						+node.getName()+" child of a "+stack.peek().getClass().getSimpleName());
				int index = Node.find(Scope.class, stack);
				if(index == -1) {
					throw new Error("Found declaration "+node.getName()+" of type "
							+node.getType()+" outside of any Scope!");
				}
				decls.add(stack.get(index), node);
				
				return true;
				
			}
			
		}).visit(unit);
		
		System.out.println("Class decls: "+decls);
		
		new Nosy<Type>(Type.class, new Opportunist<Type>() {
			
			@Override
			public boolean take(Type node, Stack<Node> stack) throws IOException {
				
				if(node.getRef() != null) return true; // already resolved
				
				System.out.println("# Should resolve type "+node.getName()+", stack = "+stack);
				
				int index = stack.size();
				stacksearch: while(index >= 0) {
					
					index = Node.find(Scope.class, stack, index - 1);
					if(index == -1) {
						System.out.println("no Scope remaining, abandon...");
						break stacksearch;
					}
					
					System.out.println("Looking in stack element "+index);
					
					Node stackElement = stack.get(index);
					
					/* The magnificent This (with a capital T) hack. */
					if(stackElement instanceof ClassDecl && node.getName().equals("This")) {
						node.setRef((ClassDecl) stackElement);
						break stacksearch;
					}
					
					for(Declaration decl: decls.get(stackElement)) {
						System.out.println("Looking through declaration "+decl.toString());
						if(decl.getName().equals(node.getName())) {
							node.setRef(decl);
							break stacksearch;
						}
					}
					
				}
				
				if(node.getRef() == null) {
					throw new Error("Couldn't resolve type "+node.getName());
				}
				
				return true;
				
			}
			
		}).visit(unit);
		
	}

	private void addBuiltins(MultiMap<Node, Declaration> decls, SourceUnit unit) {

		decls.add(unit, new BuiltinType("void"));
		decls.add(unit, new BuiltinType("int"));
		decls.add(unit, new BuiltinType("float"));
		
	}
	
}
