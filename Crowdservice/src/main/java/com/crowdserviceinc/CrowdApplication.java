package com.crowdserviceinc;

import com.crowdserviceinc.crowdservice.util.Constants;
import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
//import com.nullwire.trace.ExceptionHandler;

import android.app.Application;
import android.content.Context;

public class CrowdApplication extends Application {
	@Override
	public void onCreate() {
		
		super.onCreate();

		FlurryAgent.setLogEnabled(true);
		FlurryAgent.setLogEvents(true);
		FlurryAgent.setCaptureUncaughtExceptions(true);
		
        // init Flurry
        FlurryAgent.init(this, Constants.FLURRY_KEY);
        
//        ExceptionHandler.register(this, Constants.SERVER_ERROR_LOG_URL);
        
		initImageLoader(getApplicationContext());
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
		
		
	}
}
