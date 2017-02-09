package com.crowdserviceinc.crowdservice.activity.login;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.activity.TutorialActivity;
import com.crowdserviceinc.crowdservice.activity.map.MapActivity;
import com.crowdserviceinc.crowdservice.activity.rating.RatingProviderActivity;
import com.crowdserviceinc.crowdservice.util.http.HttpPostConnector;
import com.crowdserviceinc.crowdservice.model.ResultData;
import com.crowdserviceinc.crowdservice.parser.DataPostParser;
import com.crowdserviceinc.crowdservice.parser.JobbidParser;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.util.Utils;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class LoginActivity extends Activity implements OnClickListener,
		LocationListener {

	private EditText edtUsername, edtPassword;
	private Button btnSignup, btnsignRegister;
	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	public static final int FORGOTPASS = 3;
	public static final String LOGINDATA = "GoferLogin";
	private SharedPreferences loginDB;
	private TextView txtBack, txtTerms, txtPrivacyPolicy;
	private Button btnTutorial;
	//txtLoginAsGuest;
	private LocationManager mlocManager = null;
	private RelativeLayout viewrltLayout, rlt_logInRegister;
	LinearLayout rlt_signupRegister;
	ImageView btnForgot;

	CallbackManager callbackManager;
	private ImageButton btnFBLogin;


	
	ProgressDialog progressBar;

	private MixpanelAPI mixpanel;
	private JSONObject loginEvent;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FacebookSdk.sdkInitialize(this.getApplicationContext());
		
		setContentView(R.layout.activity_login);
		
		initViews();
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
				0, this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		boolean isFromPush = getIntent().getBooleanExtra("isFromPush", false);
		if (isFromPush) {
			int msgType = getIntent().getIntExtra("msgType", -1);
			if (msgType == 5) {
				String isReply = getIntent().getStringExtra("reply");
				if (isReply.equalsIgnoreCase("YES"))
					displayReplyDialog();
				else
					displayNoReplyDialog();

			} else if (msgType == 3) {
				String jobName = getIntent().getStringExtra("jobName");
				String userName = getIntent().getStringExtra("username");
				String msg = getIntent().getStringExtra("message");

				displayPushAlert(jobName, msg);
			} else if (msgType == 6) {

				// notificationIntent.putExtra("job_id", msgArray[1]);
				// notificationIntent.putExtra("jobName", msgArray[2]);
				// notificationIntent.putExtra("from_id", msgArray[3]);
				// notificationIntent.putExtra("from_name", msgArray[4]);
				// notificationIntent.putExtra("to_id", msgArray[5]);
				// notificationIntent.putExtra("to_name", msgArray[6]);
				// notificationIntent.putExtra("status", msgArray[7]);
				// notificationIntent.putExtra("msgType", 6);
				// notificationIntent.putExtra("isFromPush", true);
				String messageToDisplay = getIntent().getStringExtra(
						"from_name")
						+ "has completed "
						+ getIntent().getStringExtra("jobName");

				String status = getIntent().getStringExtra("status");
				if (status.equalsIgnoreCase("pending")) {
					displayPushAlert(getIntent().getStringExtra("jobName"),
							messageToDisplay);
				} else {
					Intent jobCompleteIntent = new Intent(LoginActivity.this,
							RatingProviderActivity.class);
					String jobId = getIntent().getStringExtra("job_id");
					String toRateId = getIntent().getStringExtra("to_id");

					jobCompleteIntent.putExtra("completedJobId", jobId);
					jobCompleteIntent.putExtra("completedJobuserId", toRateId);
					startActivity(jobCompleteIntent);

				}
			}

		}
	}

	/*
	 * Called To initialize to user interface.
	 */
	private void initViews() {
		// @Ashish
		
		
		
		viewrltLayout = (RelativeLayout) findViewById(R.id.linearLayoutid);
		rlt_signupRegister = (LinearLayout) findViewById(R.id.signupRegister);
		rlt_logInRegister = (RelativeLayout) findViewById(R.id.logInRegister);
		btnsignRegister = (Button) findViewById(R.id.btnSignin);
		txtTerms = (TextView) findViewById(R.id.txtTerms);
		txtTerms.setOnClickListener(this);
		btnTutorial = (Button) findViewById(R.id.btnTutorial);
		btnTutorial.setOnClickListener(this);
		txtPrivacyPolicy = (TextView) findViewById(R.id.txtPrivacyPolicy);
		txtPrivacyPolicy.setOnClickListener(this);
//		txtLoginAsGuest = (TextView) findViewById(R.id.txtLoginAsGuest);
//		txtLoginAsGuest.setOnClickListener(this);

		txtBack = (TextView) findViewById(R.id.viewjob_btnBack);

		edtUsername = (EditText) findViewById(R.id.loginName);
		edtPassword = (EditText) findViewById(R.id.loginPassword);

		btnFBLogin = (ImageButton) findViewById(R.id.btnFBLogin);
		btnFBLogin.setOnClickListener(this);
		
		btnForgot = (ImageView) findViewById(R.id.loginForgot);
		btnSignup = (Button) findViewById(R.id.btnRegister);

		btnSignup.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
					Log.d("TouchTest", "Touch down");
					GradientDrawable bgShape11 = (GradientDrawable) btnSignup
							.getBackground();
					bgShape11.setColor(Color.WHITE);
					btnSignup.setTextColor(Color.parseColor("#1583A2"));

				} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
					Log.d("TouchTest", "Touch up");
					GradientDrawable bgShape11 = (GradientDrawable) btnSignup
							.getBackground();
					bgShape11.setColor(Color.TRANSPARENT);
					btnSignup.setTextColor(Color.WHITE);
					Intent intent = new Intent(LoginActivity.this,
							SignupActivity.class);
					startActivity(intent);
				}
				return true;
			}
		});

		btnsignRegister.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
					Log.d("TouchTest", "Touch down");
					GradientDrawable bgShape11 = (GradientDrawable) btnsignRegister
							.getBackground();
					bgShape11.setColor(Color.WHITE);
					btnsignRegister.setTextColor(Color.parseColor("#1583A2"));

				} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
					Log.d("TouchTest", "Touch up");
					GradientDrawable bgShape11 = (GradientDrawable) btnsignRegister
							.getBackground();
					bgShape11.setColor(Color.TRANSPARENT);
					btnsignRegister.setTextColor(Color.WHITE);
					showLoginForm(true);
				}
				return true;
			}
		});

		btnTutorial.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
					Log.d("TouchTest", "Touch down");
					GradientDrawable bgShape11 = (GradientDrawable) btnTutorial
							.getBackground();
					bgShape11.setColor(Color.WHITE);
					btnTutorial.setTextColor(Color.parseColor("#1583A2"));

				} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
					Log.d("TouchTest", "Touch up");
					GradientDrawable bgShape11 = (GradientDrawable) btnTutorial
							.getBackground();
					bgShape11.setColor(Color.TRANSPARENT);
					btnTutorial.setTextColor(Color.WHITE);
					startActivity(new Intent(LoginActivity.this, TutorialActivity.class));
				}
				return true;
			}
		});
		/*
		 * btnsignRegister.setOnKeyListener(new OnKeyListener() {
		 * 
		 * @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
		 * Log.d("event.getAction()"+event.getAction(),""+keyCode); // your
		 * custom implementation if (KeyEvent.KEYCODE_ENTER == keyCode) { //
		 * match ENTER key { // show the toast
		 * Toast.makeText(LoginActivity.this, "Hello World",
		 * Toast.LENGTH_SHORT).show(); return true; // indicate that we handled
		 * event, won't propagate it } return false; } });
		 */

		// btnsignRegister.setOnFocusChangeListener(new
		// OnFocusChangeListener());
		/*
		 * 
		 * btnsignRegister.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 * //Log.d("sdsdd","sdsd"); GradientDrawable bgShape =
		 * (GradientDrawable)btnsignRegister.getBackground();
		 * bgShape.setColor(Color.WHITE);
		 * btnsignRegister.setTextColor(Color.parseColor("#1583A2"));
		 * 
		 * // showLoginForm(true); return true;} });
		 */
		txtBack.setOnClickListener(this);
		btnForgot.setOnClickListener(this);
		edtPassword.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (validate(LoginActivity.this)) {
					doLogin(edtUsername.getText().toString(), edtPassword
							.getText().toString());
					if (edtUsername.hasFocus())
						hideSoftInput(edtUsername);
					else if (edtPassword.hasFocus())
						hideSoftInput(edtPassword);
				}
				return true;
			}
		});
		edtUsername.setText("");
		edtPassword.setText("");
		showRemember();
	}

	// Code of Back Button Functionality
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// Its visible
		if (rlt_signupRegister.getVisibility() == View.VISIBLE
				&& rlt_logInRegister.getVisibility() == View.GONE) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {

				moveTaskToBack(true);
				return true;
			}

		} else if (rlt_signupRegister.getVisibility() == View.GONE
				&& rlt_logInRegister.getVisibility() == View.VISIBLE) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				GradientDrawable bgShape = (GradientDrawable) btnSignup
						.getBackground();
				bgShape.setColor(Color.TRANSPARENT);
				btnSignup.setTextColor(Color.parseColor("#FFFFFF"));

				GradientDrawable bgShape1 = (GradientDrawable) btnsignRegister
						.getBackground();
				bgShape1.setColor(Color.TRANSPARENT);
				btnsignRegister.setTextColor(Color.parseColor("#FFFFFF"));

				showLoginForm(false);
				return (true);
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		if (view == btnForgot) {
			enterEmailDialog();
		} else
		if (view == btnSignup) {
			GradientDrawable bgShape = (GradientDrawable) btnSignup
					.getBackground();
			bgShape.setColor(Color.WHITE);

			btnSignup.setTextColor(Color.parseColor("#1583A2"));
			Intent intent = new Intent(LoginActivity.this,
					SinupOptionActivity.class);
			startActivity(intent);
		} else if (view == txtBack) {
			GradientDrawable bgShape = (GradientDrawable) btnSignup
					.getBackground();
			bgShape.setColor(Color.TRANSPARENT);
			btnSignup.setTextColor(Color.parseColor("#FFFFFF"));

			GradientDrawable bgShape1 = (GradientDrawable) btnsignRegister
					.getBackground();
			bgShape1.setColor(Color.TRANSPARENT);
			btnsignRegister.setTextColor(Color.parseColor("#FFFFFF"));

			showLoginForm(false);
		} else if (view == btnsignRegister) {
			GradientDrawable bgShape11 = (GradientDrawable) btnsignRegister
					.getBackground();
			bgShape11.setColor(Color.WHITE);
			btnsignRegister.setTextColor(Color.parseColor("#1583A2"));

			showLoginForm(true);
		} else if (view == txtTerms) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("http://crowdserviceinc.com/terms-refund.html"));
			startActivity(i);
		} else if (view == txtPrivacyPolicy) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("http://crowdserviceinc.com/privacy.html"));
			startActivity(i);
		} else if (view == btnFBLogin) {
			onFBLogin();
		}
