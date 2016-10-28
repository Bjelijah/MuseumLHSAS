package com.howell.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 霍之昊 
 *
 * 类说明:计算程序运行时间
 */
public class SystemUpTimeUtils {
	private int systemUptime;
	private Timer timer;
	public SystemUpTimeUtils() {
		// TODO Auto-generated constructor stub
		this.systemUptime = 0;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				systemUptime ++;
			}
		}, 0, 1 * 1000);
	}
	
	public int getSystemUpTime(){
		return systemUptime;
	}
	
	public void stopTimer(){
		if(timer != null){
			timer.cancel();
			timer = null;
		}
	}
	
}
