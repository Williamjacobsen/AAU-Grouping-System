package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/// An item stored in one of the maps in the database.
public abstract class DatabaseItem implements Serializable {

	private String id;
	private CopyOnWriteArrayList<DatabaseItemChildGroup> childItemGroups = new CopyOnWriteArrayList<>();
	private DatabaseItemChildGroup parentItemChildGroup = null;

	void cascadeRemoveChildItems(Database db) {
		for (DatabaseItemChildGroup childGroup : childItemGroups) {
			for (String childId : childGroup.getChildItemIds()) {
				db.getMap(childGroup.getMapId()).cascadeRemoveItem(db, childId);
			}
		}
	}

	void disconnectFromParentItem(Database db) {
		if (parentItemChildGroup != null) {
			parentItemChildGroup.removeChildItem(this.id);
		}
	}

	void addChildItemGroup(DatabaseItemChildGroup childGroup) {
		childItemGroups.add(childGroup);
	}

	// @formatter:off
	public String getId() { return this.id; }
	void setId(String id) { this.id = id; }
	DatabaseItemChildGroup getParentItemChildGroup() { return parentItemChildGroup; }
	void setParentItemChildGroup(DatabaseItemChildGroup parentItemChildGroup) { this.parentItemChildGroup = parentItemChildGroup; }
}
