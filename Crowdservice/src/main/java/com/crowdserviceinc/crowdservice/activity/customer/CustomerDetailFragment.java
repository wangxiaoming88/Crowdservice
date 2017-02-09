package com.crowdserviceinc.crowdservice.activity.customer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.adapter.CommissionedJobListingAdapter;
import com.crowdserviceinc.crowdservice.adapter.ProfileJobAdapter;
import com.crowdserviceinc.crowdservice.widget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.crowdserviceinc.crowdservice.model.ProfileJobData;
import com.crowdserviceinc.crowdservice.model.ResultData;
import com.crowdserviceinc.crowdservice.model.UserDetail;
import com.crowdserviceinc.crowdservice.parser.ProfileJobParser;
import com.crowdserviceinc.crowdservice.parser.UserDataPostParser;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.util.ImageProcessing;

public class CustomerDetailFragment extends Fragment implements
		OnClickListener, OnTouchListener {

	// public static final int SUCCESS = 300;
	// protected static final int FAILURE = 301;
	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	public static final int SUCCESS_1 = 3;
	
	public static final int RED_STAR = 100;
	public static final int SILVER_STAR = 500;
	public static final int GOLD_STAR = 1000;
	
	/*
	 * 
	 * New code for customer profile
	 */

	List<ProfileJobData> arrayOfList;
	// private TextView temp_karma_count, temp_level_testing;
	private Button btnPublic, btnJob, btnProvider, btnContact, btnRequest;
	private FragmentManager fragmentManager;
	private TextView btnSetting, title, btnedit, textView5, user_name,
			username, txtaddress, txtemail, txttotaljobcount,
			provided_services;
	private ScrollView scrollLayout;
	private ImageView karma1, karma2, karma3, level1, level2, level3, star1_c,
			star2_c, star3_c, star4_c, star5_c, star6_c, txtshield;
	private CircleImageView image;
	private URL image_url, url;
	private Bitmap bitImg = null;
	private ProgressBar pbar;
	
	private ListView requestedJobListView, providedJobListView;
	private ProfileJobAdapter mProfileJobAdapter1, mProfileJobAdapter2;
	private Boolean isProvierJob = true;
	
	// private RatingBar ratingbar, ratingbar1;
	String[] values;
	
	ArrayList list = null;

	ArrayList<ProfileJobData> providedJobs = new ArrayList<ProfileJobData>();
	ArrayList<ProfileJobData> requestedJobs = new ArrayList<ProfileJobData>();

	private LinearLayout listItemView;
	private RelativeLayout header;
	private static String visibleList;
	/*
	 * 
	 * old code
	 */
	private ListView jobListView = null;
	private RelativeLayout listViewHeader = null;
	// private RelativeLayout profile_other;
	private ScrollView scrollView = null;
	private Button btnJobListing = null;
	private LinearLayout photocontainer = null;
	private TextView txtDone = null;
	private CommissionedJobListingAdapter mJobListingAdapter = null;

	private ArrayList<Bitmap> photos = null;
	private String userType = "2";
	private String userId = "";
	boolean onlyJob;
	private Bitmap bitmap;

	ImageView img_close;

	private DisplayImageOptions options;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// return inflater.inflate(R.layout.fragment_customer_detail, container,
		// false);
		return inflater.inflate(R.layout.activity_profile, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUi(getArguments());

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.com_facebook_profile_picture_blank_square)
				.showImageForEmptyUri(R.drawable.com_facebook_profile_picture_blank_square)
				.showImageOnFail(R.drawable.com_facebook_profile_picture_blank_square)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				//.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
				.build();
		
		requestedJobListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				requestedJobListView.setVisibility(View.GONE);
				listItemView.setVisibility(View.VISIBLE);

				ProfileJobData listItem = requestedJobs.get(position);
					

				((TextView) getView().findViewById(R.id.job_name1))
						.setText(listItem.getJobName());
				((TextView) getView().findViewById(R.id.job_category))
						.setText(listItem.getJobCategory());
				((TextView) getView().findViewById(R.id.job_feddback))
						.setText(listItem.getJobFeedback());

				// ratingbar1.setRating(Float.parseFloat(listItem.getJobRating()));
				Log.d("CustomerJobRating", listItem.getJobRating());

				float a = 0.0f;
				if (!(listItem.getJobRating().equals(""))
						&& !(listItem.getJobRating().equals(" "))
						&& (listItem.getJobRating() != null))

				{
					a = Float.valueOf(listItem.getJobRating());
				}

				else {
					a = 0.0f;
				}

				int b = Math.round(a);
				int c = (int) b;
				Log.d("a", String.valueOf(a));
				Log.d("c", String.valueOf(c));
				if (c == 5)

				{
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star3_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star4_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star5_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);

				} else if (c == 4) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star3_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star4_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);
				} else if (c == 3) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star3_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);

				} else if (c == 2) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);

					showRatingStar(true);
				}

				else if (c == 1) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);
					
				} else if (c != 0) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star3_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star4_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star5_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);
				}else{
					showRatingStar(false);
				}
			}
		});

		providedJobListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				providedJobListView.setVisibility(View.GONE);
				listItemView.setVisibility(View.VISIBLE);

				ProfileJobData listItem = providedJobs.get(position);
					

				((TextView) getView().findViewById(R.id.job_name1))
						.setText(listItem.getJobName());
				((TextView) getView().findViewById(R.id.job_category))
						.setText(listItem.getJobCategory());
				((TextView) getView().findViewById(R.id.job_feddback))
						.setText(listItem.getJobFeedback());

				// ratingbar1.setRating(Float.parseFloat(listItem.getJobRating()));
				Log.d("CustomerJobRating", listItem.getJobRating());

				float a = 0.0f;
				if (!(listItem.getJobRating().equals(""))
						&& !(listItem.getJobRating().equals(" "))
						&& (listItem.getJobRating() != null))

				{
					a = Float.valueOf(listItem.getJobRating());
				}

				else {
					a = 0.0f;
				}

				int b = Math.round(a);
				int c = (int) b;
				Log.d("a", String.valueOf(a));
				Log.d("c", String.valueOf(c));
				if (c == 5)

				{
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star3_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star4_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star5_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);

				} else if (c == 4) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star3_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star4_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);
				} else if (c == 3) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star3_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);

				} else if (c == 2) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);

				}

				else if (c == 1) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);
				} else if (c != 0) {
					((ImageView) getView().findViewById(R.id.star1_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star2_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star3_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star4_r))
							.setImageResource(R.drawable.yellowstar);
					((ImageView) getView().findViewById(R.id.star5_r))
							.setImageResource(R.drawable.yellowstar);
					
					showRatingStar(true);
				} else{
					showRatingStar(false);
				}
			}
		});
	}

	private void showRatingStar(Boolean flag){
		if(flag){
			
			((ImageView) getView().findViewById(R.id.star1_r)).setVisibility(View.VISIBLE);
			((ImageView) getView().findViewById(R.id.star2_r)).setVisibility(View.VISIBLE);
			((ImageView) getView().findViewById(R.id.star3_r)).setVisibility(View.VISIBLE);
			((ImageView) getView().findViewById(R.id.star4_r)).setVisibility(View.VISIBLE);
			((ImageView) getView().findViewById(R.id.star5_r)).setVisibility(View.VISIBLE);
		}else{
			
			((ImageView) getView().findViewById(R.id.star1_r)).setVisibility(View.GONE);
			((ImageView) getView().findViewById(R.id.star2_r)).setVisibility(View.GONE);
			((ImageView) getView().findViewById(R.id.star3_r)).setVisibility(View.GONE);
			((ImageView) getView().findViewById(R.id.star4_r)).setVisibility(View.GONE);
			((ImageView) getView().findViewById(R.id.star5_r)).setVisibility(View.GONE);
			
			
			String jobName = ((TextView) getView().findViewById(R.id.job_name1)).getText().toString() + " (Pending)";
			((TextView) getView().findViewById(R.id.job_name1)).setText(jobName);
		}
	}
	
	private void initUi(Bundle bundle) {
		
		scrollLayout = (ScrollView) getView().findViewById(R.id.scrollLayout);
		userId = bundle.getString("UserId");
		onlyJob = bundle.getBoolean("onlyJob");
		Log.e("VIP", "< C. Get ." + userId);
		userType = bundle.getString("UserType");

		photos = new ArrayList<Bitmap>();
		// listViewHeader =
		// (RelativeLayout)getView().findViewById(R.id.customerdetail_listview_header);
		img_close = (ImageView) getView().findViewById(R.id.img_close);
		img_close.setOnClickListener(this);
		
		mProfileJobAdapter1 = new ProfileJobAdapter(null, getLayoutInflater(bundle));
		mProfileJobAdapter2 = new ProfileJobAdapter(null, getLayoutInflater(bundle));
		
		providedJobListView = (ListView) getView().findViewById(R.id.provided_job_listView);
		providedJobListView.setOnTouchListener(this);
		providedJobListView.setAdapter(mProfileJobAdapter1);
		
		requestedJobListView = (ListView) getView().findViewById(R.id.requested_job_listView);
		requestedJobListView.setOnTouchListener(this);
		requestedJobListView.setAdapter(mProfileJobAdapter2);
		
		listItemView = (LinearLayout) getView().findViewById(
				R.id.list_item_view);
		karma1 = (ImageView) getView().findViewById(R.id.karma1);
		karma2 = (ImageView) getView().findViewById(R.id.karma2);
		karma3 = (ImageView) getView().findViewById(R.id.karma3);

		level1 = (ImageView) getView().findViewById(R.id.level1);
		level2 = (ImageView) getView().findViewById(R.id.level2);
		level3 = (ImageView) getView().findViewById(R.id.level3);

		image = (CircleImageView) getView().findViewById(R.id.imgView);
		// profile_other = (RelativeLayout) getView().findViewById(
		// R.id.profile_other);
		// temp_karma_count = (TextView) getView().findViewById(
		// R.id.temp_karma_count);
		user_name = (TextView) getView().findViewById(R.id.user_name);
		username = (TextView) getView().findViewById(R.id.username);
		// temp_level_testing = (TextView) getView().findViewById(
		// R.id.temp_level_testing);
		txtaddress = (TextView) getView().findViewById(R.id.txtaddress);
		txtshield = (ImageView) getView().findViewById(R.id.txtshield);
		// ratingbar1 = (RatingBar) getView().findViewById(R.id.job_rating);
		txtemail = (TextView) getView().findViewById(R.id.txtemail);
		btnProvider = (Button) getView().findViewById(R.id.btnProvider);
		btnRequest = (Button) getView().findViewById(R.id.btnRequest);
		star1_c = (ImageView) getView().findViewById(R.id.star1);
		star2_c = (ImageView) getView().findViewById(R.id.star2);
		star3_c = (ImageView) getView().findViewById(R.id.star3);
		star4_c = (ImageView) getView().findViewById(R.id.star4);
		star5_c = (ImageView) getView().findViewById(R.id.star5);
		btnProvider.setOnClickListener(this);
		btnRequest.setOnClickListener(this);
		txttotaljobcount = (TextView) getView().findViewById(
				R.id.txttotaljobcount);
		// mRatingBar = (RatingBar)getView().findViewById(R.id.ratingBar1_c);
		// close = (ImageView) getView().findViewById(R.id.close_view);
		header = (RelativeLayout) getView().findViewById(R.id.headerLayout);
		header.setVisibility(View.GONE);
		provided_services = (TextView) getView().findViewById(
				R.id.provided_services);
		provided_services.setVisibility(View.VISIBLE);
		// profile_other.setVisibility(View.INVISIBLE);

		if (Constants.isNetAvailable(getActivity())) {
			ServerCommunication server = new ServerCommunication();
			server.execute(new String[] { "" });
		} else {
			Constants.NoInternetError(getActivity());
		}
	}

	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub
		if (v == btnRequest) {

			isProvierJob = true;

			btnRequest.setSelected(true);
			btnProvider.setSelected(false);
			requestedJobListView.setVisibility(View.GONE);
			providedJobListView.setVisibility(View.VISIBLE);
			
			if (providedJobs.isEmpty()) {
				getJobList11 download1 = new getJobList11();
				download1.execute(new String[] { "" });
			} else {
			}

		} else if (v == btnProvider) {
			isProvierJob = false;

			requestedJobListView.setVisibility(View.VISIBLE);
			providedJobListView.setVisibility(View.GONE);
			
			btnProvider.setSelected(true);
			btnRequest.setSelected(false);

			if (requestedJobs.isEmpty()) {
				getJobList11 download1 = new getJobList11();
				download1.execute(new String[] { "" });
			} else {
			}
		}
		if (v == img_close) {
			listItemView.setVisibility(View.GONE);
			
			if(isProvierJob)
				providedJobListView.setVisibility(View.VISIBLE);
			else
				requestedJobListView.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * Register handler.
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == SUCCESS) {
				if (msg.obj instanceof ResultData) {
					ResultData data = (ResultData) msg.obj;
					if (data.getAuthenticated() != null
							&& data.getAuthenticated().equalsIgnoreCase(
									"success")) {
						showAlertDialog(data.getMessage());
					} else {
						showAlertDialog(data.getMessage());
					}
				}
				if (msg.obj instanceof UserDetail) {
					UserDetail data = (UserDetail) msg.obj;
					if (data != null) {
						showcustomerDetail(data);
					}
				}

			} else if (msg.arg1 == SUCCESS_1) {

				ArrayList<ProfileJobData> dataList = (ArrayList<ProfileJobData>) msg.obj;
				Log.d("dataList1 ", "dataList1 " + dataList);
				if (isProvierJob == true){
					providedJobs = dataList;
					mProfileJobAdapter1.refereshAdapter(dataList);
				}else{
					requestedJobs = dataList;
					mProfileJobAdapter2.refereshAdapter(dataList);
				}
				
				Log.e("help", "refereshAdapter Call");

			}

			else if (msg.arg1 == FAILURE) {
				showAlertDialog("Please try again.");
			}
			
			if (isProvierJob == true)
				setListViewHeightBasedOnChildren(providedJobListView);
			else
				setListViewHeightBasedOnChildren(requestedJobListView);
		}
	};

	private void showAlertDialog(String s) {
		Context context = getActivity().getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, s, duration);
		toast.show();
	}

	private void showcustomerDetail(UserDetail userDetail) {

		// To update the security shield
		if (userDetail.getSecurity().equals("blue"))
			txtshield.setImageResource(R.drawable.shieldx);
		else if (userDetail.getSecurity().equals("silver"))
			txtshield.setImageResource(R.drawable.shield_silver);
		else if (userDetail.getSecurity().equals("gold"))
			txtshield.setImageResource(R.drawable.shield_gold);

		username.setText(userDetail.getUsername() + " | ");
		user_name.setText(userDetail.getFirst_name() + " "
				+ userDetail.getLast_name());
		// temp_karma_count.setText(userDetail.getKarmaCount() + " Points");
		// temp_level_testing.setText(userDetail.getSJCount() + " Points");
		txtaddress.setText(" " + userDetail.getAddress3() + ", "
				+ userDetail.getAddress4());
		txtemail.setText(userDetail.getEmail());
		btnProvider.setText(userDetail.getCJCount() + " Services Provided");
		btnRequest.setText(userDetail.getSJCount() + " Services Requested");

		int total_count, temp1, temp2;
		if (userDetail.getSJCount() != "")
			temp1 = Integer.valueOf(userDetail.getSJCount());
		else
			temp1 = 0;

		if (userDetail.getCJCount() != "")
			temp2 = Integer.valueOf(userDetail.getCJCount());
		else
			temp2 = 0;

		total_count = Integer.valueOf(temp1) + Integer.valueOf(temp2);

		txttotaljobcount.setText("(" + total_count + ")");
		
		showLevel(total_count);
		
		if (!userDetail.getKarmaCount().equals(""))
			showKarma(userDetail.getKarmaCount());

		if (userDetail.getAvg_rating() != "") {
			float a = Float.parseFloat(userDetail.getAvg_rating());
			int b = Math.round(a);
			int c = (int) b;
			Log.d("a", String.valueOf(a));
			Log.d("c", String.valueOf(c));
			if (c == 0) {
				star1_c.setImageResource(R.drawable.star);
				star2_c.setImageResource(R.drawable.star);
				star3_c.setImageResource(R.drawable.star);
				star4_c.setImageResource(R.drawable.star);
				star5_c.setImageResource(R.drawable.star);
			} else if (c == 5) {
				star1_c.setImageResource(R.drawable.yellowstar);
				star2_c.setImageResource(R.drawable.yellowstar);
				star3_c.setImageResource(R.drawable.yellowstar);
				star4_c.setImageResource(R.drawable.yellowstar);
				star5_c.setImageResource(R.drawable.yellowstar);

			} else if (c == 4) {
				star1_c.setImageResource(R.drawable.yellowstar);
				star2_c.setImageResource(R.drawable.yellowstar);
				star3_c.setImageResource(R.drawable.yellowstar);
				star4_c.setImageResource(R.drawable.yellowstar);
			} else if (c == 3) {
				star1_c.setImageResource(R.drawable.yellowstar);
				star2_c.setImageResource(R.drawable.yellowstar);
				star3_c.setImageResource(R.drawable.yellowstar);

			} else if (c == 2) {
				star1_c.setImageResource(R.drawable.yellowstar);
				star2_c.setImageResource(R.drawable.yellowstar);
			}

			else if (c == 1) {
				star1_c.setImageResource(R.drawable.yellowstar);
			}

		}
		// mRatingBar.setRating(Float.valueOf(userDetail.getAvg_rating()));

		ImageLoader.getInstance().displayImage(userDetail.getImage_big(), image, options);
		
		/*
		if (bitmap != null) {

			image.setImageBitmap(Constants.getRoundedCornerImage(bitmap,
					image.getWidth(), image.getHeight()));

		} else {
			Bitmap image_bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.com_facebook_profile_picture_blank_square);
			image.setImageBitmap(Constants.getRoundedCornerImage(image_bitmap,
					image.getWidth(), image.getHeight()));

		}
		*/
		
		// To display the preferred services of a user
		String mstring = getString(R.string.detailstr) + " "
				+ userDetail.getcouriercategory() + ',' + ' '
				+ userDetail.gethomecategory();
		provided_services.setText(mstring);

		if (userDetail.getcouriercategory().equals("")
				&& userDetail.gethomecategory().equals("")) {
			provided_services.setVisibility(View.GONE);
		}

	}

	private class getJobList11 extends
			AsyncTask<String, String, ArrayList<ProfileJobData>> {
		private final ProgressDialog progressBar = new ProgressDialog(
				getActivity());

		public getJobList11() {
			// TODO Auto-generated constructor stub
			progressBar.setCancelable(false);
		}

		@Override
		protected ArrayList<ProfileJobData> doInBackground(String... strParams) {
			ProfileJobParser parser;

			Log.d("button", isProvierJob + "");

			if (isProvierJob) {

				parser = new ProfileJobParser(Constants.HTTP_HOST
						+ "getProviderCompletedJob");
			} else {
				parser = new ProfileJobParser(Constants.HTTP_HOST
						+ "getCustomerCompletedJob");
			}
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("user_id", userId));
			Log.d("button1", isProvierJob + "");
			
			String param = "completed_provider_job";
			if(isProvierJob){
				param = "completed_provider_job";
			}else{
				param = "completed_customer_job";
			}
			ArrayList<ProfileJobData> dataList = parser.getParseData(nameValuePairs, param);

			return dataList;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setMessage("Please wait while loading...");
			progressBar.show();

		}

		@Override
		protected void onPostExecute(ArrayList<ProfileJobData> dataList) {
			super.onPostExecute(dataList);
			
			if(progressBar != null & progressBar.isShowing())
				progressBar.dismiss();
			
			Collections.sort(dataList, new ProfileJobDataComparator());
			Collections.reverse(dataList);
			
			Message msg = handler.obtainMessage();
			msg.arg1 = SUCCESS_1;
			msg.obj = dataList;
			handler.sendMessage(msg);
		}
	}

	class ProfileJobDataComparator implements Comparator<ProfileJobData>
	{
	    public int compare(ProfileJobData left, ProfileJobData right) {
	        return left.getJobId().compareTo(right.getJobId());
	    }
	}
	
	public class ServerCommunication extends
			AsyncTask<String, String, UserDetail> {
		private final ProgressDialog progressBar = new ProgressDialog(
				getActivity());

		public ServerCommunication() {
			progressBar.setCancelable(false);
		}

		@Override
		protected UserDetail doInBackground(String... strParams) {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Log.e("VIP", " Condition >> " + onlyJob);
			if (!onlyJob)
				nameValuePairs.add(new BasicNameValuePair("id", userId));
			else
				nameValuePairs.add(new BasicNameValuePair("user_id", userId));
			// nameValuePairs.add(new BasicNameValuePair("user_type",
			// userType));

			Log.e("VIP", "Profile > " + Constants.HTTP_HOST + "viewprofile"
					+ " ID " + userId);
			UserDataPostParser parser = new UserDataPostParser(
					Constants.HTTP_HOST + "viewprofile");
			UserDetail data = parser.getParseData(nameValuePairs);
			if (data != null) {
				bitmap = ImageProcessing.downloadImage(data.getImage_big());
			}
			Log.d("USER NAME", "" + data.getUsername());
			return data;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setMessage("Please wait while loading...");
			progressBar.show();
		}

		@Override
		protected void onPostExecute(UserDetail data) {
			super.onPostExecute(data);
			progressBar.dismiss();
			scrollLayout.setVisibility(View.VISIBLE);

			Message msg = handler.obtainMessage();
			msg.arg1 = SUCCESS;
			msg.obj = data;
			handler.sendMessage(msg);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return false;
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
						LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		Log.e("VIP", "totalHeight Call > " + totalHeight);
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	private void showKarma(String strKarCount) {

		int point = Integer.parseInt(strKarCount);
		if (0 == point) {
			karma1.setImageResource(R.drawable.crown_d);
			karma2.setImageResource(R.drawable.crown_10_d);
			karma3.setImageResource(R.drawable.crown_20_d);
		} else if (1 <= point && point <= 9) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_10_d);
			karma3.setImageResource(R.drawable.crown_20_d);

		} else if (10 <= point && point <= 19) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_10);
			karma3.setImageResource(R.drawable.crown_20_d);

		} else if (20 <= point && point <= 29) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_20);
			karma3.setImageResource(R.drawable.crown_30_d);

		} else if (30 <= point && point <= 39) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_30);
			karma3.setImageResource(R.drawable.crown_40_d);

		} else if (40 <= point && point <= 49) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_40);
			karma3.setImageResource(R.drawable.crown_50_d);

		} else if (50 <= point && point <= 59) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_50);
			karma3.setImageResource(R.drawable.crown_60_d);

		} else if (60 <= point && point <= 69) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_60);
			karma3.setImageResource(R.drawable.crown_70_d);

		} else if (70 <= point && point <= 79) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_70);
			karma3.setImageResource(R.drawable.crown_80_d);

		} else if (80 <= point && point <= 89) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_80);
			karma3.setImageResource(R.drawable.crown_90_d);

		} else if (90 <= point && point <= 99) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_90);
			karma3.setImageResource(R.drawable.crown_silver_d);

		} else if (100 <= point && point <= 199) {

			karma1.setImageResource(R.drawable.crown);
			karma2.setImageResource(R.drawable.crown_silver);
			karma3.setImageResource(R.drawable.crown_gold_d);

		} else if (200 <= point && point <= 299) {

			karma1.setImageResource(R.drawable.crown_silver);
			karma2.setImageResource(R.drawable.crown_gold);
			karma3.setImageResource(R.drawable.crown_red_d);

		} else if (300 <= point) {
			karma1.setImageResource(R.drawable.crown_silver);
			karma2.setImageResource(R.drawable.crown_gold);
			karma3.setImageResource(R.drawable.crown_red);

		}
	}

	private void showLevel(String strLvlCount) {

		int point = Integer.parseInt(strLvlCount);
		showLevel(point);
	}

	private void showLevel(int point) {

		Log.e("VIPI", "Point >> " + point);
		if (0 == point) {
			Log.e("VIPI", "Point 1>> " + point);
			level1.setImageResource(R.drawable.chalice_d);
			level2.setImageResource(R.drawable.chalice_10_d);
			level3.setImageResource(R.drawable.chalice_20_d);

		} else if (1 <= point && point <= 9) {
			Log.e("VIPI", "Point 2>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_10_d);
			level3.setImageResource(R.drawable.chalice_20_d);

		} else if (10 <= point && point <= 19) {
			Log.e("VIPI", "Point 3>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_10);
			level3.setImageResource(R.drawable.chalice_20_d);

		} else if (20 <= point && point <= 29) {
			Log.e("VIPI", "Point 4>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_20);
			level3.setImageResource(R.drawable.chalice_30_d);

		} else if (30 <= point && point <= 39) {
			Log.e("VIPI", "Point 5>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_30);
			level3.setImageResource(R.drawable.chalice_40_d);

		} else if (40 <= point && point <= 49) {
			Log.e("VIPI", "Point 6>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_40);
			level3.setImageResource(R.drawable.chalice_50_d);

		} else if (50 <= point && point <= 59) {
			Log.e("VIPI", "Point 7>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_50);
			level3.setImageResource(R.drawable.chalice_60_d);

		} else if (60 <= point && point <= 69) {
			Log.e("VIPI", "Point 8>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_60);
			level3.setImageResource(R.drawable.chalice_70_d);

		} else if (70 <= point && point <= 79) {
			Log.e("VIPI", "Point 9>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_70);
			level3.setImageResource(R.drawable.chalice_80_d);

		} else if (80 <= point && point <= 89) {
			Log.e("VIPI", "Point 10>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_80);
			level3.setImageResource(R.drawable.chalice_90_d);

		} else if (90 <= point && point < RED_STAR) {
			Log.e("VIPI", "Point 11>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_90);
			level3.setImageResource(R.drawable.chalice_red_d);

		} else if (RED_STAR <= point && point < SILVER_STAR) {
			Log.e("VIPI", "Point 12>> " + point);
			level1.setImageResource(R.drawable.chalice);
			level2.setImageResource(R.drawable.chalice_red);
			level3.setImageResource(R.drawable.chalice_silver_d);

		} else if (SILVER_STAR <= point && point < GOLD_STAR) {
			Log.e("VIPI", "Point 13>> " + point);
			level1.setImageResource(R.drawable.chalice_red);
			level2.setImageResource(R.drawable.chalice_silver);
			level3.setImageResource(R.drawable.chalice_gold_d);

		} else if (GOLD_STAR <= point) {
			Log.e("VIPI", "Point 14>> " + point);
			level1.setImageResource(R.drawable.chalice_red);
			level2.setImageResource(R.drawable.chalice_silver);
			level3.setImageResource(R.drawable.chalice_gold);

		}

	}
}