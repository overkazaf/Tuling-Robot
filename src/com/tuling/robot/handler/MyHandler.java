package com.tuling.robot.handler;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tuling.robot.R;
import com.tuling.robot.activities.MainActivity;
import com.tuling.robot.entity.HistoryEntity;
import com.tuling.robot.utils.Util;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MyHandler extends Handler {

	private static MyHandler mMyHandler;
	private MainActivity mMainActivity;
	private LinearLayout chatLayout;

	public static MyHandler getInstance(Context context) {
		if (mMyHandler == null) {
			mMyHandler = new MyHandler(context);
		}
		return mMyHandler;
	}

	public MyHandler(Context context) {
		mMainActivity = (MainActivity) context;
		chatLayout = (LinearLayout) mMainActivity.findViewById(R.id.chatLayout);
	}

	@Override
	public void handleMessage(android.os.Message msg) {
		String content;
		HistoryEntity entity;
		JSONObject object;
		switch (msg.what) {
		case 1:
			// 文本类
			content = "" + (String) msg.obj;
			entity = new HistoryEntity(-1, 1, 1, content, Util.FormatDate(new Date()));
			mMainActivity.appendAnswer(entity, true, false);
			break;

		case 2:
			// 图片类
			object = (JSONObject) msg.obj;
			try {
				content = constructUrl(object);
				entity = new HistoryEntity(-1, 1, 2, content, Util.FormatDate(new Date()));
				mMainActivity.appendAnswer(entity, true, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
			
			
		case 3:
			// 新闻类
			object = (JSONObject) msg.obj;
			try {
				content = constructNews(object);
				entity = new HistoryEntity(-1, 1, 3, content, Util.FormatDate(new Date()));
				mMainActivity.appendAnswer(entity, true, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case 4:
			//mMainActivity.mStatus.setText("��ʼʶ��");
			break;

		default:
			break;
		}
	};
	
	public String constructUrl (JSONObject object) {
		String result = "";
		try {
			result = object.getString("text") + "\n" + object.getString("url");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String constructNews (JSONObject object){
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(object.getString("text") + "\n");
			JSONArray array = object.getJSONArray("list");
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = (JSONObject)array.get(i);
				String title = obj.getString("article");
				String source = obj.getString("source");
				String url = obj.getString("detailurl");
				if (i > 0) {
					sb.append("\n");
				}
				sb.append("标题："+title+"\n");
				sb.append("来源："+source+"\n");
				sb.append("查看："+ url +"\n");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return sb.toString();
	}
}
