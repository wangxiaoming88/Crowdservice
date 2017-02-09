package com.crowdserviceinc.crowdservice.activity.settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.activity.ParentActivity;
import com.crowdserviceinc.crowdservice.model.ResultData;
import com.crowdserviceinc.crowdservice.util.Constants;

public class DisputeActivity extends ParentActivity implements OnClickListener {

	EditText edityourname, editothername, editjobdate, editjobname,
			editdiscript;
	Button btnshow_traffic;
	TextView btnBack;
	private ProgressDialog progressBar;
	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;

	Calendar myCalendar = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dispute_detail);
		Log.e("VIPI", "OP > " + Constants.uid);
		init();
	}

	private void init() {
		edityourname = (EditText) findViewById(R.id.edityourname);
		editothername = (EditText) findViewById(R.id.editothername);
		editjobdate = (EditText) findViewById(R.id.editjobdate);
		editjobname = (EditText) findViewById(R.id.editjobname);
		editdiscript = (EditText) findViewById(R.id.editdiscript);
		btnshow_traffic = (Button) findViewById(R.id.btnshow_traffic);
		btnBack = (TextView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnshow_traffic.setOnClickListener(this);
		//editjobdate.addTextChangedListener(tw);
		
		editjobdate.setOnClickListener(new OnClickListener() {

	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            new DatePickerDialog(DisputeActivity.this, date, myCalendar
	                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
	                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
	        }
	    });
	}
	
	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

	    @Override
	    public void onDateSet(DatePicker view, int year, int monthOfYear,
	            int dayOfMonth) {
	        // TODO Auto-generated method stub
	        myCalendar.set(Calendar.YEAR, year);
	        myCalendar.set(Calendar.MONTH, monthOfYear);
	        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	        updateLabel();
	    }

	};
	
	private void updateLabel() {

	    String myFormat = "MM/dd/yyyy"; //In which you need put here
	    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

	    editjobdate.setText(sdf.format(myCalendar.getTime()));
	}
	
	@Override
	public void onClick(View v) {
		if (v == btnshow_traffic) {
			doSubmit();
		}

		if (v == btnBack) {
			onBackPressed();
		}
	}

	/*
	 * To Submit Record.
	 */
	private void doSubmit() {
		progressBar = new ProgressDialog(DisputeActivity.this);
		progressBar.setMessage("Please wait...");
		progressBar.setCancelable(false);
		progressBar.show();

		Thread thr = new Thread(new Runnable() {
			// @Override
			public void run() {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("id", Constants.uid));

				// @Ashish
				MultipartEntity reqEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					reqEntity.addPart("id", new StringBody(Constants.uid));

					reqEntity.addPart("job_date", new StringBody(editjobdate
							.getText().toString()));

					reqEntity.addPart("job_name", new StringBody(editjobname
							.getText().toString()));

					reqEntity.addPart("other_name", new StringBody(
							editothername.getText().toString()));

					reqEntity.addPart("your_name", new StringBody(edityourname
							.getText().toString()));

					reqEntity.addPart("dispute_description", new StringBody(
							editdiscript.getText().toString()));

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(Constants.HTTP_HOST
						+ "dispute");
				httppost.setEntity(reqEntity);

				// Execute HTTP Post Request
				HttpResponse response = null;
				try {
					response = httpclient.execute(httppost);
					Log.e("Send", "Res >> " + response);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.e("Response when submitting request", "" + response);
				HttpEntity entity = response.getEntity();
				ResultData resultData = null;

				try {

					InputStream stream = entity.getContent();
					String strResponse = convertStreamToString(stream);
					JSONObject jsonresponse = new JSONObject(strResponse);
					Log.e("VIPI", "Response >" + jsonresponse);
					resultData = new ResultData();
					resultData.setAuthenticated(jsonresponse
							.getString("status"));
					try {
						resultData
								.setMessage(jsonresponse.getString("message"));
						// resultData.setMessage(jsonresponse.getString("id"));
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				progressBar.dismiss();

				Message msg = handler.obtainMessage();
				if (resultData != null) {
					msg.obj = resultData;
					msg.arg1 = SUCCESS;
				} else {
					msg.arg1 = FAILURE;
				}
				handler.sendMessage(msg);

			}
		});
		thr.start();
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
	 * Handler.
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.obj instanceof ResultData) {
				ResultData data = (ResultData) msg.obj;
				if (data.getAuthenticated() != null
						&& data.getAuthenticated().equalsIgnoreCase("success")) {
					
					String message = "Thank you. A Crowdservice team member will contact you as soon as possible about this problem.";
					showAlert(message, true);
				} else {
					// showAlertDialog(data.getMessage());
					showAlert(data.getMessage(), false);
				}
			}

		}
	};

	private void showAlert(String message, final boolean is_success) {
		new AlertDialog.Builder(DisputeActivity.this).setTitle("Dispute Add")
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// continue with delete
						if (is_success) {
							DisputeActivity.this.finish();
						} else {
							// dismissDialog(id)
						}
					}
				}).setIcon(android.R.drawable.ic_dialog_info).show();
	}

	TextWatcher tw = new TextWatcher() {
		private String current = "";
		private String mmddyyyy = "MMDDYYYY";
		private Calendar cal = Calendar.getInstance();

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (!s.toString().equals(current)) {
				String clean = s.toString().replaceAll("[^\\d.]", "");
				String cleanC = current.replaceAll("[^\\d.]", "");

				int cl = clean.length();
				int sel = cl;
				for (int i = 2; i <= cl && i < 6; i += 2) {
					sel++;
				}
				// Fix for pressing delete next to a forward slash
				if (clean.equals(cleanC))
					sel--;

				if (clean.length() < 8) {
					clean = clean + mmddyyyy.substring(clean.length());
				} else {
					// This part makes sure that when we finish entering numbers
					// the date is correct, fixing it otherwise
					int mon = Integer.parseInt(clean.substring(0, 2));
					int day = Integer.parseInt(clean.substring(2, 4));
					int year = Integer.parseInt(clean.substring(4, 8));

					if (mon > 12)
						mon = 12;
					cal.set(Calendar.MONTH, mon - 1);
					year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
					cal.set(Calendar.YEAR, year);
					// ^ first set year for the line below to work correctly
					// with leap years - otherwise, date e.g. 29/02/2012
					// would be automatically corrected to 28/02/2012

					day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal
							.getActualMaximum(Calendar.DATE) : day;
					clean = String.format("%02d%02d%02d", mon, day, year);
				}

				clean = String.format("%s/%s/%s", clean.substring(0, 2),
						clean.substring(2, 4), clean.substring(4, 8));

				sel = sel < 0 ? 0 : sel;
				current = clean;
				editjobdate.setText(current);
				editjobdate.setSelection(sel < current.length() ? sel : current
						.length());
			}
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}
	};
}
