package com.aau.grouping_system.Database;

import java.util.concurrent.CopyOnWriteArrayList;

/// Contains a list of IDs of a database items and its parent database map.
public class DatabaseReferences {

	// Use package-private access modifier here, because only DatabaseItem is
	// allowed access, since it handles adding and removing items in the database.

	DatabaseMap<? extends DatabaseItem> map;
	CopyOnWriteArrayList<Integer> ids = new CopyOnWriteArrayList<Integer>();

	void add(Integer id) {
		ids.add(id);
	}

	void remove(Integer id) {
		ids.remove(id);
	}

	// constructors

	public DatabaseReferences(DatabaseMap<? extends DatabaseItem> map, DatabaseItem parentItem) {
		this.map = map;
		parentItem.childLists.add(this);
	}

	// getters & setters

	public CopyOnWriteArrayList<Integer> getIds() {
		return ids;
	}

	public CopyOnWriteArrayList<? extends DatabaseItem> getItems() {
		CopyOnWriteArrayList<DatabaseItem> objects = new CopyOnWriteArrayList<>();
		for (Integer id : ids) {
			DatabaseItem item = map.getItem(id);
			objects.add(item);
		}
		return objects;
	}

}
