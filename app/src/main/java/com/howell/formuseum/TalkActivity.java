package com.howell.formuseum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.howell.formusemu.action.AudioAction;
import com.howell.formusemu.action.ITalkOB;
import com.howell.formuseum.bean.TalkDialog;
import com.howell.museumlhs.R;
import com.howell.protocol.Const;
import com.howell.utils.DebugUtil;
import com.howell.utils.TalkManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class TalkActivity extends Activity implements OnClickListener,Const,ITalkOB,OnTouchListener{
	
	private static final int LINK_STATE_UPDATA_MSG 	= 0xb1;
	private static final int TALK_STATE_UPDATA_MSG 	= 0xb2;
	
	
	Button btnTest,btnTest2;
	LinearLayout lltalkDialogSetting;
	private ImageView mImgState,mTalkBtn;
	private TextView mTvState,mTvState2;

	
	private int registerState; //与语音平台连接状态
	
	
	
	private TalkManager talkManager;
	private static final int STATE_CHANGE_TO_TALKING = 1;
	private static final int SET_REGISTER_UI 		 = 2;
	private static final int STATE_CHANGE_TO_SILENT  = 3;
	private boolean isLinked = false;//与语音平台的连接状态
	private int talkState;	//语音对讲当前状态
	private boolean bClickPlay = false;
	private boolean bInitNextTargetList = false;
	//google录音类
	private AudioRecord audioRecord;  
	private int recBufSize;  
	//语音对讲各个参数
	private static final int frequency = 8000;  
	private static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;  
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	//是否录放的标记  
	//boolean isRecording = false;
	private RecordPlayThread recordPlayThread;
	private DetectRegisterStateThread detectRegisterStateThread;


	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk);

		init();
//		initAudioRecord();
		checkConnect();
		setState();
		detectRegisterState();
		
	
		talkManager.registerOnTalk(this);
		talkManager.setTestContext(this);
		//获取dialog list
	
		talkManager.getDilagList(0); //0 all  1 mobile  //2 pc
		bInitNextTargetList = false;
		lltalkDialogSetting = (LinearLayout)findViewById(R.id.ll_talk_dialog_list);
		lltalkDialogSetting.setVisibility(View.GONE);
		lltalkDialogSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TalkActivity.this,TalkListActivity.class);
