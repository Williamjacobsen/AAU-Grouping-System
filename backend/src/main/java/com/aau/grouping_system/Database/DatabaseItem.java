package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/// An item stored in one of the maps in the database.
public abstract class DatabaseItem implements Serializable {

	private String id;
	private CopyOnWriteArrayList<DatabaseItemChildGroup> childGroups = new CopyOnWriteArrayList<>();
	private DatabaseItemChildGroup parentItemChildGroup = null;

	/// Automatically adds this to its map and to its parent item's appropriate
	/// child group.
	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here despite Java's invariance of generics.
	public DatabaseItem(Database db, DatabaseItemChildGroup parentItemChildGroup) {
		// Add item to its map
		((DatabaseMap<DatabaseItem>) getDatabaseMap(db)).add((DatabaseItem) this);

		// Add item to its parent item's child group
		if (parentItemChildGroup != null) {
			this.parentItemChildGroup = parentItemChildGroup;
			parentItemChildGroup.addChild(this.id);
		}
	}

	/// Each DatabaseItem subclass has their own map in the database dedicated only
	/// to their class, so each of them must specify which map this is.
	protected abstract DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db);

	void cascadeRemoveChildren(Database db) {
		for (DatabaseItemChildGroup childGroup : childGroups) {
			for (String childId : childGroup.getChildIds()) {
				db.getMap(childGroup.getMapId()).cascadeRemove(db, childId);
			}
		}
	}

	void disconnectFromParent(Database db) {
		if (parentItemChildGroup != null) {
			parentItemChildGroup.removeChild(this.id);
		}
	}

	void addChildGroup(DatabaseItemChildGroup childGroup) {
		childGroups.add(childGroup);
	}

	public String getId() {
		return this.id;
	}

	void setId(String id) {
		this.id = id;
	}

}
