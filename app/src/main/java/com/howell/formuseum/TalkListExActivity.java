package com.howell.formuseum;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.howell.formusemu.action.ITalkOB;
import com.howell.formuseum.bean.TalkDialog;
import com.howell.museumlhs.R;
import com.howell.utils.TalkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TalkListExActivity extends Activity implements ITalkOB{
	private final static int TALKLIST_UPDATA_MSG = 0x01;
	private final static int TALKLIST_CHECKED_MSG = 0x02;
	
	private List<String> groupArray;
	private List<List<ChildEntry>> childArray;
	private ExpandableListView exLv;
	private TalkDialogExpandableListAdapter adapter;
	
	Map<Integer, Boolean> isCheckMap =  new HashMap<Integer, Boolean>();
	
	List<TalkDialog> dialogList = new ArrayList<TalkDialog>();
	List<TalkDialog> nextTargetList = new ArrayList<TalkDialog>();
	
	TalkManager mgr = TalkManager.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk_dialog_expandable_list);
		exLv = (ExpandableListView) findViewById(R.id.ex_dailog_lv);
		
		
		
		groupArray = new ArrayList<String>();
		
		groupArray.add("主机");
		groupArray.add("用户");
		
		childArray = new ArrayList<List<ChildEntry>>();
		
//		for(int i=0;i<childArray.size();i++){
//			for(int j=0;j<childArray.get(i).size();j++){
//				Log.i("123", "index="+childArray.get(i).get(j).index+" name="+childArray.get(i).get(j).childName);
//			}
//		}
		adapter = new TalkDialogExpandableListAdapter(groupArray,childArray,TalkListExActivity.this);
		exLv.setAdapter(adapter);
		
		mgr.registerOnTalk(this);
		mgr.getDilagList(0);
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
	};
	
	@Override
	protected void onDestroy() {
		mgr.unregisterOnTalk(this);
		super.onDestroy();
	};
	

	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case TALKLIST_UPDATA_MSG:
				//				TalkDialog bar = new TalkDialog("1231", "bar", "sdfsf", 1);
				//				dialogList.add(bar);
				adapter.updataList(groupArray,childArray);
				sendEmptyMessage(TALKLIST_CHECKED_MSG);
				break;
			case TALKLIST_CHECKED_MSG:

				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	};
	
	
	public class TalkDialogExpandableListAdapter extends BaseExpandableListAdapter{
		Context context;
		List<String> groupArray;
		List<List<ChildEntry>> childArray;
		public TalkDialogExpandableListAdapter(List<String> groupArray, List<List<ChildEntry>> childArray,Context context) {
			// TODO Auto-generated constructor stub
			this.childArray = childArray;
			this.groupArray = groupArray;
			this.context = context;
		}
		
		public void updataList(List<String>groupArray,List<List<ChildEntry>> childArray){
			this.childArray = childArray;
			this.groupArray = groupArray;
			notifyDataSetChanged();
		}
		
		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return groupArray.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return childArray.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return groupArray.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childArray.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			GroupViewHold hold = null;
			if(convertView==null){
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				convertView = layoutInflater.inflate(R.layout.talk_dialog_group_item, null);
				hold = new GroupViewHold();
				hold.groupName = (TextView) convertView.findViewById(R.id.talk_list_group_tv);
				convertView.setTag(hold);
			}else{
				hold = (GroupViewHold) convertView.getTag();
			}
			String str = groupArray.get(groupPosition);
			hold.groupName.setText(str);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			LayoutInflater layoutInflater = LayoutInflater.from(context);
			convertView = layoutInflater.inflate(R.layout.talk_dialog_list_item, null);
			TextView tView = (TextView) convertView.findViewById(R.id.talk_dialog_list_tv);
			String usrName = childArray.get(groupPosition).get(childPosition).childName;
			if(groupPosition==0){
				tView.setText("主机名："+usrName);
			}else{
				tView.setText("用户名："+usrName);
			}
			CheckBox cBox = (CheckBox) convertView.findViewById(R.id.talk_dialog_list_cb);
			cBox.setTag(childArray.get(groupPosition).get(childPosition).index);
			cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					int id = Integer.parseInt(buttonView.getTag().toString());
					Log.i("123","id="+id);
					if(isChecked){
						isCheckMap.put(id, isChecked);
					}else{
						isCheckMap.remove(id);
					}
				}
			});
			
			
			if(isCheckMap!=null && isCheckMap.containsKey(childArray.get(groupPosition).get(childPosition).index))
			{
				cBox.setChecked(isCheckMap.get(childArray.get(groupPosition).get(childPosition).index));
			}
			else
			{
				cBox.setChecked(false);
			}
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	class GroupViewHold{
		public TextView groupName;
	}
	class ViewHolder {
		public int index;
		public TextView usrName;//名称
		public CheckBox checkBtn;
	}
	
	class ChildEntry{
		public ChildEntry(int i,String s) {
			// TODO Auto-generated constructor stub
			this.index = i;
			this.childName = s;
		}
		public int index;
		public String childName;
	}

	@Override
	public void onDialogListReady() {
		// TODO Auto-generated method stub
		dialogList = mgr.getDialogListRes();
		nextTargetList = mgr.getNextSeleteTarget();

		childArray.clear();
	
		if(nextTargetList.size()>0){
			for(int i=0;i<dialogList.size();i++){
				for(TalkDialog t:nextTargetList){
					if(dialogList.get(i).getDialogId().equals(t.getDialogId())){
						isCheckMap.put(i, true);
						break;
					}
				}	
			}
		}

		List<ChildEntry> child1 = new ArrayList<ChildEntry>();
		List<ChildEntry> child2 = new ArrayList<ChildEntry>();
		
		for(int i=0;i<dialogList.size();i++){
			if(dialogList.get(i).getMobileType()==2){//主机
				child1.add(new ChildEntry(i, dialogList.get(i).getDialogName()));
			}else if(dialogList.get(i).getMobileType()==1){
				child2.add(new ChildEntry(i, dialogList.get(i).getDialogName()));
			}
		}
		childArray.add(child1);
		childArray.add(child2);
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
