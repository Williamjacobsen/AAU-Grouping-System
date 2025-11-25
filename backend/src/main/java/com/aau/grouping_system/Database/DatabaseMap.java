package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/// A map using UUIDs and capable of cascade deletion.
public class DatabaseMap<T extends DatabaseItem> implements Serializable {

	private Integer id;
	private final ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();

	DatabaseMap(Integer id) {
		this.id = id;
	}

	public void cascadeRemoveItem(Database db, String id) {
		T item = getItem(id);
		item.cascadeRemoveChildItems(db);
		item.disconnectFromParentItem(db);
		map.remove(id);
	}

	public void cascadeRemoveItem(Database db, T item) {
		cascadeRemoveItem(db, item.getId());
	}

	public T addItem(Database db, DatabaseItemChildGroup parentItemChildGroup, T item) {

		// Add item to this map
		String id = getNewItemId();
		item.setId(id);
		map.put(id, item);

		// Add item to its parent item's child group
		if (parentItemChildGroup != null) {
			item.setParentItemChildGroup(parentItemChildGroup);
			parentItemChildGroup.getChildItemIds().add(item.getId());
		}

		return item;
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

	// @formatter:off
	Integer getId() { return id; }
}
