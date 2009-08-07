package org.ooc.backend;

import java.io.IOException;
import java.io.Writer;

public class TabbedWriter implements Appendable {

	private Appendable appendable;
	private int tabLevel;

	public TabbedWriter(Appendable appendable) {

		this.appendable = appendable;
		
	}
	
	public void close() throws IOException {
		if(appendable instanceof Writer) {
			((Writer) appendable).close();
		} else {
			// well, do nothing, probably trying
			// to close a StringBuilder, which is
			// nonsense.
		}
	}
	
	public TabbedWriter append(char c) throws IOException {
		appendable.append(c);
		return this;
	}
	
	public TabbedWriter append(String s) throws IOException {
		appendable.append(s);
		return this;
	}
	
	public TabbedWriter writeTabs() throws IOException {
		for(int i = 0; i < tabLevel; i++) {
			appendable.append('\t');
		}
		return this;
	}
	
	public TabbedWriter newUntabbedLine() throws IOException {
		appendable.append('\n');
		return this;
	}
	
	public TabbedWriter newLine() throws IOException {
		return newUntabbedLine().writeTabs();
	}
	
	public TabbedWriter tab() {
		tabLevel++;
		return this;
	}
	
	public TabbedWriter untab() {
		tabLevel--;
		return this;
	}

	@Override
	public TabbedWriter append(CharSequence csq) throws IOException {
		appendable.append(csq);
		return this;
	}

	@Override
	public TabbedWriter append(CharSequence csq, int start, int end) throws IOException {
		appendable.append(csq, start, end);
		return this;
	}
	
}
