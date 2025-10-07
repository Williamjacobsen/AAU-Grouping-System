package com.aau.grouping_system.EnhancedMap;

import java.util.concurrent.CopyOnWriteArrayList;

public class EnhancedMapReference<T extends EnhancedMapItem> {

	private CopyOnWriteArrayList<T> itemReferences;

	// constructors

	public EnhancedMapReference(EnhancedMapItem parentEnhancedMapItem) {
		parentEnhancedMapItem.childMapReferences.add(this);
	}

	// getters & setters

	public CopyOnWriteArrayList<T> getItemReferences() {
		return itemReferences;
	}

}
