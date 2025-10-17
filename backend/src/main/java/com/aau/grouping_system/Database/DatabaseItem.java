package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/// Items stored in the maps in the database. Upon creation ("new
/// DatabaseItem()"), this adds itself to its parent map in the database.
public abstract class DatabaseItem implements Serializable {

	private String id;
	protected DatabaseMap<? extends DatabaseItem> databaseMap;
	protected CopyOnWriteArrayList<DatabaseIdList> childIds = new CopyOnWriteArrayList<>();

	// package-private methods

	void removeChildren() {
		for (DatabaseIdList childList : childIds) {
			for (String childId : childList.ids) {
				childList.databaseMap.remove(childId);
			}
		}
	}

	// abstract methods

	/// Each DatabaseItem subclass has their own map in the database dedicated only
	/// to their class, so each of them must specify which map this is.
	protected abstract DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db);

	// constructors

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	public DatabaseItem(Database db, DatabaseIdList parentItemChildIds) {

		this.databaseMap = getDatabaseMap(db);

		// Add item to parent map in database
		((DatabaseMap<DatabaseItem>) databaseMap).put(this);

		// Add item to parent item's references
		if (parentItemChildIds != null) {
			parentItemChildIds.add(this.id);
		}
	}

	// getters & setters

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
