package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Add extends BinaryOperation implements MustBeResolved {

	public Add(Expression left, Expression right, Token startToken) {
		super(left, right, startToken);
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public boolean resolve(NodeList<Node> stack, Resolver res, boolean fatal)
			throws IOException {
	
		for(OpDecl op: res.ops) {
			if(op.getOpType() == OpType.ADD) {
				if(op.getFunc().getArguments().size() != 2) {
					throw new OocCompilationError(op, stack,
							"To overload the add operator, you need exactly two arguments, not "
							+op.getFunc().getArgsRepr());
				}
				NodeList<Argument> args = op.getFunc().getArguments();
				if(args.get(0).getType().equals(left.getType())
						&& args.get(1).getType().equals(right.getType())) {
					FunctionCall call = new FunctionCall(op.getFunc(), startToken);
					call.getArguments().add(left);
					call.getArguments().add(right);
					stack.peek().replace(this, call);
				}
			}
		}
		
		return false;
		
	}
	
}
