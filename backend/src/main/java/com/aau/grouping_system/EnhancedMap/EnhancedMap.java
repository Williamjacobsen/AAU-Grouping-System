package com.aau.grouping_system.EnhancedMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EnhancedMap<T extends EnhancedMappable> {

	private final Map<Integer, T> map = new ConcurrentHashMap<>();
	private AtomicInteger idGenerator = new AtomicInteger();

	// public methods

	public void put(T mappable) {
		int id = getNewId();
		mappable.setMapID(id);
		map.put(id, mappable);
	}

	public void remove(T mappable) {
		map.remove(mappable.getMapID());
	}

	// private methods

	private int getNewId() {

		boolean hasLoopedOnce = false;

		while (map.get(idGenerator.get()) != null) {

			if (idGenerator.get() <= Integer.MAX_VALUE - 1) {
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

	public T getEntry(Integer id) {
		return map.get(id);
	}

	public Map<Integer, T> getAllEntries() {
		return map;
	}

}
