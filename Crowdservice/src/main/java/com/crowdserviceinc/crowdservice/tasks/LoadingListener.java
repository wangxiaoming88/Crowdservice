package com.crowdserviceinc.crowdservice.tasks;

public interface LoadingListener {
    
	public void onLoadingComplete(Object obj);
	
    public void onError(Object error);
};

