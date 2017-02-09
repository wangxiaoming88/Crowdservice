package com.crowdserviceinc.crowdservice.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.model.Mail;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.widget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MailDetailListingAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<Mail> mMailList = null;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	private DisplayImageOptions options1, options2, options3;
	
	public MailDetailListingAdapter(LayoutInflater inflater, ArrayList<Mail> list) {
		mInflater = inflater;
		mMailList = list;
		
		Collections.reverse(mMailList);
		
		options1 = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.com_facebook_profile_picture_blank_square)
				.showImageForEmptyUri(R.drawable.marker_blank_blue)
				.showImageOnFail(R.drawable.marker_blank_blue)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.build();
		
		options2 = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.com_facebook_profile_picture_blank_square)
				.showImageForEmptyUri(R.drawable.marker_blank_red)
				.showImageOnFail(R.drawable.marker_blank_red)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.build();
		
		options3 = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.placeholder)
				.showImageForEmptyUri(R.drawable.placeholder)
				.showImageOnFail(R.drawable.placeholder)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.build();
	}

	public ArrayList<Mail> getDataSource() {
		return mMailList;
	}

	public void refereshAdapter(ArrayList<Mail> list) {
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
		Mail mail = mMailList.get(position);

		if (convertView == null) {
			holder = new Holder();

			RelativeLayout ll = (RelativeLayout) mInflater.inflate(
					R.layout.row_mail_detail_listing, null);

			holder.ivMessageIcon = (CircleImageView) ll.findViewById(R.id.ivMessageIcon);
			holder.txtMessage = (TextView) ll.findViewById(R.id.txtMessage);
			holder.imgMessage = (ImageView) ll.findViewById(R.id.imgMessage);
			
			holder.container = ll;
			holder.container.setTag(holder);
			convertView = holder.container;
			
		} else
			holder = (Holder) convertView.getTag();

		
		holder.txtMessage.setText(mail.getMessage());
		
		if(mail.getJobCreator().equals(mail.getFromUserId())){
			//holder.ivMessageIcon.setImageResource(R.drawable.marker_blank_blue);
			ImageLoader.getInstance().displayImage(mail.getFromUserImage(), holder.ivMessageIcon, options1, animateFirstListener);
		}else{
			//holder.ivMessageIcon.setImageResource(R.drawable.marker_blank_red);
			ImageLoader.getInstance().displayImage(mail.getFromUserImage(), holder.ivMessageIcon, options2, animateFirstListener);
		}

		if(mail.getImageUrl() == null || mail.getImageUrl().length() == 0){
			holder.imgMessage.setVisibility(View.GONE);
		}else{
			holder.imgMessage.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(mail.getImageUrl(), holder.imgMessage, options3);
		}
		return convertView;
	}

	private class Holder {
		public RelativeLayout container;
		public TextView txtMessage;
		public CircleImageView ivMessageIcon;
		public ImageView imgMessage;
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
