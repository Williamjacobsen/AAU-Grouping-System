package com.aau.grouping_system.Session;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItemReferenceList;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class SessionFactory {
	public static Session create(EnhancedMap<? extends EnhancedMapItem> parentDatabaseMap,
			EnhancedMapItemReferenceList<? extends EnhancedMapItem> parentReferenceList,
			Coordinator coordinator) {
		return new Session(parentDatabaseMap, parentReferenceList, coordinator);
	}
}