package com.aau.grouping_system.EnhancedMap;

public class EnhancedMappable {

	/// If the ID is negative, it means it is unassigned.
	int mapId = -1;

	public int getMapID() {
		return this.mapId;
	}

	public void setMapID(Integer id) {
		this.mapId = id;
	}
}
