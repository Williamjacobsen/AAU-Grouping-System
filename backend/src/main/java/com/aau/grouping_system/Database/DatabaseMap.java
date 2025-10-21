package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/// Functions as a ConcurrentHashMap that also handles IDs and hierarchy
/// (child/parent relations) of items.
public class DatabaseMap<T extends DatabaseItem> implements Serializable {

	private Integer id; // TODO: Implement ID!

	private final ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();

	// constructors

	public DatabaseMap(ConcurrentHashMap<Integer, DatabaseMap<? extends DatabaseItem>> maps, AtomicInteger idGenerator) {
		this.id = idGenerator.incrementAndGet();
		maps.put(id, this);
	}

	// public methods

	public void remove(Database db, T item) {
		item.removeChildren(db);
		map.remove(item.getId());
	}

	public void remove(Database db, String id) {
		T item = getItem(id);
		remove(db, item);
	}

	// package-private methods

	void put(T item) {
		String id = getNewId();
		item.setId(id);
		map.put(id, item);
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

	public Integer getId() {
		return id;
	}

	public T getItem(String id) {
		return map.get(id);
	}

	public CopyOnWriteArrayList<T> getItems(CopyOnWriteArrayList<String> ids) {
		CopyOnWriteArrayList<T> list = new CopyOnWriteArrayList<>();
		for (String id : ids) {
			list.add(getItem(id));
		}
		return list;
	}

	public ConcurrentHashMap<String, T> getAllItems() {
		return map;
	}

}
