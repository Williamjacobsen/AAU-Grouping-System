package com.aau.grouping_system.Database.item;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Database.DatabaseMap;

/// Upon creation, classes that extend DatabaseItem automatically add themselves
/// to the database and setup their hierarchy of child and parent DatabaseItems.
/// For example, each Coordinator item in the map of Coordinators in the database
/// has a list of Sessions as children, and each Session has a list of Students
/// and so on.
public abstract class DatabaseItem {

	/// The item's ID in the database map in which the item is stored. If the ID is
	/// negative, it means it is unassigned.
	private int id = -1;
	/// The database map in which the item is stored.
	protected DatabaseMap<? extends DatabaseItem> parentDatabaseMap;
	/// An item may have children which should be deleted when the item itself is
	/// deleted. For example, if a Session is deleted, its list of Students, list of
	/// Supervisors, list of Groups, etc. must also be deleted. In other words,
	/// Session must store a list of lists of child item references.
	protected CopyOnWriteArrayList<ItemReferenceList<? extends DatabaseItem>> childReferenceLists = new CopyOnWriteArrayList<ItemReferenceList<? extends DatabaseItem>>();

	// public methods

	@SuppressWarnings("unchecked") // Because the type safety violation warning isn't true here.
	public void removeChildren() {
		for (ItemReferenceList<? extends DatabaseItem> childReferenceList : childReferenceLists) {
			for (DatabaseItem childReference : childReferenceList.referenceList) {
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
				((DatabaseMap<DatabaseItem>) childReference.parentDatabaseMap).remove(childReference);
			}
		}
	}

	// constructors

	/// Apart from just creating the object, this also: 1) adds the item to its
	/// parent EnhancedMap in the database, 2) adds the item to its parent
	/// reference list.
	@SuppressWarnings("unchecked") // Because the type safety violation warning isn't true here.
	public DatabaseItem(DatabaseMap<? extends DatabaseItem> parentDatabaseMap,
			ItemReferenceList<? extends DatabaseItem> parentReferenceList) {

		this.parentDatabaseMap = parentDatabaseMap;

		// Add to parent map in database
		((DatabaseMap<DatabaseItem>) parentDatabaseMap).put(this);

		// Declare as parent EnhancedMapItem's child
		if (parentReferenceList != null) {
			((ItemReferenceList<DatabaseItem>) parentReferenceList).add(this);
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
