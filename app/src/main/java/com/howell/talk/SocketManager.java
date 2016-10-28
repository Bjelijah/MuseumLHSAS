package com.howell.talk;

import org.json.JSONException;



public class SocketManager implements TCPLongSocketCallback{
	private static SocketManager mInstance = null;
	public static SocketManager getInstance(){
		if(mInstance==null){
			mInstance = new SocketManager();
		}
		return mInstance;
	}
	private SocketManager(){}
	
	TcpLongSocket tcpLongSocket;
	TcpTalkManager tcpTalkMgr = TcpTalkManager.getInstance();
	boolean isConnected = false;
	
	public void writeData(byte [] data){
		if(!isConnected)return;
		tcpLongSocket.writeDate(data);
	}
	
	@Override
	public void receive(byte[] buffer) {
		// TODO Auto-generated method stub
		try {
			tcpTalkMgr.processData(buffer);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		isConnected = false;
		if(tcpLongSocket!=null){
			tcpLongSocket.close();
			tcpLongSocket = null;
		}
	}
	@Override
	public void connected(TcpLongSocket t) {
		// TODO Auto-generated method stub
		this.tcpLongSocket = t;
		isConnected = true;
	}
	
	

		
}
