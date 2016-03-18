package com.tuling.robot.handler;

import com.tuling.robot.R;
import com.tuling.robot.activities.MainActivity;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
		switch (msg.what) {
		case 1:
			mMainActivity.appendAnswer("" + (String) msg.obj);
			Log.i("info", (String) msg.obj);
			break;

		case 3:
			mMainActivity.mStatus.setText("ʶ������" + msg.obj);
			break;

		case 4:
			mMainActivity.mStatus.setText("��ʼʶ��");
			break;

		default:
			break;
		}
	};
}
