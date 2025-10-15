package com.aau.grouping_system.Database;

import java.util.concurrent.CopyOnWriteArrayList;

/// Contains a list of IDs of a database items and its parent database map.
public class DatabaseIdList {

	// Use package-private access modifier here, because only DatabaseItem is
	// allowed access, since it handles adding and removing items in the database.

	DatabaseMap<? extends DatabaseItem> map;
	CopyOnWriteArrayList<String> ids = new CopyOnWriteArrayList<String>();

	void add(String id) {
		ids.add(id);
	}

	void remove(String id) {
		ids.remove(id);
	}

	// constructors

	public DatabaseIdList(DatabaseMap<? extends DatabaseItem> map, DatabaseItem parentItem) {
		this.map = map;
		parentItem.children.add(this);
	}

	// getters & setters

	public CopyOnWriteArrayList<String> getIds() {
		return ids;
	}

	public CopyOnWriteArrayList<? extends DatabaseItem> getItems() {
		CopyOnWriteArrayList<DatabaseItem> objects = new CopyOnWriteArrayList<>();
		for (String id : ids) {
			DatabaseItem item = map.getItem(id);
			objects.add(item);
		}
		return objects;
	}

}
