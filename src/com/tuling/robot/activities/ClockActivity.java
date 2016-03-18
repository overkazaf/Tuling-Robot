package com.tuling.robot.activities;

import com.tuling.robot.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;

public class ClockActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock);
		
		Intent intent = getIntent();
		// 创建一个闹钟提醒的对话框,点击确定关闭铃声与页面
		AlertDialog alertDialog = new AlertDialog.Builder(ClockActivity.this).setTitle("闹钟事件触发")
				.setMessage("" + intent.getStringExtra("eventName")).setPositiveButton("关闭闹铃", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ClockActivity.this.finish();
					}
				}).show();
		
		alertDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				ClockActivity.this.finish();
			}
		});
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);// must store the new intent unless getIntent() will return the old one
	}

}
