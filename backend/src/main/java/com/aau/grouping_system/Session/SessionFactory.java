package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseIdList;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class SessionFactory {
	public static Session create(Database db, DatabaseIdList parentReferences,
			Coordinator coordinator) {
		return new Session(db, parentReferences, coordinator);
	}
}