package com.csei.writecard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.example.service.RFIDService;
import com.example.writecard.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
@SuppressLint("HandlerLeak")
public class ReadFileActivity extends Activity implements OnClickListener {
   public static final int FILE_RESULT_CODE=1;
   private Button selectfile;
   private Button writecard;
   private Button backbutton;
   private ListView showTextContent;
   ArrayList<HashMap<String, Object>> listItem=new ArrayList<HashMap<String,Object>>();	  
   String writedata="";
   private String activity = "com.csei.writecard.ReadFileActivity";
   private MyBroadcast myBroadcast;				//广播接收者
	public static int cmd_flag = 0;				//操作状态  0为不做其他操作，1为寻卡，2为认证，3为读数据，4为写数据
	public static int authentication_flag = 0;		//认证状态  0为认证失败和未认证  1为认证成功
	public static String TAG= "M1card";
	String ctype="";
	int canWriteCard;
	private ProgressDialog shibieDialog; //识别搜索框
	int cur_pos;
	String[] s1;
	private Timer timerDialog;  //搜索框计时器
	int f=0;                        //标识是否有卡
	private Timer timeThread;
	private int MSG_FLAG = 1;
	//Dialog结束标识
	private int MSG_OVER = 2;
	int lastItemIndex;
	int totalIndex;
	private Map<String, Object> item_map;
	private SimpleAdapter listItemAdapter;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == MSG_FLAG){
				
			}else if(msg.what == MSG_OVER){
				Toast.makeText(getApplicationContext(), "未识别到标签卡，请重试", Toast.LENGTH_SHORT).show();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readfile);
		selectfile=(Button) this.findViewById(R.id.selectfile);
		writecard=(Button) this.findViewById(R.id.writecard);
		showTextContent=(ListView) this.findViewById(R.id.showfilecontent);
		backbutton=(Button) this.findViewById(R.id.backbutton);
		selectfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ReadFileActivity.this,MyFileManager.class);
				startActivityForResult(intent,FILE_RESULT_CODE);
				
			}
		});
		backbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backbutton.setBackgroundResource(R.drawable.btn_back_active);
				finish();
			}
		});
		writecard.setOnClickListener(this);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(FILE_RESULT_CODE==requestCode){
			Bundle  bundle=null;
			if(data!=null&&(bundle=data.getExtras())!=null){             
				String ss=bundle.getString("filecontent");
				ctype=bundle.getString("ctype");
				if(ss!=null){
				s1=ss.split(" ");
				for(int i=0;i<s1.length;i++){
					Log.e("yyyy",s1[i]);
				final String[] s=s1[i].split(",");
			    	 HashMap<String, Object> map=new HashMap<String,Object>();
			    	 map.put("ItemImage",R.drawable.item);
			    	 map.put("ItemText", s[3]+":"+s[1]);
			    	 listItem.add(map);
			    	 listItemAdapter=new SimpleAdapter(this,listItem,R.layout.rolestable,new String[]{"ItemImage","ItemText"},new int[]{R.id.ItemImage,R.id.ItemText});
					 showTextContent.setAdapter(listItemAdapter);
					 showTextContent.setOnItemClickListener(new OnItemClickListener() {
						@SuppressWarnings("unchecked")
						@Override
						public void onItemClick(AdapterView<?> parent, View v,
								int position, long id) {
							// TODO Auto-generated method stub
						     //高亮显示，然后将相应的信息付给全局变量，之后在button.onclick下操作
							canWriteCard=1;
							cur_pos=position;
		                    v.setSelected(true);  
						    Log.e("poi",s1[cur_pos]);
						    writedata=s1[cur_pos];
						    item_map = (Map<String, Object>)parent.getItemAtPosition(position);
						}
					 }); 
					 showTextContent.setOnScrollListener(new OnScrollListener() {
						
						@Override
						public void onScrollStateChanged(AbsListView view, int scrollState) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onScroll(AbsListView view, int firstVisibleItem,
								int visibleItemCount, int totalItemCount) {
							  lastItemIndex=firstVisibleItem+visibleItemCount;
							  totalIndex=totalItemCount;
						}
					});
				}	   
		}else{
			Toast.makeText(ReadFileActivity.this, "对不起！没有选择正确的文件!", Toast.LENGTH_SHORT).show(); 
		}
		}	
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
			shibieDialog = new ProgressDialog(ReadFileActivity.this, R.style.mProgressDialog);
			shibieDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			shibieDialog.setMessage("写卡中...");
			shibieDialog.setCancelable(false);
			shibieDialog.show();
			timerDialog = new Timer();
			//7秒后取消搜索
			timerDialog.schedule(new TimerTask() {
				@Override
				public void run() {
					shibieDialog.cancel();
					Message msg = new Message();
					msg.what = MSG_OVER;
					mHandler.sendMessage(msg);
				}
			}, 7000);
		  if(canWriteCard==1){
		  sendCmd(writedata);
		  }else{
				Toast.makeText(ReadFileActivity.this, "请选择要写入的文件", Toast.LENGTH_SHORT).show(); 
			  shibieDialog.cancel();
		  }
	}
	private void sendCmd(String writedata) {
		Intent sendToservice = new Intent(ReadFileActivity.this,RFIDService.class);
			if(ctype.equals("00")){
		    sendToservice.putExtra("cardType", "0x01");
			}else if(ctype.equals("01")){
			sendToservice.putExtra("cardType", "0x02");	
			}
			sendToservice.putExtra("data", writedata);
			sendToservice.putExtra("activity", activity);
			startService(sendToservice);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		timeThread = new Timer();
		timeThread.schedule(new TimerTask() {
			
			@Override
			public void run() {
//				String timeStr = Tools.getTime();
				Message msg = new Message();
				msg.what = MSG_FLAG;
				mHandler.sendMessage(msg);
				
			}
		}, 0 , 1000);
		myBroadcast = new MyBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.csei.writecard.ReadFileActivity");
		registerReceiver(myBroadcast, filter); 		//注册广播接收者
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		cmd_flag = 0;  				  //写状态恢复初始状态
		authentication_flag = 0;
		unregisterReceiver(myBroadcast);  //卸载广播接收者
		super.onPause();
		timeThread.cancel();
		Log.e("M1CARDPAUSE", "PAUSE");  	
	}	
	@Override
	protected void onDestroy() {
		Intent stopService = new Intent();
		stopService.setAction("com.example.service.RFIDService");
		stopService.putExtra("stopflag", true);
		sendBroadcast(stopService);  //给服务发送广播,令服务停止
		Log.e(TAG, "send stop");
		super.onDestroy();
	}
	private class MyBroadcast extends BroadcastReceiver {
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			String receivedata = intent.getStringExtra("result");
			if(receivedata!=null){
			shibieDialog.cancel();	
			timerDialog.cancel();
			Toast.makeText(ReadFileActivity.this, receivedata, Toast.LENGTH_SHORT).show();
			item_map.put("ItemImage", R.drawable.checked);
			listItemAdapter.notifyDataSetChanged();
		}
		}
	}
    
	
}
