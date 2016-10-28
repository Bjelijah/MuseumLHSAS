package com.howell.formusemu.action;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

import com.howell.museumlhs.R;
import com.howell.utils.SdCardUtil;

/**
 * 
 * @author cbj
 * when alarm come alarm sonnd pool play
 */
public class AlarmSound {

	SoundPool soundPool = null;
	
	
	@Override
	protected void finalize() throws Throwable {
		playDeinit();
		super.finalize();
	}



	private void playInit(){
		
	}
	
	
	
	private void playDeinit(){
		Log.e("123", "playDeinit");
		if (soundPool!=null) {
			soundPool.release();
			soundPool = null;
		}
	}
	
	
	private int getResId(int AlarmDev){
		int p=0;
//		switch (AlarmDev) {
//		case 1:
//			p = com.howell.formuseum.R.raw.my_alarm1;
//			break;
//		case 2:
//			p = com.howell.formuseum.R.raw.my_alarm2;
//			break;
//		case 3:
//			p = com.howell.formuseum.R.raw.my_alarm3;
//			break;
//		case 4:
//			p = com.howell.formuseum.R.raw.my_alarm4;
//			break;
//		case 5:
//			p = com.howell.formuseum.R.raw.my_alarm5;
//			break;
//		case 6:
//			p = com.howell.formuseum.R.raw.my_alarm6;
//			break;
//		case 7:
//			p = com.howell.formuseum.R.raw.my_alarm7;
//			break;
//		case 8:
//			p = com.howell.formuseum.R.raw.my_alarm8;
//			break;
//		case 9:
//			p = com.howell.formuseum.R.raw.my_alarm9;
//			break;
//		case 10:
//			p = com.howell.formuseum.R.raw.my_alarm10;
//			break;
//		default:
//			p = com.howell.formuseum.R.raw.my_alaram_default;
//			break;
//		}
		return p;
	}
	
	
	private String getResFile(String AlarmDev){
		return SdCardUtil.getAlarmSoundFilePath(AlarmDev);
	}
	
	
	/**
	 * 
	 * @param AlarmDev : 报警器号码
	 */
	public void playSound(final Context context,final int AlarmDev){
		
		new Thread(){
			public void run() {
				soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
				soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
						// TODO play
						soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f);
					}
				});
				int p = getResId(AlarmDev);
				soundPool.load(context, p,1);
			};
		}.start();
	}
	
	public void playSound(final Context context,final String AlarmDev){
		Log.i("123", "play sound alarmdev="+AlarmDev);
		new Thread(){
			public void run() {
				soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
				soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
						// TODO play
						Log.i("123", "on load complete");
						soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f);
					}
				});
				String p = getResFile(AlarmDev);
				Log.i("123", "p="+p);
				if(p==null){
					soundPool.load(context, R.raw.my_alaram_default,1);

				}else{
					soundPool.load(p, 1);
				}
			};
		}.start();

	}
}
