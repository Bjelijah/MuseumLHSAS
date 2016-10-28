package com.howell.talk;

import com.howell.formusemu.action.LoginAction;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;



public class TcpLongSocketService extends Service{

	public static final String IP = LoginAction.getInstance().getWebserviceIp();
	public static final int PORT = 8814;
	private TcpLongSocket tcpSocket;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		startLongConnect();
		super.onCreate();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private void startLongConnect(){
		if (tcpSocket != null)
			tcpSocket.close();
		tcpSocket = null;
		if (tcpSocket == null) {
			tcpSocket = new TcpLongSocket(SocketManager.getInstance());
			tcpSocket.startConnect(IP, PORT);
		} else {
			// 检查是否连接成功
			Log.i("====",
					"fanliang....tcpSocket not null ="
							+ tcpSocket.getConnectStatus());
			if (!tcpSocket.getConnectStatus()) {
				tcpSocket.close();
				tcpSocket = null;
			}
		}
	}
	
	
}
