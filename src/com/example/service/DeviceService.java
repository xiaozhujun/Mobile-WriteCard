package com.example.service;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import com.example.psam_demo.PSAM;
import com.example.tools.Tools;
/**
 * 设备服务，用于在后台运行，发送命令和接收数据
 * 在服务初始化时，执行串口打开，设备电源开启，
 * 获取串口输入输出流
 * 启动读数据线程
 * @author Administrator
 *
 */
public class DeviceService extends Service {
	protected PSAM mSerialPort;		//用于调用本地方法
	protected OutputStream mOutputStream;  //串口输出流
	private InputStream mInputStream;      //串口输入流
	private ReadThread mReadThread;		   //读线程
	private Boolean run = true; // 线程中断信号
	private String data = null; // 返回的数据
	private StringBuffer data_buffer = new StringBuffer();
//	private Timer sendData;
	private MyReceiver myReceive;  	//广播接收者
	//射频开关计时器
	@SuppressWarnings("unused")
	private Timer stopRF;
	public String activity = null; // 回传数据的activity
	/**
	 *  读线程 ,读取设备返回的信息，将其回传给发送请求的activity
	 * @author Jimmy Pang
	 *
	 */
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (run) {
				int size;
				try {
					byte[] buffer = new byte[128];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					Log.e("size", size+"---------------");
					if (size > 0) {                  
						data = Tools.Bytes2HexString(buffer, size);
						Log.e("data",data);
						data_buffer.append(data);
						data = null;
						int strlen=data_buffer.length();
						if(strlen<10){
						if(data_buffer!=null){
						Log.e("DeviceService data", data_buffer.toString());
						Intent serviceIntent = new Intent();
						serviceIntent.setAction(activity);
						serviceIntent.putExtra("result", data_buffer.toString());
						data_buffer.setLength(0);
						sendBroadcast(serviceIntent);
						} 
						}else{
						String dataLen = data_buffer.substring(2, 6); // 取得数据包的长度
						Log.e("datalength", dataLen+"**" +data_buffer.toString());
						if(Tools.checkData(dataLen, data_buffer.toString())){
						if(data_buffer!=null){
							Log.e("DeviceService data", data_buffer.toString());
							Intent serviceIntent = new Intent();
							serviceIntent.setAction(activity);
							serviceIntent.putExtra("result", data_buffer.toString());
							data_buffer.setLength(0);
							sendBroadcast(serviceIntent);
						} 
					}
					}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
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
		init();
	}
	private void init(){
		Log.e("service on create", "service on create");
		try {
			mSerialPort = new PSAM(14, 115200); // 打开串口，设备的端口号设置为14，波特率为115200
			Log.e("mSerialPort", mSerialPort + "");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(mSerialPort == null){  //没找到设备
//			Toast.makeText(getApplicationContext(), "无法找到设备", Toast.LENGTH_SHORT).show();			
			return;
		}
		mSerialPort.PowerOn_HFPsam(); // 开启电源
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();
		myReceive = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.service.DeviceService");
		registerReceiver(myReceive, filter);
		// 注册Broadcast Receiver，用于关闭Service
//		sendData = new Timer();
		/* Create a receiving thread */
		mReadThread = new ReadThread();
		mReadThread.start(); // 开启读线程
		Log.e("DeviceService", "start thread");			
	}	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		byte[] cmd_arr = intent.getByteArrayExtra("cmd");
		Log.e("cmd_arr",cmd_arr+"");
		if (cmd_arr == null)
			return 0; // 没收到命令直接返回
		Log.e("CMD", Tools.Bytes2HexString(cmd_arr, cmd_arr.length));		
		try {
			mOutputStream.write(cmd_arr); // 发送命令
			 Log.e("WRITE", "SUCCESS");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	@Override
	public void onDestroy() {
		if (mReadThread != null)
			run = false; 					// 关闭线程
		mSerialPort.PowerOff_HFPsam(); 		// 关闭电源
		mSerialPort.close(14); 				// 关闭串口
		unregisterReceiver(myReceive); 		// 卸载注册
		super.onDestroy();
	}
	/**
	 *  广播接受者
	 * @author Jimmy Pang
	 *
	 */
	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String ac = intent.getStringExtra("activity");
			if(ac!=null) 
				Log.e("receive activity", ac);
			activity = ac; // 获取activity
			if (intent.getBooleanExtra("stopflag", false))
				stopSelf(); // 收到停止服务信号
			Log.e("stop service", intent.getBooleanExtra("stopflag", false)
					+ "");
		}
	}
}
