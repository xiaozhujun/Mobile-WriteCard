package com.csei.writecard;

import com.example.writecard.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;


public class MainActivity extends Activity {
    
	private LoadingView main_imageview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadingview);
		main_imageview= (LoadingView) findViewById(R.id.main_imageview);
		initLoadingImages();
		new Thread()
		{
			@Override
			public void run(){
				int waitingTime=1000;
			try{
				main_imageview.startAdmin();
				while(waitingTime>0){
					sleep(100);
					waitingTime-=100;
				}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
				finish();
				Intent intent=new Intent(MainActivity.this,ReadFileActivity.class);
				startActivity(intent);
				}
				}
			
		}.start();
		
		   
	}
	private void initLoadingImages() {
		// TODO Auto-generated method stub
		int[] imageIds=new int[6];
		imageIds[0]=R.drawable.loader_frame_1;
		imageIds[1]=R.drawable.loader_frame_2;
		imageIds[2]=R.drawable.loader_frame_3;
		imageIds[3]=R.drawable.loader_frame_4;
		imageIds[4]=R.drawable.loader_frame_5;
		imageIds[5]=R.drawable.loader_frame_6;
	    main_imageview.setImageIds(imageIds);
	}
	
    
}
