package com.crowdserviceinc.crowdservice.activity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.activity.job.JobImageActivity;
import com.crowdserviceinc.crowdservice.activity.login.LoginActivity;
import com.crowdserviceinc.crowdservice.activity.paypal.PayPalDetailsActivity;
import com.crowdserviceinc.crowdservice.activity.paypal.StripeRegistrationActivity;
import com.crowdserviceinc.crowdservice.model.ResultData;
import com.crowdserviceinc.crowdservice.parser.DataPostParser;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class ReviewFragment extends Fragment implements OnClickListener {

	private TextView txtCategoryName, txtJobName, txtJobPrice,
			txtStartingAddress, txtEndAddress, txtEndDate,
			txtComment;
	private RelativeLayout rlImageAttach;
	private Button btnRequest, btnPrevious;
	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	public static final int USER_NOT_EXIST = 3;
	public static final int HAS_STRIPE_KEY = 4;
	private Bundle bundle;
	private ScrollView scrollview;
	private WebView webView;
	boolean loadingFinished = true;
	boolean redirect = false;
	private ProgressDialog progressBar;
	ImageView img_attach;

    private MixpanelAPI mixpanel;

	public ReviewFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initViews();
		Constants.hasStripeRegistered = false;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.fragment_review, container, false);

		mixpanel = MixpanelAPI.getInstance(getActivity(), Constants.MIXPANEL_TOKEN);

		return view;
	}

	/*
	 * Called to initialize to user interface.
	 */
	private void initViews() {
		txtCategoryName = (TextView) getView().findViewById(
				R.id.txtCategoryName);
		txtJobName = (TextView) getView().findViewById(R.id.txtJobName);
		txtJobPrice = (TextView) getView().findViewById(R.id.txtJobPrice);
		txtStartingAddress = (TextView) getView().findViewById(
				R.id.txtStartingAddress);
//		txtStartDate = (TextView) getView().findViewById(R.id.txtStartDate);
		txtEndAddress = (TextView) getView().findViewById(R.id.txtEndAddress);
		txtEndDate = (TextView) getView().findViewById(R.id.txtEndDate);
		txtComment = (TextView) getView().findViewById(R.id.txtComment);

		img_attach = (ImageView) getView().findViewById(R.id.img_attach);

		scrollview = (ScrollView) getView().findViewById(R.id.txtScrollView);

		btnRequest = (Button) getView().findViewById(R.id.btnRequest);
		btnPrevious = (Button) getView().findViewById(R.id.btnPrevious);
		btnRequest.setOnClickListener(this);
		btnPrevious.setOnClickListener(this);

		bundle = getArguments();
		txtCategoryName.setText(bundle.getString("category_name"));
		txtJobName.setText(bundle.getString("JobName"));
		txtJobPrice.setText(bundle.getString("Price"));
		txtStartingAddress.setText(bundle.getString("Address"));
//		txtStartDate.setText(bundle.getString("Date"));
		txtEndAddress.setText(bundle.getString("EndAddress"));
		txtEndDate.setText(bundle.getString("EndDate"));
		txtComment.setText(bundle.getString("Comment"));
		// Constants.STRIPE_PUBLISHABLE_KEY = getStripePublicKey();

		//img_attach.setOnClickListener(this);

		rlImageAttach = (RelativeLayout) getView().findViewById(R.id.rlthumbnail);
		try {
			if (bundle.getString("imgpath").equals("")) {
				rlImageAttach.setVisibility(View.GONE);
			}else{
				rlImageAttach.setVisibility(View.VISIBLE);
				Bitmap resized = Bitmap.createScaledBitmap(Constants.imageComment, 162, 182, true);
				img_attach.setImageBitmap(resized);
			}
		} catch (Exception e) {
			// TODO: handle exception
			//img_attach.setVisibility(View.GONE);
			rlImageAttach.setVisibility(View.GONE);
		}

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		mixpanel.flush();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnRequest) {
			if (bundle != null) {
				doSubmit(bundle);
			}
		} else if (v == btnPrevious) {
			onBack();
		}

		else if (v == img_attach) {
			Intent intent = new Intent(getActivity(), JobImageActivity.class);
			intent.putExtra("job_name", bundle.getString("JobName"));
			intent.putExtra("job_image_url", bundle.getString("imgpath"));
			startActivity(intent);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (progressBar != null) {
			if (progressBar.isShowing())
				progressBar.dismiss();
		}
		Log.d("Constants.hasStripeRegistered   "
				+ Constants.hasStripeRegistered, "" + bundle);
		if (bundle != null && Constants.hasStripeRegistered) {
			doSubmit(bundle);
		}
	}

	/*
	 * Handler.
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
						// Alert Dialog Start here

						// showAlertDialog(data.getMessage());
						
						String message = "Your job '" + data.getJobName() + "' is now Live!";
						//showAlert(data.getMessage());
						showAlert(message);
					} else {
						// showAlertDialog(data.getMessage());
						//showAlert(data.getMessage());
						String message = "Your job '" + data.getJobName() + "' is now Live!";
						//showAlert(data.getMessage());
						showAlert(message);
					}
				}
			} else if (msg.arg1 == FAILURE) {
				Toast.makeText(getActivity(), "Please try again.",
						Toast.LENGTH_SHORT).show();
			} else if (msg.arg1 == USER_NOT_EXIST) {
				// getStripePublicKey(); //STRIPE
				// openUserRegistration();
				openPaypalRegistration(); // PAYPAL
			}
		}
	};

	private void showAlert(String message) {
		new AlertDialog.Builder(getActivity()).setTitle("Job Add")
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// continue with delete
						getActivity().finish();
					}
				}).setIcon(android.R.drawable.ic_dialog_info).show();
	}

	private void openPaypalRegistration() {
		Intent intent = new Intent(getActivity(), PayPalDetailsActivity.class);
		startActivity(intent);
	}

	/*
	 * To Submit Record.
	 */
	private void doSubmit(final Bundle bundle) {
		progressBar = new ProgressDialog(getActivity());
		progressBar.setMessage("Please wait while loading...");
		progressBar.setCancelable(false);
		progressBar.show();

		Constants.TempEndAddress = "";
		Constants.TempEndTime = "";
		Constants.TempAddress = "";
		Constants.TempTime = "";
		Constants.TempImagePath = "";

		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("user_id",
						Constants.uid));


