package org.ooc.middle.hobgoblins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Type;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

public class CoverMerger implements Hobgoblin {

	final Map<String, CoverDecl> covers = new HashMap<String, CoverDecl>();
	final List<Module> done = new ArrayList<Module>();
	
	@Override
	public void process(Module module) throws IOException {
		
		if(done.contains(module)) return;
		
		done.add(module);
		
		Nosy.get(CoverDecl.class, new Opportunist<CoverDecl>() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean take(CoverDecl node, Stack<Node> stack)
					throws IOException {

				if(node.getFromType() == null) return true;
				Type groundType = node.getFromType().getGroundType();
				String groundName = groundType.toString();
				
				CoverDecl base = covers.get(groundName);
				if(base == null) {
					covers.put(groundName, node);
				} else {
					if(base == node) return true; // pas fou, non?
					base.absorb(node);
					int index = Node.find(NodeList.class, stack);
					if(index == -1) {
						throw new CompilationFailedError(null, "");
					}
					NodeList<Node> list = (NodeList<Node>) stack.get(index);
					list.remove(node);
				}
				return true;
				
			}
		}).visit(module);
		
		for(Import imp: module.getImports()) {
			process(imp.getModule());
		}
		
	}

}
