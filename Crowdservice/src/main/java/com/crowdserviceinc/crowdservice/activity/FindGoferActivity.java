package com.crowdserviceinc.crowdservice.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.activity.job.JobsListingFragment;
import com.crowdserviceinc.crowdservice.activity.jobcreate.CategoryFragment;
import com.crowdserviceinc.crowdservice.activity.rating.RatingProviderActivity;
import com.crowdserviceinc.crowdservice.activity.settings.SettingsActivity;
import com.crowdserviceinc.crowdservice.model.notification;
import com.crowdserviceinc.crowdservice.parser.JobbidParser;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.util.Utils;

public class FindGoferActivity extends ParentFragmentActivity implements
		OnClickListener {

	private FragmentManager fragmentManager;
	private BackListener backListner = null;
	private TextView title = null;
	private TextView back = null;
	private TextView settng = null;
	private final int SUCCESS = 101;

	int requestForService = 120;
	//private static List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_findgofer);
		Log.e("VIP", "FindGoferActivity");

		initViews();
	}

	/*
	 * Called to initialize to user interface.
	 */
	private void initViews() {
		back = (TextView) findViewById(R.id.btnBack);
		back.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		settng = (TextView) findViewById(R.id.btnSetting);
		settng.setOnClickListener(this);
		requestForService = getIntent().getIntExtra("requestForService", 1);
		if (requestForService == 1) {
			back.setText("Map");
			settng.setVisibility(View.VISIBLE);
			title.setText("Request Service");
		} else if (requestForService == 0) {
			back.setText("Back");
			settng.setVisibility(View.GONE);
			title.setText("Open For Bids");
		}

		if (Constants.isNetAvailable(FindGoferActivity.this)) {
			ServerCommunication download = new ServerCommunication();
			download.execute(new String[] { "" });

		} else {
			Constants.NoInternetError(FindGoferActivity.this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnBack) {
			if (backListner != null)
				backListner.backPressed();
			else
				finish();
		}
		if (v.getId() == R.id.btnSetting) {
			startActivity(new Intent(getApplicationContext(),
					SettingsActivity.class));

		}
	}

	public void setTitle(String titleStr) {
		title.setText(titleStr);
	}

	public interface BackListener {
		public void backPressed();
	}

	public void setBackListener(BackListener listener) {
		backListner = listener;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e("VIP", "msg.arg1 --> " + msg.arg1);
			if (msg.arg1 == SUCCESS) {
				fragmentManager = getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				Fragment fragmentpublic = null;
				if (requestForService == 1) {
					fragmentpublic = new CategoryFragment();
					transaction.replace(R.id.mainContainer, fragmentpublic,
							"Category");
				} else if (requestForService == 0) {
					fragmentpublic = new JobsListingFragment();
					transaction.replace(R.id.mainContainer, fragmentpublic,
							"JobsListingFragment");
				}
				transaction.commit();
			} else
				Toast.makeText(FindGoferActivity.this, "Problem",
						Toast.LENGTH_LONG).show();
		}
	};

	private class ServerCommunication extends AsyncTask<String, String, String> {

		private final ProgressDialog progressBar = new ProgressDialog(
				FindGoferActivity.this);

		@Override
		protected String doInBackground(String... strParams) {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", Constants.uid));
			String x = requestForService == 1 ? "2" : "3";
			Log.e("TAGG", "REQ Parser >> " + Constants.uid);
			nameValuePairs.add(new BasicNameValuePair("is_customer", x));
			JobbidParser parser = new JobbidParser(Constants.HTTP_HOST
					+ "updateuser");

			String dataList = parser.getParseData(nameValuePairs);

			return dataList;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setCancelable(false);
			String str = requestForService == 1 ? "Making you customer"
					: "Making you as provider";
			progressBar.setMessage(str);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(String dataList) {
			super.onPostExecute(dataList);
			progressBar.dismiss();

			Message msg = handler.obtainMessage();
			if (dataList.equalsIgnoreCase("success")) {
				msg.arg1 = SUCCESS;
			} else
				msg.arg1 = 100;
			msg.obj = dataList;
			handler.sendMessage(msg);
		}
	}
}
