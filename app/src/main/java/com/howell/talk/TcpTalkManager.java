package com.howell.talk;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.howell.protocol.entity.TalkAlive;
import com.howell.protocol.entity.TalkRegister;
import com.howell.protocol.entity.TalkRes;
import com.howell.utils.JsonUtils;

import android.util.Log;
import struct.JavaStruct;
import struct.StructClass;
import struct.StructException;
import struct.StructField;

public class TcpTalkManager implements ITalkProtocol{
	private static TcpTalkManager mInstance = null;
	public static TcpTalkManager getInstance(){
		if(mInstance == null){
			mInstance = new TcpTalkManager();
		}
		return mInstance;
	}
	private TcpTalkManager(){}
	
	/*
	 * protocol entity
	 */
	private TalkRegister.Res 	talkRegRes;
	private TalkAlive 			talkAlive;
	private TalkRes   			talkRes;
	
	@StructClass
	private class ProtocolHead{
		@StructField(order = 0)
		public int tag;
		@StructField(order = 1)
		public int ver;
		@StructField(order = 2)
		public int seq;
		@StructField(order = 3)
		public int cmd;
		@StructField(order = 4)
		public byte type;
		@StructField(order = 5)
		public byte [] res0 = new byte[3];
		@StructField(order = 6)
		public int len;
	}
	
	private byte [] buildProtocolHead(int cmd,int sequence,int len){
		ProtocolHead head = new ProtocolHead();
		head.tag = HWVOICE_PROTOCOL_TAG;
		head.ver = HWVOICE_PROTOCOL_VERSION;
		head.seq = sequence;
		head.cmd = cmd;
		head.len = len;
		byte [] bytes = null;
		try {
			bytes = JavaStruct.pack(head);
		} catch (StructException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	private ProtocolHead parseProtocolHead(byte [] bytes){
		ProtocolHead head = new ProtocolHead();
		try {
			JavaStruct.unpack(head, bytes);
		} catch (StructException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return head;
	}
	
	public void structTest(){
		byte [] b = buildProtocolHead(HWVOICE_PROTOCOL_ALIVE,547,48);
		Log.i("123", "b len="+b.length);
		ProtocolHead head = parseProtocolHead(b);	
		Log.i("123", "tag:"+head.tag+" ver:"+head.ver+" seq:"+head.seq+" cmd:"+head.cmd+" len:"+head.len);
	}
	
	public void processData(byte [] data) throws JSONException{
		ProtocolHead head = null;
		if (data.length>=24) {
			byte [] headbuf = new byte[24];
			System.arraycopy(data, 0, headbuf, 0, 24);
			head = parseProtocolHead(headbuf);
		}
		if(head!=null&&head.len>0){
			byte [] buf = new byte[head.len];
			System.arraycopy(data, 24, buf, 0, head.len);
			processMsg(head.cmd,buf);	
		}else{
			Log.e("123", "process head error!!!");
		}	
	}
	
	private void processMsg(int cmd,byte [] msg) throws JSONException{
		
		switch (cmd) {
		case HWVOICE_PROTOCOL_ACK|HWVOICE_PROTOCOL_REGISTER:
			talkRegRes 	= JsonUtils.parseTalkRegist(new JSONObject(msg.toString()));
			break;
		case HWVOICE_PROTOCOL_ACK|HWVOICE_PROTOCOL_ALIVE:
			talkAlive 	= JsonUtils.parseTalkAlive(new JSONObject(msg.toString()));
			break;
		case HWVOICE_PROTOCOL_ACK|HWVOICE_PROTOCOL_SENDING:
			talkRes 	= JsonUtils.paraseTalkRes(new JSONObject(msg.toString()));
			break;
		case HWVOICE_PROTOCOL_ACK|HWVOICE_PROTOCOL_DIALOG_LIST:
			break;
			
		default:
			break;
		}
	}
	
	
}
