package com.howell.formuseum;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.howell.formusemu.action.ITalkOB;
import com.howell.formuseum.bean.TalkDialog;
import com.howell.museumlhs.R;
import com.howell.utils.DialogUtils;
import com.howell.utils.TalkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TalkListActivity extends Activity implements OnItemClickListener, ITalkOB {

	private final static int TALKLIST_UPDATA_MSG = 0x01;
	private final static int TALKLIST_CHECKED_MSG = 0x02;
	private final static int TALKLIST_TIMEOUT_MSG = 0x10;
	ListView lView;
	List<TalkDialog> dialogList = new ArrayList<TalkDialog>();
	List<TalkDialog> tempList = null;//回调线程中获取的dilogList 
	List<TalkDialog> nextTargetList = new ArrayList<TalkDialog>();
	TalkDialogListAdapter adapter;
	CheckBox checkBox;
	TalkManager mgr = TalkManager.getInstance();
	Map<Integer, Boolean> isCheckMap =  new HashMap<Integer, Boolean>();

	Dialog waitDialog;
	private boolean isAlreadyTimeOut = false;
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case TALKLIST_UPDATA_MSG:
				//				TalkDialog bar = new TalkDialog("1231", "bar", "sdfsf", 1);
				//				dialogList.add(bar);
				lView.requestLayout();
				dialogList = tempList;
				adapter.updataList(dialogList);
				sendEmptyMessage(TALKLIST_CHECKED_MSG);
				break;
			case TALKLIST_CHECKED_MSG:



				break;
			case TALKLIST_TIMEOUT_MSG:
				isAlreadyTimeOut = true;
				waitDialog.dismiss();
				Toast.makeText(TalkListActivity.this, "获取用户信息失败！", Toast.LENGTH_LONG).show();
				break;
				
			default:
				break;
			}

			super.handleMessage(msg);
		}

	};

	//http://www.cnblogs.com/cnblogs-lin/p/3640023.html
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk_dialog_list);
		lView = (ListView)findViewById(R.id.dialog_listView);
		adapter = new TalkDialogListAdapter(this,dialogList);
		lView.setAdapter(adapter);
		waitDialog = DialogUtils.postWaitDialog(this);
		waitDialog.show();
		//TODO get list
		mgr.getDilagList(0);
		mgr.registerOnTalk(this);
		lView.setOnItemClickListener(this);
		isAlreadyTimeOut = false;
		handler.sendEmptyMessageDelayed(TALKLIST_TIMEOUT_MSG, 7000);
	}
	
	@Override
	protected void onStop() {

		Log.i("123", "on stop");
		List<Integer> list = new ArrayList<Integer>();
		Iterator<Entry<Integer, Boolean>> it = isCheckMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, Boolean> entry = (Entry<Integer, Boolean>) it.next();
			int idx = entry.getKey();
			boolean isChecked = entry.getValue();
			if(isChecked){
				list.add(idx);
			}
		}
		mgr.setNextDialogTarget(list);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mgr.unregisterOnTalk(this);
		super.onDestroy();
	}




	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
				Log.i("123", "pos="+position);

		//Log.i("123", "name="+dialogList.get(position-1).getDialogName()+"   id="+dialogList.get(position).getDialogId());

		CheckBox rBtn= (CheckBox) view.findViewById(R.id.talk_dialog_list_cb);
		if(rBtn.isChecked()){
			rBtn.setChecked(false);
			isCheckMap.remove(position);
		}else{
			rBtn.setChecked(true);
			isCheckMap.put(position, true);
		}	
//		lView.requestLayout();
//		adapter.notifyDataSetChanged();
		handler.sendEmptyMessage(TALKLIST_UPDATA_MSG);
	}





	public class TalkDialogListAdapter extends BaseAdapter{

		Context context;
		List<TalkDialog> list;
		private int pos = 0;

		public TalkDialogListAdapter(Context context,List<TalkDialog> list) {
			this.context = context;
			this.list = list;
		}

		public void updataList(List<TalkDialog>list){
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder = null;
			if (convertView==null) {
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				convertView = layoutInflater.inflate(R.layout.talk_dialog_list_item, null);
				holder = new ViewHolder();
				view = getLayoutInflater().inflate(R.layout.talk_dialog_list_item, null);
				holder.usrName = (TextView)convertView.findViewById(R.id.talk_dialog_list_tv);
				holder.checkBtn = (CheckBox) convertView.findViewById(R.id.talk_dialog_list_cb);
				//				holder.alarmDes = (TextView)convertView.findViewById(R.id.tv_alarm_history_list_item_descripition);
				holder.checkBtn.setTag(position);
				view.setTag(holder);
				
				
				holder.checkBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						CheckBox cBox = (CheckBox) v;
						int id = Integer.parseInt(v.getTag().toString());
						// TODO Auto-generated method stub
						Log.i("123", "id="+id+" ischeck="+cBox.isChecked());
						if(cBox.isChecked()){
							isCheckMap.put(id, true);
						}else{
							isCheckMap.remove(id);
						}
					}
				});
				
				
//				holder.checkBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//						// TODO Auto-generated method stub
//						int id = Integer.parseInt(buttonView.getTag().toString());
//						Log.i("123","id="+id);
//						if(isChecked){
//							Log.e("123", "check map put id="+id);
////							isCheckMap.put(id, isChecked);
//						}else{
//							Log.e("123", "check map remove id="+id);
////							isCheckMap.remove(id);
//						}
//					}
//				});

				convertView.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder)convertView.getTag();
			}

			if(isCheckMap!=null && isCheckMap.containsKey(position))
			{
				Log.i("123", "set check true pos="+position);
				holder.checkBtn.setChecked(isCheckMap.get(position));
			}
			else
			{
				Log.i("123", "set check false pos="+position);
				holder.checkBtn.setChecked(false);
			}
			String usrName = list.get(position).getDialogName();

			holder.usrName.setText("用户名："+usrName);
			pos = position;

			return convertView;
		}
		class ViewHolder {
			public TextView usrName;//名称
			public CheckBox checkBtn;
		}
	}

	@Override
	public void onDialogListReady() {
		if (isAlreadyTimeOut) {
			return;
		}
		
		handler.removeMessages(TALKLIST_TIMEOUT_MSG);
		
		tempList = mgr.getDialogListRes();
		nextTargetList = mgr.getNextSeleteTarget();
		Log.i("123", "size="+nextTargetList.size());
		if(nextTargetList.size()>0){
			for(int i=0;i<tempList.size();i++){
				Log.e("123","tempList id="+ tempList.get(i).getDialogId());
				for(TalkDialog t:nextTargetList){
					Log.i("123", "nextTargetList id="+t.getDialogId());				
					if(tempList.get(i).getDialogId().equals(t.getDialogId())){
						Log.e("123", "xiang deng i="+i);
						isCheckMap.put(i, true);
						break;
					}
				}
			}
		}
//		lView.requestLayout();
//		adapter.updataList(dialogList);
		
		waitDialog.dismiss();
		handler.sendEmptyMessage(TALKLIST_UPDATA_MSG);
	}

	@Override
	public void onTalkLinked(boolean isLinked) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTalkState(boolean isTalkable) {
		// TODO Auto-generated method stub

	}


	





}
