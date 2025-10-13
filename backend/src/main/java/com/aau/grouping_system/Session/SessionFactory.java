package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.item.DatabaseItem;
import com.aau.grouping_system.Database.item.ItemReferenceList;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class SessionFactory {
	public static Session create(DatabaseMap<? extends DatabaseItem> parentDatabaseMap,
			ItemReferenceList<? extends DatabaseItem> parentReferenceList,
			Coordinator coordinator) {
		return new Session(parentDatabaseMap, parentReferenceList, coordinator);
	}
}