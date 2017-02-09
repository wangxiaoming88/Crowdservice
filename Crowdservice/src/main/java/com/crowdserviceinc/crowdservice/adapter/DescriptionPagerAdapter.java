
/*
 * Created by:SynapseIndia
 * Date:17 Oct 2013
 */

package com.crowdserviceinc.crowdservice.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.activity.TutorialActivity;

public class DescriptionPagerAdapter extends PagerAdapter implements OnClickListener {

	ArrayList<String> datalist;
	TutorialActivity context;
	public int pos;
	
	TextView detail;		
	LayoutInflater inflater;

	
	public DescriptionPagerAdapter(TutorialActivity context, ArrayList<String> data)
	{
		this.datalist = data;
		this.context = context;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datalist.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == ((LinearLayout) object);
	}
	
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) 
	{		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_detailpager,null);			
		final TextView imageDetail =(TextView)view.findViewById(R.id.tutorialDetail);
		
		if(datalist!=null && datalist.size()>0 )
		{	
			String desc=datalist.get(position);			
			imageDetail.setText(desc);
			imageDetail.setMovementMethod(new ScrollingMovementMethod());						
			((ViewPager)container).addView(view,0);			
			pos = position;
			if(pos==0){
				view.setBackgroundResource(R.drawable.tutoriala);
			}
			if(pos==1){
				view.setBackgroundResource(R.drawable.tutorialb);
			}
			if(pos==2){
				view.setBackgroundResource(R.drawable.tutorialc);
			}
		}				
		return view;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((LinearLayout) object);
	}
      
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
						
	}
		
	
}
