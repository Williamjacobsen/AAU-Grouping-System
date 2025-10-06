package com.aau.grouping_system.EnhancedMap;

public class EnhancedMappable {

	/// If the ID is negative, it means it is unassigned.
	int mapId = -1;

	int getMapID() {
		return this.mapId;
	}

	void setMapID(Integer id) {
		this.mapId = id;
	}
}
