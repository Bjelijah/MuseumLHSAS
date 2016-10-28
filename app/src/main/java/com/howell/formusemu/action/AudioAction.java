package com.howell.formusemu.action;

import java.util.HashSet;
import java.util.Set;

import com.howell.formuseum.JNIManager;
import com.howell.formuseum.LogoActivity;
import com.howell.utils.JsonUtils;
import com.howell.utils.TalkManager;
import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.net.rtp.AudioCodec;
import android.util.Log;

@SuppressLint("NewApi")
public class AudioAction {
	private static AudioAction mInstance = null;
	public static AudioAction getInstance(){
		if(mInstance == null){
			mInstance = new AudioAction();
		}
		return mInstance;
	}

	private AcousticEchoCanceler canceler = null;//回声消除器
	private NoiseSuppressor suppressor = null;//噪声消除器
	
	private boolean isDeviceSupportCanceler(){
		return AcousticEchoCanceler.isAvailable();
	}
	
	public boolean chkNewDev(){
//		return false;
		return android.os.Build.VERSION.SDK_INT >=16;
	}
	
	
	public boolean initAEC(int audioSession){
		if(canceler!=null){
			return false;
		}
		canceler = AcousticEchoCanceler.create(audioSession);
		suppressor = NoiseSuppressor.create(audioSession);
		
		try {
			suppressor.setEnabled(true);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(NullPointerException e){
			e.printStackTrace();
		}
		
		canceler.setEnabled(true);
		return canceler.getEnabled();
	}
	
	public boolean setAECEnable(boolean enabled){
		if(null == canceler){
			return false;
		}
		canceler.setEnabled(enabled);
		try {
			suppressor.setEnabled(enabled);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canceler.getEnabled();
	}
	
	public boolean getAECEnable(){
		if(null == canceler){
			return false;
		}
		return canceler.getEnabled();
	}
	
	public boolean releaseAEC(){
		if(null == canceler ){
			return false;
		}
		canceler.setEnabled(false);
		canceler.release();
		canceler = null;
		
		if(null == suppressor){
			return false;
		}
		suppressor.setEnabled(false);
		suppressor.release();
		suppressor = null;
		return true;
	}
	
	private Set<MyAudioPlay> audioPlaySet = null;
	
	public MyAudioPlay buildMyAudioPlay(String name){
		if(audioPlaySet==null){
			audioPlaySet = new HashSet<MyAudioPlay>();
		}
		
		for(MyAudioPlay m: audioPlaySet){
			if (m.getsenderName().equals(name)) {
				Log.i("123", "build return m   msize="+audioPlaySet.size());
				return m;
			}
		}
		MyAudioPlay bar =null; 
		try {
			bar = new MyAudioPlay(name);
			audioPlaySet.add(bar);
			Log.i("123", "build return bar");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bar;
	}
	
	public void destroyMyAudioPlayAll(){
		if(null == audioPlaySet){
			return;
		}
		for(MyAudioPlay m:audioPlaySet){
			m.audioStop();
			m.deInitAudio();
		}
		audioPlaySet.clear();
		audioPlaySet = null;
	}
	
	public void AudioPlayAll(){
		for(MyAudioPlay m: audioPlaySet){
			m.audioPlay();
		}
	}
	
	public void AudioStopAll(){
		for(MyAudioPlay m:audioPlaySet){
			m.audioStop();
		}
	}
	
	public class MyAudioPlay {//talk 回送音频
		public MyAudioPlay(String name){
			this.senderName = name;
			initAudio();
		}
		
		
		@Override
		public boolean equals(Object o) {
			// TODO Auto-generated method stub
			if (o instanceof MyAudioPlay) {
				return this.senderName.equals(((MyAudioPlay) o).senderName);
			}
			return super.equals(o);
		}
		private String senderName;

		private AudioTrack mAudioTrack;//FIXME
		private byte[] mAudioData;
		private int mAudioDataLength;
	
		private JNIManager jni = JNIManager.getInstance();
		
		public String getsenderName(){
			return senderName;
		}

		public void setMAudioDataLength(int len){
			this.mAudioDataLength = len;
		}
		public void setmAudioData(byte [] data){
			this.mAudioData = data;
		}
		public void initAudio(){
			int buffer_size = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
			if(chkNewDev()&&audioRecord!=null){
				mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size*8, AudioTrack.MODE_STREAM,audioRecord.getAudioSessionId());
			}else{
				mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size*8, AudioTrack.MODE_STREAM);
			}
			mAudioData = new byte[buffer_size*8];
			audioPlay();
//			jni.nativeAudioInit();
//			jni.nativeAudioSetCallbackObject(this, 0);
//			jni.nativeAudioSetCallbackMethodName("mAudioDataLength", 0);
//			jni.nativeAudioSetCallbackMethodName("mAudioData", 1);	
		
		}

		public void deInitAudio(){
//			jni.nativeAudioDeinit();
			if(mAudioTrack != null){
				mAudioTrack.stop();
				mAudioTrack.release();
				mAudioTrack = null;
			}
		}
		
		public void audioPlayG711Data(byte [] g711data,int len){
			int pcmLen = len*2;
			byte [] pcmBuf = new byte[pcmLen];	
			//TODO g711 to pcm
			jni.g711u2Pcm(g711data, len, pcmBuf);
//			System.arraycopy(pcmBuf, 0, mAudioData, 0, pcmLen);
			Log.i("123", "canceler enabale = "+getAECEnable());
			mAudioTrack.write(pcmBuf,0,pcmLen);
		}
		
		@Deprecated
		/**
		 * @link audioPlayG711Data
		 */
		public void audioWrite() {
			Log.e("123", "audio write  mAudioDataLength="+mAudioDataLength+" dataLen="+mAudioData.length);
			if (obSet !=null) {
				for(OnAudioComing o : obSet){
					if (o != null) {
						o.onAudioComing();
					}
				}
			}
//			for(int i=0;i<100;i++){
//				Log.i("123","maudio"+ mAudioData[i]+"");
//			}
			Log.i("123","play state="+	mAudioTrack.getPlayState()+"     1:stop 2:pause 3:play");//1 stop 2 pause 3 play
			mAudioTrack.write(mAudioData,0,mAudioDataLength);
		}    

		public void audioPlay() {
			if(mAudioTrack==null)return;		
			if(mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING){	
				mAudioTrack.play();
			}
			//jni.nativeAudioBPlayable();
		}
		//    
		public void audioStop(){
			Log.e("123", "~~~~~~~~~~~~~~audio stop");
		//	jni.nativeAudioStop();
			mAudioTrack.stop();	
		}
	}

	/**
	 * 
	 */
	private AudioTrack mAudioTrack;//FIXME
	private byte[] mAudioData;
	private int mAudioDataLength;
	private Set<OnAudioComing> obSet = null;
	private JNIManager jni = JNIManager.getInstance();
	public void registerOnAudioComing(OnAudioComing onAudioComing){
		if (obSet==null) {
			obSet = new HashSet<OnAudioComing>();
		}
		obSet.add(onAudioComing);
	} 
	//    
	public void unregisterOnAudioComing(OnAudioComing onAudioComing){
		if (obSet == null) {
			return;
		}
		obSet.remove(onAudioComing);
	}

	public void setMAudioDataLength(int len){
		this.mAudioDataLength = len;
	}
	public void setmAudioData(byte [] data){
		this.mAudioData = data;
	}
	
	public void initAudio(){
		int buffer_size = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size*8, AudioTrack.MODE_STREAM);
		mAudioData = new byte[buffer_size*8];
		jni.nativeAudioInit();
		jni.nativeAudioSetCallbackObject(this, 0);
		jni.nativeAudioSetCallbackMethodName("mAudioDataLength", 0);
		jni.nativeAudioSetCallbackMethodName("mAudioData", 1);
	}

