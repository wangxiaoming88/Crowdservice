package com.crowdserviceinc.crowdservice.activity.login;

import java.net.URL;
import java.util.Arrays;

import org.json.JSONException;

import com.crowdserviceinc.crowdservice.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
//
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.Session.StatusCallback;
//import com.facebook.SessionDefaultAudience;
//import com.facebook.SessionLoginBehavior;
//import com.facebook.SessionState;
//import com.facebook.internal.SessionTracker;
//import com.facebook.internal.Utility;
//import com.facebook.model.GraphUser;

public class SinupOptionActivity extends Activity {

	private Button fbButton = null;
//	private SessionTracker mSessionTracker;
//	private Session mCurrentSession;
	public static Bitmap image = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		setContentView(R.layout.activity_signup_option);

		image = null;
		fbButton = (Button)findViewById(R.id.facebook_register);
		fbButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//signInWithFacebook();
				fbButton.setText("Please wait...");
			}
		});

		Button emailButton = (Button)findViewById(R.id.email_register);
		emailButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SinupOptionActivity.this,SignupActivity.class);
				startActivity(intent);		
			}
		});
		
		TextView back = (TextView)findViewById(R.id.btnBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

//
//
//	private void signInWithFacebook() 
//	{
//		mSessionTracker = new SessionTracker(SinupOptionActivity.this, new StatusCallback() {
//			@Override
//			public void call(Session session, SessionState state, Exception exception) {
//			}
//
//		}, null, false);
//
//		String applicationId = Utility.getMetadataApplicationId(this);
//		mCurrentSession = mSessionTracker.getSession();
//		if (mCurrentSession == null || mCurrentSession.getState().isClosed()) {
//			System.out.println("mCurrentSession == null");
//			mSessionTracker.setSession(null);
//			Session session = new Session.Builder(this).setApplicationId(applicationId).build();
//			Session.setActiveSession(session);
//			mCurrentSession = session;
//		}
//		if (mCurrentSession != null && !mCurrentSession.isOpened())
//		{
//			System.out.println("Session opened");
//			Session.OpenRequest openRequest = null;
//			openRequest = new Session.OpenRequest(this);
//			if (openRequest != null) {
//				openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
//				openRequest.setPermissions(Arrays.asList("email"));
//				openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
//				mCurrentSession.openForRead(openRequest);
//			}
//		}else{
//			System.out.println("Session closed");
//			Request.executeMeRequestAsync(mCurrentSession, new Request.GraphUserCallback() {
//
//
//				@Override
//				public void onCompleted(GraphUser user, Response response) {
//					System.out.println("Session onCompleted");
//					Intent intent = new Intent(SinupOptionActivity.this,SignupActivity.class);
//					String accesstoken = mCurrentSession.getAccessToken();
//					if(user != null){
//						String firstName = user.getFirstName();
//						String lastName = user.getLastName();
//						String userName = user.getUsername();
//						//String email = user.asMap().get("email").toString();
//						//image = getOwnPic(accesstoken);
//						if(user.getInnerJSONObject().has("id"))
//							try {
//								System.out.println("Session onCompleted id found");
//								image = getOwnPic(user.getInnerJSONObject().getString("id"));
//							} catch (JSONException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//						
//						String email = "";
//						if(user.getInnerJSONObject().has("email"))
//							try {
//								email = user.getInnerJSONObject().getString("email");
//								if(userName == null || userName.length() == 0)
//									userName = user.getInnerJSONObject().getString("username");
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						
//						intent.putExtra("fName", firstName);
//						intent.putExtra("lName", lastName);
//						intent.putExtra("userName", userName);
//						intent.putExtra("email", email);
//						System.out.println("User  " + firstName);
//					}
//					else
//						System.out.println("User  null");
//
//					fbButton.setText("Register with Facebook");
//					startActivity(intent);	
//				}
//			});
//		}
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
//
//		Session.openActiveSession(this, true, new StatusCallback() {
//			@Override
//			public void call(Session session, SessionState state, Exception exception) {
//				if (mCurrentSession.isOpened()) {
//					String accesToken = mCurrentSession.getAccessToken();
//
//					Intent intent = new Intent(SinupOptionActivity.this,SignupActivity.class);
//					startActivity(intent);	
//				}
//				else
//				{
//					fbButton.performClick();
//				}
//			}
//		});
//
//	}
//


	public Bitmap getOwnPic(String id) {
		Bitmap mIcon1 = null;

		URL img_value = null;
		try {
			
			//img_value = new URL("https://graph.facebook.com/me/picture?access_token=" + id);
			img_value = new URL("http://graph.facebook.com/" + id + "/picture?type=large");
			mIcon1 = BitmapFactory.decodeStream(img_value.openConnection()
					.getInputStream());
			System.out.println("Image found");
		} catch (Exception e) {
			System.out.println("Image not found");
			e.printStackTrace();
		}

		return mIcon1;
	}
}
