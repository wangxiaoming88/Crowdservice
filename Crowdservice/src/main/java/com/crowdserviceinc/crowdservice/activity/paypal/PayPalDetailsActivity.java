package com.crowdserviceinc.crowdservice.activity.paypal;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.crowdserviceinc.crowdservice.util.http.HttpPostConnector;
import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.activity.FindGoferActivity;
import com.crowdserviceinc.crowdservice.model.ResultData;
import com.crowdserviceinc.crowdservice.model.UserDetail;
import com.crowdserviceinc.crowdservice.parser.DataPostParser;
import com.crowdserviceinc.crowdservice.parser.UserDataPostParser;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.util.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PayPalDetailsActivity extends Activity implements OnClickListener {
	
	EditText txtPaypalId, txtCPaypalId, txtFirstName, txtLastName;
	Button btnBack, btnVerify;
	
	int requestForService = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pay_pal_details);
		
		requestForService = getIntent().getIntExtra("requestForService", 1);
		
		txtPaypalId = (EditText) findViewById(R.id.txtPaypalId);
		txtCPaypalId = (EditText) findViewById(R.id.txtCPaypalId);
		txtFirstName = (EditText) findViewById(R.id.txtfirstname);
		txtLastName = (EditText) findViewById(R.id.txtlastname);
		
		btnBack = (Button) findViewById(R.id.btnBack);
		btnVerify = (Button) findViewById(R.id.btnPaypalVerify);
		
		btnBack.setOnClickListener(this);
		btnVerify.setOnClickListener(this);
		
		if (Constants.isNetAvailable(PayPalDetailsActivity.this)) {
			
			ServerCommunication download = new ServerCommunication();
			download.execute(new String[] { "" });

		} else {
			Constants.NoInternetError(PayPalDetailsActivity.this);
		}
	}
	
	private void showAlertDialog(String s) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, s, duration);
		toast.show();

	}

	/*
	 * To download data from server.
	 */
	private class ServerCommunication extends
			AsyncTask<String, String, UserDetail> {
		private final ProgressDialog progressBar = new ProgressDialog(
				PayPalDetailsActivity.this);

		public ServerCommunication() {
			// TODO Auto-generated constructor stub
			progressBar.setCancelable(false);
		}

		@Override
		protected UserDetail doInBackground(String... strParams) {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", Constants.uid));

			UserDataPostParser parser = new UserDataPostParser(
					Constants.HTTP_HOST + "viewprofile");
			UserDetail data = parser.getParseData(nameValuePairs);
			if (data != null) {
				

			}

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
			Message msg = handler.obtainMessage();
			msg.arg1 = Constants.SUCCESS;
			msg.obj = data;
			handler.sendMessage(msg);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == Constants.SUCCESS) {
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

						showProfileDetail(data);
					}
				}
			} else if (msg.arg1 == Constants.FAILURE) {
				showAlertDialog("Please try again.");

			}
		}
	};
	
	private void showProfileDetail(UserDetail data){
		txtFirstName.setText(data.getFirst_name());
		txtLastName.setText(data.getLast_name());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Utils.hideSoftKeyboard(this);
		
		if(v == btnBack){
			finish();
		}else if(v == btnVerify){
			
			if(validate())
				doVerify(txtPaypalId.getText().toString(), txtFirstName.getText().toString(), txtLastName.getText().toString());
		}
	}
	
	private Boolean validate(){
		
		if ((txtPaypalId.getText().toString() == null)
				|| (txtPaypalId.getText().toString().equals(""))) {
			showAlertDialog("Please enter your PayPal email address.");
			txtPaypalId.setText("");
			return false;
		} else if (!isValidEmail(txtPaypalId.getText().toString())) {
			showAlertDialog("Please enter valid email address.");
			txtPaypalId.setText("");
			return false;
		} else if(!txtPaypalId.getText().toString().equals(txtCPaypalId.getText().toString())){
			showAlertDialog("Please enter the same PayPal email address.");
			txtPaypalId.setText("");
			txtCPaypalId.setText("");
			return false;
			
		}else if ((txtFirstName.getText().toString() == null) || (txtFirstName.getText().toString().equals(""))) {
			showAlertDialog("Please enter your first name.");
			return false;
		}else if (!txtFirstName.getText().toString().matches("[a-zA-Z0-9 ]*")) {
			showAlertDialog("First name can not contain any special character.");
			return false;
		} else if (txtFirstName.getText().toString().equals(".")) {
			showAlertDialog("First name can not contain any special character.");
			return false;
		} else if (txtFirstName.getText().toString().length() > 20) {
			showAlertDialog("First name should be of maximum 20 characters!");
			return false;
		} else if ((txtLastName.getText().toString() == null) || (txtLastName.getText().toString().equals(""))) {
			showAlertDialog("Please enter your last name.");
			return false;
		} else if (!txtLastName.getText().toString().matches("[a-zA-Z0-9 ]*")) {
			showAlertDialog("Last name can not contain any special character.");
			return false;
		} else if (txtLastName.getText().toString().equals(".")) {
			showAlertDialog("Last name can not contain any special character.");
			return false;
		} else if (txtLastName.getText().toString().length() > 20) {
			showAlertDialog("Last name should be of maximum 20 characters!");
			return false;
		}

		return true;
	}
	
	private boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}
	/*
	 * To Submit Record.
	 */
	private void doVerify(final String paypalId, final String firstName, final String lastName) {
		
		final ProgressDialog progressBar = new ProgressDialog(PayPalDetailsActivity.this);
		progressBar.setMessage("Please wait...");
		progressBar.setCancelable(false);
		progressBar.show();

		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() 
			{				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("user_id",Constants.uid));
				nameValuePairs.add(new BasicNameValuePair("email",paypalId));
				nameValuePairs.add(new BasicNameValuePair("first_name",firstName));
				nameValuePairs.add(new BasicNameValuePair("last_name",lastName));
				
				HttpPostConnector conn=new HttpPostConnector(Constants.HTTP_HOST+"getVerifiedStatus", nameValuePairs);
				String response=conn.getResponseData();		
				Log.d("Paypal Verified Status", response);
				
				Message msg = handler1.obtainMessage();
				
				progressBar.dismiss();
				if(response != null && response.length() > 0)
				{
					try {
						//	{"verified_status":false,"validated":false,"success":true}
						JSONObject jObj = new JSONObject(response);
						if(jObj.getBoolean("success") == true){
							
							Constants.hasPayPalRegistered = jObj.getBoolean("validated");
							Constants.hasPayPalVerified = jObj.getBoolean("verified_status");
							
							if(Constants.hasPayPalRegistered){
								Constants.paypalId = txtPaypalId.getText().toString();
								msg.arg1 = Constants.SUCCESS;								
							}else{
								
								Constants.paypalId = "";
								msg.arg1 = Constants.FAILURE;								
							}
							
							SharedPreferences loginDB = getSharedPreferences("GoferLogin", 0);
							SharedPreferences.Editor editor = loginDB.edit();
							editor.putString("PaypalId", Constants.paypalId);
							editor.commit();
							
							handler1.sendMessage(msg);
							
						}else{
							msg.arg1 = Constants.FAILURE;
							handler1.sendMessage(msg);
						}
					}catch(Exception e){
						msg.arg1 = Constants.FAILURE;
						handler1.sendMessage(msg);
					}
				}else{
					msg.arg1 = Constants.FAILURE;
					handler1.sendMessage(msg);
				}
				
				
				/*
				DataPostParser parser = new DataPostParser(Constants.HTTP_HOST+"getVerifiedStatus");
				
				ResultData postdata=parser.getParseData(nameValuePairs);
				Message msg = handler.obtainMessage();
				if ((postdata.getAuthenticated().equals("success")) && (postdata.getMessage().equals("paypal email regstered successfully."))) {
					progressBar.dismiss();
					msg.arg1 = Constants.SUCCESS;
					handler.sendMessage(msg);
					
				}else{
					progressBar.dismiss();
					msg.arg1 = Constants.FAILURE;
					handler.sendMessage(msg);
				
					
				}
				*/
				
			}
		});
		thr.start();
	}	
	
	
	/*
	 * Handler.
	 */
	private Handler handler1 = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == Constants.SUCCESS) {	
				
				if(requestForService == 0){
					
					if(Constants.isNetAvailable(PayPalDetailsActivity.this)){
						doRequestForService("Making you as provider");
					}else{
						Constants.NoInternetError(PayPalDetailsActivity.this);
					}
					
				}else{
					setResult(RESULT_OK);
					finish();
				}
			}
			else if (msg.arg1 == Constants.FAILURE) {	
				showAlertDialog("There seems to be a problem.... Please check that you've correctly entered your name and PayPal email address.");
			}
		}
	};
	
	private void doRequestForService(String message) {
		final ProgressDialog progressBar = new ProgressDialog(PayPalDetailsActivity.this);
		progressBar.setMessage(message);
		progressBar.show();

		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("id", Constants.uid));
				nameValuePairs.add(new BasicNameValuePair("is_customer", "3"));

				DataPostParser parser = new DataPostParser(Constants.HTTP_HOST
						+ "updateuser");
				ResultData postdata = parser.getParseData(nameValuePairs);
				progressBar.dismiss();

				Message msg = handler.obtainMessage();
				if (postdata != null) {
					msg.obj = postdata;
					msg.arg1 = Constants.SUCCESS;
				} else {
					msg.arg1 = Constants.FAILURE;
				}
				requestHandler.sendMessage(msg);
			}
		});
		thr.start();
	}
	
	private Handler requestHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == Constants.SUCCESS) {
				if (msg.obj instanceof ResultData) {
					ResultData data = (ResultData) msg.obj;
					if (data.getAuthenticated() != null
							&& data.getAuthenticated().equalsIgnoreCase(
									"success")) {
						Intent intent = new Intent(PayPalDetailsActivity.this,
								FindGoferActivity.class);
						intent.putExtra("requestForService", 0);
						startActivity(intent);

						finish();
						
					} else {
						showAlertDialog(data.getMessage());
					}
				}
			} else if (msg.arg1 == Constants.FAILURE) {
				Toast.makeText(PayPalDetailsActivity.this, "Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
}
