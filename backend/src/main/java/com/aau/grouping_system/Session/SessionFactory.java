package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildList;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class SessionFactory {
	public static Session create(Database db, DatabaseItemChildList parentReferences,
			Coordinator coordinator) {
		return new Session(db, parentReferences, coordinator);
	}
}