//				JSONObject postedJob = new JSONObject();
//
//					SharedPreferences loginDB = getActivity().getSharedPreferences(
//							LoginActivity.LOGINDATA, 0);
//
//					String userId = Constants.uid;
//					String price = bundle.getString("Price");
//					String categoryName = bundle.getString("CategoryName");
//					String date = bundle.getString("Date");
//					String cookie = Constants.cookie;
//					String userName = Constants.username;
//
//					try {
//						postedJob.put("User", userName);
//						postedJob.put("UserId", userId);
////					postedJob.put("Price", price);
//						postedJob.put("CategoryName", categoryName);
////					postedJob.put("Date", date);
//						mixpanel.track("JobPostEvent", postedJob);
//					}catch (JSONException e){
//
//					}


				// DataPostParser parser1 = new
				// DataPostParser(Constants.HTTP_HOST+"getCutomerid"); //STRIPE
				DataPostParser parser1 = new DataPostParser(Constants.HTTP_HOST
						+ "getPaypalId"); // PAYPAL

				ResultData postdata1 = parser1.getParseData(nameValuePairs);
				try {
					Constants.uemail = postdata1.getUserEmail();
				} catch (Exception e) {

				}

				if ((postdata1.getAuthenticated().equals("success")) // really comeback
						&& (postdata1.getMessage().equals("Paypal Id found"))) { // PAYPAL
					Constants.uemail = postdata1.getUserEmail();

					// @Ashish
					MultipartEntity reqEntity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);
					try {
						reqEntity.addPart("amount",
								new StringBody(bundle.getString("Price")));

						reqEntity.addPart("category_id",
								new StringBody(bundle.getString("CategoryId")));
						reqEntity.addPart("date",
								new StringBody(bundle.getString("Date")));
						reqEntity.addPart("description",
								new StringBody(bundle.getString("Comment")));
						reqEntity.addPart("end_date",
								new StringBody(bundle.getString("EndDate")));

						reqEntity.addPart("from_location", new StringBody(
								bundle.getString("Address")));
						reqEntity.addPart("latitude", new StringBody(
								Constants.lat));
						reqEntity.addPart("longitude", new StringBody(
								Constants.lon));

						reqEntity.addPart("name",
								new StringBody(bundle.getString("JobName")));

//						reqEntity.addPart("start_date",
//								new StringBody(bundle.getString("Date")));
						Log.e("SAMPLE", "ADD Date " + bundle.getString("Date"));
						reqEntity.addPart("start_latitude", new StringBody(
								bundle.getString("EndLat")));
						reqEntity.addPart("start_longitude", new StringBody(
								bundle.getString("EndLon")));
						reqEntity.addPart("to_location",
								new StringBody(bundle.getString("EndAddress")));

						reqEntity.addPart("user_id", new StringBody(
								Constants.uid));
						// ------------------------------Changes------------------------

					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("SAMPLE", "ADD e " + e.toString());
					}
					if (Constants.imageComment != null) {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						Constants.imageComment.compress(CompressFormat.JPEG,
								75, bos);
						byte[] data = bos.toByteArray();
						String imageName = "job";
						ByteArrayBody bab = new ByteArrayBody(data, imageName
								+ ".png");
						reqEntity.addPart("image", bab);
					}
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(Constants.HTTP_HOST
							+ "addjob");
					httppost.setEntity(reqEntity);

					// Execute HTTP Post Request
					HttpResponse response = null;
					try {
						response = httpclient.execute(httppost);
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("SAMPLE", "ADD e 1 " + e.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("SAMPLE", "ADD e 2 " + e.toString());
					}

					Log.e("SAMPLE", "RES : " + response);


					JSONObject postedJob = new JSONObject(); //really comeback

					SharedPreferences loginDB = getActivity().getSharedPreferences(
							LoginActivity.LOGINDATA, 0);

					String userId = Constants.uid;
					String price = bundle.getString("Price");
					String categoryName = bundle.getString("CategoryName");
					String date = bundle.getString("Date");
					String cookie = Constants.cookie;
					String userName = Constants.username;

					try {
						postedJob.put("User", userName);
						postedJob.put("UserId", userId);
//					postedJob.put("Price", price);
						postedJob.put("CategoryName", categoryName);
//					postedJob.put("Date", date);
						mixpanel.track("JobPostEvent", postedJob);
					}catch (JSONException e){

					}


					HttpEntity entity = response.getEntity();
					ResultData resultData = null;

					try {

						InputStream stream = entity.getContent();

						String strResponse = convertStreamToString(stream);

						JSONObject jsonresponse = new JSONObject(strResponse);

						Log.e("SAMPLE", "request > " + jsonresponse);

						resultData = new ResultData();
						resultData.setAuthenticated(jsonresponse
								.getString("status"));

						try {
							resultData.setMessage(jsonresponse
									.getString("message"));
							// resultData.setMessage(jsonresponse.getString("id"));
							resultData.setJobName(bundle.getString("JobName"));
						} catch (Exception ex) {
							ex.printStackTrace();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					progressBar.dismiss();

					// -Ashish-

					Message msg = handler.obtainMessage();
					if (resultData != null) {
						msg.obj = resultData;
						msg.arg1 = SUCCESS;
					} else {
						msg.arg1 = FAILURE;
					}
					handler.sendMessage(msg);

				} else {

					Message msg = handler.obtainMessage();
					msg.arg1 = USER_NOT_EXIST;
					handler.sendMessage(msg);

				}
			}
		});
		thr.start();
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

	/*
	 * To register on stripe card detail page
	 */
	ProgressDialog mProgressDialog;

	public void openUserRegistration() {
		Intent intent = new Intent(getActivity(),
				StripeRegistrationActivity.class);
		// progressBar.dismiss();
		startActivity(intent);

	}

	public String dateFormatChange(String date) {

		SimpleDateFormat newDateFormat = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		Date MyDate = null;
		try {
			MyDate = newDateFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
		return newDateFormat.format(MyDate);
	}

	public void onBack() {
		FragmentManager fm = getFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
		}
	}

	private void showAlertDialog(String s) {
		Context context = getActivity().getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, s, duration);
		toast.show();
	}

	private void getStripePublicKey() {
		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("mode", "Live"));
				DataPostParser parser1 = new DataPostParser(Constants.HTTP_HOST
						+ "getStripePublicKey");
				ResultData postdata1 = parser1.getParseData(nameValuePairs);

				Constants.STRIPE_PUBLISHABLE_KEY = postdata1.getMessage();
				Log.d("Constants.STRIPE_PUBLISHABLE_KEY Key"
						+ Constants.STRIPE_PUBLISHABLE_KEY, "  test ");
			}
		});
		thr.start();
	}

}
