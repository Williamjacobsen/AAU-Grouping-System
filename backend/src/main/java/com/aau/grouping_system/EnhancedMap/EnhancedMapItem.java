package com.aau.grouping_system.EnhancedMap;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class EnhancedMapItem {

	/// If the ID is negative, it means it is unassigned.
	private int mapId = -1;
	protected EnhancedMap<? extends EnhancedMapItem> parentMap;
	protected CopyOnWriteArrayList<EnhancedMapItemReferenceList<? extends EnhancedMapItem>> children = new CopyOnWriteArrayList<EnhancedMapItemReferenceList<? extends EnhancedMapItem>>();

	// public methods

	@SuppressWarnings("unchecked") // Because the type safety violation warning isn't true here.
	public void removeChildren() {
		for (EnhancedMapItemReferenceList<? extends EnhancedMapItem> itemReferences : children) {
			for (EnhancedMapItem item : itemReferences) {
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
				((EnhancedMap<EnhancedMapItem>) item.parentMap).remove(item);
			}
		}
	}

	// constructors

	public EnhancedMapItem(EnhancedMap<? extends EnhancedMapItem> parentMap) {
		this.parentMap = parentMap;
	}

	// getters & setters

	public int getMapId() {
		return this.mapId;
	}

	public void setMapId(Integer id) {
		this.mapId = id;
	}

}
