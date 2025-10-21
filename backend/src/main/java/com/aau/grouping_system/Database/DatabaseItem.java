package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/// Items stored in the maps in the database. Upon creation ("new
/// DatabaseItem()"), this adds itself to its parent map in the database.
public abstract class DatabaseItem implements Serializable {

	private String id;

	CopyOnWriteArrayList<DatabaseItemChildList> listsOfChildren = new CopyOnWriteArrayList<>();

	// package-private methods

	void removeChildren(Database db) {
		for (DatabaseItemChildList childList : listsOfChildren) {
			for (String childId : childList.childIds) {
				db.getMap(childList.getMapId()).remove(db, childId);
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
	public DatabaseItem(Database db, DatabaseItemChildList parentItemChildList) {

		// Add item to parent map in database
		((DatabaseMap<DatabaseItem>) getDatabaseMap(db)).put(this);

		// Add item to parent item's child list
		if (parentItemChildList != null) {
			parentItemChildList.add(this.id);
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
