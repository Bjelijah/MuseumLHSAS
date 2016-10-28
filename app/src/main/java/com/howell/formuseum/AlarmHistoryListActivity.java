package com.howell.formuseum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.howell.ehlib.MyListView;
import com.howell.ehlib.MyListView.OnRefreshListener;
import com.howell.formusemu.action.AlarmHistoryAction;
import com.howell.formuseum.bean.HistoryAlarm;
import com.howell.museumlhs.R;
import com.howell.protocol.Const;
import com.howell.protocol.entity.EventNotify;
import com.howell.utils.AlarmUtils;
import com.howell.utils.DebugUtil;
import com.howell.utils.DialogUtils;
import com.howell.utils.Utils;

import java.util.ArrayList;

public class AlarmHistoryListActivity extends Activity implements Const,OnRefreshListener , OnClickListener,OnItemClickListener{
	private LinearLayout mTalk;
	private MyListView listview;
	private AlarmHistoryAction action = AlarmHistoryAction.getInstance();
	private AlarmHistoryAdapter adapter;

	private ArrayList<HistoryAlarm> hList = new ArrayList<HistoryAlarm>();

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ALARM_HISTORY_GET_LIST_ERROR:
				Toast.makeText(AlarmHistoryListActivity.this, "获取历史报警记录信息失败！！", Toast.LENGTH_SHORT).show();
				listview.onRefreshComplete();
				DialogUtils.postAlerDialog(AlarmHistoryListActivity.this, "获取历史报警记录信息失败 请稍后刷新");
				break;
			case ALARM_HISTORY_GET_LIST_OK:
				Log.i("123", "获取 历史 报警记录 ok");
				//刷新 列表;
				adapter.updateList(hList);
				listview.onRefreshComplete();
				break;

			default:
				break;
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_history_list);
		mTalk = (LinearLayout)findViewById(R.id.ll_alarm_history_talk);
		mTalk.setOnClickListener(this);
		init();
	}

	private void init(){
		action.setHandle(handler);
		action.sethList(hList);
		listview = (MyListView)findViewById(R.id.lv_alarm_history_list);
		listview.setonRefreshListener(this);
		listview.setOnItemClickListener(this);
		adapter = new AlarmHistoryAdapter(this,hList);
		listview.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

		Log.i("123", "position = "+position);
		int index = position-1;
		HistoryAlarm historyAlarm = hList.get(index);


		Intent intent = new Intent(this,AlarmDetailActivity.class);
		String session = action.getSession();
		String cookieHalf = action.getCookieHalf();
		String verify  = action.getVerify();
		String webIP = action.getWebServiceIP();
		EventNotify eventNotify = new EventNotify();

		eventNotify.setName(historyAlarm.getName());
		eventNotify.setEventType(historyAlarm.getEventType());
		eventNotify.setTime(Utils.ISODateString2ISOString(historyAlarm.getAlarmTime()));
		eventNotify.setId(historyAlarm.getComponentID());
//		eventNotify.setEventState("Inactive");
		eventNotify.setEventState("Active");
		String imageUrl = "";
		int size = historyAlarm.getPictureIDList().size();
		if(size>0){
			String [] strings = new String[size];
			for(int i=0;i<size;i++){
				strings[i] = action.changePicID2PicURL(webIP, historyAlarm.getPictureIDList().get(i));
				DebugUtil.logI(null, "string["+i+"]:"+strings[i]);
			}
			imageUrl = eventNotify.convertArrayToString(strings);//FIXME url error
			eventNotify.setImageUrl(imageUrl);
			DebugUtil.logI(null, "imageUrl="+eventNotify.getImageUrl());
		}
		
		intent.putExtra("eventNotify", eventNotify);
		intent.putExtra("session", session);
		intent.putExtra("cookieHalf", cookieHalf);
		intent.putExtra("verify", verify);
		intent.putExtra("webserviceIp", webIP);
		startActivity(intent);
	
	}

	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_alarm_history_talk:
			Intent intent = new Intent(this,TalkActivity.class);
			startActivity(intent);
			break;			
		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		Log.i("123","on refresh");
		//获取 10 条记录
		hList.clear();
		action.sethList(hList);
		action.getAlarmHistory();
	}

	@Override
	public void onFirstRefresh() {
		Log.i("123", "alarm history first refresh");
		//获取10 条记录
		action.getAlarmHistory();
	}

	@Override
	public void onFirstRefreshDown() {

	}



	public class AlarmHistoryAdapter extends BaseAdapter{

		private ArrayList<HistoryAlarm> hList;
		private Context context;

		public AlarmHistoryAdapter(Context context, ArrayList<HistoryAlarm> hList){
			this.context = context;
			this.hList = hList;
		}

		public void updateList(ArrayList<HistoryAlarm> hList){
			this.hList = hList;
			notifyDataSetChanged();
		}


		@Override
		public int getCount() {
			return hList.size();
		}

		@Override
		public Object getItem(int position) {
			return hList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView==null) {
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				convertView = layoutInflater.inflate(R.layout.alarm_history_list_item, null);
				holder = new ViewHolder();
				holder.alarmName = (TextView)convertView.findViewById(R.id.tv_alarm_history_list_item_name);
				holder.alarmDes = (TextView)convertView.findViewById(R.id.tv_alarm_history_list_item_descripition);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			String alarmName = hList.get(position).getName();
			String alarmStartTime = Utils.ISODateString2Date(hList.get(position).getAlarmTime());  
			String alarmType = AlarmUtils.getAlarmType(context, hList.get(position).getEventType());
			holder.alarmName.setText("名称："+alarmName);
			holder.alarmDes.setText("时间："+alarmStartTime);
			return convertView;
		}
		class ViewHolder {
			public TextView alarmName;//名称
			public TextView alarmDes;//描述
		}
	}
}
