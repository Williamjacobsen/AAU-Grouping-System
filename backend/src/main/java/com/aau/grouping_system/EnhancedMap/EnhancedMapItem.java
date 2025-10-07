package com.aau.grouping_system.EnhancedMap;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class EnhancedMapItem {

	/// If the ID is negative, it means it is unassigned.
	private int mapId = -1;
	private EnhancedMap<EnhancedMapItem> parentMap;
	/// We need to use Wildcards here because of how Java handles generic types. For
	/// example: When we cast a List<Subtype> as List<Supertype>, it works fine.
	/// However, when we cast List<List<Subtype>> as List<List<Supertype>>, it does
	/// not work, because Java cannot recognize nested generics and therefore does
	/// not believe it can downcast the subtype to a supertype. However, by writing
	/// it using Wildcards, we can make it work.
	protected CopyOnWriteArrayList<EnhancedMapReference<? extends EnhancedMapItem>> childMapReferences = new CopyOnWriteArrayList<EnhancedMapReference<? extends EnhancedMapItem>>();

	// public methods

	public void removeChildren() {
		for (EnhancedMapReference<? extends EnhancedMapItem> mapReference : childMapReferences) {
			for (EnhancedMapItem item : mapReference.getItemReferences()) {
				item.parentMap.remove(item);
			}
		}
	}

	// constructors

	public EnhancedMapItem(EnhancedMap<EnhancedMapItem> parentMap) {
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
