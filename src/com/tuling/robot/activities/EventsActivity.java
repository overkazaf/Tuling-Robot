package com.tuling.robot.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.tuling.robot.R;
import com.tuling.robot.config.DBConfig;
import com.tuling.robot.entity.EventEntity;
import com.tuling.robot.helper.DBOpenHelper;

import android.app.Activity;
import android.app.usage.UsageEvents.Event;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.CalendarContract.EventsEntity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventsActivity extends Activity {
	private Button btn_search, btn_back;
	private EditText etEvents;
	private DBConfig config;
	private DBOpenHelper helper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private MyEventsAdapter eventsAdapter;
	private ListView eventsListView;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		initActivity();
	}
	
	private void initActivity () {
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_search = (Button) findViewById(R.id.btn_search);
		etEvents = (EditText) findViewById(R.id.etEvents);
		config = new DBConfig();
		helper = new DBOpenHelper(EventsActivity.this, config.getDbName());
		db = helper.getReadableDatabase();
		
		eventsAdapter = new MyEventsAdapter(this);
		eventsListView = (ListView) findViewById(R.id.eventsListView);
		eventsListView.setAdapter(eventsAdapter);
		initClickEvents();
	};
	
	private void initClickEvents () {
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(EventsActivity.this, MainActivity.class);
				startActivity(intent);
				EventsActivity.this.finish();
			}
		});
		
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String key = etEvents.getText().toString();
				String sql = "SELECT * FROM " + config.getEventsTableName() + " WHERE info LIKE '%"+ key +"%' order by warn_date asc";
				
				cursor = db.rawQuery(sql, null);
				if (cursor != null) {
					eventsAdapter.arr.clear();
					while (cursor.moveToNext()) {
						EventEntity entity = new EventEntity();
						entity.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
						entity.setInfo(cursor.getString(cursor.getColumnIndex("info")));
						entity.setRegisterDate(cursor.getString(cursor.getColumnIndex("register_date")));
						entity.setWarnDate(cursor.getString(cursor.getColumnIndex("warn_date")));
						eventsAdapter.arr.add(entity);
					}
					eventsAdapter.notifyDataSetChanged();
					
					if (eventsAdapter.arr.size() == 0) {
						Toast.makeText(EventsActivity.this, "抱歉， 未查询到相关信息" , Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(EventsActivity.this, "查询到相关 " + eventsAdapter.arr.size() + " 条信息", Toast.LENGTH_LONG).show();
					}
					
				} else {
					Toast.makeText(EventsActivity.this, "Null cursor exception occurs", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	
	private class MyEventsAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		public ArrayList<EventEntity> arr;
		
		public MyEventsAdapter (Context context) {
			this.context = context;
			this.inflater = LayoutInflater.from(context);;
			this.arr = new ArrayList<EventEntity>();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.arr.size();
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
		public View getView(int position, View view, ViewGroup arg2) {
			if (view == null) {
				view = inflater.inflate(R.layout.event_item, null);
			}
			TextView registerDate = (TextView) view.findViewById(R.id.tv_register_date);
			TextView detail = (TextView) view.findViewById(R.id.tv_info_detail);
			EventEntity entity = arr.get(position);
			String detailString = entity.getInfo() + " 将于 " + entity.getWarnDate() + " 给予提醒";
			registerDate.setText(entity.getRegisterDate());
			detail.setText(detailString);
			return view;
		}
	}
}
