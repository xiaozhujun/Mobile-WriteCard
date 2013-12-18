package com.csei.writecard;
import java.util.ArrayList;
import java.util.HashMap;
import com.csei.adapter.HighLightAdapter;
import com.example.service.RFIDService;
import com.example.writecard.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
public class ReadFileActivity extends Activity implements OnClickListener {
   public static final int FILE_RESULT_CODE=1;
   private Button selectfile;
   private Button writecard;
   private Button backbutton;
   private ListView showTextContent;
   ArrayList<HashMap<String, Object>> listItem=new ArrayList<HashMap<String,Object>>();	  
   HighLightAdapter highLightAdapter;
   int cur_pos=0;
   String writedata="";
   private String activity = "com.csei.writecard.ReadFileActivity";
   private MyBroadcast myBroadcast;				//广播接收者
	public static int cmd_flag = 0;				//操作状态  0为不做其他操作，1为寻卡，2为认证，3为读数据，4为写数据
	public static int authentication_flag = 0;		//认证状态  0为认证失败和未认证  1为认证成功
	public static String TAG= "M1card";
	String ctype="";
	private TextView showalert;
	int canWriteCard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readfile);
		selectfile=(Button) this.findViewById(R.id.selectfile);
		writecard=(Button) this.findViewById(R.id.writecard);
		showTextContent=(ListView) this.findViewById(R.id.showfilecontent);
		showalert=(TextView) this.findViewById(R.id.showalert);
		backbutton=(Button) this.findViewById(R.id.backbutton);
		selectfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showalert.setVisibility(View.GONE);
				Intent intent=new Intent(ReadFileActivity.this,MyFileManager.class);
				startActivityForResult(intent,FILE_RESULT_CODE);
				canWriteCard=1;
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
				final String[] s1=ss.split(" ");
				for(int i=0;i<s1.length;i++){
					Log.e("yyyy",s1[i]);
				final String[] s=s1[i].split(",");
			    	 HashMap<String, Object> map=new HashMap<String,Object>();
			    	 map.put("ItemImage",R.drawable.item);
			    	 map.put("ItemText", s[3]+":"+s[1]);
			    	 listItem.add(map);
			    	 SimpleAdapter listItemAdapter=new SimpleAdapter(this,listItem,R.layout.rolestable,new String[]{"ItemImage","ItemText"},new int[]{R.id.ItemImage,R.id.ItemText});
					 showTextContent.setAdapter(listItemAdapter);
					 showTextContent.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View v,
								int position, long id) {
							// TODO Auto-generated method stub
						     //高亮显示，然后将相应的信息付给全局变量，之后在button.onclick下操作
							cur_pos=position;                          
		                    highLightAdapter=new HighLightAdapter(ReadFileActivity.this, listItem, cur_pos);
		                    showTextContent.setAdapter(highLightAdapter);	
						    Log.e("poi",s1[position]);
						    writedata=s1[position];
						}
					 }); 
				}	   
		}else{
			showalert.setText("对不起！没有选择正确的文件!"); 
		}
		}	
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		  Log.e("writecard",writedata);
		  if(canWriteCard==1){
		  Intent sendToservice = new Intent(ReadFileActivity.this,RFIDService.class);
			if(ctype.equals("00")){
		    sendToservice.putExtra("cardType", "0x01");
			}else if(ctype.equals("01")){
			sendToservice.putExtra("cardType", "0x02");	
			}
			sendToservice.putExtra("data", writedata);
			sendToservice.putExtra("activity", activity);
			startService(sendToservice);
		  }else{
			  showalert.setText("请选择要写入的文件");
		  }
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		myBroadcast = new MyBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.csei.inspect.PeopleValidateActivity");
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
		@Override
		public void onReceive(Context context, Intent intent) {
		
			
		}
		
	}
    
	
}
