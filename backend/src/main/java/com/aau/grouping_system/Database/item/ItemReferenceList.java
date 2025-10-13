package com.aau.grouping_system.Database.item;

import java.util.concurrent.CopyOnWriteArrayList;

/// Contains a list of DatabaseItems. For example, a Session has an
/// ItemReferenceList of Students that belong to it.
public class ItemReferenceList<T extends DatabaseItem> {

	// Since adding and removing DatabaseMapItems is solely handled
	// by the DatabaseMapItem class, we only want it to be able to
	// add and remove things in the reference list. Else, a programmer
	// may mistakenly add or remove an item from the reference list
	// directly, which won't update the maps in the database.
	// So, to ensure this, we make the add() and remove() methods
	// package-private (by not including an access modifier before their
	// method definitions because the default access modifier is
	// package-private). According to W3Schools package-private means that "The code
	// is only accessible in the same package. This is used when you don't specify a
	// modifier". So, it is only the packages that are also part of the package
	// "com.aau.grouping_system.Database.DatabaseMapItem" that have acces to this
	// class's package-private stuff. The package-private code goes here:

	CopyOnWriteArrayList<T> referenceList = new CopyOnWriteArrayList<T>();

	void add(T item) {
		referenceList.add(item);
	}

	void remove(T item) {
		referenceList.remove(item);
	}

	// constructors

	public ItemReferenceList(DatabaseItem parentEnhancedMapItem) {
		// Add this reference list to the list of reference lists under the parent
		// MapItem.
		parentEnhancedMapItem.childReferenceLists.add(this);
	}

	// getters & setters

	public CopyOnWriteArrayList<T> getReferenceList() {
		return referenceList;
	}

}
