package com.aau.grouping_system.Database;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class DatabaseMapItem {

	/// If the ID is negative, it means it is unassigned.
	private int mapId = -1;
	protected DatabaseMap<? extends DatabaseMapItem> parentDatabaseMap;
	protected CopyOnWriteArrayList<DatabaseMapItemReferenceList<? extends DatabaseMapItem>> childReferenceLists = new CopyOnWriteArrayList<DatabaseMapItemReferenceList<? extends DatabaseMapItem>>();

	// public methods

	@SuppressWarnings("unchecked") // Because the type safety violation warning isn't true here.
	public void removeChildren() {
		for (DatabaseMapItemReferenceList<? extends DatabaseMapItem> childReferenceList : childReferenceLists) {
			for (DatabaseMapItem childReference : childReferenceList) {
				// This "technically" violates type safety (if @SuppressWarnings("unchecked")
				// wasn't here, we would get a warning), since Java can't know whether the items
				// inside "EnhancedMapItemReferenceList<? extends EnhancedMapItem>" actually are
				// the same type as or a subtype of "EnhancedMapItem". This is because Java
				// treats generics as invariants, meaning e.g. "Generic<Supertype>"" and
				// "Generic<Subtype>"" are treated as being completely unrelated. However,
				// this warning is false in our case, since we know that the Wilcard (the "?"
				// symbol) extends EnhancedMapItem. So, it's alright to manually upcast like
				// this (prepending the "(EnhancedMap<EnhancedMapItem>)" cast to our statement)
				// despite it giving a warning.
				((DatabaseMap<DatabaseMapItem>) childReference.parentDatabaseMap).remove(childReference);
			}
		}
	}

	// constructors

	/// Apart from just creating the object, this also: 1) adds the item to its
	/// parent EnhancedMap in the database, 2) adds the item to its parent
	/// reference list.
	@SuppressWarnings("unchecked") // Because the type safety violation warning isn't true here.
	public DatabaseMapItem(DatabaseMap<? extends DatabaseMapItem> parentDatabaseMap,
			DatabaseMapItemReferenceList<? extends DatabaseMapItem> parentReferenceList) {
		this.parentDatabaseMap = parentDatabaseMap;
		// Add to parent map in database
		((DatabaseMap<DatabaseMapItem>) parentDatabaseMap).put(this);
		// Declare as parent EnhancedMapItem's child
		if (parentReferenceList != null) {
			((DatabaseMapItemReferenceList<DatabaseMapItem>) parentReferenceList).add(this);
		}
	}

	// getters & setters

	public int getMapId() {
		return this.mapId;
	}

	public void setMapId(Integer id) {
		this.mapId = id;
	}

}
