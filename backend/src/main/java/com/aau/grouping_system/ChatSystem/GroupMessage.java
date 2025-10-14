package com.aau.grouping_system.ChatSystem;

import com.aau.grouping_system.Database.DatabaseIdList;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;

public class GroupMessage extends DatabaseItem {

	GroupMessage(DatabaseMap<? extends DatabaseItem> parentMap, DatabaseIdList parentReferences) {
		super(parentMap, parentReferences); // By Will to jesp: calling this creates a new group message object in the
																				// database, right?
																				// I assume parent references is an array of children that belong to this
																				// object, so i should make i null when i initalize it (its not a child of
																				// itself), right?
																				// How tf do i create a message object for a group, then add messages to it?!
	}

}
