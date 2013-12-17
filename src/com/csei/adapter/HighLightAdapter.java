package com.csei.adapter;
import java.util.ArrayList;
import java.util.HashMap;
import com.example.writecard.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
@SuppressLint("UseSparseArrays")
public class HighLightAdapter extends BaseAdapter {  
	Context context;
	ArrayList<HashMap<String, Object>> listData;
	int cur_pos;
	//记录checkbox的状态
	HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();		

	LayoutInflater inflater; 
	//构造函数
	public HighLightAdapter(Context context,ArrayList<HashMap<String, Object>> listData,int cur_pos) {
		this.context = context;
		this.listData = listData;
		this.cur_pos=cur_pos;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
    public View getView(int position, View convertView, ViewGroup parent) {  
        Log.e("TEST", "refresh once");
        LayoutInflater mInflater = LayoutInflater.from(context);
        convertView = mInflater.inflate(R.layout.rolestable, null, false); 
        ImageView img=(ImageView) convertView.findViewById(R.id.ItemImage);
        TextView tv = (TextView) convertView  
                .findViewById(R.id.ItemText);// 显示文字
        Resources resources=context.getResources();
        Drawable btnDrawable=resources.getDrawable(R.drawable.item);
        img.setImageDrawable(btnDrawable);
        tv.setText((String)listData.get(position).get("ItemText"));        
        if (position == cur_pos) {// 如果当前的行就是ListView中选中的一行，就更改显示样式  
            convertView.setBackgroundColor(Color.DKGRAY);// 更改整行的背景色  
            tv.setTextColor(Color.WHITE);// 更改字体颜色  
            
        }  
        return convertView;  
    }  
}  
