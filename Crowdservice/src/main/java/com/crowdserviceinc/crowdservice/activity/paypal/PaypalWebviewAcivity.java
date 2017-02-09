package com.crowdserviceinc.crowdservice.activity.paypal;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

public class PaypalWebviewAcivity extends Activity {

	private WebView webView;
	private EditText urlEditText;
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webviewlayout);

		urlEditText = (EditText) findViewById(R.id.urlField);
		webView = (WebView) findViewById(R.id.webView);
		webView.setWebChromeClient(new MyWebViewClient());

		progress = (ProgressBar) findViewById(R.id.progressBar);
		progress.setMax(100);

		Button openUrl = (Button) findViewById(R.id.goButton);
		openUrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				String url = urlEditText.getText().toString();
				if (validateUrl(url)) {
					webView.getSettings().setJavaScriptEnabled(true);
					PaypalWebviewAcivity.this.progress.setProgress(0);
				}
			}

		});

		String url = getIntent().getStringExtra("url");
		if (validateUrl(url)) {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(url);
			PaypalWebviewAcivity.this.progress.setProgress(0);
		}
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.e("VIPIII", "WebView Test " + url);

				if(url.contains("paymentReturn")){
					// Fund Success. Please add mixpanel code here

					MixpanelAPI mixpanel = MixpanelAPI.getInstance(PaypalWebviewAcivity.this, Constants.MIXPANEL_TOKEN);
					JSONObject acceptBidEvent = new JSONObject();

					String userName = Constants.username;
					String userId = Constants.uid;

					try {
//				acceptBidEvent.put("BidUserId",bd.getId());
//						acceptBidEvent.put("JobId", jobid);
						acceptBidEvent.put("PostedUserId", userId);
						acceptBidEvent.put("User", userName);

						mixpanel.track("AcceptBidEvent", acceptBidEvent);
					}catch (JSONException e){

					}

				}else if(url.contains("paymentCancel")){
					// Fund Failed.
				}

				finish();
				return false;
			}
		});

		/*
		NSString *html = [webView stringByEvaluatingJavaScriptFromString:@"document.body.innerHTML"];

		if ([html rangeOfString:@"paypalReturn"].location != NSNotFound ||
		[html rangeOfString:@"paypalCancel"].location != NSNotFound)
		{
			if ([html rangeOfString:@"paypalReturn"].location != NSNotFound){
			NSLog(@"Paypal Return");
		*/
	}

	private boolean validateUrl(String url) {
		return true;
	}

	private class MyWebViewClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			PaypalWebviewAcivity.this.setValue(newProgress);

			if (newProgress == 100) {
				progress.setVisibility(View.GONE);
			}
		}

	}

	public void setValue(int progress) {
		this.progress.setProgress(progress);
	}

}
