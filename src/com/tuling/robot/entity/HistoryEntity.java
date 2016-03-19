package com.tuling.robot.entity;

public class HistoryEntity {
	private int _id;
	private int type;
	private int msgType;
	private String content;
	private String date;
	
	public HistoryEntity () {}
	
	public HistoryEntity (int _id, int type, int msgType, String content, String date) {
		this._id = _id;
		this.type = type;
		this.msgType = msgType;
		this.content = content;
		this.date = date;
	}
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}
	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
