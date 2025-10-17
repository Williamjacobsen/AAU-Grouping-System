package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

@Component
public class Database {

	private DatabaseData data = new DatabaseData();

	// getters & setters

	void setData(DatabaseData data) {
		this.data = data;
	}

	public DatabaseData getData() {
		return data;
	}
}
