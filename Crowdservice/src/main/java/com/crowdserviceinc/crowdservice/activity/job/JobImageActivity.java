package com.crowdserviceinc.crowdservice.activity.job;

import com.crowdserviceinc.crowdservice.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class JobImageActivity extends Activity implements OnClickListener {

	private ImageView imageView = null;
	private ProgressBar bar = null;
	private String imageUrl = "";
	private Bitmap bitmap = null;
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_job_image);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.com_facebook_profile_picture_blank_square)
				.showImageForEmptyUri(R.drawable.com_facebook_profile_picture_blank_square)
				.showImageOnFail(R.drawable.com_facebook_profile_picture_blank_square)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				//.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
				.build();
		
		TextView back = (TextView) findViewById(R.id.btnBack);
		back.setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);

		title.setText(getIntent().getStringExtra("job_name"));

		bar = (ProgressBar) findViewById(R.id.progress_bar);
		imageView = (ImageView) findViewById(R.id.job_image);

		imageUrl = getIntent().getStringExtra("job_image_url");

		if (imageUrl.contains("http")) {
			//loadr.DisplayImage(imageUrl, imageView, false);
			ImageLoader.getInstance().displayImage(imageUrl, imageView, options);
		} else {
			//loadr.displayImage("file://" + imageUrl, imageView, bar);
			ImageLoader.getInstance().displayImage("file://" + imageUrl, imageView, options);
		}
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnBack) {
			finish();
			overridePendingTransition(0, R.anim.slide_top_to_bottom);
		}
	}

}
