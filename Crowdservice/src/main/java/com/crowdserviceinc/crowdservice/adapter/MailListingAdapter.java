package com.crowdserviceinc.crowdservice.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebHistoryItem;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.model.Mail;
import com.crowdserviceinc.crowdservice.model.MailData;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.widget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MailListingAdapter extends BaseAdapter {

	private Activity mContext;
	private ArrayList<MailData> mMailList = null;
	
	private DisplayImageOptions options;
	
	public MailListingAdapter(Activity activity, ArrayList<MailData> list) {
		mContext = activity;
		mMailList = list;
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.com_facebook_profile_picture_blank_square)
				.showImageForEmptyUri(R.drawable.com_facebook_profile_picture_blank_square)
				.showImageOnFail(R.drawable.com_facebook_profile_picture_blank_square)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.build();
	}

	public ArrayList<MailData> getDataSource() {
		return mMailList;
	}

	public void refereshAdapter(ArrayList<MailData> list) {
		mMailList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mMailList == null)
			return 0;
		else
			return mMailList.size();
	}

	@Override
	public Object getItem(int arg0) {

		return mMailList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		Holder holder = null;
		MailData data = mMailList.get(position);

		if (convertView == null) {
			holder = new Holder();

			RelativeLayout ll = (RelativeLayout) mContext.getLayoutInflater().inflate(
					R.layout.row_mail_listing, null);

			holder.ivMessageIcon = (CircleImageView) ll.findViewById(R.id.ivMessageIcon);
			holder.txtFromUserName = (TextView) ll.findViewById(R.id.txtFromUserName);
			holder.txtMessage = (TextView) ll.findViewById(R.id.txtMessage);
			holder.txtDate = (TextView) ll.findViewById(R.id.txtDate);
			holder.container = ll;

			holder.container.setTag(holder);

			convertView = holder.container;
		} else
			holder = (Holder) convertView.getTag();

		
		Mail mail = data.getMails().get(0);
		
		holder.txtFromUserName.setText(mail.getJobName());
		
		if(mail.getStatus().equals("3")){
			holder.txtMessage.setText(mail.getMessage());
		}else{
			holder.txtMessage.setText(mail.getFromUserName() + ": " + mail.getMessage());
		}
		
		
		if(DateUtils.isToday(mail.getDate().getTime()))
			holder.txtDate.setText("Today");
		else 
			holder.txtDate.setText(Constants.deviceFormate.format(mail.getDate()));
		
		holder.container.setBackgroundColor(Color.WHITE);
		
		if(mail.getStatus().equals("2")){
			//holder.container.setBackgroundColor(mContext.getResources().getColor(Color.WHITE));
			holder.ivMessageIcon.setImageResource(R.drawable.marker_question);
		}else if(mail.getStatus().equals("3")){
			//holder.container.setBackgroundColor(mContext.getResources().getColor(Color.WHITE));
			holder.ivMessageIcon.setImageResource(R.drawable.marker_brain_red);
		}else{
			//holder.container.setBackgroundColor(Color.WHITE);
			if(mail.getToUserId().equals(Constants.uid)){
				//imageLoader.DisplayImage(mail.getFromUserImage(), holder.ivMessageIcon, R.drawable.marker_blank_blue);
				//holder.ivMessageIcon.setImageResource(R.drawable.marker_blank_blue);
				
				//ImageLoader.getInstance().displayImage(mail.getFromUserImage(), holder.ivMessageIcon, options, animateFirstListener);
				ImageLoader.getInstance().displayImage(mail.getFromUserImage(), holder.ivMessageIcon, options);
			}else{
				//imageLoader.DisplayImage(mail.getFromUserImage(), holder.ivMessageIcon, R.drawable.marker_blank_red);
				//holder.ivMessageIcon.setImageResource(R.drawable.marker_blank_red);
				//ImageLoader.getInstance().displayImage(mail.getFromUserImage(), holder.ivMessageIcon, options, animateFirstListener);
				ImageLoader.getInstance().displayImage(mail.getToUserImage(), holder.ivMessageIcon, options);
			}
		}	

		return convertView;
	}

	private class Holder {
		public RelativeLayout container;
		public TextView txtFromUserName, txtMessage, txtDate;
		public CircleImageView ivMessageIcon;
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
