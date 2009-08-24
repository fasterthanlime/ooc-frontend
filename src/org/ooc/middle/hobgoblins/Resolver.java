package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.ooc.frontend.model.BuiltinType;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.OpDecl;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

public class Resolver implements Hobgoblin {

	protected static final int MAX = 1;
	boolean running;
	boolean fatal = false;
	
	public MultiMap<Node, VariableDecl> vars;
	public MultiMap<Node, FunctionDecl> funcs;
	public List<TypeDecl> types;
	public List<OpDecl> ops;
	public BuildParams params;
	
	@Override
	public void process(Module module, BuildParams params) throws IOException {
		
		this.params = params;
		getInfos(module);
		
		Nosy<MustBeResolved> nosy = Nosy.get(MustBeResolved.class, new Opportunist<MustBeResolved>() {
			@Override
			public boolean take(MustBeResolved node, Stack<Node> stack) throws IOException {
				
				if(!node.isResolved()) {
					if(node.resolve(stack, Resolver.this, fatal)) {
						running = true;
					}
				}
				return true;
				
			}
		});
		
		int count = 0;
		running = true;
		while(running) {
			if(count > MAX) {
				fatal = true;
				nosy.start().visit(module);
				throw new Error(getClass().getSimpleName()
						+" going round in circles! More than "+MAX+" runs, abandoning...");
			}
			running = false;
			getInfos(module);
			nosy.start().visit(module);
			count++;
		}
		
	}

	protected void getInfos(Module module) throws IOException {
		vars = module.getDeclarationsMap(VariableDecl.class);
		funcs = module.getDeclarationsMap(FunctionDecl.class);
		types = module.getDeclarationsList(TypeDecl.class);
		addBuiltins(types);
		ops = module.getDeclarationsList(OpDecl.class);
	}
	
	protected void addBuiltins(List<TypeDecl> decls) {
		decls.add(new BuiltinType("Func"));
		
		// TODO This should probably not be hardcoded. Or should it? Think of meta.
		decls.add(new BuiltinType("void"));
		decls.add(new BuiltinType("short"));
		decls.add(new BuiltinType("unsigned short"));
		decls.add(new BuiltinType("int"));		
		decls.add(new BuiltinType("unsigned int"));
		decls.add(new BuiltinType("long"));
		decls.add(new BuiltinType("unsigned long"));
		decls.add(new BuiltinType("long long"));
		decls.add(new BuiltinType("unsigned long long"));
		decls.add(new BuiltinType("long double"));
		decls.add(new BuiltinType("unsigned long double"));
		decls.add(new BuiltinType("float"));
		decls.add(new BuiltinType("double"));
		decls.add(new BuiltinType("char"));
		decls.add(new BuiltinType("unsigned char"));
		decls.add(new BuiltinType("signed char"));
		
		decls.add(new BuiltinType("bool"));
		
		decls.add(new BuiltinType("int8_t"));
		decls.add(new BuiltinType("int16_t"));
		decls.add(new BuiltinType("int32_t"));
		
		decls.add(new BuiltinType("uint8_t"));
		decls.add(new BuiltinType("uint16_t"));
		decls.add(new BuiltinType("uint32_t"));
		
		decls.add(new BuiltinType("size_t"));
		decls.add(new BuiltinType("time_t"));
	}

	public void resolveType(Type type) {
		
		for(TypeDecl typeDecl: types) {
			if(typeDecl.getName().equals(type.getName())) {
				type.setRef(typeDecl);
				return;
			}
		}
		
	}
	
}
