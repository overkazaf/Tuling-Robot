package com.tuling.robot.listeners;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.turing.androidsdk.HttpRequestWatcher;

public class MyHttpRequestWatcher extends MyBasePublicElement implements HttpRequestWatcher{

	public MyHttpRequestWatcher(Context context) {
		super(context);
	}
	
	@Override
	public void onSuceess(String arg0) {
		try {
			JSONObject jsonObject = new JSONObject(arg0);
			if (jsonObject.has("text")) {
				mMyHandler.obtainMessage(1,
						jsonObject.get("text")).sendToTarget();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String arg0) {
	}
	
}
