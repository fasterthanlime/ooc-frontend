package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.BuiltinType;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.Scope;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

/**
 * Resolves types, e.g. setRef on any type found, e.g. a ref can be a 
 * ClassDecl, a CoverDecl, or a BuiltinType.
 * 
 * BuiltinTypes are those from C. They can't be used in VariableDecls and
 * the such because they aren't CamelCase, thus you should use a cover, e.g.
 * <code>
 * cover Int from int;
 * Int i = 42; // alright.
 * </code>
 * 
 * Of course if you build a language from ooc, you probably want to put default
 * covers in a file you'll import in every source file of your language.
 * (e.g. like ooclib.ooc for ooc)
 * 
 * @author Amos Wenger
 */
public class TypeResolver implements Hobgoblin {

	@Override
	public void process(final Module module) throws IOException {

		final MultiMap<Node, Declaration> decls = new MultiMap<Node, Declaration>();
		addBuiltins(decls, module);
		
		Nosy<Declaration> declNosy = new Nosy<Declaration>(Declaration.class, new Opportunist<Declaration>() {
			
			@Override
			public boolean take(Declaration node, Stack<Node> stack) throws IOException {
				
				if(!(node instanceof TypeDecl)) return true;
				
				int index = Node.find(Scope.class, stack);
				if(index == -1) {
					throw new Error("Found declaration "+node.getName()+" of type "
							+node.getType()+" outside of any Scope!");
				}
				decls.add(stack.get(index), node);
				
				return true;
				
			}
			
		});
		
		for(Import imp: module.getImports()) {
			declNosy.visit(imp.getModule());
		}
		declNosy.visit(module);
		
		new Nosy<Type>(Type.class, new Opportunist<Type>() {
			
			@Override
			public boolean take(Type node, Stack<Node> stack) throws IOException {
				
				System.out.print(" [[[ Should resolve "+node+" in "+stack+" ]]] ");
				
				if(node.getRef() != null) return true; // already resolved
				
				int index = stack.size();
				stacksearch: while(index >= 0 && node.getRef() == null) {
					
					index = Node.find(Scope.class, stack, index - 1);
					if(index == -1) {
						break stacksearch;
					}
					
					Node stackElement = stack.get(index);
					
					/* The magnificent This (with a capital T) hack. */
					if(stackElement instanceof ClassDecl && node.getName().equals("This")) {
						ClassDecl classDecl = (ClassDecl) stackElement;
						node.setName(classDecl.getName());
						node.setRef(classDecl);
						return true; // we're done here.
					}
					
					Iterable<Declaration> declList = decls.get(stackElement);
					searchRef(node, declList);
					
				}
				
				if(node.getRef() == null) {
					for(Import imp: module.getImports()) {
						searchRef(node, decls.get(imp.getModule()));
					}
				}
				
				if(node.getRef() == null) {
					throw new Error("Couldn't resolve type "+node.getName());
				}
				
				return true;
				
			}

			private void searchRef(Type node, Iterable<Declaration> declList) {
				for(Declaration decl: declList) {
					if(decl.getName().equals(node.getName())) {
						node.setRef(decl);
					}
				}
			}
			
		}).setDebug(true).visit(module);
		
	}

	private void addBuiltins(MultiMap<Node, Declaration> decls, Module module) {

		// TODO This should probably not be hardcoded. Or should it? Think of meta.
		decls.add(module, new BuiltinType("void"));
		decls.add(module, new BuiltinType("short"));
		decls.add(module, new BuiltinType("int"));
		decls.add(module, new BuiltinType("unsigned int"));
		decls.add(module, new BuiltinType("long"));
		decls.add(module, new BuiltinType("long long"));
		decls.add(module, new BuiltinType("long double"));
		decls.add(module, new BuiltinType("float"));
		decls.add(module, new BuiltinType("double"));
		decls.add(module, new BuiltinType("char"));
		decls.add(module, new BuiltinType("Tuple")); // FIXME that's a tough one.
		
		decls.add(module, new BuiltinType("int8_t"));
		decls.add(module, new BuiltinType("int16_t"));
		decls.add(module, new BuiltinType("int32_t"));
		
		decls.add(module, new BuiltinType("uint8_t"));
		decls.add(module, new BuiltinType("uint16_t"));
		decls.add(module, new BuiltinType("uint32_t"));
		
		decls.add(module, new BuiltinType("size_t"));
		decls.add(module, new BuiltinType("time_t"));
		
	}
	
}
