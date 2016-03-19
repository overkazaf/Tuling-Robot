package com.tuling.robot.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.tuling.robot.R;
import com.tuling.robot.config.DBConfig;
import com.tuling.robot.entity.HistoryEntity;
import com.tuling.robot.helper.DBOpenHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Type;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends Activity {
	private Button btn_search, btn_back;
	private EditText etHistory;
	private DBConfig config;
	private DBOpenHelper helper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private MyHistoryAdapter historyAdapter;
	private ListView historyListView;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		initActivity();
	}
	
	private void initActivity () {
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_search = (Button) findViewById(R.id.btn_search);
		etHistory = (EditText) findViewById(R.id.etHistory);
		config = new DBConfig();
		helper = new DBOpenHelper(HistoryActivity.this, config.getDbName());
		db = helper.getReadableDatabase();
		
		historyAdapter = new MyHistoryAdapter(this);
		historyListView = (ListView) findViewById(R.id.historyListView);
		historyListView.setAdapter(historyAdapter);
		initClickEvents();
	}
	
	private void initClickEvents () {
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(HistoryActivity.this, MainActivity.class);
				startActivity(intent);
				HistoryActivity.this.finish();
			}
		});
		
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String key = etHistory.getText().toString();
				String sql = "SELECT * FROM " + config.getHistoryTableName() + " WHERE content LIKE '%"+ key +"%' order by date asc";
				
				cursor = db.rawQuery(sql, null);
				if (cursor != null) {
					historyAdapter.arr.clear();
					while (cursor.moveToNext()) {
						HistoryEntity entity = new HistoryEntity();
						entity.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
						entity.setType(cursor.getInt(cursor.getColumnIndex("type")));
						entity.setContent(cursor.getString(cursor.getColumnIndex("content")));
						entity.setDate(cursor.getString(cursor.getColumnIndex("date")));
						historyAdapter.arr.add(entity);
					}
					historyAdapter.notifyDataSetChanged();
					if (historyAdapter.arr.size() == 0) {
						Toast.makeText(HistoryActivity.this, "抱歉， 未查询到相关信息" , Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(HistoryActivity.this, "查询到 " + historyAdapter.arr.size() + " 条相关信息", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(HistoryActivity.this, "Null cursor exception occurs", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	
	private class MyHistoryAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		public ArrayList<HistoryEntity> arr;
		
		public MyHistoryAdapter(Context context) {
			super();
			this.context = context;
			inflater = LayoutInflater.from(context);
			arr = new ArrayList<HistoryEntity>();
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
				view = inflater.inflate(R.layout.history_item, null);
			}
			TextView sendTime = (TextView) view.findViewById(R.id.tv_sendtime);
			TextView chatContent = (TextView) view.findViewById(R.id.tv_chatcontent);
			TextView userName = (TextView) view.findViewById(R.id.tv_username);
			ImageView userHead = (ImageView) view.findViewById(R.id.iv_userhead);
			HistoryEntity entity = arr.get(position);
			try {
				
				boolean robotFlag = entity.getType() == 1;
				String user = robotFlag ? "机器人" : "我";
				Resources resources = getBaseContext().getResources(); 
				Drawable drawableHead;
				
				sendTime.setText(entity.getDate());
				chatContent.setText(entity.getContent());
				userName.setText(user);
				if (robotFlag) {
					drawableHead = resources.getDrawable(R.drawable.ic_launcher); 
					
				} else {
					drawableHead = resources.getDrawable(R.drawable.mini_avatar_shadow); 
				}
				
				userHead.setBackgroundDrawable(drawableHead);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			return view;
		}
		
	}
}
