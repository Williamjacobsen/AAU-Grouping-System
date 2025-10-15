package com.aau.grouping_system.Database;

import java.util.concurrent.CopyOnWriteArrayList;

/// Items stored in the maps in the database. Upon creation ("new
/// DatabaseItem()"), this adds itself to its parent map in the database.
public abstract class DatabaseItem {

	private int id = -1;
	protected DatabaseMap<? extends DatabaseItem> parent;
	protected CopyOnWriteArrayList<DatabaseIdList> children = new CopyOnWriteArrayList<>();

	// package-private methods

	void removeChildren() {
		for (DatabaseIdList childList : children) {
			for (Integer childId : childList.ids) {
				childList.map.remove(childId);
			}
		}
	}

	// constructors

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	public DatabaseItem(DatabaseMap<? extends DatabaseItem> parentMap, DatabaseIdList parentReferences) {

		this.parent = parentMap;

		// Add item to parent map in database
		((DatabaseMap<DatabaseItem>) parentMap).put(this);

		// Add item to parent item's references
		if (parentReferences != null) {
			parentReferences.add(this.id);
		}
	}

	// getters & setters

	public int getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
