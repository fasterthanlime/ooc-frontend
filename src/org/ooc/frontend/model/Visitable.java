package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public interface Visitable {

	public abstract void accept(Visitor visitor);

	public abstract void acceptChildren(Visitor visitor);

}