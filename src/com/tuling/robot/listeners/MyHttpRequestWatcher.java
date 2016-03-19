package com.tuling.robot.listeners;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tuling.robot.activities.MainActivity;
import com.turing.androidsdk.HttpRequestWatcher;

public class MyHttpRequestWatcher extends MyBasePublicElement implements HttpRequestWatcher{

	public MyHttpRequestWatcher(Context context) {
		super(context);
	}
	
	@Override
	public void onSuceess(String arg0) {
		try {
			JSONObject jsonObject = new JSONObject(arg0);
			
			if (jsonObject.has("code")) {
				int code = jsonObject.getInt("code");
				switch (code) {
					case 100000:
						// 文本类
						mMyHandler.obtainMessage(1, jsonObject.get("text")).sendToTarget();
						break;
					case 200000:
						// 图片类
						mMyHandler.obtainMessage(2, jsonObject).sendToTarget();
						break;
					case 302000:
						// 新闻类
						mMyHandler.obtainMessage(3, jsonObject).sendToTarget();
						break;
					default:
						mMyHandler.obtainMessage(1, jsonObject.get("text")).sendToTarget();
						break;
				}
				
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String arg0) {
	}
	
}
