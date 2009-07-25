package org.ooc.middle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A MultiMap allows you to store several values for the same
 * key, in a relatively lightweight fashion (memory-wise) 
 * @author Amos Wenger
 *
 * @param <K> the keys type
 * @param <V> the values type
 */
public class MultiMap<K, V> {

	final Map<K, Object> map;
	
	public MultiMap() {
		
		map = new HashMap<K, Object>();
		
	}
	
	@SuppressWarnings("unchecked")
	public void add(K key, V value) {
		
		Object o = map.get(key);
		if(o == null) {
			map.put(key, value);
		} else {
			if(o instanceof List<?>) {
				List<V> list = (List<V>) o;
				list.add(value);
			} else {
				List<V> list = new ArrayList<V>();
				list.add((V) o);
				list.add(value);
				map.put(key, value);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<V> get(final K key) {
		
		final Object o = map.get(key);
		if(o instanceof List<?>) {
			List<V> list = (List<V>) o;
			return list;
		} else if(o != null) {
			return new Iterable<V>() {
			@Override
			public Iterator<V> iterator() {
				return new Iterator<V>() {
					
					boolean hasNext = true;
					
					@Override
					public boolean hasNext() {
						return hasNext;
					}

					@Override
					public V next() {
						hasNext = false;
						return (V) o;
					}

					@Override
					public void remove() {
						map.remove(key);
					}
				};
			}
			};
		} else {
			return Collections.emptySet(); // it's iterable, and empty. what else? (Nespresso)
		}
		
	}
	
}
