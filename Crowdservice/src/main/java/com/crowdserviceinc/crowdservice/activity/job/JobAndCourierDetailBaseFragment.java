package com.crowdserviceinc.crowdservice.activity.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.model.JobBean;
import com.crowdserviceinc.crowdservice.model.ResultData;
import com.crowdserviceinc.crowdservice.parser.FundPostParser;
import com.crowdserviceinc.crowdservice.parser.JobbidParser;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.util.Utils;
import com.crowdserviceinc.crowdservice.activity.FindGoferActivity.BackListener;
import com.crowdserviceinc.crowdservice.activity.contact.ContactActivity;
import com.crowdserviceinc.crowdservice.activity.customer.CustomerDetailFragment;
import com.crowdserviceinc.crowdservice.activity.paypal.PaypalWebviewAcivity;
import com.crowdserviceinc.crowdservice.activity.rating.RatingCustomerActivity;
import com.crowdserviceinc.crowdservice.activity.rating.RatingProviderActivity;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class JobAndCourierDetailBaseFragment extends Fragment implements
		BackListener, OnClickListener {
	private JobBean currentJob = null;
	// private JobData jobDetail = null;
	private int x = 0;
	private static final int APPLY_JOB = 102;
	private String jobId = "";
	public static String courierId = "";
	private String tempcourierId = "";
	private RadioButton customerbtn = null;
	private RadioButton jobDetailBtn = null;
	private LinearLayout header = null;
	private Button acceptBid = null, attachment = null, btn_contact, btnComplete;
	private Boolean jobAccept = false;
	RelativeLayout relative_option;
	LinearLayout activeLayout;

	String jobName, postedId;
	public static JobAndCourierDetailBaseFragment self = null;

	private boolean paypalShow = false;
	private String mMerch, mService, mPayKey, Status = "";
	boolean onlyJob;

	private boolean isFromCustomer = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("SAMPLE", " JobAndCourierDetailBaseFragment CALL ");
		return inflater.inflate(R.layout.fragment_job_and_courier_detail_base,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		self = this;
		iniUi(getArguments());

		// ((ViewJobsActivity) getActivity()).setBackListener(this);

	}

	private void iniUi(Bundle arguments) {
		
		btn_contact = (Button) getView().findViewById(R.id.btn_contact);
		btnComplete = (Button) getView().findViewById(R.id.btnComplete);
		
		header = (LinearLayout) getView().findViewById(R.id.header);
		activeLayout = (LinearLayout) getView().findViewById(R.id.active_layout);
		
		acceptBid = (Button) getView().findViewById(R.id.accept_for_bid);
		acceptBid.setOnClickListener(this);
		customerbtn = (RadioButton) getView().findViewById(
				R.id.btnCustomerDetails);
		customerbtn.setOnClickListener(this);
		relative_option = (RelativeLayout) getView().findViewById(
				R.id.relative_option);
		attachment = (Button) getView().findViewById(R.id.attachment);
		attachment.setOnClickListener(this);
		
		jobDetailBtn = (RadioButton) getView().findViewById(R.id.btnJobDetails);
		jobDetailBtn.setOnClickListener(this);

		btn_contact.setOnClickListener(this);
		btnComplete.setOnClickListener(this);
		
		isFromCustomer = arguments.getBoolean("fromCustomer", true);


		jobName = arguments.getString("job_name", "");
		postedId = arguments.getString("posted_id", "");

		jobId = arguments.getString("data", "");
		tempcourierId = arguments.getString("courierId", "");
		courierId = arguments.getString("courierId", "");
		Status = arguments.getString("status", "");
		
		// Only for Customers
		x = arguments.getInt("bidCount", 0);
		jobAccept = arguments.getBoolean("jobAccept", false);
		mMerch = arguments.getString("merch", "");
		mService = arguments.getString("service", "");
		onlyJob = arguments.getBoolean("onlyJobDetail", false);
		
		//attachment.setVisibility(View.INVISIBLE);
		
		if(isFromCustomer){
			((ViewJobsActivity) getActivity()).setTitle("Job and Provider");
			customerbtn.setText("Provider Details");
			
			if (onlyJob) {
				hideAcceptBid(true);
				hideHeader(true);
			}
		}else{
			((ViewJobsActivity) getActivity()).setTitle("Job and Customer");
			customerbtn.setText("Customer Details");
		}
		// Show Contact Button or not
		
		if (Status.equals("A")) {
			activeLayout.setVisibility(View.VISIBLE);				
		}else {
			activeLayout.setVisibility(View.GONE);
		}
		
		acceptBid.setVisibility(View.GONE);
		
		jobDetailBtn.performClick();
		
		
	}

	@Override
	public void backPressed() {

		FragmentManager fm = getFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
		} else
			getActivity().finish();

		((ViewJobsActivity) getActivity()).setBackListener(null);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.accept_for_bid) {

			if (!paypalShow) {
				paypalShow = true;
			} else {
				return;
			}

			try {

				if (Constants.isNetAvailable(getActivity())) {
					ServerCommunicateFund servicefund = new ServerCommunicateFund();
					servicefund.execute();
				} else {
					Constants.NoInternetError(getActivity());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// ServerCommuniBidjob download=new ServerCommuniBidjob();
			// download.execute(new String[]{""});
		} else if (v == attachment) {

			Intent intent = new Intent(getActivity(), JobImageActivity.class);
			intent.putExtra("job_image_url", (String) attachment.getTag());
			intent.putExtra("job_name", "");
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.slide_bottom_to_top,
					0);
		} else if (v.getId() == R.id.btnCustomerDetails) {

			FragmentTransaction ft = getChildFragmentManager()
					.beginTransaction();

			Fragment fragmentpublic = new CustomerDetailFragment();
			Bundle bundle = new Bundle();

			if (!onlyJob && tempcourierId != "")
				courierId = tempcourierId;
			bundle.putString("UserId", courierId);
			
			if(isFromCustomer){
				bundle.putString("UserType", "3");
				bundle.putBoolean("onlyjob", onlyJob);

			}else{
				bundle.putString("UserType", "2");
			}
			
			fragmentpublic.setArguments(bundle);
			ft.replace(R.id.job_courier_detail_container, fragmentpublic,
					"CustomerDetailFragment");
			ft.commit();

			jobDetailBtn.setSelected(false);
			customerbtn.setSelected(true);
			relative_option.setVisibility(View.GONE);
			// customerbtn.setBackgroundResource(R.drawable.rightcorner);
			// jobDetailBtn
			// .setBackgroundResource(R.drawable.leftcorner_unselected);
			// ((ViewJobsActivity) getActivity()).setTitle("Courier Details");
			
			// if (x == 0)
			// ((ViewJobsActivity) getActivity()).SetBackText("Back");
			// else
			// ((ViewJobsActivity) getActivity())
			// .SetBackText("List of Couriers");
		} else if (v.getId() == R.id.btnJobDetails) {
			FragmentTransaction ft = getChildFragmentManager()
					.beginTransaction();

			Fragment fragmentpublic = new JobDetailFragment();
			Bundle bundle = new Bundle();
			bundle.putString("data", jobId);
			bundle.putInt("biduser", x);
			bundle.putBoolean("jobAccept", jobAccept);
			
			if(isFromCustomer){
				bundle.putInt("UserType", 3);
			}else{
				bundle.putInt("UserType", 2);
			}
			
			fragmentpublic.setArguments(bundle);
			ft.replace(R.id.job_courier_detail_container, fragmentpublic,
					"JobDetailFragment");
			ft.commit();

			// customerbtn
			// .setBackgroundResource(R.drawable.rightcorner_unselected);
			// jobDetailBtn.setBackgroundResource(R.drawable.leftcorner);
			relative_option.setVisibility(View.VISIBLE);
			jobDetailBtn.setSelected(true);
			customerbtn.setSelected(false);
		}else if (v == btn_contact) {
			Intent intent = new Intent(getActivity(),
					ContactActivity.class);
			intent.putExtra("data", jobId);
			
			Constants.tempuserid = courierId;

			Log.e("VIPVIP", "Redirect " + jobId);
			startActivity(intent);
		}else if (v == btnComplete) {
			completeJobConfirmation();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e("VIPI", "Call handleMessage");
			if (msg.what == APPLY_JOB && msg.arg1 == Constants.SUCCESS) {
				backPressed();
				// backPressed();
			}
		}
	};

	private class ServerCommuniBidjob extends AsyncTask<String, String, String> {

		private final ProgressDialog progressBar = new ProgressDialog(
				getActivity());

		@Override
		protected String doInBackground(String... strParams) {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("job_id", jobId));
			nameValuePairs.add(new BasicNameValuePair("user_id", courierId));
			nameValuePairs.add(new BasicNameValuePair("pay_key", mPayKey));
			JobbidParser parser = new JobbidParser(Constants.HTTP_HOST
					+ "UpdateBid");
			String dataList = parser.getParseData(nameValuePairs);

			return dataList;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setCancelable(false);
			progressBar.setMessage("Please wait while loading...");
			progressBar.show();
		}

		@Override
		protected void onPostExecute(String dataList) {
			super.onPostExecute(dataList);
			progressBar.dismiss();

			Message msg = handler.obtainMessage();
			msg.arg1 = Constants.SUCCESS;
			msg.what = APPLY_JOB;
			msg.obj = dataList;
			handler.sendMessage(msg);
		}
	}

	public void setJobDetail(JobBean job){
		currentJob = job;
	}
	
	public void hideHeader(boolean flag) {
		if (flag)
			header.setVisibility(View.GONE);
		else
			header.setVisibility(View.VISIBLE);
	}

	public void hideAcceptBid(boolean flag) {
		if (flag)
			acceptBid.setVisibility(View.GONE);
		else
			acceptBid.setVisibility(View.VISIBLE);
	}

	public void showContact(boolean flag){
		if (flag) {
			activeLayout.setVisibility(View.VISIBLE);				
		}else {
			activeLayout.setVisibility(View.GONE);
		}
	}
	public void enableAttachment(boolean flag, String url) {
		
		/*
		if (flag) {
			if (url != null && url.length() > 0) {
				attachment.setVisibility(View.VISIBLE);
				attachment.setTag(url);
			}

		} else {
			attachment.setVisibility(View.GONE);
			attachment.setTag(url);
		}
		*/
	}

	private class ServerCommunicateFund extends
			AsyncTask<String, String, String> {

		private final ProgressDialog progressBar = new ProgressDialog(
				getActivity());

		@Override
		protected String doInBackground(String... strParams) {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("job_id", jobId));
			nameValuePairs.add(new BasicNameValuePair("user_id", courierId));
			FundPostParser parser = new FundPostParser(Constants.HTTP_HOST
					+ "fund");
			String dataList = parser.getParseData(nameValuePairs);

			return dataList;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setCancelable(false);
			progressBar.setMessage("Please wait...");
			progressBar.show();
		}

		@Override
		protected void onPostExecute(String dataList) {
			super.onPostExecute(dataList);
			progressBar.dismiss();
			if (!dataList.equals("")) {
				Intent intent = new Intent(getActivity(),
						PaypalWebviewAcivity.class);
				intent.putExtra("url", dataList);
				startActivity(intent);
			}
		}
	}
	
	void completeJobConfirmation() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

		String jobName = jobId;
		if(currentJob != null)
			jobName = currentJob.getName();
		// Setting Dialog Title
		alertDialog.setTitle(getString(R.string.app_alert_title) + "-"
				+ jobName);

		// Setting Dialog Message
		alertDialog.setMessage("Are you sure you want Complete this job?");

		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						doCompleteRequest();
					}
				});
		// Setting Negative "NO" Button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to invoke NO event
						Toast.makeText(getActivity().getApplicationContext(),
								"You clicked on NO", Toast.LENGTH_SHORT).show();
						dialog.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}
	
	public void doCompleteRequest() {

		final ProgressDialog progressBar = new ProgressDialog(
				getActivity());
		progressBar.setMessage("Sending...");
		progressBar.show();

		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() {
				MultipartEntity reqEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					reqEntity.addPart("job_id", new StringBody(jobId));
					reqEntity.addPart("user_id", new StringBody(Constants.uid));

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("VIPIII", "Error 3> " + e.toString());
				}

				HttpClient httpclient = new DefaultHttpClient();
				 HttpPost httppost = new HttpPost(Constants.HTTP_HOST
				 + "requestComplete");
				 
				httppost.setHeader("Cookie", Constants.cookie);

				Log.d("Send", "job_id Data > " + jobId + " requestComplete "
						+ " > user_id > " + Constants.uid);
				httppost.setEntity(reqEntity);
				// Execute HTTP Post Request
				HttpResponse response = null;
				try {
					response = httpclient.execute(httppost);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("VIPIII", "Error> " + e.toString());
				}
				HttpEntity entity = response.getEntity();
				ResultData resultData = null;
				try {
					InputStream stream = entity.getContent();
					String strResponse = Utils.convertStreamToString(stream);
					Log.d("Send", "RES> " + strResponse);
					JSONObject jsonresponse = new JSONObject(strResponse);
					
					if(jsonresponse.has("error")){
						resultData = new ResultData();
						resultData.setMessage(jsonresponse.getString("error"));
					}else{
						if(jsonresponse.has("status")){
							resultData = new ResultData();
							resultData.setAuthenticated(jsonresponse
									.getString("status"));
							
							try {

								MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), Constants.MIXPANEL_TOKEN);
								JSONObject completeJobEvent = new JSONObject();

								String userName = Constants.username;

								try {

									completeJobEvent.put("JobName", jobName);
									completeJobEvent.put("JobId", jobId);
									completeJobEvent.put("PostedUserId", postedId);
									completeJobEvent.put("User", userName);

									mixpanel.track("CompleteJobEvent", completeJobEvent);
								}catch (JSONException e){

								}

								resultData.setMessage(jsonresponse.getString("message"));
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("Send", "Error 1> " + e.toString());
				}
				progressBar.dismiss();

				Message msg = completeHandler.obtainMessage();
				if (resultData != null) {
					msg.obj = resultData;
					msg.arg1 = Constants.SUCCESS;
					//RedirectFeedbackScreen();
				} else {
					msg.arg1 = Constants.FAILURE;
				}
				completeHandler.sendMessage(msg);
			}
		});
		thr.start();
	}
	
	

	private Handler completeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == Constants.SUCCESS) {
				if (msg.obj instanceof ResultData) {
					ResultData data = (ResultData) msg.obj;
					if (data.getMessage().equals("refund failed")) {
						showToastMessage("Message sending failed.");
					} else if (data.getMessage().equals("unauthorized")) {
						showToastMessage("Your session is expired. Please login again.");
					} else {
						//showAlertDialog(data.getMessage(), true);
						
						redirectFeedbackScreen();
					}
					// finish();
					// overridePendingTransition(0, R.anim.slide_top_to_bottom);
				} else {
					String message = (String) msg.obj;
					if (message.equals("refund failed")) {
						showToastMessage("Message sending failed.");
					} else {
						//showAlertDialog(message, false);
						redirectFeedbackScreen();
					}
					// finish();
				}
			} else if (msg.arg1 == Constants.FAILURE) {
				Toast.makeText(getActivity(), "Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public void redirectFeedbackScreen(){
		
		Log.d("Job Id", jobId);
		Log.d("other user Id", courierId);
		Log.d("current user Id", Constants.uid);
		
		Intent jobCompleteIntent;
		if (Constants.is_customer) {
			jobCompleteIntent = new Intent(getActivity(),
					RatingProviderActivity.class);
		}else{
			jobCompleteIntent = new Intent(getActivity(),
					RatingCustomerActivity.class);
		}
		
		jobCompleteIntent.putExtra("completedJobId", jobId);
		jobCompleteIntent.putExtra("completedJobuserId", courierId);
		getActivity().startActivity(jobCompleteIntent);
	}

	private void showToastMessage(String s) {
		Context context = getActivity().getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, s, duration);
		toast.show();
	}
}
