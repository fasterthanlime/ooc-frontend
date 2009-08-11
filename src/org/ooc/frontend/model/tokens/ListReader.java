package org.ooc.frontend.model.tokens;

import java.util.List;

public class ListReader<T> {

	List<T> list;
	int index;
	int length;
	int mark;

	public ListReader(List<T> list) {
		this.list = list;
		this.index = 0;
		this.length = list.size();
		this.mark = 0;
	}

	public boolean hasNext() {
		return index < length;
	}
	
	public T read() {
		/*
		System.out.println("Read/read a "+list.get(index + 1));
		if( ((Token) list.get(index + 1)).type == TokenType.CLOS_BRACK ) {
			Thread.dumpStack();
		}
		*/
		return list.get(index++);
	}
	
	public T peek() {
		/*
		System.out.println("Read/peek a "+list.get(index));
		if( ((Token) list.get(index)).type == TokenType.CLOS_BRACK ) {
			Thread.dumpStack();
		}
		*/
		return list.get(index);
	}
	
	public T prev() {
		if(index < 1) {
			//System.out.println("Read/prev(<1) a "+list.get(index));
			return list.get(index);
		}
		//System.out.println("Read/prev a "+list.get(index - 1));
		return list.get(index - 1);
	}
	
	public T prev(int offset) {
		//System.out.println("Read/prev(offset) a "+list.get(index - offset));
		return list.get(index - offset);
	}
	
	public int mark() {
		mark = index;
		return mark;
	}
	
	public void reset() {
		index = mark;
	}
	
	public void reset(int index) {
		this.index = index;
	}
	
	public void rewind() {
		index--;
	}
	
	public void skip() {
		index++;
	}
	
	public void skip(int offset) {
		index += offset;
	}
	
}
