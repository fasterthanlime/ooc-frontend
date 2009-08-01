package org.ooc.middle.walkers;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.model.Node;

/**
 * A miner takes a list and searches for a node, from the top (end of the list)
 * to the bottom (start of the list).
 * 
 * @author Amos Wenger
 */
public class Miner {
	
	public static <T> void mine(Class<T> clazz, Opportunist<T> oppo, Stack<Node> list) throws IOException {
		
		int index = list.size();
		while(index >= 0) {
			index = Node.find(clazz, list, index - 1);
			if(index == -1) return;
			if(!oppo.take(clazz.cast(list.get(index)), list)) break;
		}
		
	}

}
