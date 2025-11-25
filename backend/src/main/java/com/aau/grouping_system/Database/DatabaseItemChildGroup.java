package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/// A group of children that can be added as a field to a database item. For
/// example, each Session must have fields for its Supervisors, Students,
/// Projects, Groups, etc.
public class DatabaseItemChildGroup implements Serializable {

	private Integer mapId;
	private CopyOnWriteArrayList<String> childItemIds = new CopyOnWriteArrayList<String>();

	/// Automatically adds this to its parent item's lists of child groups.
	public DatabaseItemChildGroup(DatabaseMap<? extends DatabaseItem> databaseMap, DatabaseItem parentItem) {
		this.mapId = databaseMap.getId();
		parentItem.addChildItemGroup(this);
	}

	public CopyOnWriteArrayList<? extends DatabaseItem> getItems(Database db) {
		return db.getMap(mapId).getItems(childItemIds);
	}

	// @formatter:off
	Integer getMapId() { return mapId; }
	CopyOnWriteArrayList<String> getChildItemIds() { return childItemIds; }
}
