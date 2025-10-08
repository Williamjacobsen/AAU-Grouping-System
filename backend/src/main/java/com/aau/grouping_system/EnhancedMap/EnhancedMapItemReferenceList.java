package com.aau.grouping_system.EnhancedMap;

import java.util.concurrent.CopyOnWriteArrayList;

public class EnhancedMapItemReferenceList<T extends EnhancedMapItem> extends CopyOnWriteArrayList<T> {

	// constructors

	public EnhancedMapItemReferenceList(EnhancedMapItem parentEnhancedMapItem) {
		// Add this reference list to the list of reference lists under the parent
		// MapItem.
		parentEnhancedMapItem.children.add(this);
	}

}
