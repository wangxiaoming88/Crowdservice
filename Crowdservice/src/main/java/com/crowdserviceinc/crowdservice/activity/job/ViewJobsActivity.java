package com.crowdserviceinc.crowdservice.activity.job;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.activity.ParentFragmentActivity;
import com.crowdserviceinc.crowdservice.util.Constants;

@SuppressLint("Recycle")
public class ViewJobsActivity extends ParentFragmentActivity implements
		OnClickListener {

	private FragmentManager fragmentManager;
	private BackListener backListner = null;
	private TextView title = null;
	private TextView viewjob_btnBack = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_viewjobs);
		Log.e("VIP", "ViewJobsActivity");
		
		Constants.is_customer = true;
		
		initViews();
	}

	private void initViews() {
		TextView back = (TextView) findViewById(R.id.viewjob_btnBack);
		back.setOnClickListener(this);
		title = (TextView) findViewById(R.id.viewjob_title);

		fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Fragment fragmentpublic = null;
		Log.e("VIPVIP", "Constants.userType  >" + Constants.userType);
		
		title.setText("Active Jobs");
		fragmentpublic = new ViewCustomerJobFragment();
		transaction.replace(R.id.mainContainer, fragmentpublic,
				"ViewCustomerJobFragment");
		
		transaction.commit();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.viewjob_btnBack) {
			if (backListner != null)
				backListner.backPressed();
			else
				finish();
		}
	}

	public void setTitle(String titleStr) {
		title.setText(titleStr);
	}

	// public void SetBackText(String backStr) {
	// viewjob_btnBack.setText(backStr);
	// }

	public interface BackListener {
		public void backPressed();
	}

	public void setBackListener(BackListener listener) {
		backListner = listener;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
