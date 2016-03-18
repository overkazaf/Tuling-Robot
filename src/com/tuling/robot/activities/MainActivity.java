package com.tuling.robot.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.tuling.robot.R;
import com.tuling.robot.listeners.MyHttpRequestWatcher;
import com.tuling.robot.listeners.MyInitListener;
import com.turing.androidsdk.TuringApiConfig;
import com.turing.androidsdk.TuringApiManager;
import com.turing.androidsdk.asr.VoiceRecognizeManager;
import com.turing.androidsdk.tts.TTSManager;

import android.R.bool;
import android.animation.AnimatorSet.Builder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("ServiceCast")
public class MainActivity extends Activity {
	public VoiceRecognizeManager mVoiceRecognizeManager;
	public TuringApiManager mTuringApiManager;
	public TTSManager ttsManager;
	public String myWakeUpWords;
	public TextView mStatus;
	public EditText mText;
	public Button mSend;

	public Intent clockIntent;
	public PendingIntent pi;
	public TimePickerDialog timePickerDialog;
	public AlarmManager alarmManager;
	public MyMsgAdapter msgAdapter;
	public ListView msgListView;
	public TuringApiConfig turingApiConfig;
	public static final String MY_TULING_KEY = "d78361cfe323e083abc5908626b42d2c";
	public static final String MY_USER_ID = "anoynomous";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initApplication();
	}

	public void initApplication() {

		mStatus = (TextView) findViewById(R.id.status);
		mText = (EditText) findViewById(R.id.editText);
		mSend = (Button) findViewById(R.id.btn_send);
		msgListView = (ListView) findViewById(R.id.msgListView);

		msgAdapter = new MyMsgAdapter(this);
		msgListView.setAdapter(msgAdapter);

		mSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String mString = mText.getText().toString();
				if (mString != null && !TextUtils.isEmpty(mString) && !isExtraFn(mString)) {
					appendQuestion(mString);
					doRequest();
				} else {
					if (isExtraFn(mString)) {
						// 显示事件
						final EditText dialogText = new EditText(MainActivity.this);
						new AlertDialog.Builder(MainActivity.this).setTitle("请输入需要提醒的事件")
								.setIcon(android.R.drawable.ic_dialog_info).setView(dialogText)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								String eventName = dialogText.getText().toString();
								appendQuestion("设置提醒事件：" + eventName);
								// 显示闹钟
								displayAlarm(eventName);
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// appendAnswer("取消提醒");
							}
						}).show();
						
					} else {
						Toast.makeText(MainActivity.this, "请输入聊天信息", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
		// 初始化图灵api
		initTulingApiManager();

		// 监听http请求
		addListener();
	}

	public void displayAlarm(final String eventName) {
		clockIntent = new Intent(MainActivity.this, ClockActivity.class);
		clockIntent.putExtra("eventName", eventName);
		
		alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
		pi = PendingIntent.getActivity(MainActivity.this, 0, clockIntent, 0);
		Calendar currentTime = Calendar.getInstance();
		// 弹出一个时间设置的对话框,供用户选择时间
		timePickerDialog = new TimePickerDialog(MainActivity.this, 0, new OnTimeSetListener() {
			
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// 设置当前时间
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				// 根据用户选择的时间来设置Calendar对象
				c.set(Calendar.HOUR, hourOfDay);
				c.set(Calendar.MINUTE, minute);
			}
		}, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false);

		timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE,  
                getString(R.string.BTN_OK),  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) { 
                    	Calendar c = Calendar.getInstance();
                    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        				String eventDate = sdf.format(new Date(c.getTimeInMillis()));
        				// ②设置AlarmManager在Calendar对应的时间启动Activity
        				alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                    	appendAnswer("事件("+ eventName + ")将提醒于：" + eventDate);
                    }  
                });  
		timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE,  
                getString(R.string.BTN_CANCEL),  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) { 
                    	alarmManager.cancel(pi);
                    	appendAnswer("提醒事件("+ eventName + ")已被取消");
                    }  
                });  
		timePickerDialog.show();
	}

	public boolean isExtraFn(String fnName) {
		return fnName.equals("alarm");
	}

	public void doRequest() {
		String info = mText.getText().toString();
		Log.i("info", "asking:" + info);
		mTuringApiManager.requestTuringAPI(info);
	}

	private void addListener() {
		mTuringApiManager.setRequestWatcher(new MyHttpRequestWatcher(this));
	}

	private void initTulingApiManager() {
		turingApiConfig = new TuringApiConfig(this, MY_TULING_KEY);

		turingApiConfig.setInitListener(new MyInitListener());

		turingApiConfig.init(this);

		mTuringApiManager = new TuringApiManager(turingApiConfig, this);
	}

	public void test() {
		SQLiteDatabase database = openOrCreateDatabase("tr.db", MODE_PRIVATE, null);
		database.execSQL(
				"create table if not exists trtb(_id integer primary key autoincrement, name text not null, age integer not null)");

		ContentValues values = new ContentValues();

		values.put("name", "john");
		values.put("age", 27);
		database.insert("trtb", null, values);
		values.clear();

		values.put("name", "kevin");
		values.put("age", 23);
		database.insert("trtb", null, values);
		values.clear();

		values.put("name", "tracy");
		values.put("age", 22);
		database.insert("trtb", null, values);
		values.clear();

		Cursor cursor = database.query("trtb", null, "_id > ?", new String[] { "1" }, null, null, "age");
		if (cursor != null) {
			String[] columnNames = cursor.getColumnNames();
			while (cursor.moveToNext()) {
				for (String column : columnNames) {
					Log.i("info", cursor.getString(cursor.getColumnIndex(column)));
				}
			}
		}
	}

	public void appendQuestion(String question) {
		msgAdapter.arr.add(question);
		msgAdapter.notifyDataSetChanged();
		
		updateInputArea(false);
	}

	public void appendAnswer(String answer) {
		msgAdapter.arr.add(answer);
		msgAdapter.notifyDataSetChanged();
		
		updateInputArea(true);
	}
	
	public void updateInputArea (boolean inputEnabled) {
		mText.setText("");
		mSend.setClickable(inputEnabled);
	}

	private class MyMsgAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		public ArrayList<String> arr;

		public MyMsgAdapter(Context context) {
			super();
			this.context = context;
			inflater = LayoutInflater.from(context);
			arr = new ArrayList<String>();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arr.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			String currentUser = "机器人";
			if (position % 2 == 0) {
				view = inflater.inflate(R.layout.msg_right_item, null);
				currentUser = "我";
			} else {
				view = inflater.inflate(R.layout.msg_left_item, null);
			}

			TextView tView = (TextView) view.findViewById(R.id.tv_chatcontent);
			TextView sendTime = (TextView) view.findViewById(R.id.tv_sendtime);
			TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
			tView.setText(arr.get(position)); // 在重构adapter的时候不至于数据错乱

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String currentTime = format.format(new Date());
			sendTime.setText(currentTime);
			tv_username.setText(currentUser);

			msgListView.setSelection(msgListView.getCount() - 1);

			return view;
		}
	}

}
