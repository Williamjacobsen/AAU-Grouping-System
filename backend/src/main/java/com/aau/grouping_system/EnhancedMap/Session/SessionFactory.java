package com.aau.grouping_system.EnhancedMap.Session;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.User.Coordinator.Coordinator;

public class SessionFactory {
	public static Session create(EnhancedMap<EnhancedMapItem> parentMap,
			Coordinator coordinator) {
		return new Session(parentMap, coordinator);
	}
}