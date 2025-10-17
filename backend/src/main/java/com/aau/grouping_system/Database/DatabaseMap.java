package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/// Functions as a ConcurrentHashMap that also handles IDs and hierarchy
/// (child/parent relations) of items.
public class DatabaseMap<T extends DatabaseItem> implements Serializable {

	private final Map<String, T> map = new ConcurrentHashMap<>();

	// public methods

	public void put(T item) {
		String id = getNewId();
		item.setId(id);
		map.put(id, item);
	}

	public void remove(T item) {
		item.removeChildren();
		map.remove(item.getId());
	}

	public void remove(String id) {
		T item = getItem(id);
		remove(item);
	}

	// private methods

	private String getNewId() {

		String id = UUID.randomUUID().toString();

		// Ensure ID isn't already used
		while (map.get(id) != null) {
			id = UUID.randomUUID().toString();
		}

		return id;
	}

	// getters & setters

	public T getItem(String id) {
		return map.get(id);
	}

	public Map<String, T> getAllItems() {
		return map;
	}

}
