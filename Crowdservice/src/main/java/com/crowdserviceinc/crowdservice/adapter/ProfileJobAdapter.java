package com.crowdserviceinc.crowdservice.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.model.ProfileJobData;

public class ProfileJobAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	private ArrayList<ProfileJobData> mJobsList = null;
	private SimpleDateFormat serverFormate = null;
	private SimpleDateFormat deviceFormate = null;
	private Date currenDate = null;

	public ProfileJobAdapter(ArrayList<ProfileJobData> list,
			LayoutInflater inflater) {
		mInflater = inflater;
		mJobsList = list;
		serverFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		deviceFormate = new SimpleDateFormat("yyyy-MM-dd");
		currenDate = Calendar.getInstance().getTime();

	}

	// public JobData[] getDataSource(){return mJobsList;}

	public void refereshAdapter(ArrayList<ProfileJobData> list) {
		if(mJobsList == null)
			mJobsList = new ArrayList<ProfileJobData>();
		else
			mJobsList.clear();
		mJobsList.addAll(list);
		currenDate = Calendar.getInstance().getTime();
		notifyDataSetChanged();
		// checkBid = bids;
	}

	@Override
	public int getCount() {
		if (mJobsList == null)
			return 0;
		else
			return mJobsList.size();
	}

	@Override
	public Object getItem(int arg0) {

		return mJobsList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		Holder holder = null;

		if (convertView == null) {
			holder = new Holder();

			RelativeLayout ll = (RelativeLayout) mInflater.inflate(
					R.layout.profile_job_listing, null);

			holder.jobName = (TextView) ll.findViewById(R.id.row_job_desc);
			holder.txtPending = (TextView) ll.findViewById(R.id.row_pending);
			
			// holder.jobRating =
			// (RatingBar)ll.findViewById(R.id.list_rating_bar);
			holder.star1_j = (ImageView) ll.findViewById(R.id.star1_j);
			holder.star2_j = (ImageView) ll.findViewById(R.id.star2_j);
			holder.star3_j = (ImageView) ll.findViewById(R.id.star3_j);
			holder.star4_j = (ImageView) ll.findViewById(R.id.star4_j);
			holder.star5_j = (ImageView) ll.findViewById(R.id.star5_j);

			
			holder.container = ll;
			holder.container.setTag(holder);
			holder.jobName.setTag(mJobsList.get(position).getJobRating());

			convertView = holder.container;
		} else {
			holder = (Holder) convertView.getTag();
		}

		ProfileJobData data = mJobsList.get(position);

		holder.jobName.setText(data.getJobCategory());
		
		
		if (data.getJobRating() != null) {
			// holder.jobRating.setRating(Float.valueOf(data.getJobRating()));
			// Float a=1.70F;

			if (holder.jobName.getTag().equals("5")) {
				holder.star1_j.setImageResource(R.drawable.yellowstar);
				holder.star2_j.setImageResource(R.drawable.yellowstar);
				holder.star3_j.setImageResource(R.drawable.yellowstar);
				holder.star4_j.setImageResource(R.drawable.yellowstar);
				holder.star5_j.setImageResource(R.drawable.yellowstar);
				
				showRatingStar(holder, true);

			} else if (holder.jobName.getTag().equals("4")) {
				holder.star1_j.setImageResource(R.drawable.yellowstar);
				holder.star2_j.setImageResource(R.drawable.yellowstar);
				holder.star3_j.setImageResource(R.drawable.yellowstar);
				holder.star4_j.setImageResource(R.drawable.yellowstar);
				holder.star5_j.setImageResource(R.drawable.blackstar);
				
				showRatingStar(holder, true);
				
			} else if (holder.jobName.getTag().equals("3")) {
				holder.star1_j.setImageResource(R.drawable.yellowstar);
				holder.star2_j.setImageResource(R.drawable.yellowstar);
				holder.star3_j.setImageResource(R.drawable.yellowstar);
				holder.star4_j.setImageResource(R.drawable.blackstar);
				holder.star5_j.setImageResource(R.drawable.blackstar);
				
				showRatingStar(holder, true);

			} else if (holder.jobName.getTag().equals("2")) {
				holder.star1_j.setImageResource(R.drawable.yellowstar);
				holder.star2_j.setImageResource(R.drawable.yellowstar);
				holder.star3_j.setImageResource(R.drawable.blackstar);
				holder.star4_j.setImageResource(R.drawable.blackstar);
				holder.star5_j.setImageResource(R.drawable.blackstar);
				
				showRatingStar(holder, true);
			}

			else if (holder.jobName.getTag().equals("1")) {
				holder.star1_j.setImageResource(R.drawable.yellowstar);
				holder.star2_j.setImageResource(R.drawable.blackstar);
				holder.star3_j.setImageResource(R.drawable.blackstar);
				holder.star4_j.setImageResource(R.drawable.blackstar);
				holder.star5_j.setImageResource(R.drawable.blackstar);
				
				showRatingStar(holder, true);
			}else{
				holder.star1_j.setImageResource(R.drawable.blackstar);
				holder.star2_j.setImageResource(R.drawable.blackstar);
				holder.star3_j.setImageResource(R.drawable.blackstar);
				holder.star4_j.setImageResource(R.drawable.blackstar);
				holder.star5_j.setImageResource(R.drawable.blackstar);
				
				showRatingStar(holder, false);
			}

		} else {
			// holder.jobRating.setRating(0.0F);
			
			holder.star1_j.setImageResource(R.drawable.blackstar);
			holder.star2_j.setImageResource(R.drawable.blackstar);
			holder.star3_j.setImageResource(R.drawable.blackstar);
			holder.star4_j.setImageResource(R.drawable.blackstar);
			holder.star5_j.setImageResource(R.drawable.blackstar);
			
			showRatingStar(holder, false);
		}
		
		return convertView;
	}
	
	private void showRatingStar(Holder holder, Boolean flag){
		if(flag){
			holder.star1_j.setVisibility(View.VISIBLE);
			holder.star2_j.setVisibility(View.VISIBLE);
			holder.star3_j.setVisibility(View.VISIBLE);
			holder.star4_j.setVisibility(View.VISIBLE);
			holder.star5_j.setVisibility(View.VISIBLE);
			
			holder.txtPending.setVisibility(View.GONE);
		}else{
			holder.star1_j.setVisibility(View.GONE);
			holder.star2_j.setVisibility(View.GONE);
			holder.star3_j.setVisibility(View.GONE);
			holder.star4_j.setVisibility(View.GONE);
			holder.star5_j.setVisibility(View.GONE);
			
			holder.txtPending.setVisibility(View.VISIBLE);
		}
	}

	private class Holder {
		public RelativeLayout container;
		public TextView jobName, txtPending;
		public ImageView nextArrow, star1_j, star2_j, star3_j, star4_j,
				star5_j;
		public RatingBar jobRating;
	}

}
