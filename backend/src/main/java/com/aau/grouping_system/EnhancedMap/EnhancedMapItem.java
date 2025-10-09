package com.aau.grouping_system.EnhancedMap;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class EnhancedMapItem {

	/// If the ID is negative, it means it is unassigned.
	private int mapId = -1;
	protected EnhancedMap<? extends EnhancedMapItem> parentDatabaseMap;
	protected CopyOnWriteArrayList<EnhancedMapItemReferenceList<? extends EnhancedMapItem>> childReferenceLists = new CopyOnWriteArrayList<EnhancedMapItemReferenceList<? extends EnhancedMapItem>>();

	// public methods

	@SuppressWarnings("unchecked") // Because the type safety violation warning isn't true here.
	public void removeChildren() {
		for (EnhancedMapItemReferenceList<? extends EnhancedMapItem> childReferenceList : childReferenceLists) {
			for (EnhancedMapItem childReference : childReferenceList) {
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
				((EnhancedMap<EnhancedMapItem>) childReference.parentDatabaseMap).remove(childReference);
			}
		}
	}

	// constructors

	/// Apart from just creating the object, this also: 1) adds the item to its
	/// parent EnhancedMap in the database, 2) adds the item to its parent
	/// reference list.
	@SuppressWarnings("unchecked") // Because the type safety violation warning isn't true here.
	public EnhancedMapItem(EnhancedMap<? extends EnhancedMapItem> parentDatabaseMap,
			EnhancedMapItemReferenceList<? extends EnhancedMapItem> parentReferenceList) {
		this.parentDatabaseMap = parentDatabaseMap;
		// Add to parent map in database
		((EnhancedMap<EnhancedMapItem>) parentDatabaseMap).put(this);
		// Declare as parent EnhancedMapItem's child
		if (parentReferenceList != null) {
			((EnhancedMapItemReferenceList<EnhancedMapItem>) parentReferenceList).add(this);
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
