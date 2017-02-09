package com.crowdserviceinc.crowdservice.activity.mailbox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.activity.ParentActivity;
import com.crowdserviceinc.crowdservice.activity.SendMessageActivity;
import com.crowdserviceinc.crowdservice.adapter.MailAnswerListingAdapter;
import com.crowdserviceinc.crowdservice.adapter.MailDetailListingAdapter;
import com.crowdserviceinc.crowdservice.model.Mail;
import com.crowdserviceinc.crowdservice.model.MailData;
import com.crowdserviceinc.crowdservice.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MailDetailActivity extends ParentActivity implements OnClickListener {

	private MailData message;
	
	private TextView txtJobName, txtJobDate, txtProviderName, txtCustomerName;
	private Button btnGoContact;
	private LinearLayout llUser;

	private TextView btnBack;

	private ListView mListView;
	private SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy' at 'HH:mm a");;
	
	private int SEND_MENSSAGE_REQEUST_CODE = 1110;
	
	private int messageType = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_detail);
		
		message = (MailData)getIntent().getSerializableExtra("message");
		
		messageType = Integer.parseInt(message.getStatus());
			
		initView();
		populateData();
	}
	
	private void initView(){
		txtJobName = (TextView) findViewById(R.id.txtJobName);
		txtJobDate = (TextView) findViewById(R.id.txtJobDate);
		txtProviderName = (TextView) findViewById(R.id.txtProviderName);
		txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);

		btnBack = (TextView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);

		btnGoContact = (Button) findViewById(R.id.btnGoContact);	
		llUser = (LinearLayout) findViewById(R.id.layout_user);
		
		mListView = (ListView) findViewById(R.id.listview_messagedatail);
		
		if(messageType == 2 || messageType == 3){
			llUser.setVisibility(View.GONE);
			btnGoContact.setVisibility(View.GONE);
		}else{
			btnGoContact.setOnClickListener(this);
		}
	}
	
	private void populateData(){
		
		Mail mail = message.getMails().get(0);
		
		txtJobName.setText(mail.getJobName());
		txtJobDate.setText(outputFormat.format(mail.getDate()));
		
		if(messageType == 1){
			
			String customerName = "";
			String providerName = "";
			
			if(Constants.uid.equals(mail.getJobCreator())){
				
				customerName = "Me";
				providerName = Constants.uid.equals(mail.getFromUserId()) ? mail.getToUserName() : mail.getFromUserName();
			}else{
				
				providerName = "Me";
				customerName = Constants.uid.equals(mail.getFromUserId()) ? mail.getToUserName() : mail.getFromUserName();
			}
			
			txtCustomerName.setText(customerName);
			txtProviderName.setText(providerName);
			
			MailDetailListingAdapter mListViewAdapter = new MailDetailListingAdapter(getLayoutInflater(), message.getMails());
			mListView.setAdapter(mListViewAdapter);
			
			
		}else if(messageType == 2){
			
			ArrayList<Mail> messages = new ArrayList<Mail>();
			HashMap<String, ArrayList<Mail>> temp = new HashMap<String, ArrayList<Mail>>();
			ArrayList<Mail> answers = new ArrayList<Mail>();
			
			for(Mail m : message.getMails()){
				
				if(Integer.parseInt(m.getParentId()) > 0){
					if(temp.containsKey(m.getParentId())){
						answers = temp.get(m.getParentId());
					}else{
						answers = new ArrayList<Mail>();
					}
					
					answers.add(m);
					
					temp.put(m.getParentId(), answers);
					
				}else{
					messages.add(m);
				}
			}
			
			for(Mail m : messages){
				m.setAnswers(temp.get(m.getQuestionId()));
			}
			
			MailAnswerListingAdapter mListViewAdapter = new MailAnswerListingAdapter(this, messages);
			mListView.setAdapter(mListViewAdapter);
			
		}else if(messageType == 3){
			
			MailDetailListingAdapter mListViewAdapter = new MailDetailListingAdapter(getLayoutInflater(), message.getMails());
			mListView.setAdapter(mListViewAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == btnGoContact){
			
			Mail mail = message.getMails().get(0);
			
			String toUserId = Constants.uid.equals(mail.getFromUserId()) ? mail.getToUserId() : mail.getFromUserId();
			String toUserName = Constants.uid.equals(mail.getFromUserName()) ? mail.getToUserName() : mail.getFromUserName();
			
			Intent intent  = new Intent(MailDetailActivity.this, SendMessageActivity.class);
			
			intent.putExtra("job_id", mail.getJobId());
			intent.putExtra("from_user_id", Constants.uid);
			intent.putExtra("to_user_id", toUserId);
			intent.putExtra("to_user_name", toUserName);
			intent.putExtra("from_mailbox", true);
			startActivityForResult(intent, SEND_MENSSAGE_REQEUST_CODE);
			
		}else if (v == btnBack) {
			finish();

		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == SEND_MENSSAGE_REQEUST_CODE){
			if (resultCode == Activity.RESULT_OK) {
				finish();
			}
		}
	}
}
