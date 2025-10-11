package com.aau.grouping_system.Database;

import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseMapItemReferenceList<T extends DatabaseMapItem> extends CopyOnWriteArrayList<T> {

	// constructors

	public DatabaseMapItemReferenceList(DatabaseMapItem parentEnhancedMapItem) {
		// Add this reference list to the list of reference lists under the parent
		// MapItem.
		parentEnhancedMapItem.childReferenceLists.add(this);
	}

}
