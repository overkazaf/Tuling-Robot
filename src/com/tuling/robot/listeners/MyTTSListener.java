package com.tuling.robot.listeners;
import android.content.Context;

import com.turing.androidsdk.tts.TTSListener;

public class MyTTSListener extends MyBasePublicElement implements TTSListener {

	public MyTTSListener(Context context) {
		super(context);
	}

	@Override
	public void onSpeechCancel() {
	}

	@Override
	public void onSpeechError(int arg0) {

	}

	@Override
	public void onSpeechFinish() {
		mVoiceRecognizeManager.startRecognize();
		mMyHandler.obtainMessage(4).sendToTarget();
	}

	@Override
	public void onSpeechPause() {
	}

	@Override
	public void onSpeechProgressChanged() {
	}

	@Override
	public void onSpeechStart() {
	}
}
