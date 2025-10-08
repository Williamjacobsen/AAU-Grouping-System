package com.aau.grouping_system.EnhancedMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EnhancedMap<T extends EnhancedMapItem> {

	private final Map<Integer, T> map = new ConcurrentHashMap<>();
	private AtomicInteger idGenerator = new AtomicInteger();

	// public methods

	public void put(T item) {
		int id = getNewId();
		item.setMapId(id);
		map.put(id, item);
	}

	public void remove(T item) {
		item.removeChildren();
		map.remove(item.getMapId());
	}

	// private methods

	private int getNewId() {

		boolean hasLoopedOnce = false;

		while (map.get(idGenerator.get()) != null) {

			if (idGenerator.get() >= Integer.MAX_VALUE - 1) {
				if (hasLoopedOnce) {
					throw new IllegalStateException("A valid new ID cannot be found because the Map is completely full.");
				} else {
					hasLoopedOnce = true;
					idGenerator.set(0);
				}
			}

			idGenerator.incrementAndGet();
		}

		return idGenerator.get();
	}

	// getters & setters

	public T getItem(Integer id) {
		return map.get(id);
	}

	public Map<Integer, T> getAllItems() {
		return map;
	}

}
