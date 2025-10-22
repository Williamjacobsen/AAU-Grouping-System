package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/// A map using UUIDs and capable of cascade deletion.
public class DatabaseMap<T extends DatabaseItem> implements Serializable {

	private Integer id;
	private final ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();

	/// Automatically adds this to the list of maps in the database.
	public DatabaseMap(ConcurrentHashMap<Integer, DatabaseMap<? extends DatabaseItem>> maps, AtomicInteger idGenerator) {
		this.id = idGenerator.incrementAndGet();
		maps.put(id, this);
	}

	public void cascadeRemove(Database db, String id) {
		T item = getItem(id);
		item.cascadeRemoveChildren(db);
		item.disconnectFromParent(db);
		map.remove(id);
	}

	public void cascadeRemove(Database db, T item) {
		cascadeRemove(db, item.getId());
	}

	void add(T item) {
		String id = getNewItemId();
		item.setId(id);
		map.put(id, item);
	}

	private String getNewItemId() {
		String id;
		do {
			id = UUID.randomUUID().toString();
		} while (map.get(id) != null);
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

	Integer getId() {
		return id;
	}

}
