package com.crowdserviceinc.crowdservice.activity.paypal;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.crowdserviceinc.crowdservice.util.http.HttpPostConnector;
import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.model.ResultData;
import com.crowdserviceinc.crowdservice.model.UserDetail;
import com.crowdserviceinc.crowdservice.parser.DataPostParser;
import com.crowdserviceinc.crowdservice.parser.UserDataPostParser;
import com.crowdserviceinc.crowdservice.util.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PayPalVerificationActivity extends Activity implements OnClickListener {

	EditText txtPaypalId, txtCPaypalId, txtFirstName, txtLastName;
	Button btnBack, btnVerify;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paypal_verification);
		
		txtPaypalId = (EditText) findViewById(R.id.txtPaypalId);
		txtCPaypalId = (EditText) findViewById(R.id.txtCPaypalId);
		txtFirstName = (EditText) findViewById(R.id.txtfirstname);
		txtLastName = (EditText) findViewById(R.id.txtlastname);
		
		btnBack = (Button) findViewById(R.id.btnBack);
		btnVerify = (Button) findViewById(R.id.btnPaypalVerify);
		
		btnBack.setOnClickListener(this);
		btnVerify.setOnClickListener(this);
		
		if (Constants.isNetAvailable(PayPalVerificationActivity.this)) {
			
			ServerCommunication download = new ServerCommunication();
			download.execute(new String[] { "" });

		} else {
			Constants.NoInternetError(PayPalVerificationActivity.this);
		}
	}
	
	private void showAlertDialog(String s) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, s, duration);
		toast.show();

	}

	/*
	 * To download data from server.
	 */
	private class ServerCommunication extends
			AsyncTask<String, String, UserDetail> {
		private final ProgressDialog progressBar = new ProgressDialog(
				PayPalVerificationActivity.this);

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
		if(v == btnBack){
			finish();
		}else if(v == btnVerify){
			doVerify(txtPaypalId.getText().toString(), txtFirstName.getText().toString(), txtLastName.getText().toString());
		}
	}
	
	/*
	 * To Submit Record.
	 */
	private void doVerify(final String paypalId, final String firstName, final String lastName) {
		final ProgressDialog progressBar = new ProgressDialog(PayPalVerificationActivity.this);
		progressBar.setMessage("Please wait while updating...");
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
						msg.arg1 = Constants.SUCCESS;
						handler.sendMessage(msg);
					}catch(Exception e){
						msg.arg1 = Constants.FAILURE;
						handler.sendMessage(msg);
					}
				}
				
				msg.arg1 = Constants.FAILURE;
				handler.sendMessage(msg);
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
				onBackPressed();
			}
			else if (msg.arg1 == Constants.FAILURE) {	
				showAlertDialog("Please try later!");
			}
		}
	};
}
