package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public interface Visitable {

	public abstract void accept(Visitor visitor) throws IOException;
	public abstract void acceptChildren(Visitor visitor) throws IOException;

}