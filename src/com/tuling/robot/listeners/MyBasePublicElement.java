package com.tuling.robot.listeners;

import android.content.Context;

import com.tuling.robot.activities.MainActivity;
import com.tuling.robot.handler.MyHandler;
import com.turing.androidsdk.TuringApiManager;
import com.turing.androidsdk.asr.VoiceRecognizeManager;

public class MyBasePublicElement {

	public MyHandler mMyHandler;
	public String myWakeUpWords;
	public TuringApiManager mTuringApiManager;
	public VoiceRecognizeManager mVoiceRecognizeManager;
	public MainActivity mMainActivity;
	
	public MyBasePublicElement(Context context) {
		mMyHandler = MyHandler.getInstance(context);
		mMainActivity = (MainActivity) context;  
		myWakeUpWords = mMainActivity.myWakeUpWords;
		mTuringApiManager = mMainActivity.mTuringApiManager;
		mVoiceRecognizeManager = mMainActivity.mVoiceRecognizeManager;
	}
}
