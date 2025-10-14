package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseReferences;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class SessionFactory {
	public static Session create(DatabaseMap<? extends DatabaseItem> parentMap, DatabaseReferences parentReferences,
			Database db, Coordinator coordinator) {
		return new Session(parentMap, parentReferences, db, coordinator);
	}
}