package com.crowdserviceinc.crowdservice.activity.contact;

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
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdserviceinc.crowdservice.activity.rating.RatingCustomerActivity;
import com.crowdserviceinc.crowdservice.activity.rating.RatingProviderActivity;
import com.crowdserviceinc.crowdservice.activity.ParentActivity;
import com.crowdserviceinc.crowdservice.activity.SendMessageActivity;
import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.model.JobBean;
import com.crowdserviceinc.crowdservice.model.JobData;
import com.crowdserviceinc.crowdservice.model.JobsModel;
import com.crowdserviceinc.crowdservice.model.ResultData;
import com.crowdserviceinc.crowdservice.model.UserDetail;
import com.crowdserviceinc.crowdservice.parser.JobsPostParser;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class ContactActivity extends ParentActivity implements OnClickListener {

	TextView txt_customer, txt_provider, txt_enddate, txt_title;
	Button btn_send_message, btn_complete, btn_cancel;

	private ArrayList jobsModelList = null;
	ProgressDialog progressBar;
	ArrayList<UserDetail> userarray;
	TextView btnBack;
	String jobid = "", jobname = "";
	String clientId = "";
	String otherUserName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_detail);

		init();

		if (Constants.isNetAvailable(ContactActivity.this)) {
			ViewJobProfile viewprofile = new ViewJobProfile();
			viewprofile.execute();
		} else {
			Constants.NoInternetError(ContactActivity.this);
		}
	}

	private void init() {

		userarray = new ArrayList<UserDetail>();
		jobid = getIntent().getStringExtra("data");
		txt_customer = (TextView) findViewById(R.id.txt_customer);
		txt_enddate = (TextView) findViewById(R.id.txt_enddate);
		txt_provider = (TextView) findViewById(R.id.txt_provider);
		//txt_startdate = (TextView) findViewById(R.id.txt_startdate);
		txt_title = (TextView) findViewById(R.id.txt_title);
		btnBack = (TextView) findViewById(R.id.btnBack);

		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_complete = (Button) findViewById(R.id.btn_complete);
		btn_send_message = (Button) findViewById(R.id.btn_send_message);

		btn_cancel.setOnClickListener(this);
		btn_complete.setOnClickListener(this);
		btn_send_message.setOnClickListener(this);
		btnBack.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v == btn_cancel) {
			CancelJobConfirmation();
		}
		if (v == btn_complete) {

			CompleteJobConfirmation();
		}
		if (v == btn_send_message) {
			Intent intent = new Intent(ContactActivity.this,
					SendMessageActivity.class);

			intent.putExtra("from_user_id", Constants.uid);
			intent.putExtra("to_user_id", Constants.tempuserid);
			intent.putExtra("job_id", jobid);
			intent.putExtra("to_user_name", otherUserName);

			startActivity(intent);

		}
		if (v == btnBack) {
			onBackPressed();
		}
	}

	private class ViewJobProfile extends AsyncTask<String, String, ArrayList> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(ContactActivity.this);
			progressBar.setCancelable(false);
			progressBar.setMessage("Please wait...");
			progressBar.show();
		}

		@Override
		protected ArrayList doInBackground(String... params) {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			Log.e("SAMPLE", "EX > uid. " + Constants.uid + " temp."
					+ Constants.tempuserid);
			if (Constants.is_customer) {
				nameValuePairs.add(new BasicNameValuePair("login_user_id",
						Constants.uid));
				nameValuePairs.add(new BasicNameValuePair("login_user_type",
						String.valueOf(2)));
				nameValuePairs.add(new BasicNameValuePair("other_user_id",
						Constants.tempuserid));
				nameValuePairs.add(new BasicNameValuePair("status", "A"));
			} else {
				nameValuePairs.add(new BasicNameValuePair("login_user_id",
						Constants.tempuserid));
				nameValuePairs.add(new BasicNameValuePair("login_user_type",
						String.valueOf(2)));
				nameValuePairs.add(new BasicNameValuePair("other_user_id",
						Constants.uid));
				nameValuePairs.add(new BasicNameValuePair("status", "A"));
			}

			JobsPostParser jobsParser = new JobsPostParser(Constants.HTTP_HOST
					+ "viewUserjobs");
			jobsModelList = jobsParser.getParseData(nameValuePairs);

			return jobsModelList;
		}

		@Override
		protected void onPostExecute(ArrayList result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressBar.dismiss();

			if (result != null && result.size() > 0) {
				jobsModelList = result;
				JobsModel model = (JobsModel) result.get(0);
				if (model.getJobData() != null) {
					JobData data[] = model.getJobData();
					if (data != null && data.length > 0) {
						int count = data.length;
						for (int i = 0; i < count; i++) {
							JobBean jobBean = data[i].getJob();
							if (jobid.equals(jobBean.getId())) {
								txt_enddate.setText(jobBean.getEndDate());
								//txt_startdate.setText(jobBean.getStartDate());
								txt_title.setText(jobBean.getName());
								jobname = jobBean.getName();

								if (data[i].getBidDetail().getJobPostedById()
										.equals(Constants.uid)) {
									txt_customer.setText("You");
									txt_customer.setTextColor(getResources()
											.getColor(R.color.bg_color_red));
									txt_provider.setText(data[i].getUser()
											.getUsername());
									
									otherUserName = data[i].getUser()
											.getUsername();
									clientId = Constants.uid;
								} else {
									txt_customer.setText(data[i].getUser()
											.getUsername());
									txt_provider.setText("You");
									txt_provider.setTextColor(getResources()
											.getColor(R.color.bg_color_red));
									
									otherUserName = data[i].getUser()
											.getUsername();

									clientId = data[i].getBidDetail().getJobPostedById();
								}
							}
						}
					}
				}
			}
		}
	}

	private void doCompleteRequest1() {

		final ProgressDialog progressBar = new ProgressDialog(
				ContactActivity.this);
		progressBar.setMessage("Sending...");
		progressBar.show();

		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() {
				MultipartEntity reqEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				try {

					reqEntity.addPart("job_id", new StringBody(jobid));
					reqEntity.addPart("user_id", new StringBody(Constants.uid));

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("VIPIII", "Error 3> " + e.toString());
				}

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(Constants.HTTP_HOST
						+ "completeJobCancelNotification");
				Log.d("Send", "Check Data > " + Constants.HTTP_HOST
						+ "completeJobCancelNotification");
				Log.d("Send", "job_id Data > " + jobid + " requestComplete "
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
					String strResponse = convertStreamToString(stream);
					Log.d("VIPIII", "RES> " + strResponse);
					JSONObject jsonresponse = new JSONObject(strResponse);
					resultData = new ResultData();
					resultData.setAuthenticated(jsonresponse
							.getString("status"));
					try {
						resultData.setMessage(jsonresponse.getString("message"));
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("VIPIII", "Error 1> " + e.toString());
				}
				progressBar.dismiss();

				Message msg = handler.obtainMessage();
				if (resultData != null) {
					msg.obj = resultData;
					msg.arg1 = Constants.SUCCESS;
				} else {
					msg.arg1 = Constants.FAILURE;
				}
				handler.sendMessage(msg);
			}
		});
		thr.start();
	}

	public void doCompleteRequest() {

		final ProgressDialog progressBar = new ProgressDialog(
				ContactActivity.this);
		progressBar.setMessage("Sending...");
		progressBar.show();

		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() {
				MultipartEntity reqEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					reqEntity.addPart("job_id", new StringBody(jobid));
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
//				HttpPost httppost = new HttpPost(Constants.HTTP_HOST
//						+ "requestcompleteJobNotification");

				Log.d("Send", "Check Data > " + Constants.HTTP_HOST
						+ "requestComplete");
				Log.d("Send", "job_id Data > " + jobid + " requestComplete "
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
					String strResponse = convertStreamToString(stream);
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

				Message msg = handler.obtainMessage();
				if (resultData != null) {
					msg.obj = resultData;
					msg.arg1 = Constants.SUCCESS;
					//RedirectFeedbackScreen();
				} else {
					msg.arg1 = Constants.FAILURE;
				}
				handler.sendMessage(msg);
			}
		});
		thr.start();
	}

	public void RedirectFeedbackScreen(){
		
		Log.d("Job Id", jobid);
		Log.d("completedJobuserId", Constants.tempuserid);
		Log.d("current user Id", Constants.uid);
		
		Intent jobCompleteIntent;
		if (Constants.is_customer) {
			jobCompleteIntent = new Intent(ContactActivity.this,
					RatingProviderActivity.class);
		}else{
			jobCompleteIntent = new Intent(ContactActivity.this,
					RatingCustomerActivity.class);
		}
		 		
		jobCompleteIntent.putExtra("completedJobId", jobid);
		jobCompleteIntent.putExtra("completedJobuserId", Constants.tempuserid);
		startActivity(jobCompleteIntent);
	}
	
	public String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (Exception e) {
			Log.e("HttpReaderException", ">>>" + e.getMessage());
		}
		return sb.toString();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == Constants.SUCCESS) {
				if (msg.obj instanceof ResultData) {
					ResultData data = (ResultData) msg.obj;
					if (data.getMessage().equals("refund failed")) {
						showToastMessage("Message sending failed.");
					}else if (data.getMessage().equals("unauthorized")) {
						showToastMessage("Your session is expired. Please login again.");
					} else {
						//showAlertDialog(data.getMessage(), true);

						MixpanelAPI mixpanel = MixpanelAPI.getInstance(ContactActivity.this, Constants.MIXPANEL_TOKEN);
						JSONObject completeJobEvent = new JSONObject();

						String userName = Constants.username;

						try {

							completeJobEvent.put("JobName", jobname);
							completeJobEvent.put("JobId", jobid);
							completeJobEvent.put("PostedUserId", clientId);
							completeJobEvent.put("User", userName);

							mixpanel.track("CompleteJobEvent", completeJobEvent);
						}catch (JSONException e){

						}

						RedirectFeedbackScreen();
					}
					// finish();
					// overridePendingTransition(0, R.anim.slide_top_to_bottom);
				} else {
					String message = (String) msg.obj;
					if (message.equals("refund failed")) {
						showToastMessage("Message sending failed.");
					} else {
						//showAlertDialog(message, false);
						RedirectFeedbackScreen();
					}
					// finish();
				}
			} else if (msg.arg1 == Constants.FAILURE) {
				showToastMessage("Please try again.");
			}
		}
	};

	private void showToastMessage(String s){
		Context context = ContactActivity.this.getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, s, duration);
		toast.show();
	}
	private void showCancelAlertDialog(String s) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
		builder.setTitle(R.string.app_alert_title);
		String msg = "You have cancelled your job, \"" + jobname + "\" and " + otherUserName + " has received full payment.";
		
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				finish();
			}
		});
		
		builder.create().show();
	}

	private void doCancelRequest() {

		final ProgressDialog progressBar = new ProgressDialog(
				ContactActivity.this);
		progressBar.setMessage("Sending...");
		progressBar.show();

		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() {
				MultipartEntity reqEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					reqEntity.addPart("job_id", new StringBody(jobid));
					reqEntity.addPart("user_id", new StringBody(Constants.uid));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(Constants.HTTP_HOST
						+ "cancel_job");
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
				}
				HttpEntity entity = response.getEntity();
				ResultData resultData = null;
				try {
					InputStream stream = entity.getContent();
					String strResponse = convertStreamToString(stream);
					Log.d("VIPIII", "RES> " + strResponse);
					JSONObject jsonresponse = new JSONObject(strResponse);
					resultData = new ResultData();
					resultData.setAuthenticated(jsonresponse
							.getString("status"));
					try {
						resultData.setMessage(jsonresponse.getString("message"));
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				progressBar.dismiss();

				Message msg = handler1.obtainMessage();
				if (resultData != null) {
					msg.obj = resultData;
					msg.arg1 = Constants.SUCCESS;
				} else {
					msg.arg1 = Constants.FAILURE;
				}
				handler1.sendMessage(msg);
			}
		});
		thr.start();
	}

	private Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == Constants.SUCCESS) {
				if (msg.obj instanceof ResultData) {
					ResultData data = (ResultData) msg.obj;
					if (data.getMessage().equals("refund failed")) {
						showCancelAlertDialog("Message sending failed.");
					} else {
						showCancelAlertDialog(data.getMessage());
					}
					// finish();
					// overridePendingTransition(0, R.anim.slide_top_to_bottom);
				} else {
					String message = (String) msg.obj;
					if (message.equals("refund failed")) {
						showCancelAlertDialog("Message sending failed.");
					} else {
						showCancelAlertDialog(message);
					}
					// finish();
				}
			} else if (msg.arg1 == Constants.FAILURE) {
				Toast.makeText(ContactActivity.this, "Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	void CancelJobConfirmation() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				ContactActivity.this);

		// Setting Dialog Title
		alertDialog.setTitle(R.string.app_alert_title);

		
		// Setting Dialog Message
		
		String msg = "";
		if(Constants.is_customer)
			msg = "Your job is not complete, if you cancel now " + otherUserName +" will receive full payment.";
		else
			msg = "You have not completed this job. If you cancel now, your customer will receive a full refund";
		
		alertDialog.setMessage(msg);

		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton("Cancel Anyway",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
						AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
						builder.setTitle(R.string.app_alert_title);
						builder.setMessage("Are you sure?");
						builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								
								doCancelRequest();
							}
						});
						
						builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						builder.create().show();
					}
				});
		// Setting Negative "NO" Button
		alertDialog.setNegativeButton("Never Mind",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to invoke NO event
						
						dialog.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}

	void CompleteJobConfirmation() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				ContactActivity.this);

		// Setting Dialog Title
		alertDialog.setTitle(getString(R.string.app_alert_title) + "-"
				+ jobname);

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
						Toast.makeText(getApplicationContext(),
								"You clicked on NO", Toast.LENGTH_SHORT).show();
						dialog.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}
}
