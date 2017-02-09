package com.crowdserviceinc.crowdservice.activity.mailbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.activity.ParentActivity;
import com.crowdserviceinc.crowdservice.adapter.MailListingAdapter;
import com.crowdserviceinc.crowdservice.util.http.HttpPostConnector;
import com.crowdserviceinc.crowdservice.model.Mail;
import com.crowdserviceinc.crowdservice.model.MailData;
import com.crowdserviceinc.crowdservice.util.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class MailboxActivity extends ParentActivity implements View.OnClickListener {

	ProgressDialog progressBar;
	private ArrayList<MailData> messages = new ArrayList<MailData>();
	
	private ListView mListView;
	private MailListingAdapter mListViewAdapter = null;

	private TextView btnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mailbox);

		btnBack = (TextView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.mail_listview);
		mListViewAdapter = new MailListingAdapter(this, messages);
		
		mListView.setAdapter(mListViewAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				
				MailData message = messages.get(position);
				Intent intent = new Intent(MailboxActivity.this, MailDetailActivity.class);
				intent.putExtra("message", message);

				MixpanelAPI mixpanel = MixpanelAPI.getInstance(MailboxActivity.this, Constants.MIXPANEL_TOKEN);
				JSONObject mailBoxEvent = new JSONObject();

				String userId = Constants.uid;
				String jobId = message.getJobId();
				String userName = Constants.username;

				try {
					mailBoxEvent.put("User", userName);
					mailBoxEvent.put("UserId", userId);
					mailBoxEvent.put("JobId", jobId);

					mixpanel.track("MailBoxEvent", mailBoxEvent);
				}catch (JSONException e){

				}
				
				startActivity(intent);
			}
		});
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		new loadMessagesTask(Constants.uid, "", "", "", null).execute();
	}

	@Override
	public void onClick(View v) {

		if (v == btnBack) {
			finish();

		}
	}

	private class loadMessagesTask extends AsyncTask<String, String, ArrayList<MailData>> {

		private String userId;
		private String jobId;
		private String fromUserId;
		private String lastMessageId;
		private String limit;
		
		public loadMessagesTask(String userId, String jobId, String fromUserId, String lastMessageId, String limit) {
			// TODO Auto-generated constructor stub
			
			this.userId = userId;
			this.jobId = jobId;
			this.fromUserId = fromUserId;
			this.lastMessageId = lastMessageId;
			this.limit = limit;					
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(progressBar == null){
				progressBar = new ProgressDialog(
						MailboxActivity.this);
				progressBar.setMessage("Please wait while loading...");
				progressBar.setCancelable(false);
			}
			progressBar.show();
		}
		
		@Override
		protected ArrayList<MailData> doInBackground(String... strParams) {
			
			ArrayList<MailData> result = new ArrayList<MailData>();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs
					.add(new BasicNameValuePair("login_user_id", userId));
			nameValuePairs.add(new BasicNameValuePair("other_user_id", fromUserId));
			nameValuePairs.add(new BasicNameValuePair("job_id", jobId));
			nameValuePairs.add(new BasicNameValuePair("last_message_id",lastMessageId));
			if (limit != null) {
				nameValuePairs.add(new BasicNameValuePair("limit",limit));
		    }
			
			HttpPostConnector conn = new HttpPostConnector(Constants.HTTP_HOST
					+ "getmessages", nameValuePairs);
			String response = conn.getResponseData();
			
			try {
				JSONObject jsonObj = new JSONObject(response);
				if (jsonObj.getString("status").equals("success")) {
					
					JSONArray jsonMails = jsonObj.getJSONArray("mails");
					
					int count = jsonMails.length();
					
					result = parseMailData(jsonMails);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<MailData> result) {
			super.onPostExecute(result);
			
			messages = result;
			if(progressBar != null && progressBar.isShowing())
				progressBar.dismiss();
			
			mListViewAdapter.refereshAdapter(messages);
		}
	}
	
	private void showAlertDialog(String s) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, s, duration);
		toast.show();
	}
	
	private ArrayList<MailData> parseMailData(JSONArray jsonMails){
		
		ArrayList<MailData> result = new ArrayList<MailData>();
		
		try {
			
			Map<String, MailData> map = new HashMap<String, MailData>();
			
			for(int i = 0; i < jsonMails.length(); i++){
				JSONObject jMail = jsonMails.getJSONObject(i);
				String jobId = jMail.getJSONObject("Pushnotification").getString("job_id");
				String status = jMail.getJSONObject("Pushnotification").getString("status");
				String from_user_id = jMail.getJSONObject("Pushnotification").getString("from_user_id");
				
				if(from_user_id.equals("0"))
					status = "3";
				
				
				String key = jobId + "_" + status;
				
				if(map.containsKey(key)){
					map.get(key).addMail(new Mail(jMail));
				}else{
					MailData mailData = new MailData();
					mailData.setJobId(jobId);
					mailData.setStatus(status);
					mailData.addMail(new Mail(jMail));
					
					map.put(key, mailData);
				}
			}
			
			for(Entry<String, MailData> entry: map.entrySet()) {
				result.add(entry.getValue());
		    }
			
			Collections.sort(result, new Comparator<MailData>() {

		        public int compare(MailData o1, MailData o2) {
		            return o2.getDate().compareTo(o1.getDate());
		        }
		    });

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
