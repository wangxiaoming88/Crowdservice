package com.crowdserviceinc.crowdservice.activity.jobcreate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdserviceinc.crowdservice.R;
import com.crowdserviceinc.crowdservice.util.Constants;
import com.crowdserviceinc.crowdservice.widget.CustomDateTimePicker;
import com.crowdserviceinc.crowdservice.widget.CustomDateTimePicker.ICustomDateTimeListener;

public class EndAddressSelectionFragment extends Fragment implements
		OnClickListener, ICustomDateTimeListener {

	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	private EditText edtEndAddress;
	private TextView txtDate;
	private Button btnNext, btnPrevious, btnNow, btnTimeRange, btnApproxAddr,
			btnStartingAddr, btnCustom, btnTrueAddr;
	private String dateStr = "0-00-0000";
	private static final int DATE_DIALOG_ID = 0;
	protected static final int ADDRESS_NOT_VALID = 3;
	private int mYear = 2013;
	private int mMonth = 0;
	private int mDay = 1;
	private boolean isCustomPopOpen = false;
	Dialog alert;

	private CustomDateTimePicker startDate;
	String addr = "";

	public EndAddressSelectionFragment() {
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initViews();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_endaddress, container,
				false);
		return view;
	}

	/*
	 * Called to initialize to user interface.
	 */
	private void initViews() {
		edtEndAddress = (EditText) getView().findViewById(R.id.edtEndAddress);
		btnNext = (Button) getView().findViewById(R.id.btnNext);
		btnPrevious = (Button) getView().findViewById(R.id.btnPrevious);
		btnNow = (Button) getView().findViewById(R.id.btnNow);
		btnTimeRange = (Button) getView().findViewById(R.id.btnTimeRange);
		txtDate = (TextView) getView().findViewById(R.id.txtDate);

		btnApproxAddr = (Button) getView().findViewById(R.id.btnApproxAddr);
		btnStartingAddr = (Button) getView().findViewById(R.id.btnStartingAddr);
		btnCustom = (Button) getView().findViewById(R.id.btnCustom);
		btnTrueAddr = (Button) getView().findViewById(R.id.btnTrueAddr);

		btnNext.setOnClickListener(this);
		btnPrevious.setOnClickListener(this);
		btnNow.setOnClickListener(this);
		btnTimeRange.setOnClickListener(this);

		btnApproxAddr.setOnClickListener(this);
		btnStartingAddr.setOnClickListener(this);
		btnCustom.setOnClickListener(this);
		btnTrueAddr.setOnClickListener(this);

		edtEndAddress.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) { // TODO
				// Auto-generated method stub // do your stuff here..
				if (!isCustomPopOpen || !alert.isShowing()) {
					edtEndAddress.setEnabled(true); //
					edtEndAddress.setInputType(InputType.TYPE_CLASS_TEXT);
					edtEndAddress.setFocusable(true);
					customAddressAlert(edtEndAddress);
					isCustomPopOpen = true;
				}
				return true;
			}
		});
		SetPrefText();
	}

	private void SetPrefText() {
		if (!Constants.TempEndAddress.equals("")) {
			edtEndAddress.setText(Constants.TempEndAddress);
		}
		if (!Constants.TempEndTime.equals("")) {
			txtDate.setText(Constants.TempEndTime);
		}
		if (!Constants.TempEndTime.equals("")) {
			dateStr = Constants.TempEndTime;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnNow) {
			dateStr = Constants.getCurrentDateTime();
			txtDate.setText(dateStr);
			Log.d("End Date", dateStr);
		} else if (v == btnTimeRange) {
			startDate = new CustomDateTimePicker(getActivity(), this);
			startDate.set24HourFormat(false);
			startDate.setDate(Calendar.getInstance());
			startDate.showDialog();
		} else if (v == btnNext) {
			Constants.TempEndTime = txtDate.getText().toString();
			Constants.TempEndAddress = edtEndAddress.getText().toString();
			if (edtEndAddress.getText().toString().length() == 0) {
				showAlertDialog("Please enter address !");
			} else if (dateStr.equals("0-00-0000")) {
				showAlertDialog("Please select date !");
			} else if (AddressSelectionFragment.time == Constants
					.getMilliseconds(txtDate.getText().toString())
					|| (Constants.getMilliseconds(txtDate.getText().toString()) < AddressSelectionFragment.time)) {
				long time1;
				time1 = Constants.getMilliseconds(txtDate.getText().toString());
				Log.d("Send", "Check Time>  " + time1 + " >> "
						+ AddressSelectionFragment.time);
				showAlertDialog("End date should be greater than start date");
			} else {
				New_SearchAddressByName(edtEndAddress.getText().toString());
			}

		} else if (v == btnApproxAddr)
			// edtEndAddress.setText(Constants.approxAddress);
			if (Constants.approxAddress.toString().replace(" ", "")
					.equals(",,,,")) {
				edtEndAddress.setText("");
			} else {
				edtEndAddress.setText(Constants.approxAddress);
			}
		else if (v == btnTrueAddr)
			// edtEndAddress.setText(Constants.trueAddress);
			edtEndAddress.setText(Constants.trueAddress.toString().replace(
					", ,", ","));
		else if (v == btnStartingAddr) {
			String str = AddressSelectionFragment.edtStartAddress.getText()
					.toString();
			Log.e("TAGG", "equalsIgnoreCase -->> " + str);
			if (str.equalsIgnoreCase("None")) {
				edtEndAddress.setText(str);
			} else {
				edtEndAddress.setText(str);
			}
		} else if (v == btnCustom) {
			if (!isCustomPopOpen || !alert.isShowing()) {
				edtEndAddress.setEnabled(true);
				// edtEndAddress.setInputType(InputType.TYPE_CLASS_TEXT);
				edtEndAddress.setFocusable(true);
				customAddressAlert(edtEndAddress);
				isCustomPopOpen = true;
			}
		} else if (v == btnPrevious) {
			Constants.TempEndTime = txtDate.getText().toString();
			Constants.TempEndAddress = edtEndAddress.getText().toString();
			onBack();
		}
	}

	public void onBack() {
		FragmentManager fm = getFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
		}
	}

	private void showAlertDialog(String s) {
		Context context = getActivity().getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, s, duration);
		toast.show();
	}

	// dd/MM/yyyy hh:mm:ss a
	@Override
	public void onSet(Dialog dialog, Calendar calendarSelected,
			Date dateSelected, int year, String monthFullName,
			String monthShortName, int monthNumber, int date,
			String weekDayFullName, String weekDayShortName, int hour24,
			int hour12, int min, int sec, String AM_PM) {
		// TODO Auto-generated method stub
		dateStr = (monthNumber + 1) + "/" + date + "/" + year + " " + hour24
				+ ":" + min + ":" + sec;
		txtDate.setText(dateStr);
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub

	}

	/*
	 * Handler.
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == SUCCESS) {
				if (msg.obj instanceof List) {
					List data = (List) msg.obj;
					if (data != null && data.size() > 0) {
						Address adress = (Address) data.get(0);
						Bundle bundle = getArguments();

						if (edtEndAddress.getText().toString().equals("None"))
							bundle.putString("EndAddress", "None");

						else
							bundle.putString("EndAddress",
									adress.getAddressLine(0));

						Constants.TempEndAddress = adress.getAddressLine(0);
						// bundle.putString("EndAddress",
						// Constants.TempEndAddress);
						bundle.putString("EndDate", dateStr);
						bundle.putString("EndLat",
								String.valueOf(adress.getLatitude()));
						bundle.putString("EndLon",
								String.valueOf(adress.getLongitude()));

						FragmentManager fragmentManager = getActivity()
								.getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager
								.beginTransaction();
						Fragment fragment = new CommentsAddFragment();
						fragmentTransaction.replace(R.id.mainContainer,
								fragment, "Comments");
						fragmentTransaction.addToBackStack("EndAddress");
						fragment.setArguments(bundle);
						fragmentTransaction.commit();

					} else {
						showAlertDialog("Please Enter Valid Address !");
					}
				}
			} else if (msg.arg1 == ADDRESS_NOT_VALID) {
				Toast.makeText(getActivity(), "Please enter valid Address",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void New_SearchAddressByName(String name) {
		final ProgressDialog progressBar = new ProgressDialog(getActivity());
		progressBar.setMessage("Checking Address...");
		progressBar.show();
		final StringBuilder stringBuilder = new StringBuilder();
		final String addressTobeSearch = name.replaceAll(" ", "%20");

		String str = "No address Found";

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Geocoder geocoder = new Geocoder(getActivity());
				try {

					HttpPost httppost = new HttpPost(
							"http://maps.google.com/maps/api/geocode/json?address="
									+ addressTobeSearch + "&sensor=false");
					HttpClient client = new DefaultHttpClient();
					HttpResponse response;

					response = client.execute(httppost);
					HttpEntity entity = response.getEntity();
					InputStream stream = entity.getContent();
					int b;
					while ((b = stream.read()) != -1) {
						stringBuilder.append((char) b);
					}

					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject = new JSONObject(stringBuilder.toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// List<Address> searchedAddress = geocoder
					// .getFromLocationName(addressTobeSearch, 1);
					List<Address> searchedAddress = getAddrByWeb(jsonObject);
					progressBar.dismiss();
					Message msg = handler.obtainMessage();
					if (stringBuilder != null && stringBuilder.length() > 0) {
						msg.obj = searchedAddress;
						msg.arg1 = SUCCESS;

					} else {
						msg.arg1 = ADDRESS_NOT_VALID;
					}
					handler.sendMessage(msg);
					Log.e("Send", "O/p >> " + stringBuilder);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("Send", "Error " + e.toString() + " > "
							+ addressTobeSearch);
				}
			}
		}).start();

	}

	private List<Address> getAddrByWeb(JSONObject jsonObject) {
		List<Address> res = new ArrayList<Address>();
		try {
			JSONArray array = (JSONArray) jsonObject.get("results");
			for (int i = 0; i < array.length(); i++) {
				Double lon = new Double(0);
				Double lat = new Double(0);
				String name = "";
				try {
					lon = array.getJSONObject(i).getJSONObject("geometry")
							.getJSONObject("location").getDouble("lng");
					Log.e("Send", "Lon >> " + lon);
					lat = array.getJSONObject(i).getJSONObject("geometry")
							.getJSONObject("location").getDouble("lat");
					Log.e("Send", "Lat >> " + lat);
					name = array.getJSONObject(i)
							.getString("formatted_address");
					Log.e("Send", "NAme >> " + name);
					Address addr = new Address(Locale.getDefault());
					addr.setLatitude(lat);
					addr.setLongitude(lon);
					addr.setAddressLine(0, name != null ? name : "");
					res.add(addr);
					Log.e("Send", "addr >> " + addr);
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e("Send", "Error >> " + e.toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	private void searchAddressByName(String name) {
		final ProgressDialog progressBar = new ProgressDialog(getActivity());
		progressBar.setCancelable(false);
		progressBar.setMessage("Checking address...");
		progressBar.show();
		final String addressTobeSearch = name;
		String str = "No address Found";

		new Thread(new Runnable() {

			@Override
			public void run() {
				String str = addressTobeSearch;

				Geocoder geocoder = new Geocoder(getActivity());
				try {
					List<Address> searchedAddress = geocoder
							.getFromLocationName(addressTobeSearch, 1);
					progressBar.dismiss();
					Message msg = handler.obtainMessage();
					if (searchedAddress != null && searchedAddress.size() > 0) {
						msg.obj = searchedAddress;
						msg.arg1 = SUCCESS;
					} else {
						msg.arg1 = ADDRESS_NOT_VALID;
					}
					handler.sendMessage(msg);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		}).start();

	}

	private void openAddressDialog() {
		final StringBuffer buff = new StringBuffer();
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle("");
		View layout = getActivity().getLayoutInflater().inflate(
				R.layout.popup_address, null);
		final EditText edtEndAddress1 = (EditText) layout
				.findViewById(R.id.edtEndAddress1);
		final EditText edtEndAddress2 = (EditText) layout
				.findViewById(R.id.edtEndAddress2);
		final EditText edtEndAddress3 = (EditText) layout
				.findViewById(R.id.edtEndAddress3);
		final EditText edtEndAddress4 = (EditText) layout
				.findViewById(R.id.edtEndAddress4);
		final EditText edtEndAddress5 = (EditText) layout
				.findViewById(R.id.edtEndAddress5);

		alertDialog.setView(layout);
		alertDialog.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (edtEndAddress1.getText().toString().length() > 0) {
							buff.append(edtEndAddress1.getText().toString()
									.toString()
									+ ",");
						}
						if (edtEndAddress2.getText().toString().length() > 0) {
							buff.append(edtEndAddress2.getText().toString()
									.toString()
									+ ",");
						}
						if (edtEndAddress3.getText().toString().length() > 0) {
							buff.append(edtEndAddress3.getText().toString()
									.toString()
									+ ",");
						}
						if (edtEndAddress4.getText().toString().length() > 0) {
							buff.append(edtEndAddress4.getText().toString()
									+ ",");
						}
						if (edtEndAddress5.getText().toString().length() > 0) {
							buff.append(edtEndAddress5.getText().toString());
						}
						addr = buff.toString();
						if (!addr.equals("")) {
							String adr = addr.substring(0,
									addr.lastIndexOf(","));
							edtEndAddress.setText(adr);
						}
					}
				});
		// Setting Negative "NO" Button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to invoke NO event
						dialog.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}

	private void customAddressAlert(final EditText field) {
		isCustomPopOpen = true;
		alert = new Dialog(getActivity());

		LinearLayout rl = (LinearLayout) getActivity().getLayoutInflater()
				.inflate(R.layout.custom_address_alert_dialog, null);

		final EditText address1 = (EditText) rl
				.findViewById(R.id.custom_alert_address1);
		final EditText address2 = (EditText) rl
				.findViewById(R.id.custom_alert_address2);
		final EditText address3 = (EditText) rl
				.findViewById(R.id.custom_alert_address3);
		final EditText address4 = (EditText) rl
				.findViewById(R.id.custom_alert_address4);
		final EditText address5 = (EditText) rl
				.findViewById(R.id.custom_alert_address5);

		/*
		 * address1.setText(Constants.job_address1);
		 * address2.setText(Constants.job_address2);
		 * address3.setText(Constants.job_address3);
		 * address4.setText(Constants.job_address4);
		 * address5.setText(Constants.job_address5);
		 */

		ImageButton resetfield1 = (ImageButton) rl
				.findViewById(R.id.resetfield1);
		ImageButton resetfield2 = (ImageButton) rl
				.findViewById(R.id.resetfield2);
		ImageButton resetfield3 = (ImageButton) rl
				.findViewById(R.id.resetfield3);
		ImageButton resetfield4 = (ImageButton) rl
				.findViewById(R.id.resetfield4);
		ImageButton resetfield5 = (ImageButton) rl
				.findViewById(R.id.resetfield5);

		resetfield1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				address1.setText("");
			}
		});

		resetfield2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				address2.setText("");
			}
		});

		resetfield3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				address3.setText("");
			}
		});

		resetfield4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				address4.setText("");
			}
		});

		resetfield5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				address5.setText("");
			}
		});

		Button done = (Button) rl.findViewById(R.id.custom_alert_done);
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = "";
				if (address1.getText().toString().length() > 0) {
					str += str.length() == 0 ? address1.getText().toString()
							: "," + address1.getText().toString();
				}

				if (address2.getText().toString().length() > 0) {
					str += str.length() == 0 ? address2.getText().toString()
							: "," + address2.getText().toString();
				}

				if (address3.getText().toString().length() > 0) {
					str += str.length() == 0 ? address3.getText().toString()
							: "," + address3.getText().toString();
				}

				if (address4.getText().toString().length() > 0) {
					str += str.length() == 0 ? address4.getText().toString()
							: "," + address4.getText().toString();
				}
				if (address5.getText().toString().length() > 0) {
					str += str.length() == 0 ? address5.getText().toString()
							: "," + address5.getText().toString();
				}

				field.setText(str);

				if (address1.hasFocus()) {
					hideSoftInput(address1);
				}

				if (address2.hasFocus()) {
					hideSoftInput(address2);
				}

				if (address3.hasFocus()) {
					hideSoftInput(address3);
				}

				if (address4.hasFocus()) {
					hideSoftInput(address4);
				}
				if (address5.hasFocus()) {
					hideSoftInput(address5);
				}

				alert.dismiss();
				isCustomPopOpen = false;
			}
		});

		Button cancel = (Button) rl.findViewById(R.id.custom_alert_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alert.dismiss();
				isCustomPopOpen = false;
			}
		});

		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.setContentView(rl);
		alert.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alert.show();

	}

	public void hideSoftInput(View view) {
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}
