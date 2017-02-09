package com.crowdserviceinc.crowdservice.activity;

import java.util.ArrayList;
import java.util.List;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.model.notification;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ParentFragmentActivity extends FragmentActivity {

	
	private static List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (!isReceiverRegistered(mMessageReceiver)) {
			LocalBroadcastManager.getInstance(this).registerReceiver(
					mMessageReceiver, new IntentFilter("custom-event-name"));
			receivers.add(mMessageReceiver);
		}
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent

			notification _notification = (notification) intent
					.getSerializableExtra("notification");

			String message = intent.getStringExtra("message");

			if (_notification.getMsgType().equals("11")) {
				Utils.displayCustomOkAlertRedirect(ParentFragmentActivity.this,
						_notification.getMessage(),
						getString(R.string.app_alert_title) + "-"
								+ _notification.getJobname(), _notification);
			} else if (_notification.getMsgType().equals("8")) {
				Utils.displayCustomAgreeAlert(ParentFragmentActivity.this, message,
						getString(R.string.app_alert_title) + "-"
								+ _notification.getJobname(), _notification);
			}else if (_notification.getMsgType().equals("12")) {
				Utils.displayCustomAgreeAlert(ParentFragmentActivity.this, message,
						getString(R.string.app_alert_title) + "-"
								+ _notification.getJobname(), _notification);
			} else if (_notification.getMsgType().equals("9")) {
				Utils.displayCustomOkAlertRedirect(ParentFragmentActivity.this,
						message,
						getString(R.string.app_alert_title) + "-"
								+ _notification.getJobname(), _notification);
				
			}else if (_notification.getMsgType().equals("10")) {
				Utils.displayCustomOkAlert(ParentFragmentActivity.this, message,
						getString(R.string.txtalert_bid_0));
			} else if (_notification.getMsgType().equals("0")) {
				Utils.displayCustomOkAlert(ParentFragmentActivity.this,
						_notification.getMessage(),
						getString(R.string.txtalert_bid_0));
			} else if (_notification.getMsgType().equals("1")) {
				Utils.displayCustomOkAlert(ParentFragmentActivity.this,
						_notification.getMessage(),
						getString(R.string.txtalert_bid_1));
			} else if (_notification.getMsgType().equals(Constants.NOTIFICATION_TYPE_RATEPROVIDER)) {
				
//				Intent jobCompleteIntent = new Intent(ParentActivity.this,
//						RatingProviderActivity.class);
//				jobCompleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				String jobId = _notification.getJobid();
//				String toRateId = _notification.getTo_id();
//
//				jobCompleteIntent.putExtra("completedJobId", jobId);
//				jobCompleteIntent.putExtra("completedJobuserId", toRateId);
//				context.startActivity(jobCompleteIntent);
				
				Utils.displayCustomOkAlert(ParentFragmentActivity.this,
						message,
						getString(R.string.app_alert_title));
				
			} else{
				Utils.displayCustomOkAlert(ParentFragmentActivity.this,
						message,
						getString(R.string.app_alert_title) + "-"
								+ _notification.getJobname());
			}

			// Toast.makeText(getApplicationContext(), "msg >> " + message,
			// Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		if (!isReceiverRegistered(mMessageReceiver)) {
			LocalBroadcastManager.getInstance(this).registerReceiver(
					mMessageReceiver, new IntentFilter("custom-event-name"));
			
			receivers.add(mMessageReceiver);
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mMessageReceiver);
		if (isReceiverRegistered(mMessageReceiver)) {
			receivers.remove(mMessageReceiver);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean isReceiverRegistered(BroadcastReceiver receiver) {
		boolean registered = receivers.contains(receiver);
		Log.i("Send", "is receiver " + receiver + " registered? " + registered);
		return registered;
	}
}
