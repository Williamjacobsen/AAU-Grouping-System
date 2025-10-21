package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/// Contains a list of IDs of a database items and its parent database map.
public class DatabaseItemChildList implements Serializable {

	// Use package-private access modifier here, because only DatabaseItem is
	// allowed access, since it handles adding and removing items in the database.

	private Integer mapId;
	CopyOnWriteArrayList<String> childIds = new CopyOnWriteArrayList<String>();

	void add(String id) {
		childIds.add(id);
	}

	void remove(String id) {
		childIds.remove(id);
	}

	// constructors

	public DatabaseItemChildList(DatabaseMap<? extends DatabaseItem> databaseMap, DatabaseItem parentItem) {
		this.mapId = databaseMap.getId();
		parentItem.listsOfChildren.add(this);
	}

	// getters & setters

	public Integer getMapId() {
		return mapId;
	}

	public CopyOnWriteArrayList<String> getChildIds() {
		return childIds;
	}

	public CopyOnWriteArrayList<? extends DatabaseItem> getItems(Database db) {
		return db.getMap(mapId).getItems(childIds);
	}

}
