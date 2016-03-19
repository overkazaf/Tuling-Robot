package com.tuling.robot.entity;

public class EventEntity {
	private int _id;
	private String info;
	private String registerDate;
	private String warnDate;
	
	public EventEntity() {
		// TODO Auto-generated constructor stub
	};
	
	public EventEntity (int _id, String info, String registerDate, String warnDate) {
		this._id = _id;
		this.info = info;
		this.registerDate = registerDate;
		this.warnDate = warnDate;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}
	public String getWarnDate() {
		return warnDate;
	}
	public void setWarnDate(String warnDate) {
		this.warnDate = warnDate;
	}
}
