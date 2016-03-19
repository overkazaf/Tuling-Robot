package com.tuling.robot.entity;

import android.app.Activity;

public class Turing {
	private Activity mainActivity;
	private Activity historyActivity;
	private Activity eventsActivity;
	
	public Turing () {
		
	}
	
	public Activity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(Activity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public Activity getHistoryActivity() {
		return historyActivity;
	}

	public void setHistoryActivity(Activity historyActivity) {
		this.historyActivity = historyActivity;
	}

	public Activity getEventsActivity() {
		return eventsActivity;
	}

	public void setEventsActivity(Activity eventsActivity) {
		this.eventsActivity = eventsActivity;
	}
}
