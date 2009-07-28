package org.ooc.middle;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.middle.Nosy.Opportunist;

public class ThisMemberAdder implements Hobgoblin {

	@Override
	public void process(SourceUnit unit) throws IOException {
		
		System.out.println(">>> Running ThisMemberAdder...");
		
		new Nosy<FunctionDecl>(FunctionDecl.class, new Opportunist<FunctionDecl>() {

			@Override
			public boolean take(FunctionDecl node, Stack<Node> stack)
					throws IOException {
				
				if(node.isStatic()) return true; // static functions don't have a this.
				
				int index = Node.find(ClassDecl.class, stack);
				if(index == -1) return true;
				
				ClassDecl classDecl = (ClassDecl) stack.get(index);
				node.getArguments().add(0, new RegularArgument(classDecl.getInstanceType(), "this"));
				
				return true;
				
			}
			
		}).visit(unit);

	}

}