//				Intent intent = new Intent(TalkActivity.this,TalkListExActivity.class);
				TalkActivity.this.startActivity(intent);
			}
		});
		
		
		
		
		//test 
		/*
		btnTest = (Button)findViewById(R.id.btn_talk_test);
//		btnTest.setVisibility(View.GONE);
		btnTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//TalkManager.getInstance().testSetLinked(true);
				TalkManager.getInstance().registTest();
			}
		});
	
		btnTest2 = (Button)findViewById(R.id.btn_talk_test2);
		btnTest2.setVisibility(View.GONE);
		btnTest2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				JNIManager.getInstance().talkUnregister();
				Toast.makeText(TalkActivity.this, "unregister", Toast.LENGTH_SHORT).show();
			}
		});
		*/
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(isLinked && talkState!=TALK_STATE_TALKING){
			mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_1));
		}
		
		super.onResume();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(talkState == REQUEST){
			talkManager.stopTalk();
		}
		talkState = STOP;  
		if(recordPlayThread != null){
			//recordPlayThread.join();
			recordPlayThread = null;
		}
		if(audioRecord != null){
			audioRecord.stop();
			audioRecord.release();
			audioRecord = null;
		}
		if(detectRegisterStateThread != null){
			detectRegisterStateThread = null;
		}
		talkManager.setbInTalk(false);
		talkManager.unregisterOnTalk(this);
		talkManager.stopPlayAudioFromQueue();
		AudioAction.getInstance().stopAudioRecord();
		AudioAction.getInstance().deInitAudioRecord();
	}

	@Override
	public void onBackPressed() {
		Log.i("123", "talk activy on back pressed");
		if(	TalkManager.getInstance().isBHear()){
			Toast.makeText(this, "服务器正在呼叫中", Toast.LENGTH_SHORT).show();
			return;
		}
		super.onBackPressed();
	}


	private void init(){
		mImgState = (ImageView)findViewById(R.id.iv_talk_state);
		mTalkBtn = (ImageView)findViewById(R.id.iv_talk_btn);
//		mTalkBtn.setOnClickListener(this);
		mTalkBtn.setOnTouchListener(this);
		mTvState = (TextView)findViewById(R.id.tv_talk_state);
		mTvState2 = (TextView)findViewById(R.id.tv_talk_state2);
		talkManager = TalkManager.getInstance();
		//		talkManager.test();
		talkManager.setbInTalk(true);
		//		boolean ret = talkManager.registerService();//FIXME add 
		//		Log.i("123", "talk manager register service ret="+ret);
		AudioAction.getInstance().initAudioRecord();
	}

	private void checkConnect(){
//		if(talkManager.get_register_state() == 1){	//连接状态
//			registerState = CONNECT_SERVICE;
//		}else{
//			registerState = DISCONNECT_SERVICE;
//		}
		//talkState = STOP;
	}

	private void setState(){
		if(registerState == CONNECT_SERVICE){
			mImgState.setImageResource(R.mipmap.online_b);
			mTvState.setText(getResources().getString(R.string.online));
			mTalkBtn.setImageResource(R.mipmap.talk_red);
			mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_1));
		}else if(registerState == DISCONNECT_SERVICE){
			mImgState.setImageResource(R.mipmap.offline_b);
			mTvState.setText(getResources().getString(R.string.offline));
			mTalkBtn.setImageResource(R.mipmap.talk_disconnect);
			mTvState2.setText(getResources().getString(R.string.disconnect_2_talk_service));
		}
		talkState = STOP;
	}

	private void setLinkState(){
		if(isLinked){
			mImgState.setImageResource(R.mipmap.online_b);
			mTvState.setText(getResources().getString(R.string.online));
			mTalkBtn.setImageResource(R.mipmap.talk_red);
			mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_1));
		}else{
			mImgState.setImageResource(R.mipmap.offline_b);
			mTvState.setText(getResources().getString(R.string.offline));
			mTalkBtn.setImageResource(R.mipmap.talk_disconnect);
			mTvState2.setText(getResources().getString(R.string.disconnect_2_talk_service));
		}
	}
	
	
	private void setTalkState(){
		switch (talkState) {
		case TALK_STATE_TALKING:
			mTalkBtn.setImageResource(R.mipmap.talk_green);
			mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_3));
			break;

		case TALK_STATE_STOP:
			mTalkBtn.setImageResource(R.mipmap.talk_red);
			mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_1));
			break;
		case TALK_STATE_SILENT:
			//Toast.makeText(this, "通话失败", Toast.LENGTH_SHORT).show();
			mTalkBtn.setImageResource(R.mipmap.talk_red);
			mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_4));
			talkState = TALK_STATE_STOP;
			break;
			
		default:
			break;
		}
	}
	
	
	private void detectRegisterState(){
		if(detectRegisterStateThread == null){
//			detectRegisterStateThread = new DetectRegisterStateThread();
		//	detectRegisterStateThread.start();//FIXME
		}
	}



	class DetectRegisterStateThread extends Thread{
		@Override
		public void run() {
			super.run();
			/*
			while(!isFinishing()){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int tempState = 0;
				int bar=-1;
				if((bar=talkManager.get_register_state()) == 1){	//连接状态
					tempState = CONNECT_SERVICE;
					DebugUtil.logI("CONNECT_SERVICE registerState:"+registerState, this.getClass());
				}else{
					tempState = DISCONNECT_SERVICE;
					DebugUtil.logE("DISCONNECT_SERVICE registerState:"+registerState, this.getClass());
				}

				if(tempState != registerState){//状态改变
					registerState = tempState;
					handler.sendEmptyMessage(SET_REGISTER_UI);
				}
			}
			*/
		}
	}
	
	class CheckTalkStateThread extends Thread{
		@Override
		public void run() {
			super.run();
			/*
			int voice_state = 0;
			while((talkState == REQUEST && voice_state != 2)||talkState == TALKING   ){
				voice_state = talkManager.getTalkState();
				//voice_state = 2;//force to 2 because that request talk is Deprecated //FIXME
				if(voice_state == 2){	//成功
					talkState = TALKING;
					handler.sendEmptyMessage(STATE_CHANGE_TO_TALKING);

					//setData
					if(recordPlayThread == null){
						recordPlayThread = new RecordPlayThread();
						recordPlayThread.start();
					}
				}else if(voice_state == SILENT){
					handler.sendEmptyMessage(STATE_CHANGE_TO_SILENT);
					talkState = STOP;
				}
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			*/

		}
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case STATE_CHANGE_TO_TALKING:
				mTalkBtn.setImageResource(R.mipmap.talk_green);
				mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_3));
				break;
			case SET_REGISTER_UI:
				setState();
				break;
			case STATE_CHANGE_TO_SILENT:
			//	mTalkBtn.setImageResource(R.drawable.talk_red);
				mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_4));
				
				//new
			case LINK_STATE_UPDATA_MSG:
				if(!isLinked){
					//断连，关闭录音		
					AudioAction.getInstance().stopAudioRecord();
					bClickPlay = false;
					talkState = TALK_STATE_STOP;
				}
				setLinkState();
				break;
			case TALK_STATE_UPDATA_MSG:
				Log.i("123", "get msg TALK_STATE_UPDATA_MSG");
				setTalkState();
				break;
			default:
				break;
			}
		}
	};

	//初始化语音对讲

	public void initAudioRecord(){
		recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);  
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,  
				channelConfiguration, audioEncoding, recBufSize);  
	}

	class RecordPlayThread extends Thread {  
		public void run() {  
			try {  
				byte[] buffer = new byte[recBufSize];  
				audioRecord.startRecording();//开始录制  
				//          audioTrack.play();//开始播放  
				//				System.out.println("isRecording:"+isRecording);
				while (talkState == TALKING) { 
					//从MIC保存数据到缓冲区  
					int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);  
					System.out.println("bufferReadResult:"+bufferReadResult);

					byte[] tmpBuf = new byte[bufferReadResult];  
					System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);  
					//写入数据即播放  
					int ret = talkManager.setData(tmpBuf, bufferReadResult);
					System.out.println("startTalk ret :"+ret);
					if (ret == -1) {
						DebugUtil.logE(null, "audio setData error ret=-1");
					}
				} 
				audioRecord.stop();  
				talkManager.stopTalk();
			} catch (Throwable t) {  
				//Toast.makeText(Talk.this, t.getMessage(), 1000);  
				t.printStackTrace();
			}  
		}  
	}

	

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
	
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN://按下说话
			Log.i("123", "按下");
			
			if(isLinked){
				talkState = TALK_STATE_TALKING;
				bClickPlay = true;
				//TODO start record play
				setTalkState();
				AudioAction.getInstance().startAudioRecord();
			}else{
				Toast.makeText(this, "与语音平台断开连接，正在重连", Toast.LENGTH_SHORT).show();
			}
			break;
		case MotionEvent.ACTION_UP://抬起停止
			Log.i("123", "抬起");		
			if(isLinked){
				talkState = TALK_STATE_STOP;
				bClickPlay = false;
				//TODO stop record play
				setTalkState();
				AudioAction.getInstance().stopAudioRecord();
			}
		
			break;
		case MotionEvent.ACTION_CANCEL://取消
			Log.i("123", "取消");
			break;	
		default:
			break;
		}
		
		
		return true;
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_talk_btn:
			/*
			if(registerState == CONNECT_SERVICE  ||registerState==DISCONNECT_SERVICE){//test
				if(talkState == STOP){
					talkState = REQUEST;
					mTalkBtn.setImageResource(R.drawable.talk_yellow);
					mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_2));
					//请求连接
					//					talkManager.setIs_voice_start();
					talkManager.requestTalk();
					CheckTalkStateThread thread = new CheckTalkStateThread();
					thread.start();

				}else if(talkState == TALKING){
					talkState = STOP;
					mTalkBtn.setImageResource(R.drawable.talk_red);
					mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_1));
					//					talkManager.setIs_voice_start(0);
					if(recordPlayThread != null){
						try {
							recordPlayThread.join();
							recordPlayThread = null;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				Toast.makeText(this, "与语音平台断开连接，正在重连", 1000).show();
			}
*/
			Log.d("123", "talk state = "+talkState );
			if(isLinked){
				if(talkState == TALK_STATE_STOP){
					talkState = TALK_STATE_TALKING;
					bClickPlay = true;
					//TODO start record play
					setTalkState();
					AudioAction.getInstance().startAudioRecord();
				}else{
					talkState = TALK_STATE_STOP;
					bClickPlay = false;
					//TODO stop record play
					setTalkState();
					AudioAction.getInstance().stopAudioRecord();
				}
			//	Log.i("123", "send msg  TALK_STATE_UPDATA_MSG");
			}else{
				Toast.makeText(this, "与语音平台断开连接，正在重连", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onDialogListReady() {
		if(bInitNextTargetList)return;
		List<TalkDialog> list = talkManager.getDialogListRes();
		List<Integer> lnext = new ArrayList<Integer>();
		for(int i=0;i<list.size();i++){
			if(list.get(i).getMobileType()==2 && !lnext.contains(list.get(i)) ){
				lnext.add(i);
			}
		}
		talkManager.setNextDialogTarget(lnext);
		bInitNextTargetList = true;
		
		handler.sendEmptyMessage(TALK_STATE_UPDATA_MSG);
		
	}

	@Override
	public void onTalkLinked(boolean isLinked) {
		// TODO Auto-generated method stub
		this.isLinked = isLinked;
		handler.sendEmptyMessage(LINK_STATE_UPDATA_MSG);
	}

	@Override
	public void onTalkState(boolean isTalkable) {
		// TODO Auto-generated method stub
		if(isTalkable){
			if(bClickPlay){
				Log.i("123", "talk state = 8");
				talkState = TALK_STATE_TALKING;
			}	
		}else{
			if(bClickPlay){
				AudioAction.getInstance().stopAudioRecord();
				bClickPlay = false;
				talkState = TALK_STATE_SILENT;
				handler.sendEmptyMessage(TALK_STATE_UPDATA_MSG);
			}
			bInitNextTargetList = false;
		}
	}
}
