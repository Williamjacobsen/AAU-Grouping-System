package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/// An item stored in one of the maps in the database.
public abstract class DatabaseItem implements Serializable {

	private String id;
	private CopyOnWriteArrayList<DatabaseItemChildSubgroup> childSubgroups = new CopyOnWriteArrayList<>();
	private DatabaseItemChildSubgroup parentItemChildSubgroup = null;

	/// Automatically adds this to its map and parent item's appropriate
	/// child subgroup.
	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here despite Java's invariance of generics.
	public DatabaseItem(Database db, DatabaseItemChildSubgroup parentItemChildSubgroup) {
		// Add item to its map
		((DatabaseMap<DatabaseItem>) getDatabaseMap(db)).add((DatabaseItem) this);

		// Add item to its parent item's child subgroup
		if (parentItemChildSubgroup != null) {
			this.parentItemChildSubgroup = parentItemChildSubgroup;
			parentItemChildSubgroup.addChild(this.id);
		}
	}

	/// Each DatabaseItem subclass has their own map in the database dedicated only
	/// to their class, so each of them must specify which map this is.
	protected abstract DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db);

	void cascadeRemoveChildren(Database db) {
		for (DatabaseItemChildSubgroup childSubgroup : childSubgroups) {
			for (String childId : childSubgroup.getChildIds()) {
				db.getMap(childSubgroup.getMapId()).cascadeRemove(db, childId);
			}
		}
	}

	void disconnectFromParent(Database db) {
		if (parentItemChildSubgroup != null) {
			parentItemChildSubgroup.removeChild(this.id);
		}
	}

	void addChildSubgroup(DatabaseItemChildSubgroup childSubgroup) {
		childSubgroups.add(childSubgroup);
	}

	public String getId() {
		return this.id;
	}

	void setId(String id) {
		this.id = id;
	}

}
