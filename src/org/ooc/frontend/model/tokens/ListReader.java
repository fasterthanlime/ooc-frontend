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
		return list.get(index++);
	}
	
	public T peek() {
		return list.get(index);
	}
	
	public T prev() {
		return list.get(index - 1);
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
	
	public void skip() {
		index++;
	}
	
	public void skip(int offset) {
		index += offset;
	}
	
}
