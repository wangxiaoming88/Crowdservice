package com.crowdserviceinc.crowdservice.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crowdserviceinc.crowdservice.model.Category;
import com.crowdserviceinc.crowdservice.parser.CategoryPostParser;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.util.ShowDialog;
import com.crowdserviceinc.crowdservice.util.http.HttpPostConnector;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class LoadCategoriesTask extends AsyncTask<String, String, Boolean> {

	private static final String API_URL = Constants.HTTP_HOST + "getCategories";
	private LoadingListener mListener;
	private Activity mContext;
	
	public LoadCategoriesTask(Activity activity, LoadingListener listener) {
		// TODO Auto-generated constructor stub
		
		mContext = activity;
		mListener = listener;
		
		Constants.courierCategories.clear();
		Constants.homeCategories.clear();
		Constants.autoCategories.clear();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		ShowDialog.showLoadingDialog(mContext, "Please wait while loading...");
	}
	
	@Override
	protected Boolean doInBackground(String... strParams) {
	
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		HttpPostConnector conn = new HttpPostConnector(API_URL, nameValuePairs);
		String response = conn.getResponseData();
		Log.e("TAGG", "response Cat > " + response);
		//{"categories":[{"Category":{"id":"5","name":"GROCERIES - BUY","category_type":"courier"}},{"Category":{"id":"4","name":"HARDWARE - LIGHT","category_type":"courier"}},{"Category":{"id":"6","name":"GROCERY - FARMER'S MARKET","category_type":"courier"}},{"Category":{"id":"7","name":"GIFT - DELIVER FROM ME","category_type":"courier"}},{"Category":{"id":"8","name":"GIFT - PURCHASE AND DELIVER","category_type":"courier"}},{"Category":{"id":"9","name":"DOCUMENTS - DELIVER","category_type":"courier"}},{"Category":{"id":"10","name":"MOVING - LOADING HELP","category_type":"courier"}},{"Category":{"id":"11","name":"PHARMACY PICKUP","category_type":"courier"}},{"Category":{"id":"12","name":"DOCUMENTS - FETCH","category_type":"courier"}},{"Category":{"id":"64","name":"FURNITURE - ASSEMBLE","category_type":"home"}},{"Category":{"id":"14","name":"HOLD MY PLACE IN LINE","category_type":"courier"}},{"Category":{"id":"15","name":"STORE PICKUP - PAID FOR","category_type":"courier"}},{"Category":{"id":"16","name":"MERCHANDIZE - GENERAL BUY AND DELIVER","category_type":"courier"}},{"Category":{"id":"17","name":"MERCHANDIZE - GENERAL PAID FOR","category_type":"courier"}},{"Category":{"id":"20","name":"DRY CLEANING - DELIVER","category_type":"courier"}},{"Category":{"id":"21","name":"DRY CLEANING - FETCH","category_type":"courier"}},{"Category":{"id":"22","name":"CARPET - CLEAN","category_type":"home"}},{"Category":{"id":"23","name":"CARPET - INSTALL","category_type":"home"}},{"Category":{"id":"24","name":"PAINT - INTERIOR","category_type":"home"}},{"Category":{"id":"25","name":"PAINT - EXTERIOR","category_type":"home"}},{"Category":{"id":"26","name":"DONATE","category_type":"home"}},{"Category":{"id":"27","name":"TRASH - RECYCLE","category_type":"home"}},{"Category":{"id":"28","name":"REPAIRS - INTERIOR","category_type":"home"}},{"Category":{"id":"29","name":"REPAIRS - EXTERIOR","category_type":"home"}},{"Category":{"id":"30","name":"LIGHTBULB CHANGE","category_type":"home"}},{"Category":{"id":"31","name":"DOOR - INSTALL","category_type":"home"}},{"Category":{"id":"32","name":"WINDOW - CLEAN","category_type":"home"}},{"Category":{"id":"33","name":"HVAC","category_type":"home"}},{"Category":{"id":"34","name":"FURNITURE - REARRANGE","category_type":"home"}},{"Category":{"id":"59","name":"TAKE OUT - PAID","category_type":"courier"}},{"Category":{"id":"36","name":"PETS - FEED","category_type":"home"}},{"Category":{"id":"37","name":"PETS - WALK","category_type":"home"}},{"Category":{"id":"38","name":"MOVE - PACK\/LOAD","category_type":"home"}},{"Category":{"id":"39","name":"MOVE - NEED TRUCK","category_type":"home"}},{"Category":{"id":"40","name":"POOL - CLEAN","category_type":"home"}},{"Category":{"id":"41","name":"POOL - MAINTAIN","category_type":"home"}},{"Category":{"id":"42","name":"SNOW - SHOVEL","category_type":"home"}},{"Category":{"id":"43","name":"DECORATE - INTERIOR","category_type":"home"}},{"Category":{"id":"44","name":"DECORATE - EXTERIOR","category_type":"home"}},{"Category":{"id":"45","name":"CLEAN GUTTERS","category_type":"home"}},{"Category":{"id":"46","name":"PLUMBING","category_type":"home"}},{"Category":{"id":"47","name":"CLEANING - INTERIOR","category_type":"home"}},{"Category":{"id":"48","name":"CLEANING - EXTERIOR","category_type":"home"}},{"Category":{"id":"49","name":"ENTERTAINMENT SYSTEM","category_type":"home"}},{"Category":{"id":"50","name":"WINDOW - REPAIR\/INSTALL","category_type":"home"}},{"Category":{"id":"52","name":"DETAIL - OUTSIDE","category_type":"auto"}},{"Category":{"id":"57","name":"BODY WORK","category_type":"auto"}},{"Category":{"id":"55","name":"DETAIL - OUTSIDE\/INSIDE","category_type":"auto"}},{"Category":{"id":"56","name":"CHANGE TIRE - HAVE SPARE","category_type":"auto"}},{"Category":{"id":"58","name":"DIAGNOSE FAULT CODE","category_type":"auto"}},{"Category":{"id":"60","name":"TAKE OUT - BUY","category_type":"courier"}},{"Category":{"id":"63","name":"MOVE - HAVE TRUCK","category_type":"home"}},{"Category":{"id":"62","name":"LIVE MUSIC","category_type":"home"}},{"Category":{"id":"65","name":"FURNITURE - REPAIR","category_type":"home"}},{"Category":{"id":"66","name":"TRASH - DUMP","category_type":"home"}},{"Category":{"id":"67","name":"CLEANING - YARD","category_type":"home"}},{"Category":{"id":"68","name":"TOW NEEDED","category_type":"auto"}},{"Category":{"id":"69","name":"TRANSPORT CAR","category_type":"auto"}},{"Category":{"id":"70","name":"GARDEN  WORK","category_type":"home"}},{"Category":{"id":"71","name":"FAST FOOD","category_type":"courier"}},{"Category":{"id":"72","name":"CONSULTATION - HOME REPAIR","category_type":"home"}},{"Category":{"id":"73","name":"PACKAGE AND SHIP","category_type":"courier"}},{"Category":{"id":"74","name":"PACKAGE - ACCEPT DELIVERY","category_type":"courier"}},{"Category":{"id":"75","name":"COMPUTER SUPPORT","category_type":"home"}},{"Category":{"id":"76","name":"TECHNICAL SUPPORT - GENERAL","category_type":"home"}},{"Category":{"id":"77","name":"EQUIPMENT RENTAL","category_type":"home"}},{"Category":{"id":"78","name":"BUY ME A COFFEE","category_type":"courier"}}],"success":true,"message":"Results found"}
		if (response != null) {
			JSONObject jsonresponse;
			try {
				jsonresponse = new JSONObject(response);
				if(jsonresponse.getBoolean("success")){
					
					JSONArray jCategories = jsonresponse.getJSONArray("categories");
					for(int i = 0; i < jCategories.length(); i++){
						
						JSONObject jobj = jCategories.getJSONObject(i).getJSONObject("Category");
						
						Category category = new Category(jobj);
						int catType = category.getType();
						if(catType == Constants.CATEGORY_TYPE_COURIER){
							Constants.courierCategories.add(category);							
						}else if(catType == Constants.CATEGORY_TYPE_HOME){
							Constants.homeCategories.add(category); 
						}else if(catType == Constants.CATEGORY_TYPE_AUTO){
							Constants.autoCategories.add(category);
						}
						
					}
					
					Collections.sort(Constants.courierCategories, new CustomComparator());
					Collections.sort(Constants.homeCategories, new CustomComparator());
					Collections.sort(Constants.autoCategories, new CustomComparator());
					
					return true;
					
				}else{
					return false;
				}
				
			}catch(JSONException e){
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		ShowDialog.removeLoadingDialog();
        if (mListener != null) {
        	if(result == true)
        		mListener.onLoadingComplete(null);
        	else
        		mListener.onError(null);
        }
	}
	
	public class CustomComparator implements Comparator<Category> {
	    @Override
	    public int compare(Category o1, Category o2) {
	        return o1.getName().compareTo(o2.getName());
	    }
	}
}