//		else if (view == txtLoginAsGuest) {
//			startActivity(new Intent(LoginActivity.this, MapActivity.class));
//		}
	}

	@Override
	public void onResume() {
		super.onResume();
		GradientDrawable bgShape = (GradientDrawable) btnSignup.getBackground();
		bgShape.setColor(Color.TRANSPARENT);
		btnSignup.setTextColor(Color.parseColor("#FFFFFF"));

		GradientDrawable bgShape1 = (GradientDrawable) btnsignRegister
				.getBackground();
		bgShape1.setColor(Color.TRANSPARENT);
		btnsignRegister.setTextColor(Color.parseColor("#FFFFFF"));

	}

	/*
	 * Handler.
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == FORGOTPASS) {
				ResultData data = (ResultData) msg.obj;
				if (data != null
						&& !data.getAuthenticated().equalsIgnoreCase("fail"))
					Utils.displayOkAlert(LoginActivity.this,
							"Email has been sent to your registered e-mail. Please check your mail.");
				else
					Utils.displayOkAlert(LoginActivity.this, data.getMessage());
			} else if (msg.arg1 == SUCCESS) {
				if (msg.obj instanceof ResultData) {
					ResultData data = (ResultData) msg.obj;
					if (data.getAuthenticated() != null
							&& data.getAuthenticated().equalsIgnoreCase(
									"success")) {
						Constants.uid = data.getUserid();
						Log.e("VIPVIP", "Insert Data Constants.uid >> "
								+ Constants.uid);
						Constants.userType = data.getUserType();
						Constants.approxAddress = data.getApproxAddress();
						Constants.trueAddress = data.getTrueAddress();
						Constants.cookie = data.getCookie();
						Constants.paypalId = data.getPaypalId();
						
						saveLogin(edtUsername.getText().toString(), edtPassword
								.getText().toString(), Constants.uid,
								Constants.userType, Constants.approxAddress,
								Constants.trueAddress,
								Constants.cookie, Constants.paypalId);

						SharedPreferences pref = getSharedPreferences(
								"goffer_pref", Context.MODE_PRIVATE);
						Constants.mapRefreshRate = pref.getLong(
								"maprefresh_rate", 60000);
						Constants.mapRadius = pref.getFloat("map_radius", 10);
						
						checkActiveJobs();

					} else {
						showAlertDialog(data.getMessage());
					}
				}
			} else if (msg.arg1 == FAILURE) {
				Toast.makeText(LoginActivity.this, "Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void checkActiveJobs() {
		
		if(progressBar == null){
			progressBar = new ProgressDialog(
					LoginActivity.this);
			progressBar.setMessage("Loading...");
			progressBar.setCancelable(false);
			progressBar.show();
		}
		
		Thread thread = new Thread(new Runnable() {
			// @Override
			public void run() {
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("user_id", Constants.uid));
				nameValuePairs.add(new BasicNameValuePair("latitude",
						Constants.lat));
				nameValuePairs.add(new BasicNameValuePair("longitude",
						Constants.lon));
				nameValuePairs.add(new BasicNameValuePair("location",
						Constants.locationAdd));
				nameValuePairs.add(new BasicNameValuePair("distance","10000000"));
				nameValuePairs.add(new BasicNameValuePair("condition","related"));
				
				HttpPostConnector conn=new HttpPostConnector(Constants.API_UPDATE_MAPMARKER, nameValuePairs);
				
				String response=conn.getResponseData();	
				
				if(response != null && response.length() > 0)
				{
					try {
						
						JSONArray responseArray = new JSONArray(response);
						
						int count = responseArray.length();
						Log.d("Total Users : ",""+count);
						
						Constants.hasActiveJobs = (count > 0);
						
						Message msg = handler.obtainMessage();
						handler1.sendMessage(msg);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}

	private Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			startActivity(new Intent(LoginActivity.this,
					MapActivity.class));
			finish();
		}
	};
	
	boolean validate(Context cont) {
		if ((edtUsername.getText().toString() == null)
				|| (edtUsername.getText().toString().equals(""))) {
			showAlertDialog("Please enter user name.");
			return false;
		} else if ((edtPassword.getText().toString() == null)
				|| (edtPassword.getText().toString().equals(""))) {
			showAlertDialog("Please enter password.");
			return false;
		}
		return true;
	}

	private void showAlertDialog(String s) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, s, duration);
		toast.show();
	}

	private void saveLogin(final String user, final String password,
			final String userId, final int userType, final String approxAd,
			String trueAddress, String cookie, String paypalId) {
		loginDB = getSharedPreferences(LOGINDATA, 0);
		SharedPreferences.Editor editor = loginDB.edit();
		editor.putString("UserId", userId);
		editor.putInt("UserType", userType);
		editor.putString("ApproxAdd", approxAd);
		editor.putString("TrueAdd", trueAddress);
		editor.putBoolean("isLogin", true);
		editor.putString("Cookie", cookie);
		editor.putString("PaypalId", paypalId);
		editor.commit();
	}

	private void showRemember() {
		loginDB = getSharedPreferences(LOGINDATA, 0);
		if (loginDB != null) {
			String name = loginDB.getString("Username", "").toString();
			String password = loginDB.getString("Password", "").toString();
			if (!name.equals("") && !password.equals("")) {
				edtUsername.setText(name);
				edtPassword.setText(password);
			}
		}
	}

	/*
	 * To register on to access application.
	 */
	private void doLogin(final String user, final String password) {
		
		if(progressBar == null){
			progressBar = new ProgressDialog(
					LoginActivity.this);
			progressBar.setMessage("Please Wait...");
			progressBar.setCancelable(false);
			progressBar.show();
		}
		
		Thread thread = new Thread(new Runnable() {
			// @Override
			public void run() {
				Location loc = mlocManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				Geocoder geocoder = new Geocoder(LoginActivity.this);
				try {
					if(loc != null){
						Constants.lat = String.valueOf(loc.getLatitude());
						Constants.lon = String.valueOf(loc.getLongitude());
						Constants.locationAdd = Utils.getfullAddres((geocoder
								.getFromLocation(loc.getLatitude(),
										loc.getLongitude(), 1)).get(0));
					}
					
					// Constants.locationAdd =
					// Utils.getfullAddres((geocoder.getFromLocation(37.422006,
					// -122.084095, 1)).get(0));
				} catch (Exception e) {
					e.printStackTrace();
				}
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", user));
				nameValuePairs
						.add(new BasicNameValuePair("password", password));
				nameValuePairs.add(new BasicNameValuePair("device_id",
						Constants.gcmRegistrationId));
				nameValuePairs.add(new BasicNameValuePair("latitude",
						Constants.lat));
				nameValuePairs.add(new BasicNameValuePair("longitude",
						Constants.lon));
				nameValuePairs.add(new BasicNameValuePair("is_login", "1"));
				nameValuePairs.add(new BasicNameValuePair("device_type", "2"));
				nameValuePairs.add(new BasicNameValuePair("location",
						Constants.locationAdd));
				DataPostParser parser = new DataPostParser(Constants.HTTP_HOST
						+ "login");
				ResultData data = parser.getParseData(nameValuePairs);
				progressBar.dismiss();
				Message msg = handler.obtainMessage();
				if (data != null) {

					msg.obj = data;
					msg.arg1 = SUCCESS;

					Constants.username = user.toString();
					Constants.runonetime = 0;

					 mixpanel = MixpanelAPI.getInstance(LoginActivity.this, Constants.MIXPANEL_TOKEN);
					JSONObject loginEvent = new JSONObject();

					String userName = edtUsername.getText().toString();

					try{
						loginEvent.put("User", userName);
						mixpanel.track("LoginEvent", loginEvent);
					}catch (JSONException e){

					}

				} else {
					msg.arg1 = FAILURE;
					
					LoginManager.getInstance().logOut();
				}
				handler.sendMessage(msg);
			}
		});
		thread.start();
	}

	/*
	 * Create and return an example alert dialog with an edit text box.
	 */
	private void enterEmailDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Forgot Password");
		builder.setMessage("Please Enter Email:");
		final EditText input = new EditText(this);
		builder.setView(input);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			// @Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if (input.getText().toString().length() == 0) {
					showAlertDialog("Please enter email address.");
				} else if (!isValidEmail(input.getText().toString())) {
					showAlertDialog("Please enter valid email address.");
				} else {
					doForgotPwd(input.getText().toString());
				}
				return;
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
		builder.show();
	}

	/*
	 * To login.
	 */
	private void doForgotPwd(final String userEmail) {
		final ProgressDialog progressBar = new ProgressDialog(
				LoginActivity.this);
		progressBar.setMessage("Please wait while loading...");
		progressBar.setCancelable(false);
		progressBar.show();
		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", userEmail));
				DataPostParser parser = new DataPostParser(Constants.HTTP_HOST
						+ "forgot");
				ResultData postdata = parser.getParseData(nameValuePairs);
				progressBar.dismiss();
				Message msg = handler.obtainMessage();
				if (postdata != null) {
					msg.obj = postdata;
					msg.arg1 = FORGOTPASS;
				} else {
					msg.arg1 = FAILURE;
				}
				handler.sendMessage(msg);
			}
		});
		thr.start();
	}

	/*
	 * To validate.
	 */
	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	public void hideSoftInput(View view) {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void showSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	protected void onDestroy() {
		mlocManager.removeUpdates(this);
		super.onDestroy();
	}

	private void displayReplyDialog() {

		final String fromId = getIntent().getStringExtra("from_id");
		String username = getIntent().getStringExtra("username");
		final String jobId = getIntent().getStringExtra("job_id");
		final String message = getIntent().getStringExtra("message");
		final String toId = getIntent().getStringExtra("to_id");
		final String jobName = getIntent().getStringExtra("jobName");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("CrowdService - " + jobName);
		builder.setMessage(username + "has asked query : " + message);

		final EditText input = new EditText(this);
		input.setHint("Enter Reply");

		LinearLayout ll = new LinearLayout(this);

		ll.setGravity(Gravity.CENTER);
		ll.addView(input);
		builder.setView(ll);

		builder.setPositiveButton("Reply",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (Constants.isNetAvailable(LoginActivity.this)) {
							new ServerCommunicationSendMessage().execute(toId,
									fromId, jobId, input.getText().toString(),
									"NO");
						} else {
							Constants.NoInternetError(LoginActivity.this);
						}

						dialog.dismiss();
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();

	}

	private void displayNoReplyDialog() {
		String fromId = getIntent().getStringExtra("from_id");
		String username = getIntent().getStringExtra("username");
		String jobId = getIntent().getStringExtra("job_id");
		String message = getIntent().getStringExtra("message");
		String toId = getIntent().getStringExtra("to_id");
		final String jobName = getIntent().getStringExtra("jobName");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("CrowdService - " + jobName);
		builder.setMessage(username + "has send reply" + message);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	private void displayPushAlert(String jobName, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Gofer - " + jobName);
		builder.setMessage(message);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	private class ServerCommunicationSendMessage extends
			AsyncTask<String, String, String> {

		private final ProgressDialog progressBar = new ProgressDialog(
				LoginActivity.this);

		@Override
		protected String doInBackground(String... strParams) {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("from_id", strParams[0]));
			nameValuePairs.add(new BasicNameValuePair("to_id", strParams[1]));
			nameValuePairs.add(new BasicNameValuePair("job_id", strParams[2]));
			nameValuePairs.add(new BasicNameValuePair("message", strParams[3]));
			nameValuePairs.add(new BasicNameValuePair("reply", strParams[4]));
			JobbidParser parser = new JobbidParser(Constants.HTTP_HOST
					+ "messaging");
			String response = parser.getParseData(nameValuePairs);
			return response;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setCancelable(false);
			progressBar.setMessage("Please wait...");
			progressBar.show();
		}

		@Override
		protected void onPostExecute(String resp) {
			super.onPostExecute(resp);
			progressBar.dismiss();

			showAlertDialog("Replay Send");
		}
	}

	public void showLoginForm(Boolean isShow) {

		if (isShow) {
			edtUsername.setText("");
			edtPassword.setText("");

			viewrltLayout.setBackgroundResource(R.drawable.login_bg_new);
			rlt_signupRegister.setVisibility(View.GONE);
			rlt_logInRegister.setVisibility(View.VISIBLE);

		} else {

			viewrltLayout.setBackgroundResource(R.drawable.bg_gofer_icon);
			rlt_signupRegister.setVisibility(View.VISIBLE);
			rlt_logInRegister.setVisibility(View.GONE);

		}

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private void onFBLogin(){
		
		callbackManager = CallbackManager.Factory.create();

	    // Set permissions 
	    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));

	    LoginManager.getInstance().registerCallback(callbackManager,
	            new FacebookCallback<LoginResult>() {
	                @Override
	                public void onSuccess(LoginResult loginResult) {

	                	progressBar = new ProgressDialog(
	            				LoginActivity.this);
	            		progressBar.setMessage("Please wait while login...");
	            		progressBar.setCancelable(false);
	            		progressBar.show();
	            		
	                    System.out.println("Success");
	                    GraphRequest request = GraphRequest.newMeRequest(
	                            loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
	                                @Override
	                                public void onCompleted(JSONObject json, GraphResponse response) {
	                                    if (response.getError() != null) {
	                                        // handle error
	                                        System.out.println("ERROR");
	                                    } else {
	                                        System.out.println("Success");
	                                        try {

	                                            String jsonresult = String.valueOf(json);
	                                            System.out.println("JSON Result"+jsonresult);

	                                            String str_email = json.getString("email");
	                                            String str_id = json.getString("id");
	                                            String str_password = str_id + "_P@ssword";

	                                            mixpanel = MixpanelAPI.getInstance(LoginActivity.this, Constants.MIXPANEL_TOKEN);
												loginEvent = new JSONObject();
												try{
													loginEvent.put("User", str_email);
													mixpanel.track("LoginEvent", loginEvent);
												}catch (JSONException e){

												}
	                                            
	                                            doLogin(str_email, str_password);
	                                            
	                                        } catch (JSONException e) {
	                                            e.printStackTrace();
	                                        }
	                                    }
	                                }

	                            });
	                    Bundle parameters = new Bundle();
	                    parameters.putString("fields", "id,first_name,last_name,picture,email");
	                    request.setParameters(parameters);
	                    request.executeAsync();

	                }

	                @Override
	                public void onCancel() {
	                    Log.d("FB Login","On cancel");
	                }

	                @Override
	                public void onError(FacebookException error) { 
	                    Log.d("FB Login",error.toString());
	                }
	    });
	}
}