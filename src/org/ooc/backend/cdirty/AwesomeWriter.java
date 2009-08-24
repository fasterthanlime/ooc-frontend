package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.backend.TabbedWriter;

public class AwesomeWriter extends TabbedWriter {

	public AwesomeWriter(Appendable appendable) {
		super(appendable);
	}

	protected AwesomeWriter closeBlock() throws IOException {
		untab().nl().app("}");
		return this;
	}

	protected AwesomeWriter openBlock() throws IOException {
		nl().app('{').tab();
		return this;
	}
	
	protected AwesomeWriter openSpacedBlock() throws IOException {
		return openBlock().nl();
	}
	
	protected AwesomeWriter closeSpacedBlock() throws IOException {
		return closeBlock().nl().nl();
	}
	
	@Override
	public AwesomeWriter tab() {
		super.tab();
		return this;
	}
	
	@Override
	public AwesomeWriter untab() {
		super.untab();
		return this;
	}
	
	@Override
	public AwesomeWriter nl() throws IOException {
		super.nl();
		return this;
	}
	
	public AwesomeWriter app(char c) throws IOException {
		appendable.append(c);
		return this;
	}
	
	public AwesomeWriter app(String s) throws IOException {
		appendable.append(s);
		return this;
	}
	
	
}
