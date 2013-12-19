package com.example.service;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import com.example.psam_demo.PSAM;
import com.example.tools.Tools;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
@SuppressLint("HandlerLeak")
public class RFIDService extends Service {
	// 操作员卡片密码
	private static final String OP_PASSWORD = "FFFFFFFFFFFF";
	// 区域卡密码
	@SuppressWarnings("unused")
	private static final String AREA_PASSWORD = "FFFFFFFFFFFF";
	private PSAM mSerialPort; // 用于调用本地方法
	private InputStream mInputStream; // 串口输入流
	private OutputStream mOutputStream; // 串口输出流
	private String data; // 接收数据
	private StringBuffer data_buffer;
	private Timer sendData; // 数据接收计时器
	private Timer searchCard; // 搜寻卡片计时器
	private String activity;
	private boolean run = true; // 线程结束标识
	@SuppressWarnings("unused")
	private boolean skip = false;
	private int readOp = 0;
	private final int SEARCH_CARD = 1; // 寻卡标识
	private final int AUTH_CARD = 2; // 认证标识
	private final int WRITE_CARD = 3; // 读卡数据标识
	private MyReceiver myReceiver; // 广播接收者
	private ReadThread mReadThread; // 读数据线程
	public String TAG = "RFIDservice"; // Debug
    byte[] value_array;
    String opSector="04";           //人员卡经过处理后的验证所需的区值
    String areaSector="08";           //区域卡经过处理后的验证所需的区值
    String authPeopSector="01";    //读人员卡时的区值
    String authAreaSector="02";   //读区域卡时的区值
    String val="01";              //块值
    String val1="02";
    String readcardflag;
    String searchflag;                             
    String authflag;
    String readflag;
    String cardType;
    String result=null;
    String result1=null;
    int readcount=2;
    String[] resultArray=new String[2];
    String re="";
    String writedata;
    String ss="";
    String uname="";
    String uid="";
    String rolename="";
    String rid="";
    String devnum="";
    String area="";
    String aid="";
    String code="";
	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			skip = true;
			Bundle receiverData = msg.getData();
			String rec = receiverData.getString("receiver");
			Log.e("rec",rec);
			String crd=receiverData.getString("card");
			Log.e("crd",crd);
			if (rec == null || "".equals(rec))
				return;
			Log.e(TAG + "  RECEIVER", rec);
			handleCard(rec, crd,opSector,areaSector,authPeopSector,authAreaSector,val);
			//startSearch(cardType);
			skip = false;
		}
		private void handleCard(String rec, String crd,String opSector,String areaSector,String authPeopSector,String authAreaSector,String value) {
			switch (readOp) {
			case SEARCH_CARD: // 得到寻卡结果,并进行认证
				// 寻卡结果
				if(crd.equals("01")){
				searchCard(rec,opSector);
				}else if(crd.equals("02")){
				searchCard(rec,areaSector);	
				}
				break;
			case AUTH_CARD: // 得到认证结果，并进行读数据
				// 认证结果
				if(crd.equals("01")){
				authCard(rec,authPeopSector,value);
				}else if(crd.equals("02")){
				authCard(rec,authAreaSector,value);
				}
				break;
			case WRITE_CARD:
				if(crd.equals("01")){
					writeCard(rec,authPeopSector,value,writedata);
					}else if(crd.equals("02")){
					writeCard(rec,authAreaSector,value,writedata);
					}
				readcount--;
				if(readcount!=0&&readcount>0){
					startSearch(cardType,2);
				}
				if(readcount==0){
					run=false;
					Intent serviceIntent = new Intent();
					serviceIntent.setAction(activity);
					serviceIntent.putExtra("result","写卡成功!");
					sendBroadcast(serviceIntent);
					}
			default:
				break;
			}
			
		}
		private String searchCard(String rec,String sector) {      //寻卡
		    String searchresult = "00";
			byte[] handlerCMD;
			byte[] cardID = mSerialPort.resolveDataFromDevice(Tools
					.HexString2Bytes(rec));
			if (cardID != null) {
				searchCard.cancel();
				Log.e(TAG, "send auth");
				// 认证信息为：密码类型 + 扇区号*4 + 密码
				byte[] auth_byte = Tools.HexString2Bytes("00" + sector
						+ OP_PASSWORD);
				handlerCMD = mSerialPort.rf_authentication_cmd(auth_byte);
				Log.e(TAG+"sss", Tools.Bytes2HexString(handlerCMD,
						handlerCMD.length));
				readOp = AUTH_CARD;
				try {
					mOutputStream.write(handlerCMD);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				searchresult="01";
			}
			return searchresult;
		}
		private String authCard(String rec,String sector,String block ) {               //验证
			String authresult="00";
			byte[] handlerCMD;
			int auth_flag = mSerialPort.rf_check_data(Tools
					.HexString2Bytes(rec));
			Log.e("auth_result", rec);
			if (auth_flag == 0) {
				// 读数据
				String sector_str =sector.toString().trim();
				int sector_int = Integer.parseInt(sector_str);                //Sector
				int sector_int_temp = sector_int*4; 
				Log.e("block",block);
				int value = sector_int_temp + Integer.parseInt(block);
				Log.e("value",value+"");
				String value_str;
				if(value > 15){
					value_str = Integer.toHexString(value);
				}else{
					value_str = "0" + Integer.toHexString(value);
				}
				Log.e("value_str",value_str);
				value_array = Tools.HexString2Bytes(value_str);  // 读、写数据块
			    Log.e("value_array",Tools.Bytes2HexString(value_array, value_array.length));
				handlerCMD = mSerialPort.rf_read_cmd(value_array);
				Log.e("read_cmd", Tools.Bytes2HexString(handlerCMD, handlerCMD.length));
				readOp = WRITE_CARD;
				try {
					mOutputStream.write(handlerCMD);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				authresult="01";
			}
			return authresult;
		}
		public String writeCard(String rec,String sector,String block,String write_data){
			String s=" ";
			byte[] handlerCMD;
			String write_data_src=write_data.toString().trim();
			Log.e("write_data_src",write_data_src+"");
			byte[] b=write_data_src.getBytes();
			Log.e("b.length",b.length+"");
			if(b.length!=16){           //当b的长度不足16位时,在后面补0
			int diff=16-b.length; 
			for(int i=1;i<diff;i++){
				s+=" ";        
			}
			write_data_src+=s;
			b=write_data_src.getBytes();
			}
			String bb=Tools.Bytes2HexString(b, b.length);
			String sector_str =sector.toString().trim();
			int sector_int = Integer.parseInt(sector_str);                //Sector
			int sector_int_temp = sector_int*4; 
			Log.e("block",block);
			int value = sector_int_temp + Integer.parseInt(block);
			Log.e("value",value+"");
			String value_str;
			if(value > 15){
				value_str = Integer.toHexString(value);
			}else{
				value_str = "0" + Integer.toHexString(value);
			}
			Log.e("value_str",value_str);
			value_array = Tools.HexString2Bytes(value_str);  // 读、写数据块
			if(!checkData(bb)){
				Toast.makeText(getApplicationContext(), "The data format error，Please input the right 32-bit hexadecimal values", Toast.LENGTH_SHORT).show();
				
			}
			String write_data_buffer = value_str + bb ;
			byte[] write_buffer = Tools.HexString2Bytes(write_data_buffer);
			 Log.e("bb",bb);
			 Log.e("bb.length",bb.length()+"");
			 Log.e("value_str",value_str);
			 Log.e("write_data_buffer",write_data_buffer);
			handlerCMD = mSerialPort.rf_write_cmd(write_buffer);
			try {
				mOutputStream.write(handlerCMD);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int write_data_flag = mSerialPort.rf_check_data(Tools.HexString2Bytes(rec));
			Log.e("w1",write_data_flag+"");
			if(write_data_flag != 0){
				/*re="write fail";*/
			}else{
				re="write success";
			}
			return block;
			
		}
	};
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (run) {
				/*if (!skip) {*/
				Log.e("hh","1");
					int size;
					try {
						byte[] buffer = new byte[128];
						if (mInputStream == null)
							return;
						size = mInputStream.read(buffer);
						if (size > 0) {
							// 取消寻卡
							Log.e("hh","2");
							data = Tools.Bytes2HexString(buffer, size);
							data_buffer.append(data);
							// 设置数据超时为50ms，发送数据到activity
							sendData.schedule(new TimerTask() {
								@Override
								public void run() {
									boolean valid = true;
									if (data_buffer != null
											&& data_buffer.length() != 0
											&& activity != null) {
										Log.e("hh","3");
										// 数据发送给mhandler，交给handler处理
										data = null;
										int strlen = data_buffer.length();
										if (strlen >= 10) {
											Log.e("hh","4");
											String dataLen = data_buffer
													.substring(2, 6); // 取得数据包的长度
											valid = Tools.checkData(dataLen,
													data_buffer.toString());
											Log.e("datalength", dataLen + "**"
													+ data_buffer.toString()+" valid:"+valid);
										}				
										Message msg = new Message();
										Bundle bundle = new Bundle();
										bundle.putString("receiver",
												data_buffer.toString());
										if(readcardflag.equals("01")){
										bundle.putString("card", "01");
									    }else if(readcardflag.equals("02")){
									    bundle.putString("card", "02");										
									    }
										msg.setData(bundle);
										data_buffer.setLength(0);
										msg.setData(bundle);
										mhandler.sendMessage(msg);										
										if(valid){	
											
										}else{
											//valid为false时停止线程
											data_buffer.setLength(0);
										}
									}
								}
							}, 5);
						}
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				/*}*/
			}

		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 初始化
		init();
	}
	@SuppressWarnings("unused")
	private void init() {
		Log.e("service on create", "service on create");
		try {
			mSerialPort = new PSAM(14, 115200); // 打开串口，设备的端口号设置为3或者14，波特率为115200
			Log.e("mSerialPort", mSerialPort + "");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mSerialPort == null) { // 没有打开串口
			return;
		}
		int powerflag = mSerialPort.PowerOn_HFPsam(); // 开启电源
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();
		data_buffer = new StringBuffer();
		// 注册Broadcast Receiver，用于关闭Service
		myReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.service.RFIDService");
		registerReceiver(myReceiver, filter);
	}
	private void startSearchCard() {
		readOp = SEARCH_CARD;
		searchCard = new Timer();
		searchCard.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					mOutputStream.write(mSerialPort.rf_card()); // 寻卡

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 1000, 100);
		sendData = new Timer();
		/* Create a receiving thread */
		mReadThread = new ReadThread();
		mReadThread.start(); // 开启读线程
		if(readcount==2){
			run=true;
		}
		Log.e("readcount",readcount+""+run);
		Log.e(TAG, "start thread");
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    cardType = intent.getStringExtra("cardType");
	    ss=intent.getStringExtra("data");       //接受到的数据
	    String[] d=ss.split(",");
	        if(cardType!=null){
	    	if(cardType.equals("0x01")){
	        uname=d[1];
	    	uid=d[2];
	    	rolename=d[3];
	    	rid=d[4];
	    	}else if(cardType.equals("0x02")){
	    	area=d[1];
	    	devnum=d[3];
	    	aid=d[4];
	    	}
	        }else{
	         Log.e("无卡","无卡");
	        }
		if (intent.getStringExtra("activity") != null) {
			activity = intent.getStringExtra("activity");
		}
		Log.e("cardType", cardType);
		re="";
		readcount=2;
		startSearch(cardType,1);
		return 0;
	}
	private void startSearch(String cardType,int count) {
		/*if(count==1){
			val="01";
		}else if(count==2){
			val="02";
		}*/
		if (cardType.equals("0x01")) {
			if(count==1){
				val="01";
				writedata="x1"+uid+rid+uname;
			}else if(count==2){
				val="02";
				if(rolename.length()>5){
					rolename=rolename.substring(rolename.length()-5, rolename.length());
				}
				writedata=rolename+"0";
			}
			startSearchCard();
			readcardflag="01";
		} else if (cardType.equals("0x02")) {
			if(count==1){
				val="01";
				writedata="x2"+devnum+aid;
			}else if(count==2){
				val="02";
				writedata=aid+area;
			}
			startSearchCard();
			readcardflag="02";
		} else {
			Log.e("cardType", cardType + " is not right!0x01|0x02");
			// 返回读到的数据给请求者
			Intent serviceIntent = new Intent();
			serviceIntent.setAction(activity);
			serviceIntent.putExtra("code", "1");
			serviceIntent.putExtra("result", "卡类型错误");
			sendBroadcast(serviceIntent);
		}
	}
	@Override
	public void onDestroy() {
		if (mReadThread != null)
			run = false; // 关闭线程
		mSerialPort.PowerOff_HFPsam(); // 关闭电源
		mSerialPort.close(14); // 关闭串口
		unregisterReceiver(myReceiver); // 卸载注册
		super.onDestroy();
	}
	/**
	 * 广播接受者
	 * 
	 * @author Jimmy Pang
	 * 
	 */
	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String ac = intent.getStringExtra("activity");
			if (ac != null)
				Log.e("receive activity", ac);
			activity = ac; // 获取activity
			if (intent.getBooleanExtra("stopflag", false)) {
				searchCard.cancel();
				stopSelf(); // 收到停止服务信号
				Log.e("stop service", intent.getBooleanExtra("stopflag", false)
						+ "");
			}
		}

	}
	public static boolean checkData(String src){
		boolean flag = false;
		String regString = "[a-f0-9A-F]{32}";
		flag = Pattern.matches(regString, src); //匹配数据，是否为32位的十六进制
		return flag;
	}
}
