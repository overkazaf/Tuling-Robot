package com.tuling.robot.listeners;
import android.content.Context;

import com.turing.androidsdk.asr.VoiceRecognizeListener;

public class MyVoiceRecognizeListener extends MyBasePublicElement implements
		VoiceRecognizeListener {

	public MyVoiceRecognizeListener(Context context) {
		super(context);
	}

	@Override
	public void onRecognizeError(String arg0) {
		mVoiceRecognizeManager.startRecognize();
		mMyHandler.obtainMessage(4).sendToTarget();
	}

	@Override
	public void onRecognizeResult(String result) {
		handleRecognizeResult(result);
	}

	private void handleRecognizeResult(String result) {
		// ʶ�𵽻���󣬽��䷢�������������������������ش�
		mTuringApiManager.requestTuringAPI(result);
		mMyHandler.obtainMessage(3, result).sendToTarget();
	}

	@Override
	public void onRecordEnd() {
	}

	@Override
	public void onRecordStart() {
	}

	@Override
	public void onStartRecognize() {
		// ����԰ٶ�ʶ����Ч
	}

	@Override
	public void onVolumeChange(int arg0) {
		// ����Ե���Ѷ��ʱ��Ч
	}

}
