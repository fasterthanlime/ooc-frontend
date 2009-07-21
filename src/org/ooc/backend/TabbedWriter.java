package org.ooc.backend;

import java.io.IOException;
import java.io.Writer;

public class TabbedWriter {

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
	
	public void append(char c) throws IOException {
		
		appendable.append(c);
		
	}
	
	public void append(String s) throws IOException {
		
		appendable.append(s);
		
	}
	
	public void writeTabs() throws IOException {
		
		for(int i = 0; i < tabLevel; i++) {
			appendable.append('\t');
		}
		
	}
	
	public void newUntabbedLine() throws IOException {
		
		appendable.append('\n');
		
	}
	
	public void newLine() throws IOException {
		
		newUntabbedLine();
		writeTabs();
		
	}
	
	public void tab() {
		
		tabLevel++;
		
	}
	
	public void untab() {
		
		tabLevel--;
		
	}
	
}
