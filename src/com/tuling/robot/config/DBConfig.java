package com.tuling.robot.config;

public class DBConfig {
	private String dbName = "tr.db";
	private String historyTableName = "history";
	private String eventsTableName = "events";
	private String version = "1.0";
	
	public DBConfig () {}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getHistoryTableName() {
		return historyTableName;
	}

	public void setHistoryTableName(String historyTableName) {
		this.historyTableName = historyTableName;
	}

	public String getEventsTableName() {
		return eventsTableName;
	}

	public void setEventsTableName(String eventsTableName) {
		this.eventsTableName = eventsTableName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	
}
