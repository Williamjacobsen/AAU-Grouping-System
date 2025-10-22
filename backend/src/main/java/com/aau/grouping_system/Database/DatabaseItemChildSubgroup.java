package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/// A subgroup of children that can be added as a field to a database item. For
/// example, each Session must have fields for its Supervisors, Students,
/// Projects, Groups, etc.
public class DatabaseItemChildSubgroup implements Serializable {

	private Integer mapId;
	private CopyOnWriteArrayList<String> childIds = new CopyOnWriteArrayList<String>();

	/// Automatically adds this to its parent item's lists of child lists.
	public DatabaseItemChildSubgroup(DatabaseMap<? extends DatabaseItem> databaseMap, DatabaseItem parentItem) {
		this.mapId = databaseMap.getId();
		parentItem.addChildSubgroup(this);
	}

	void addChild(String id) {
		childIds.add(id);
	}

	void removeChild(String id) {
		childIds.remove(id);
	}

	Integer getMapId() {
		return mapId;
	}

	CopyOnWriteArrayList<String> getChildIds() {
		return childIds;
	}

	public CopyOnWriteArrayList<? extends DatabaseItem> getItems(Database db) {
		return db.getMap(mapId).getItems(childIds);
	}

}