	public void deInitAudio(){
		jni.nativeAudioDeinit();
		if(mAudioTrack != null){
			if(mAudioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING){
				mAudioTrack.stop();
			}
			mAudioTrack.release();
			mAudioTrack = null;
		}
	}

	public void audioComing(){
		if (obSet !=null) {
			for(OnAudioComing o : obSet){
				if (o != null) {
					o.onAudioComing();
				}
			}
		}else{
			Log.e("123", "obset = null");
		}
	}

	
	public void audioWrite() {
		Log.e("123", "audio write  mAudioDataLength="+mAudioDataLength+" dataLen="+mAudioData.length);
		
		if (obSet !=null) {
			for(OnAudioComing o : obSet){
				if (o != null) {
					o.onAudioComing();
				}
			}
		}
//		for(int i=0;i<100;i++){
//			Log.i("123","maudio"+ mAudioData[i]+"");
//		}
		Log.i("123","play state="+	mAudioTrack.getPlayState()+"     1:stop 2:pause 3:play");//1 stop 2 pause 3 play
//		mAudioTrack.flush();
		mAudioTrack.write(mAudioData,0,mAudioDataLength);
	}    

	public void audioPlay() {
		if(mAudioTrack==null)return;		
		if(mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING){	
			mAudioTrack.play();
		}
		jni.nativeAudioBPlayable();
	}
	//    
	public void audioStop(){
		Log.e("123", "~~~~~~~~~~~~~~audio stop");
		jni.nativeAudioStop();
		mAudioTrack.stop();	
	}

	
	
