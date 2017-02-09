package com.crowdserviceinc.crowdservice.activity.jobcreate;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.adapter.SpinnerCustomAdapter;
import com.crowdserviceinc.crowdservice.model.Category;
import com.crowdserviceinc.crowdservice.tasks.LoadCategoriesTask;
import com.crowdserviceinc.crowdservice.tasks.LoadingListener;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.util.ShowDialog;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryFragment extends Fragment implements OnClickListener{

	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	private Spinner spinnerCategory;
	private EditText edtJob, edtMerchandiseCost;
	private AutoCompleteTextView searchTextView;
	private ImageView btnNext;
	private Button btnSendNewCategory;
	private SpinnerCustomAdapter spinnerAdapter;
	private ArrayList<Category> allCatList, homeCatList, autoCatList, courierCatList, currentCatList;
	private Category selectedCategory;
	
	RadioButton btnCourier, btnHome, btnAuto;

	private ArrayList<String> allCatNameList = new ArrayList<String>();
	
	private LoadingListener listener = new LoadingListener() {
		
		@Override
		public void onLoadingComplete(Object obj) {
			// TODO Auto-generated method stub
			
			displayCategories();
		}
		
		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			ShowDialog.showAlertDialog(getActivity(), "API Error", "There might be some error in API. Please try later.");
		}
	};
	
	public CategoryFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initViews();
		
		allCatList = new ArrayList<Category>();
		homeCatList = new ArrayList<Category>();
		autoCatList = new ArrayList<Category>();
		courierCatList = new ArrayList<Category>();
		currentCatList = new ArrayList<Category>();
		
		selectedCategory = null;
		
		if(Constants.courierCategories == null || Constants.courierCategories.size() == 0){
			loadCategories();
		}else{
			displayCategories();
		}
		
		/*
		if (Constants.isNetAvailable(getActivity())) {
			ServerCommunication download = new ServerCommunication();
			download.execute(new String[] { "" });
		} else {
			Constants.NoInternetError(getActivity());
		}
		*/
	}

	private void loadCategories(){
		if (Constants.isNetAvailable(getActivity())) {
			new LoadCategoriesTask(getActivity(), listener).execute();
		} else {
			Constants.NoInternetError(getActivity());
		}
	}
	
	private void displayCategories(){
		
		courierCatList = Constants.courierCategories;
		homeCatList = Constants.homeCategories;
		autoCatList = Constants.autoCategories;
		
		allCatList.addAll(courierCatList);
		allCatList.addAll(homeCatList);
		allCatList.addAll(autoCatList);
				
		for(Category cat : allCatList){
			allCatNameList.add(cat.getName());
		}
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, allCatNameList);
		
		searchTextView.setThreshold(1);					    
		searchTextView.setAdapter(adapter);
		searchTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
				// TODO Auto-generated method stub
				
				int index = allCatNameList.indexOf(adapter.getItem(pos)); 
				
				Log.d("Selected Category ", adapter.getItem(pos));
				
				selectedCategory = allCatList.get(index);
				
				if(homeCatList.contains(selectedCategory)){
					btnHome.performClick();
				}else if(courierCatList.contains(selectedCategory)){
					btnCourier.performClick();
				}else if(autoCatList.contains(selectedCategory)){
					btnAuto.performClick();
				}
			}
			
		});
	    
		currentCatList = courierCatList;
		refreshCategorySpinner();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_categoryselection,
				container, false);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	/*
	 * Called To initialize to user interface.
	 */
	private void initViews() {
		spinnerCategory = (Spinner) getView().findViewById(R.id.spinner);
		edtJob = (EditText) getView().findViewById(R.id.edtJobname);
		edtMerchandiseCost = (EditText) getView().findViewById(
				R.id.edtMerchandiseCost);
		
		searchTextView = (AutoCompleteTextView) getView().findViewById(R.id.searchTextView);
		
		btnNext = (ImageView) getView().findViewById(R.id.btnNext);
		btnSendNewCategory = (Button) getView().findViewById(
				R.id.btnSendNewCategory);
		btnNext.setOnClickListener(this);
		btnSendNewCategory.setOnClickListener(this);

		btnCourier = (RadioButton) getView().findViewById(R.id.btnCourier);
		btnCourier.setOnClickListener(this);

		btnHome = (RadioButton) getView().findViewById(R.id.btnHome);
		btnHome.setOnClickListener(this);

		btnAuto = (RadioButton) getView().findViewById(R.id.btnAuto);
		btnAuto.setOnClickListener(this);

		MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), Constants.MIXPANEL_TOKEN);
		JSONObject requestServiceEvent = new JSONObject();

		String userId = Constants.uid;
		String userName = Constants.username;

		try {
			requestServiceEvent.put("User", userName);
			requestServiceEvent.put("UserId", userId);
			mixpanel.track("RequestServiceEvent", requestServiceEvent);
		}catch (JSONException e){

		}

		btnCourier.setSelected(true);
		edtMerchandiseCost
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							if (edtMerchandiseCost.getText().toString() != null
									&& edtMerchandiseCost.getText().toString()
											.length() == 0) {
								edtMerchandiseCost.setText("$");
								edtMerchandiseCost.setSelection(1);
							}
						}

					}
				});
	}

	@Override
	public void onClick(View v) {

		if (v == btnNext) {
			if (currentCatList == null || currentCatList.size() == 0) {
				showAlertDialog("Please select category !");
			} else if (edtJob.getText().toString().length() == 0) {
				showAlertDialog("Please enter job !");
			} else {
				Constants.hasjob_address = true;
				if (edtJob.hasFocus())
					hideSoftInput(edtJob);
				if (edtMerchandiseCost.hasFocus())
					hideSoftInput(edtMerchandiseCost);

				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();

				Fragment fragment = new AddressSelectionFragment();
				int index = spinnerCategory.getSelectedItemPosition();
				String id = currentCatList.get(index).getId();
				String category_name = currentCatList.get(index).getName();
				Bundle bundle = new Bundle();
				bundle.putString("CategoryId", id);
				bundle.putString("category_name", category_name);
				bundle.putString("JobName", edtJob.getText().toString());
				bundle.putString("Price", edtMerchandiseCost.getText()
						.toString());
				fragment.setArguments(bundle);

				fragmentTransaction.replace(R.id.mainContainer, fragment,
						"Address");
				fragmentTransaction.addToBackStack("Category");
				fragmentTransaction.commit();
			}

		} else if (v == btnSendNewCategory) {
			String to = "Gofer.CategorySuggestions@Crowdserviceinc.com";
			String subject = "Suggest a new category";
			Intent email = new Intent(Intent.ACTION_SEND);
			email.setType("text/html");
			email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
			email.putExtra(Intent.EXTRA_SUBJECT, subject);
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email,
					"Choose an Email client :"));

		} else if (v == btnHome) {
			btnCourier.setSelected(false);
			btnHome.setSelected(true);
			btnAuto.setSelected(false);
			//ServerCommunication download = new ServerCommunication();
			//download.execute(new String[] { "" });
			
			currentCatList = homeCatList;
			refreshCategorySpinner();
			
		} else if (v == btnCourier) {
			btnCourier.setSelected(true);
			btnHome.setSelected(false);
			btnAuto.setSelected(false);
			
			currentCatList = courierCatList;
			refreshCategorySpinner();
			
		} else if (v == btnAuto) {
			btnCourier.setSelected(false);
			btnHome.setSelected(false);
			btnAuto.setSelected(true);
			
			currentCatList = autoCatList;
			refreshCategorySpinner();
		}

	}

	private void refreshCategorySpinner(){
		if (currentCatList != null) {
			spinnerAdapter = new SpinnerCustomAdapter(
					getActivity(), R.layout.row_spinner, currentCatList);
			// spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
			spinnerCategory.setAdapter(spinnerAdapter);
			
			if(selectedCategory != null){
				int position = spinnerAdapter.getPosition(selectedCategory);
				spinnerCategory.setSelection(position);
			}
		}
	}
	private void showAlertDialog(String s) {
		Context context = getActivity().getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, s, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public void hideSoftInput(View view) {
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void onBack() {
		FragmentManager fm = getFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
		}
	}
}
