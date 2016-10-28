package com.howell.formuseum;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.howell.formusemu.action.AudioAction;
import com.howell.formusemu.action.LoginAction;
import com.howell.museumlhs.R;
import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.ClientCredential;
import com.howell.protocol.entity.Fault;
import com.howell.protocol.entity.ServerNonce;
import com.howell.utils.CacheUtils;
import com.howell.utils.DebugUtil;
import com.howell.utils.DialogUtils;
import com.howell.utils.JsonUtils;
import com.howell.utils.MD5;
import com.howell.utils.SharedPreferencesUtils;
import com.howell.utils.TalkManager;
import com.howell.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class LoginActivity extends Activity implements OnClickListener{
	
	private EditText mAccount,mPassword,mWebserviceIp/*,mTalkIp*/; 
	private Button mLogin;
	
	private TalkManager talkManager;
	private JNIManager jni;
	private Dialog waitDialog;
	
	private boolean talkingDebug;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		Log.i("123", "loginActivity   oncreate");
		init();
		Button button = (Button) findViewById(R.id.button1_login);

		button.setVisibility(View.GONE);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AudioAction.getInstance().initAudio();
				AudioAction.getInstance().audioPlay();
				talkManager.getInstance().start(LoginActivity.this);
				Intent intent = new Intent(LoginActivity.this,TalkActivity.class);
				LoginActivity.this.startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(talkingDebug){
			talkManager.audioStop();
			talkManager.setExit(true);
		}
	}
	
	//192.168.128.253
	//18.149
	private void init(){
		/*
		 * talk 初始化  移至 mapListActivity 
		 */
		talkingDebug = false;//FIXME unused
		CacheUtils.createBitmapDir();
		if(talkingDebug){
			talkManager = TalkManager.getInstance();
			talkManager.test();
		}
		jni = JNIManager.getInstance();
		mAccount = (EditText)findViewById(R.id.et_login_account);
		mPassword = (EditText)findViewById(R.id.et_login_password);
		mWebserviceIp = (EditText)findViewById(R.id.et_login_webserviceip);
//		mTalkIp = (EditText)findViewById(R.id.et_login_talkip);
		
		mLogin = (Button)findViewById(R.id.btn_login);
		mLogin.setOnClickListener(this);
		
		SharedPreferences sharedPreferences = getSharedPreferences("set",Context.MODE_PRIVATE);
		String account = sharedPreferences.getString("account", "");
	    String password = sharedPreferences.getString("password", "");
	    String websocket_ip = sharedPreferences.getString("webserviceIp", "192.168.18.245");
	
	    //String talk_ip = sharedPreferences.getString("talkIp", "192.168.18.104");
	    mAccount.setText(account);
	    mPassword.setText(password);
	    mWebserviceIp.setText(websocket_ip);
//	    mTalkIp.setText(talk_ip);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		DebugUtil.logI("onOptionsItemSelected", this.getClass());
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			String account = mAccount.getText().toString();
			String password = mPassword.getText().toString();
			String webserviceIp = mWebserviceIp.getText().toString();
			//String talkIp = mTalkIp.getText().toString();
			String talkIp = "";
			rememberSettings(account,password,webserviceIp,talkIp);
			
			startWaitingAnimation(this);
			LoginTask task = new LoginTask(account, password, webserviceIp, talkIp);
			task.execute();
			break;

		default:
			break;
		}
	}
	
	private void rememberSettings(String account,String password,String webserviceIp,String talkIp){
		SharedPreferences sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("account",account);
        editor.putString("password",password);
        editor.putString("webserviceIp",webserviceIp);
        editor.putString("talkIp",talkIp);
        editor.commit();
	}
	
	public void startWaitingAnimation(Context context){
		waitDialog = DialogUtils.postWaitDialog(context);
		waitDialog.show();
	}
	
	public void finishWaitingAnimation(){
		waitDialog.dismiss();
	}
	
	//获取故障信息
	class LoginTask extends AsyncTask<Void, Integer, Void> {
		private String account,password,webserviceIp,talkIp;
		private HttpProtocol hp;
		private Fault fault = null;
		private String cookieHalf;
		private String password2;
		//192.168.18.149
		public LoginTask(String account, String password, String webserviceIp,
				String talkIp) {
			super();
			this.account = account;
			this.password = password;
			this.webserviceIp = webserviceIp;
			this.talkIp = talkIp;
			this.hp = new HttpProtocol();
		}
		
		private String createClientNonce(int length){
			String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
	        Random random = new Random();  
	        StringBuffer sb = new StringBuffer();  
	        for(int i=0;i<length;i++){  
	            int number =random.nextInt(62);  
	            sb.append(str.charAt(number));  
	        }  
	        return sb.toString();  
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				//获取session
				
				ServerNonce sn= JsonUtils.parseNonceJsonObject(new JSONObject(hp.nonce(webserviceIp,account)));
				if(sn != null){
					String clientNonce = createClientNonce(32);
					String md5 = MD5.getMD5(password);
					//UserName@Domain:Nonce:ClientNonce:MD5(Password)
					password2 = account+"@"+sn.getDomain()+":"+sn.getNonce()+":"+clientNonce+":"+md5;
					ClientCredential clientCredential = new ClientCredential(account,sn.getNonce(),sn.getDomain(),clientNonce,MD5.getMD5(password2));
					fault = JsonUtils.parseAuthenticateJsonObject(new JSONObject(hp.authenticate(webserviceIp,clientCredential)));
					Log.e("fault", fault.toString());
					//Cookie: username =admin; sid=会话Id; domain=192.109.10.21;verifysession=MD5(METHOD:URL:Verifysession)
//					String cookie = "username="+account+";sid="+fault.getId()+";domain="+sn.getDomain()+";verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps:"+MD5.getMD5(password2));
					cookieHalf = "username="+account+";sid="+fault.getId()+";domain="+sn.getDomain()+";";
				}
				if(talkingDebug){
				//连接语音对讲
				//jni.register2service(fault.getId(),"",account, talkIp , (short)5500);
					Log.i("123", "register service");
//					talkManager.registerService(fault.getId(),"",account, talkIp , (short)5500);//guid //mac
			
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			finishWaitingAnimation();
			if(fault != null && fault.getId() != null){
				SharedPreferencesUtils.saveLoginInfo(LoginActivity.this, account, password, webserviceIp);
				
				String uuid = Utils.getPhoneUid(LoginActivity.this);
				String mac  = Utils.getPhoneMac(LoginActivity.this);
				
				LoginAction.getInstance().setName(account).setPwd(password).setWebserviceIp(webserviceIp)
				.setUuid(uuid).setMac(mac)
				.saveTalkInfo(LoginActivity.this);
				Intent intent = new Intent(LoginActivity.this,MapListActivity.class);
				intent.putExtra("session", fault.getId());
				intent.putExtra("cookieHalf", cookieHalf);
				intent.putExtra("account", account);
				try {
					intent.putExtra("verify", MD5.getMD5(password2));
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				intent.putExtra("webserviceIp", webserviceIp);
				startActivity(intent);
				LoginActivity.this.finish();
			}else{
				DialogUtils.postAlerDialog(LoginActivity.this,"登录失败,请重新登录！");
			}
		}
	}

}