	/**
	 * 
	 */
	//google录音类
	private AudioRecord audioRecord;  
	private int recBufSize;  
	//语音对讲各个参数
	private static final int frequency = 8000;  
	private static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;  
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private boolean bAudioRecording = false;
	
	public void initAudioRecord(){//talk activity on create
		recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);  
		if(chkNewDev()){
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, frequency,
					channelConfiguration, audioEncoding, recBufSize);
			//初始化 回声消除器
			if(isDeviceSupportCanceler()){
				initAEC(audioRecord.getAudioSessionId());
			}
		}else{
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,  
					channelConfiguration, audioEncoding, recBufSize);  
		}		
	}
	
	public void deInitAudioRecord(){
		if(audioRecord!=null){
			if(audioRecord.getRecordingState()==AudioRecord.RECORDSTATE_RECORDING){
				audioRecord.stop();
			}
			audioRecord.release();
			audioRecord = null;
		}
		releaseAEC();
	}
	
	public void startAudioRecord(){
		if(bAudioRecording)return;
			bAudioRecording = true;
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] buffer = new byte[recBufSize];  
				audioRecord.startRecording();//开始录制  
				
				while(bAudioRecording){
					int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);  
					//TalkManager.getInstance().setData(buffer, bufferReadResult);//FIXME
					
					boolean ret = TalkManager.getInstance().sendVoiceData2Service(buffer, bufferReadResult);
					if (!ret) {
						bAudioRecording = false;
					}
					
//					TalkManager.getInstance().audioTest(buffer, bufferReadResult);
				//	JsonUtils.audioData(buffer, bufferReadResult);
				//	TalkManager.getInstance().setAudioData(buffer, bufferReadResult);
//					Log.i("123", "on bufferReadResult:"+bufferReadResult+"  recBufSize:"+recBufSize);
				//	byte[] tmpBuf = new byte[bufferReadResult];  
				//	System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);  
				//	TalkManager.getInstance().sendVoiceData2Service(tmpBuf, bufferReadResult);
//					TalkManager.getInstance().setData(tmpBuf, bufferReadResult);
				}
			
				if(audioRecord!=null){
					audioRecord.stop();				
				}				
				super.run();
			}
		}.start();
	}
	
	public void stopAudioRecord(){
		bAudioRecording = false;
	}
	
}
