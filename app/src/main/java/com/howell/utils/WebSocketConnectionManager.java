package com.howell.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class WebSocketConnectionManager {
	private int heartBeatCount;
	private int lastCount;
	private Timer timer;
	
	private OnDisconncetListener disconncetListener;
	
	public WebSocketConnectionManager() {
		// TODO Auto-generated constructor stub
		startTimer();
	}
	
	public interface OnDisconncetListener{//ob 接口
		public void OnDisconnect();
	};
	
	public void setOnDisconncetListener(OnDisconncetListener disconncetListener){
		this.disconncetListener = disconncetListener;
	}
	
	public void keepAlive(){
		heartBeatCount ++;
	}
	
	private void startTimer(){
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e("", "heartBeatCount:"+heartBeatCount+",lastCount:"+lastCount);
				if(lastCount == heartBeatCount){
					if(disconncetListener != null){
						disconncetListener.OnDisconnect();
					}
				}
				lastCount = heartBeatCount;
			}
		} , 120 * 1000 , 120 * 1000);
	}
	
	public void stopTimer(){
		if(timer != null){
			timer.cancel();
			timer = null;
		}
	}
	
}
