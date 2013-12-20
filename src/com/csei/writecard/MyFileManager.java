package com.csei.writecard;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.csei.adapter.MyAdapter;
import com.csei.analysexml.ParseXml;
import com.example.writecard.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
public class MyFileManager extends ListActivity {
	private List<String> items=null;
	private List<String> paths=null;
	private String rootPath="/";
	private String curPath="/";
	private TextView mPath;
	List<String> list=new ArrayList<String>();
	String result="";
	@SuppressWarnings("unused")
	private final static String TAG="bb";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fileselect);
		mPath=(TextView) findViewById(R.id.mPath);
		Button buttonCancle=(Button) findViewById(R.id.buttonCancle);
		buttonCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			     finish();	
			}
		});
		getFileDir(rootPath);
	}
	private void getFileDir(String filePath) {
		mPath.setText(filePath);
		items=new ArrayList<String>();
		paths=new ArrayList<String>();
		File f=new File(filePath);
		File[] files=f.listFiles();
		if(files==null){
			new AlertDialog.Builder(this).setMessage("ÎÄ¼þ¼ÐÎª¿Õ").show();
		}else{
			Log.i("not null",files.toString());
		if(!filePath.equals(rootPath)){
			items.add("b1");
			paths.add(rootPath);
			items.add("b2");
			paths.add(f.getParent());	
		}
		for(int i=0;i<files.length;i++){
			File file=files[i];
			items.add(file.getName());
			paths.add(file.getPath());	
		}
		setListAdapter(new MyAdapter(this,items,paths));
		}
	}
	@SuppressWarnings("rawtypes")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	     File file=new File(paths.get(position));
	     Log.e("0h0h",paths.get(position)+"");
	     if(file.isDirectory()){
	    	 Log.i("Directory","Directory");
	    	 curPath=paths.get(position);
	    	 getFileDir(paths.get(position)); 
	     }else{ 
	    	Log.i("file","file");
	    	Intent data=new Intent(MyFileManager.this,ReadFileActivity.class);
	    	Bundle bundle=new Bundle();
	    	ParseXml p=new ParseXml();
	    	Log.e("fileName",file.getName());
	    	Log.e("file",curPath+"/"+file.getName());
	    	String filepath=curPath+"/"+file.getName();
	    	if(file.getName().equals("Employer.xml")){
	    	String s="";
	        list=p.parseEmployers(filepath);
	        int len=p.getEmployersCount(filepath);
	        Log.e("len",len+"");
	        Log.e("list.size",list.size()+"");
	        if(list.size()!=0){
		    	Iterator it=list.iterator();
		    	while(it.hasNext()){
		    		 s=(String) it.next();
		    	     Log.e("opo",s);
		    	     result+=s;
		    	}	
		    	}
		    	bundle.putString("filecontent", result);
		    	bundle.putString("ctype","00");
				data.putExtras(bundle);
				setResult(2,data);
		    	finish();
	    	}else if(file.getName().equals("tags.xml")){
	    		String s="";
		        list=p.parseTagXml(filepath);
		        int len=p.getEmployersCount(filepath);
		        Log.e("len",len+"");
		        Log.e("list.size",list.size()+"");
		        if(list.size()!=0){
			    	Iterator it=list.iterator();
			    	while(it.hasNext()){
			    		 s=(String) it.next();
			    	     Log.e("opo",s);
			    	     result+=s;
			    	}	
			    	}
			    	bundle.putString("filecontent", result);
			    	bundle.putString("ctype","01");
					data.putExtras(bundle);
					setResult(2,data);
			    	finish();	
	    	}
	         else{
	    	 bundle.putStringArray("filecontent", null);
	    	 data.putExtras(bundle);
			 setResult(2,data);
			 finish();	 
	     }
	     }
	}			
}
