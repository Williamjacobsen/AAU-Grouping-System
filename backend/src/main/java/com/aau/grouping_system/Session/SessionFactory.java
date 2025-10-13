package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.databaseMapItem.DatabaseMapItem;
import com.aau.grouping_system.Database.databaseMapItem.DatabaseMapItemReferenceList;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class SessionFactory {
	public static Session create(DatabaseMap<? extends DatabaseMapItem> parentDatabaseMap,
			DatabaseMapItemReferenceList<? extends DatabaseMapItem> parentReferenceList,
			Coordinator coordinator) {
		return new Session(parentDatabaseMap, parentReferenceList, coordinator);
	}
}