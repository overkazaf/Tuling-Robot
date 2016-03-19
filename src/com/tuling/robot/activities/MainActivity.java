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
import com.tuling.robot.config.DBConfig;
import com.tuling.robot.entity.EventEntity;
import com.tuling.robot.entity.HistoryEntity;
import com.tuling.robot.helper.DBOpenHelper;
import com.tuling.robot.listeners.MyHttpRequestWatcher;
import com.tuling.robot.listeners.MyInitListener;
import com.tuling.robot.utils.Util;
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
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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
	public Button mHistory;
	public Button mEvents;

	public TimePickerDialog timePickerDialog;
	public MyMsgAdapter msgAdapter;
	public ListView msgListView;
	public TuringApiConfig turingApiConfig;
	
	public static final String MY_TULING_KEY = "d78361cfe323e083abc5908626b42d2c";
	public static final String MY_USER_ID = "9527";
	
	private int pickedHour;
	private int pickedMinute;

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
		mHistory = (Button) findViewById(R.id.btn_history);
		mEvents = (Button) findViewById(R.id.btn_events);
		msgListView = (ListView) findViewById(R.id.msgListView);

		msgAdapter = new MyMsgAdapter(this);
		msgListView.setAdapter(msgAdapter);

		initClickEvents();
		initTuling();
		initListView();
	}
	
	public void initListView () {
		DBConfig config = new DBConfig();
		DBOpenHelper helper = new DBOpenHelper(this, config.getDbName());
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "SELECT * FROM " + config.getHistoryTableName() + " order by date desc, type asc limit 10";
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				int msgType = cursor.getInt(cursor.getColumnIndex("msg_type"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				String date = cursor.getString(cursor.getColumnIndex("date"));
				boolean robotFlag = type == 1;
				
				HistoryEntity entity = new HistoryEntity(_id, type, msgType, content, date);
				if (robotFlag) {
					appendAnswer(entity, false, true);
				} else {
					appendQuestion(entity, false, true);
				}
			}
			db.close();
		}
	}
	
	public void initClickEvents () {
		mEvents.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent evtIntent = new Intent();
				evtIntent.setClass(MainActivity.this, EventsActivity.class);
				startActivity(evtIntent);
				MainActivity.this.finish();
			}
		});
		
		
		mHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent hisIntent = new Intent();
				hisIntent.setClass(MainActivity.this, HistoryActivity.class);
				startActivity(hisIntent);
				MainActivity.this.finish();
			}
		});
		
		// 发送问题
		mSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String mString = mText.getText().toString();
				if (mString != null && !TextUtils.isEmpty(mString) && !isAvailableCommand(mString)) {
					String formatedDate = Util.FormatDate(new Date());
					HistoryEntity entity = new HistoryEntity(-1, 0, 1, mString, formatedDate);
					appendQuestion(entity, true, false);
					doRequest();
				} else {
					if (isAvailableCommand(mString)) {
						// 显示事件
						final EditText dialogText = new EditText(MainActivity.this);
						new AlertDialog.Builder(MainActivity.this).setTitle("请输入需要提醒的事件")
								.setIcon(android.R.drawable.ic_dialog_info).setView(dialogText)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								String eventName = dialogText.getText().toString();
								String eventString = "设置提醒事件：" + eventName;
								HistoryEntity entity = new HistoryEntity(-1, 0, 1, eventString, Util.FormatDate(new Date()));
								appendQuestion(entity, true, false);
								// 显示闹钟
								displayAlarm(eventName);
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// appendAnswer("取消提醒");
							}
						}).show();
						
					} else {
						Toast.makeText(MainActivity.this, "请输入聊天内容", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
	}
	
	public void initTuling () {
		// 初始化图灵api
		initTulingApiManager();
		// 监听http请求
		addListener();
	}

	public void displayAlarm(final String eventName) {
		
		
		Calendar currentTime = Calendar.getInstance();
		// 弹出一个时间设置的对话框,供用户选择时间
		
		timePickerDialog = new TimePickerDialog(MainActivity.this, 0,  new OnTimeSetListener() {

			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// 设置当前时间
				Calendar timeSetCalander = Calendar.getInstance();
				timeSetCalander.set(Calendar.HOUR, hourOfDay);
				timeSetCalander.set(Calendar.MINUTE, minute);
				timeSetCalander.set(Calendar.SECOND, 0);
				
				
				Intent intent = new Intent(MainActivity.this, ClockActivity.class);
				intent.putExtra("eventName", eventName);
				
				AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
				PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
				
				// ②设置AlarmManager在Calendar对应的时间启动Activity
				String eventDate = Util.FormatDate(timeSetCalander.getTime());
				alarmManager.set(AlarmManager.RTC_WAKEUP, timeSetCalander.getTimeInMillis(), pi);
				String eventString = "事件("+ eventName + ")将提醒于：" + eventDate;
				HistoryEntity entity = new HistoryEntity(-1, 1, 1, eventString, eventDate);
				appendAnswer(entity, true, false);
				
				EventEntity eventEntity = new EventEntity(-1, eventName, Util.FormatDate(new Date()), eventDate);
				updateEvents(eventEntity);
			}
		}
		, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);

		
		
		timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE,  
                getString(R.string.BTN_OK),  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) { 
                    	
                    }  
                });  
		timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE,  
               "",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) { 
                    }  
                });  
		
		timePickerDialog.setCancelable(false);
		timePickerDialog.show();
	}
	

	public boolean isAvailableCommand(String fnName) {
		// 自定义提醒的命令值
		String[] availableCommands = new String[]{
			"alarm", "提醒", "闹钟", "闹铃", "备忘"
		};
		
		for (String command : availableCommands) {
			if (command.equals(fnName)) {
				return true;
			}
		}
		
		return false;
	}

	public void doRequest() {
		String info = mText.getText().toString();
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


	public void appendQuestion(HistoryEntity entity, boolean updateDatabase, boolean addFromHead) {
		if (addFromHead) {
			msgAdapter.arr.add(0, entity);
		} else {
			msgAdapter.arr.add(entity);
		}
		
		msgAdapter.notifyDataSetChanged();
		enableSendBtn(false);
		
		if (updateDatabase) {
			Toast.makeText(MainActivity.this, "工作人员紧张查询中...", Toast.LENGTH_LONG).show();
			updateHistory(entity);
		}
	}

	public void appendAnswer(HistoryEntity entity, boolean updateDatabase, boolean addFromHead) {
		
		if (addFromHead) {
			msgAdapter.arr.add(0, entity);
		} else {
			msgAdapter.arr.add(entity);
		}
		
		msgAdapter.notifyDataSetChanged();
		enableSendBtn(true);
		
		if (updateDatabase) {
			updateHistory(entity);
		}
		
	}
	
	public void updateHistory (HistoryEntity entity) {
		DBConfig config = new DBConfig();
		DBOpenHelper helper = new DBOpenHelper(this, config.getDbName());
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("type", entity.getType());
		values.put("msg_type", entity.getMsgType());
		values.put("content", entity.getContent());
		values.put("date", entity.getDate());
		db.insert(config.getHistoryTableName(), null, values);
		db.close();
	}
	
	public void updateEvents (EventEntity entity) {
		DBConfig config = new DBConfig();
		DBOpenHelper helper = new DBOpenHelper(this, config.getDbName());
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("info", entity.getInfo());
		values.put("register_date", entity.getRegisterDate());
		values.put("warn_date", entity.getWarnDate());
		db.insert(config.getEventsTableName(), null, values);
		db.close();
	}
	
	public void enableSendBtn (boolean inputEnabled) {
		//mText.setText("");
		//mSend.setClickable(inputEnabled);
	}

	private class MyMsgAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		public ArrayList<HistoryEntity> arr;

		public MyMsgAdapter(Context context) {
			super();
			this.context = context;
			inflater = LayoutInflater.from(context);
			arr = new ArrayList<HistoryEntity>();
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
			HistoryEntity entity = arr.get(position);
			String currentUser = "机器人";
			if (entity.getType() == 0) {
				view = inflater.inflate(R.layout.msg_right_item, null);
				currentUser = "我";
			} else {
				view = inflater.inflate(R.layout.msg_left_item, null);
			}

			TextView tView = (TextView) view.findViewById(R.id.tv_chatcontent);
			TextView sendTime = (TextView) view.findViewById(R.id.tv_sendtime);
			TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
			
			 // 在重构adapter的时候不至于数据错乱
			sendTime.setText(entity.getDate());
			tv_username.setText(currentUser);

			
			switch (entity.getMsgType()) {
				case 2:
				case 3:
					tView.setText(entity.getContent());
					tView.setAutoLinkMask(Linkify.ALL); 
					tView.setMovementMethod(LinkMovementMethod.getInstance());  
					break;
				default:
					tView.setText(entity.getContent());
					break;
			}
			msgListView.setSelection(msgListView.getCount() - 1);

			return view;
		}
	}

}
